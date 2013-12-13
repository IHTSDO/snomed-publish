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

import test.com.ihtsdo.snomed.web.SpringProxyUtil;

import com.google.common.base.Objects;
import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.RuleDto;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.BaseRule;
import com.ihtsdo.snomed.model.refset.BaseRule.RuleType;
import com.ihtsdo.snomed.model.refset.Plan;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.UnionRefsetRule;
import com.ihtsdo.snomed.repository.refset.PlanRepository;
import com.ihtsdo.snomed.repository.refset.RuleRepository;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.refset.PlanService;
import com.ihtsdo.snomed.service.refset.RepositoryPlanService;
import com.ihtsdo.snomed.service.refset.RuleService;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = {
        "classpath:sds-applicationContext.xml", 
        "classpath:test-spring-data.xml"})
public class RepositoryRefsetPlanServiceTest {
    
    private static final Long REFSET_PLAN_ID = 1L;

    @Inject
    private PlanService planService;

    @Mock
    private PlanRepository refsetPlanRepositoryMock; 
    
    @Mock
    private RuleRepository refsetRuleRepositoryMock;
    
    @Mock
    private RuleService refsetRuleServiceMock;

    @Mock
    private ConceptService conceptServiceMock;

    private Plan plan;
    private RuleDto listRuleDtoLeft;
    private RuleDto listRuleDtoRight;
    private RuleDto unionRuleDto;
    private PlanDto planDto;
    private ListConceptsRefsetRule listRuleLeft;
    private ListConceptsRefsetRule listRuleRight;
    private UnionRefsetRule unionRule;
    private ConceptDto c1, c2, c3, c4, c5, c6;
    private Plan updatedRefsetPlan;
    private PlanDto updatedDtoRefsetPlan;
    
    private ListConceptsRefsetRule newLeft;

    private ListConceptsRefsetRule newRight;

    private UnionRefsetRule newUnion;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(
                ((RepositoryPlanService) SpringProxyUtil.unwrapProxy(planService)), 
                "planRepository", 
                refsetPlanRepositoryMock);
        
        ReflectionTestUtils.setField(
                ((RepositoryPlanService) SpringProxyUtil.unwrapProxy(planService)), 
                "ruleRepository", 
                refsetRuleRepositoryMock);
        
        
        ReflectionTestUtils.setField(
                ((RepositoryPlanService) SpringProxyUtil.unwrapProxy(planService)), 
                "ruleService", 
                refsetRuleServiceMock);
        
        ReflectionTestUtils.setField(
                ((RepositoryPlanService) SpringProxyUtil.unwrapProxy(planService)), 
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
        
        listRuleDtoLeft = RuleDto.getBuilder()
                .id(-1L)
                .type(RuleType.LIST)
                .add(c1).add(c2).add(c3)
                .build();
        
        listRuleDtoRight = RuleDto.getBuilder()
                .id(-2L)
                .add(c4).add(c5).add(c6)
                .type(RuleType.LIST)
                .build();
        
        unionRuleDto = RuleDto.getBuilder()
                .id(-3L)
                .type(RuleType.UNION)
                .left(listRuleDtoLeft.getId())
                .right(listRuleDtoRight.getId())
                .build();
        
        planDto = PlanDto.getBuilder()
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
        
        updatedRefsetPlan = Plan.getBuilder(updatedUnion).id(REFSET_PLAN_ID).build();      
        
        RuleDto updatedDtoLeft = RuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .add(uc11dto).add(uc12dto).add(uc13dto)
                .build();
        
        RuleDto updatedDtoRight = RuleDto.getBuilder()
                .id(2L)
                .add(uc14dto).add(uc15dto).add(uc16dto)
                .type(RuleType.LIST)
                .build();
        
        RuleDto updatedDtoUnion = RuleDto.getBuilder()
                .id(3L)
                .type(RuleType.UNION)
                .left(updatedDtoLeft.getId())
                .right(updatedDtoRight.getId())
                .build();
        
        updatedDtoRefsetPlan = PlanDto.getBuilder()
               .terminal(updatedDtoUnion.getId())
               .id(REFSET_PLAN_ID)
               .add(updatedDtoLeft)
               .add(updatedDtoRight)
               .add(updatedDtoUnion)
               .build();
              

        newLeft = (ListConceptsRefsetRule) BaseRule.getRuleInstanceFor(listRuleDtoLeft);
        newLeft.setId(1l);
        newRight = (ListConceptsRefsetRule) BaseRule.getRuleInstanceFor(listRuleDtoRight);
        newRight.setId(2l);
        newUnion = (UnionRefsetRule) BaseRule.getRuleInstanceFor(unionRuleDto);
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
        
        plan = Plan.getBuilder(u).id(REFSET_PLAN_ID).build();        
    }

    @Test
    public void create() throws ValidationException {
        Plan persisted = plan;
        PlanDto created = planDto;
        
        RuleDto eLeft = RuleDto.getBuilder()
                .id(1L)
                .type(RuleType.LIST)
                .add(c1).add(c2).add(c3)
                .build();
        
        RuleDto eRight = RuleDto.getBuilder()
                .id(2L)
                .add(c4).add(c5).add(c6)
                .type(RuleType.LIST)
                .build();
        
        RuleDto eUnion = RuleDto.getBuilder()
                .id(3L)
                .type(RuleType.UNION)
                .left(eLeft.getId())
                .right(eRight.getId())
                .build();
        
        PlanDto expected = PlanDto.getBuilder()
               .terminal(eUnion.getId())
               .id(0l)
               .add(eLeft)
               .add(eRight)
               .add(eUnion)
               .build();        
        
        when(refsetPlanRepositoryMock.save(any(Plan.class))).thenReturn(persisted);        
        when(refsetRuleRepositoryMock.save(any(BaseRule.class))).thenReturn(newLeft, newRight, newUnion);
                
        Plan returned = planService.create(created);
        
        ArgumentCaptor<Plan> refsetPlanArgument = ArgumentCaptor.forClass(Plan.class);
        verify(refsetPlanRepositoryMock, times(1)).save(refsetPlanArgument.capture());

        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        verifyNoMoreInteractions(refsetRuleServiceMock);
        
        assertRefsetPlan(expected, refsetPlanArgument.getValue());
        assertEquals(persisted, returned);
    }
    
    @Test
    public void delete() throws RefsetPlanNotFoundException {
        Plan deleted = plan;
        deleted.setId(REFSET_PLAN_ID);
        
        when(refsetPlanRepositoryMock.findOne(deleted.getId())).thenReturn(deleted);
        
        Plan returned = planService.delete(REFSET_PLAN_ID);
        
        verify(refsetPlanRepositoryMock, times(1)).findOne(REFSET_PLAN_ID);
        verify(refsetPlanRepositoryMock, times(1)).delete(deleted);
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        
        assertEquals(deleted, returned);  
    }
    
    @Test(expected = RefsetPlanNotFoundException.class)
    public void deleteWhenRefsetPlanIsNotFound() throws RefsetPlanNotFoundException {
        Plan deleted = plan;
        
        when(refsetPlanRepositoryMock.findOne(REFSET_PLAN_ID)).thenReturn(null);
        
        Plan returned = planService.delete(REFSET_PLAN_ID);
        
        verify(refsetPlanRepositoryMock, times(1)).findOne(REFSET_PLAN_ID);
        verify(refsetPlanRepositoryMock, times(1)).delete(deleted);
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        
        assertEquals(deleted, returned);        
    }
    
    @Test
    public void findById() {
        when(refsetPlanRepositoryMock.findOne(REFSET_PLAN_ID)).thenReturn(plan);
        Plan returned = planService.findById(REFSET_PLAN_ID);
        verify(refsetPlanRepositoryMock, times(1)).findOne(REFSET_PLAN_ID);
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        assertEquals(plan, returned);
    }
    
    @Test
    public void update() throws ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException {

        when(refsetPlanRepositoryMock.save(any(Plan.class))).thenReturn(updatedRefsetPlan);
        when(refsetPlanRepositoryMock.findOne(plan.getId())).thenReturn(plan);
        when(refsetRuleRepositoryMock.save(any(BaseRule.class))).thenReturn(newLeft, newRight, newUnion);

        Plan returned = planService.update(updatedDtoRefsetPlan);
        
        ArgumentCaptor<Plan> refsetPlanArgument = ArgumentCaptor.forClass(Plan.class);
        verify(refsetPlanRepositoryMock, times(1)).save(refsetPlanArgument.capture());
        verify(refsetPlanRepositoryMock, times(1)).findOne(updatedDtoRefsetPlan.getId());
        verify(refsetRuleRepositoryMock, times(3)).save(any(BaseRule.class));
        verify(refsetRuleServiceMock, times(1)).delete(plan.getTerminal().getId());
                
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        verifyNoMoreInteractions(refsetRuleServiceMock);
        verifyNoMoreInteractions(refsetRuleRepositoryMock);
        
        assertEquals(updatedDtoRefsetPlan, PlanDto.parse(refsetPlanArgument.getValue()));
        assertEquals(updatedRefsetPlan, returned);
    }
    
    @Test(expected = RefsetPlanNotFoundException.class)
    public void updateWhenRefsetIsNotFound() throws ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException {
        PlanDto updatedDto = planDto;
        when(refsetPlanRepositoryMock.findOne(updatedDto.getId())).thenReturn(null);
        planService.update(updatedDto);
    }

    public static void assertRefsetPlan(PlanDto expected, Plan actual) {
        PlanDto parsedActual = PlanDto.parse(actual);
        assertTrue(
                (Objects.equal(expected.getRefsetRules(), parsedActual.getRefsetRules())) &&
                (Objects.equal(expected.getTerminal(), parsedActual.getTerminal())));
    }


}