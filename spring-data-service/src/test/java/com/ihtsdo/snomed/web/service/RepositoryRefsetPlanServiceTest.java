package com.ihtsdo.snomed.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto.RuleType;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.UnconnectedRefsetRuleException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.UnionRefsetRule;
import com.ihtsdo.snomed.service.RefsetPlanService;
import com.ihtsdo.snomed.service.RefsetRuleService;
import com.ihtsdo.snomed.service.UnReferencedReferenceRuleException;
import com.ihtsdo.snomed.web.repository.RefsetPlanRepository;
import com.ihtsdo.snomed.web.testing.SpringProxyUtil;

/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = {
        "classpath:sds-applicationContext.xml", 
        "classpath:sds-spring-data.xml",
        "classpath:test-spring-data.xml"})
public class RepositoryRefsetPlanServiceTest {

    @Inject
    private RefsetPlanService planService;

    @Mock
    private RefsetPlanRepository refsetPlanRepositoryMock; 
    
    @Mock
    private RefsetRuleService refsetRuleServiceMock;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetPlanService) SpringProxyUtil.unwrapProxy(planService)), 
                "refsetPlanRepository", 
                refsetPlanRepositoryMock);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetPlanService) SpringProxyUtil.unwrapProxy(planService)), 
                "refsetRuleService", 
                refsetRuleServiceMock);
    }
        
    public RepositoryRefsetPlanServiceTest(){
    }

    @Test
    public void create() throws UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException {
        
        ConceptDto c1 = ConceptDto.getBuilder().id(1L).build();
        ConceptDto c2 = ConceptDto.getBuilder().id(2L).build();
        ConceptDto c3 = ConceptDto.getBuilder().id(3L).build();
        
        ConceptDto c4 = ConceptDto.getBuilder().id(4L).build();
        ConceptDto c5 = ConceptDto.getBuilder().id(5L).build();
        ConceptDto c6 = ConceptDto.getBuilder().id(6L).build();
        
        RefsetRuleDto listRuleDtoLeft = RefsetRuleDto.getBuilder()
                .id(-1L)
                .type(RuleType.LIST)
                .add(c1).add(c2).add(c3)
                .build();
        
        RefsetRuleDto listRuleDtoRight = RefsetRuleDto.getBuilder()
                .id(-2L)
                .add(c4).add(c5).add(c6)
                .type(RuleType.LIST)
                .build();
        
        RefsetRuleDto unionRuleDto = RefsetRuleDto.getBuilder()
                .id(-3L)
                .type(RuleType.UNION)
                .left(listRuleDtoLeft.getId())
                .right(listRuleDtoRight.getId())
                .build();
        
        RefsetPlanDto created = RefsetPlanDto.getBuilder()
               .terminal(unionRuleDto.getId())
               .add(listRuleDtoLeft)
               .add(listRuleDtoRight)
               .add(unionRuleDto)
               .build();
        
        Concept cc1 = new Concept(1L);
        Concept cc2 = new Concept(2L);
        Concept cc3 = new Concept(3L);
        Concept cc4 = new Concept(4L);
        Concept cc5 = new Concept(5L);
        Concept cc6 = new Concept(6L);       
        
        ListConceptsRefsetRule listRuleLeft = new ListConceptsRefsetRule();
        listRuleLeft.setId(-1L);
        listRuleLeft.addConcept(cc1);
        listRuleLeft.addConcept(cc2);
        listRuleLeft.addConcept(cc3);
        
        ListConceptsRefsetRule listRuleRight = new ListConceptsRefsetRule();
        listRuleRight.setId(-2L);
        listRuleRight.addConcept(cc4);
        listRuleRight.addConcept(cc5);
        listRuleRight.addConcept(cc6);
        
        UnionRefsetRule unionRule = new UnionRefsetRule();
        unionRule.setId(-3L);
        unionRule.setLeftRule(listRuleLeft);
        unionRule.setRightRule(listRuleRight);
        
        RefsetPlan persisted = RefsetPlan.getBuilder(unionRule).build();
        
        when(refsetPlanRepositoryMock.save(any(RefsetPlan.class))).thenReturn(persisted);
        
        when(refsetRuleServiceMock.create(listRuleDtoLeft)).thenReturn(listRuleLeft);
        when(refsetRuleServiceMock.create(listRuleDtoRight)).thenReturn(listRuleRight);
        when(refsetRuleServiceMock.create(unionRuleDto)).thenReturn(unionRule);        
        
        RefsetPlan returned = planService.create(created);
        
        ArgumentCaptor<RefsetPlan> refsetPlanArgument = ArgumentCaptor.forClass(RefsetPlan.class);
        
        verify(refsetPlanRepositoryMock, times(1)).save(refsetPlanArgument.capture());
        verify(refsetRuleServiceMock, times(3)).create(any(RefsetRuleDto.class));
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        verifyNoMoreInteractions(refsetRuleServiceMock);
        
        assertRefsetPlan(created, refsetPlanArgument.getValue());
        assertEquals(persisted, returned);
    }
    
    @Test
    public void delete() throws RefsetPlanNotFoundException {
       
    }
    
//    @Test(expected = RefsetPlanNotFoundException.class)
//    public void deleteWhenRefsetPlanIsNotFound() throws RefsetPlanNotFoundException {
//       
//    }
    
    @Test
    public void findById() {
       
    }
    
    @Test
    public void update() throws RefsetPlanNotFoundException {
     
    }
//    
//    @Test(expected = RefsetPlanNotFoundException.class)
//    public void updateWhenRefsetIsNotFound() throws RefsetPlanNotFoundException {
//       
//    }

    private void assertRefsetPlan(RefsetPlanDto expected, RefsetPlan actual) {
        RefsetPlanDto parsedActual = RefsetPlanDto.parse(actual);
        assertTrue(expected.equals(parsedActual));
    }


}