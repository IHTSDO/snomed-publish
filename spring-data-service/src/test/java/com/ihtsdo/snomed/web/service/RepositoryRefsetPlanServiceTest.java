package com.ihtsdo.snomed.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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

import com.google.common.base.Objects;
import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule.RuleType;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.UnionRefsetRule;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.RefsetPlanService;
import com.ihtsdo.snomed.service.RefsetRuleService;
import com.ihtsdo.snomed.web.repository.RefsetPlanRepository;
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
public class RepositoryRefsetPlanServiceTest {
    
    private static final Long REFSET_PLAN_ID = 1L;

    @Inject
    private RefsetPlanService planService;

    @Mock
    private RefsetPlanRepository refsetPlanRepositoryMock; 
    
    @Mock
    private RefsetRuleRepository refsetRuleRepositoryMock;
    
    @Mock
    private RefsetRuleService refsetRuleServiceMock;

    @Mock
    private ConceptService conceptServiceMock;

    private RefsetPlan refsetPlan;
    private RefsetRuleDto listRuleDtoLeft;
    private RefsetRuleDto listRuleDtoRight;
    private RefsetRuleDto unionRuleDto;
    private RefsetPlanDto refsetPlanDto;
    private ListConceptsRefsetRule listRuleLeft;
    private ListConceptsRefsetRule listRuleRight;
    private UnionRefsetRule unionRule;
    private ConceptDto c1, c2, c3, c4, c5, c6;
    private RefsetPlan updatedRefsetPlan;
    private RefsetPlanDto updatedDtoRefsetPlan;
    
    private ListConceptsRefsetRule newLeft;

    private ListConceptsRefsetRule newRight;

    private UnionRefsetRule newUnion;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetPlanService) SpringProxyUtil.unwrapProxy(planService)), 
                "refsetPlanRepository", 
                refsetPlanRepositoryMock);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetPlanService) SpringProxyUtil.unwrapProxy(planService)), 
                "refsetRuleRepository", 
                refsetRuleRepositoryMock);
        
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetPlanService) SpringProxyUtil.unwrapProxy(planService)), 
                "refsetRuleService", 
                refsetRuleServiceMock);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetPlanService) SpringProxyUtil.unwrapProxy(planService)), 
                "conceptService", 
                conceptServiceMock);        
    }
        
    public RepositoryRefsetPlanServiceTest(){}
    
    @Before
    public void init() throws Exception{        
        
        c1 = ConceptDto.getBuilder().id(1L).build();
        c2 = ConceptDto.getBuilder().id(2L).build();
        c3 = ConceptDto.getBuilder().id(3L).build();
        
        c4 = ConceptDto.getBuilder().id(4L).build();
        c5 = ConceptDto.getBuilder().id(5L).build();
        c6 = ConceptDto.getBuilder().id(6L).build();
        
        listRuleDtoLeft = RefsetRuleDto.getBuilder()
                .id(-1L)
                .type(RuleType.LIST)
                .add(c1).add(c2).add(c3)
                .build();
        
        listRuleDtoRight = RefsetRuleDto.getBuilder()
                .id(-2L)
                .add(c4).add(c5).add(c6)
                .type(RuleType.LIST)
                .build();
        
        unionRuleDto = RefsetRuleDto.getBuilder()
                .id(-3L)
                .type(RuleType.UNION)
                .left(listRuleDtoLeft.getId())
                .right(listRuleDtoRight.getId())
                .build();
        
        refsetPlanDto = RefsetPlanDto.getBuilder()
               .terminal(unionRuleDto.getId())
               .id(REFSET_PLAN_ID)
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
        
        when(conceptServiceMock.findBySerialisedId(eq(1l))).thenReturn(cc1);
        when(conceptServiceMock.findBySerialisedId(eq(2l))).thenReturn(cc2);
        when(conceptServiceMock.findBySerialisedId(eq(3l))).thenReturn(cc3);
        when(conceptServiceMock.findBySerialisedId(eq(4l))).thenReturn(cc4);
        when(conceptServiceMock.findBySerialisedId(eq(5l))).thenReturn(cc5);
        when(conceptServiceMock.findBySerialisedId(eq(6l))).thenReturn(cc6);
        
        Concept uc11 = new Concept(11L);
        Concept uc12 = new Concept(12L);
        Concept uc13 = new Concept(13L);
        Concept uc14 = new Concept(14L);
        Concept uc15 = new Concept(15L);
        Concept uc16 = new Concept(16L);       
        
        when(conceptServiceMock.findBySerialisedId(eq(11l))).thenReturn(uc11);
        when(conceptServiceMock.findBySerialisedId(eq(12l))).thenReturn(uc12);
        when(conceptServiceMock.findBySerialisedId(eq(13l))).thenReturn(uc13);
        when(conceptServiceMock.findBySerialisedId(eq(14l))).thenReturn(uc14);
        when(conceptServiceMock.findBySerialisedId(eq(15l))).thenReturn(uc15);
        when(conceptServiceMock.findBySerialisedId(eq(16l))).thenReturn(uc16);
        
        ConceptDto uc11dto = ConceptDto.getBuilder().id(11L).build();
        ConceptDto uc12dto = ConceptDto.getBuilder().id(12L).build();
        ConceptDto uc13dto = ConceptDto.getBuilder().id(13L).build();
        ConceptDto uc14dto = ConceptDto.getBuilder().id(14L).build();
        ConceptDto uc15dto = ConceptDto.getBuilder().id(15L).build();
        ConceptDto uc16dto = ConceptDto.getBuilder().id(16L).build();
        
        ListConceptsRefsetRule updatedLeft = new ListConceptsRefsetRule();
        updatedLeft.setId(1L);
        updatedLeft.addConcept(uc11);
        updatedLeft.addConcept(uc12);
        updatedLeft.addConcept(uc13);
        
        ListConceptsRefsetRule updatedRight = new ListConceptsRefsetRule();
        updatedRight.setId(2L);
        updatedRight.addConcept(uc14);
        updatedRight.addConcept(uc15);
        updatedRight.addConcept(uc16);
        
        UnionRefsetRule updatedUnion = new UnionRefsetRule();
        updatedUnion.setId(3L);
        updatedUnion.setLeftRule(updatedLeft);
        updatedUnion.setRightRule(updatedRight);
        
        updatedRefsetPlan = RefsetPlan.getBuilder(updatedUnion).id(REFSET_PLAN_ID).build();      
        
        RefsetRuleDto updatedDtoLeft = RefsetRuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .add(uc11dto).add(uc12dto).add(uc13dto)
                .build();
        
        RefsetRuleDto updatedDtoRight = RefsetRuleDto.getBuilder()
                .id(2L)
                .add(uc14dto).add(uc15dto).add(uc16dto)
                .type(RuleType.LIST)
                .build();
        
        RefsetRuleDto updatedDtoUnion = RefsetRuleDto.getBuilder()
                .id(3L)
                .type(RuleType.UNION)
                .left(updatedDtoLeft.getId())
                .right(updatedDtoRight.getId())
                .build();
        
        updatedDtoRefsetPlan = RefsetPlanDto.getBuilder()
               .terminal(updatedDtoUnion.getId())
               .id(REFSET_PLAN_ID)
               .add(updatedDtoLeft)
               .add(updatedDtoRight)
               .add(updatedDtoUnion)
               .build();
              

        newLeft = (ListConceptsRefsetRule) BaseRefsetRule.getRuleInstanceFor(listRuleDtoLeft);
        newLeft.setId(1l);
        newRight = (ListConceptsRefsetRule) BaseRefsetRule.getRuleInstanceFor(listRuleDtoRight);
        newRight.setId(2l);
        newUnion = (UnionRefsetRule) BaseRefsetRule.getRuleInstanceFor(unionRuleDto);
        newUnion.setId(3l);
        
        listRuleLeft = new ListConceptsRefsetRule();
        listRuleLeft.setId(-1L);
        listRuleLeft.addConcept(cc1);
        listRuleLeft.addConcept(cc2);
        listRuleLeft.addConcept(cc3);
        
        listRuleRight = new ListConceptsRefsetRule();
        listRuleRight.setId(-2L);
        listRuleRight.addConcept(cc4);
        listRuleRight.addConcept(cc5);
        listRuleRight.addConcept(cc6);
        
        unionRule = new UnionRefsetRule();
        unionRule.setId(-3L);
        unionRule.setLeftRule(listRuleLeft);
        unionRule.setRightRule(listRuleRight);
        
        ListConceptsRefsetRule l = new ListConceptsRefsetRule();
        l.setId(1L);
        l.addConcept(cc1);
        l.addConcept(cc2);
        l.addConcept(cc3);
        
        ListConceptsRefsetRule r = new ListConceptsRefsetRule();
        r.setId(2L);
        r.addConcept(cc4);
        r.addConcept(cc5);
        r.addConcept(cc6);
        
        UnionRefsetRule u = new UnionRefsetRule();
        u.setId(3L);
        u.setLeftRule(l);
        u.setRightRule(r);        
        
        refsetPlan = RefsetPlan.getBuilder(u).id(REFSET_PLAN_ID).build();        
    }

    @Test
    public void create() throws ValidationException {
        RefsetPlan persisted = refsetPlan;
        RefsetPlanDto created = refsetPlanDto;
        
        RefsetRuleDto eLeft = RefsetRuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .add(c1).add(c2).add(c3)
                .build();
        
        RefsetRuleDto eRight = RefsetRuleDto.getBuilder()
                .id(2L)
                .add(c4).add(c5).add(c6)
                .type(RuleType.LIST)
                .build();
        
        RefsetRuleDto eUnion = RefsetRuleDto.getBuilder()
                .id(3L)
                .type(RuleType.UNION)
                .left(eLeft.getId())
                .right(eRight.getId())
                .build();
        
        RefsetPlanDto expected = RefsetPlanDto.getBuilder()
               .terminal(eUnion.getId())
               .id(0l)
               .add(eLeft)
               .add(eRight)
               .add(eUnion)
               .build();        
        
        when(refsetPlanRepositoryMock.save(any(RefsetPlan.class))).thenReturn(persisted);        
        when(refsetRuleRepositoryMock.save(any(BaseRefsetRule.class))).thenReturn(newLeft, newRight, newUnion);
                
        RefsetPlan returned = planService.create(created);
        
        ArgumentCaptor<RefsetPlan> refsetPlanArgument = ArgumentCaptor.forClass(RefsetPlan.class);
        verify(refsetPlanRepositoryMock, times(1)).save(refsetPlanArgument.capture());

        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        verifyNoMoreInteractions(refsetRuleServiceMock);
        
        assertRefsetPlan(expected, refsetPlanArgument.getValue());
        assertEquals(persisted, returned);
    }
    
    @Test
    public void delete() throws RefsetPlanNotFoundException {
        RefsetPlan deleted = refsetPlan;
        deleted.setId(REFSET_PLAN_ID);
        
        when(refsetPlanRepositoryMock.findOne(deleted.getId())).thenReturn(deleted);
        
        RefsetPlan returned = planService.delete(REFSET_PLAN_ID);
        
        verify(refsetPlanRepositoryMock, times(1)).findOne(REFSET_PLAN_ID);
        verify(refsetPlanRepositoryMock, times(1)).delete(deleted);
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        
        assertEquals(deleted, returned);  
    }
    
    @Test(expected = RefsetPlanNotFoundException.class)
    public void deleteWhenRefsetPlanIsNotFound() throws RefsetPlanNotFoundException {
        RefsetPlan deleted = refsetPlan;
        
        when(refsetPlanRepositoryMock.findOne(REFSET_PLAN_ID)).thenReturn(null);
        
        RefsetPlan returned = planService.delete(REFSET_PLAN_ID);
        
        verify(refsetPlanRepositoryMock, times(1)).findOne(REFSET_PLAN_ID);
        verify(refsetPlanRepositoryMock, times(1)).delete(deleted);
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        
        assertEquals(deleted, returned);        
    }
    
    @Test
    public void findById() {
        when(refsetPlanRepositoryMock.findOne(REFSET_PLAN_ID)).thenReturn(refsetPlan);
        RefsetPlan returned = planService.findById(REFSET_PLAN_ID);
        verify(refsetPlanRepositoryMock, times(1)).findOne(REFSET_PLAN_ID);
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        assertEquals(refsetPlan, returned);
    }
    
    @Test
    public void update() throws ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException {

        when(refsetPlanRepositoryMock.save(any(RefsetPlan.class))).thenReturn(updatedRefsetPlan);
        when(refsetPlanRepositoryMock.findOne(refsetPlan.getId())).thenReturn(refsetPlan);
        when(refsetRuleRepositoryMock.save(any(BaseRefsetRule.class))).thenReturn(newLeft, newRight, newUnion);

        RefsetPlan returned = planService.update(updatedDtoRefsetPlan);
        
        ArgumentCaptor<RefsetPlan> refsetPlanArgument = ArgumentCaptor.forClass(RefsetPlan.class);
        verify(refsetPlanRepositoryMock, times(1)).save(refsetPlanArgument.capture());
        verify(refsetPlanRepositoryMock, times(1)).findOne(updatedDtoRefsetPlan.getId());
        verify(refsetRuleRepositoryMock, times(3)).save(any(BaseRefsetRule.class));
        verify(refsetRuleServiceMock, times(1)).delete(refsetPlan.getTerminal().getId());
                
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        verifyNoMoreInteractions(refsetRuleServiceMock);
        verifyNoMoreInteractions(refsetRuleRepositoryMock);
        
        assertEquals(updatedDtoRefsetPlan, RefsetPlanDto.parse(refsetPlanArgument.getValue()));
        assertEquals(updatedRefsetPlan, returned);
    }
    
    @Test(expected = RefsetPlanNotFoundException.class)
    public void updateWhenRefsetIsNotFound() throws ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException {
        RefsetPlanDto updatedDto = refsetPlanDto;
        when(refsetPlanRepositoryMock.findOne(updatedDto.getId())).thenReturn(null);
        planService.update(updatedDto);
    }

    public static void assertRefsetPlan(RefsetPlanDto expected, RefsetPlan actual) {
        RefsetPlanDto parsedActual = RefsetPlanDto.parse(actual);
        assertTrue(
                (Objects.equal(expected.getRefsetRules(), parsedActual.getRefsetRules())) &&
                (Objects.equal(expected.getTerminal(), parsedActual.getTerminal())));
    }


}