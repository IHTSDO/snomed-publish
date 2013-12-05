package com.ihtsdo.snomed.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto.Builder;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto.RuleType;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.RefsetRuleService;
import com.ihtsdo.snomed.web.repository.RefsetRuleRepository;
import com.ihtsdo.snomed.web.testing.SpringProxyUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = {
        "classpath:sds-applicationContext.xml", 
        "classpath:sds-spring-data.xml",
        "classpath:test-spring-data.xml"})
public class RepositoryRefsetRuleServiceTest {

    private static final long REFSET_RULE_ID = 1L;
    //private static final long UPDATED_REFSET_RULE_ID = 2L;

    @Inject
    private RefsetRuleService ruleService;

    @Mock
    private RefsetRuleRepository refsetRuleRepositoryMock;
    
    @Mock
    private ConceptService conceptServiceMock;
    
    private List<ConceptDto> conceptDtos;
    private Concept c1, c2, c3;
    private Set<Concept> concepts;
    Builder builder;
    
    public RepositoryRefsetRuleServiceTest(){
        conceptDtos = Arrays.asList(
                new ConceptDto(1),
                new ConceptDto(2),
                new ConceptDto(3)
                );
        
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        
        concepts = new HashSet<>(Arrays.asList(c1, c2, c3));
    }
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetRuleService) SpringProxyUtil.unwrapProxy(ruleService)), 
                "refsetRuleRepository", 
                refsetRuleRepositoryMock);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetRuleService) SpringProxyUtil.unwrapProxy(ruleService)), 
                "conceptService", 
                conceptServiceMock);
        
        builder = RefsetRuleDto.getBuilder()
                .id(-1l)
                .type(RuleType.LIST)
                .concepts(conceptDtos);
    }
    
    @Test
    public void shouldCreateListRule() throws ConceptNotFoundException {
        RefsetRuleDto created = builder.build();
        
        ListConceptsRefsetRule persisted = new ListConceptsRefsetRule();
        persisted.setConcepts(concepts);
        
        when(refsetRuleRepositoryMock.save(any(BaseRefsetRule.class))).thenReturn(persisted);
        when(conceptServiceMock.findBySerialisedId(1L)).thenReturn(c1);
        when(conceptServiceMock.findBySerialisedId(2L)).thenReturn(c2);
        when(conceptServiceMock.findBySerialisedId(3L)).thenReturn(c3);

        BaseRefsetRule returned = ruleService.create(created);
        assertTrue(returned instanceof ListConceptsRefsetRule);
        ArgumentCaptor<ListConceptsRefsetRule> refsetRuleArgument = ArgumentCaptor.forClass(ListConceptsRefsetRule.class);
        
        verify(refsetRuleRepositoryMock, times(1)).save(refsetRuleArgument.capture());
        verify(conceptServiceMock, times(3)).findBySerialisedId(any(Long.class));
        verifyNoMoreInteractions(refsetRuleRepositoryMock);
        verifyNoMoreInteractions(conceptServiceMock);
        
        assertRefsetRule(created, refsetRuleArgument.getValue());
        assertEquals(persisted, returned);
    }
    
    @Test
    public void delete() throws RefsetNotFoundException, RefsetRuleNotFoundException {
        ListConceptsRefsetRule deleted = new ListConceptsRefsetRule();
        deleted.setId(REFSET_RULE_ID);
        
        when(refsetRuleRepositoryMock.findOne(REFSET_RULE_ID)).thenReturn(deleted);
        
        BaseRefsetRule returned = ruleService.delete(REFSET_RULE_ID);
        
        verify(refsetRuleRepositoryMock, times(1)).findOne(REFSET_RULE_ID);
        verify(refsetRuleRepositoryMock, times(1)).delete(deleted);
        verifyNoMoreInteractions(refsetRuleRepositoryMock);
        
        assertEquals(deleted, returned);        
    }
    
    @Test(expected = RefsetRuleNotFoundException.class)
    public void deleteWhenRefsetIsNotFound() throws RefsetRuleNotFoundException {
        when(refsetRuleRepositoryMock.findOne(REFSET_RULE_ID)).thenReturn(null);
        
        ruleService.delete(REFSET_RULE_ID);
        
        verify(refsetRuleRepositoryMock, times(1)).findOne(REFSET_RULE_ID);
        verifyNoMoreInteractions(refsetRuleRepositoryMock);       
    }
    
    @Test
    public void findById() {
        ListConceptsRefsetRule rule = new ListConceptsRefsetRule();
        rule.setId(REFSET_RULE_ID);
        when(refsetRuleRepositoryMock.findOne(REFSET_RULE_ID)).thenReturn(rule);
        
        BaseRefsetRule returned = ruleService.findById(REFSET_RULE_ID);
        
        verify(refsetRuleRepositoryMock, times(1)).findOne(REFSET_RULE_ID);
        verifyNoMoreInteractions(refsetRuleRepositoryMock);
        
        assertEquals(rule, returned);
    }
    
    @Test
    public void update() throws RefsetNotFoundException, RefsetRuleNotFoundException, ConceptNotFoundException {
        ListConceptsRefsetRule original = new ListConceptsRefsetRule();
        original.setConcepts(concepts);
        original.setId(REFSET_RULE_ID);
        
        List<ConceptDto>updatedConceptDtos = Arrays.asList(
                new ConceptDto(4),
                new ConceptDto(5),
                new ConceptDto(6)
                );
        
        RefsetRuleDto updatedDto = builder.concepts(updatedConceptDtos).build();
        
        Concept uc1 = new Concept(4);
        Concept uc2 = new Concept(5);
        Concept uc3 = new Concept(6);
        
        Set<Concept> updatedConcepts = new HashSet<>(Arrays.asList(uc1, uc2, uc3));
                
        ListConceptsRefsetRule updated = new ListConceptsRefsetRule();
        updated.setConcepts(updatedConcepts);
        updated.setId(REFSET_RULE_ID);

        when(refsetRuleRepositoryMock.findOne(updatedDto.getId())).thenReturn(original);
        when(conceptServiceMock.findBySerialisedId(uc1.getSerialisedId())).thenReturn(uc1);
        when(conceptServiceMock.findBySerialisedId(uc2.getSerialisedId())).thenReturn(uc2);
        when(conceptServiceMock.findBySerialisedId(uc3.getSerialisedId())).thenReturn(uc3);        
        when(refsetRuleRepositoryMock.save(any(BaseRefsetRule.class))).thenReturn(updated);

        ListConceptsRefsetRule returned = (ListConceptsRefsetRule) ruleService.update(updatedDto);
        
        verify(refsetRuleRepositoryMock, times(1)).findOne(updatedDto.getId());
        verify(refsetRuleRepositoryMock, times(1)).save(any(BaseRefsetRule.class));
        verify(conceptServiceMock, times(3)).findBySerialisedId(any(Long.class));
        verifyNoMoreInteractions(refsetRuleRepositoryMock);
        verifyNoMoreInteractions(conceptServiceMock);
        
        assertRefsetRule(updatedDto, returned);
    }
    
    @Test(expected = RefsetRuleNotFoundException.class)
    public void updateWhenRefsetRuleIsNotFound() throws RefsetRuleNotFoundException, ConceptNotFoundException {
        RefsetRuleDto updatedDto = builder.build();
        when(refsetRuleRepositoryMock.findOne(updatedDto.getId())).thenReturn(null);
        ruleService.update(updatedDto);
    }

    private void assertRefsetRule(RefsetRuleDto expected, ListConceptsRefsetRule actual) {
        assertEquals(actual.getConcepts().size(), expected.getConcepts().size());
        for (ConceptDto dto : expected.getConcepts()){
            assertTrue(actual.getConcepts().contains(new Concept(dto.getId())));
        }
        
    }


}