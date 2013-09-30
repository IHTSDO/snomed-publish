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

import com.google.common.base.Objects;
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
    
    private static final Long REFSET_PLAN_ID = 1L;

    @Inject
    private RefsetPlanService planService;

    @Mock
    private RefsetPlanRepository refsetPlanRepositoryMock; 
    
    @Mock
    private RefsetRuleService refsetRuleServiceMock;

    private RefsetPlan refsetPlan;
    private RefsetRuleDto listRuleDtoLeft;
    private RefsetRuleDto listRuleDtoRight;
    private RefsetRuleDto unionRuleDto;
    private RefsetPlanDto refsetPlanDto;
    private ListConceptsRefsetRule listRuleLeft;
    private ListConceptsRefsetRule listRuleRight;
    private UnionRefsetRule unionRule;
    
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
        
    public RepositoryRefsetPlanServiceTest(){}
    
    @Before
    public void init(){
        ConceptDto c1 = ConceptDto.getBuilder().id(1L).build();
        ConceptDto c2 = ConceptDto.getBuilder().id(2L).build();
        ConceptDto c3 = ConceptDto.getBuilder().id(3L).build();
        
        ConceptDto c4 = ConceptDto.getBuilder().id(4L).build();
        ConceptDto c5 = ConceptDto.getBuilder().id(5L).build();
        ConceptDto c6 = ConceptDto.getBuilder().id(6L).build();
        
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
        
        refsetPlan = RefsetPlan.getBuilder(unionRule).id(REFSET_PLAN_ID).build();        
    }

    @Test
    public void create() throws UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException {
        RefsetPlan persisted = refsetPlan;
        RefsetPlanDto created = refsetPlanDto;
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
    public void update() throws RefsetPlanNotFoundException, UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException {
        ConceptDto c11 = ConceptDto.getBuilder().id(11L).build();
        ConceptDto c12 = ConceptDto.getBuilder().id(12L).build();
        ConceptDto c13 = ConceptDto.getBuilder().id(13L).build();
        
        ConceptDto c14 = ConceptDto.getBuilder().id(14L).build();
        ConceptDto c15 = ConceptDto.getBuilder().id(15L).build();
        ConceptDto c16 = ConceptDto.getBuilder().id(16L).build();
        
        RefsetRuleDto uListRuleDtoLeft = RefsetRuleDto.getBuilder()
                .id(-1L)
                .type(RuleType.LIST)
                .add(c11).add(c12).add(c13)
                .build();
        
        RefsetRuleDto uListRuleDtoRight = RefsetRuleDto.getBuilder()
                .id(-2L)
                .add(c14).add(c15).add(c16)
                .type(RuleType.LIST)
                .build();
        
        RefsetRuleDto uUnionRuleDto = RefsetRuleDto.getBuilder()
                .id(-3L)
                .type(RuleType.UNION)
                .left(uListRuleDtoLeft.getId())
                .right(uListRuleDtoRight.getId())
                .build();
        
        RefsetPlanDto uRefsetPlanDto = RefsetPlanDto.getBuilder()
               .terminal(unionRuleDto.getId())
               .id(REFSET_PLAN_ID)
               .add(uListRuleDtoLeft)
               .add(uListRuleDtoRight)
               .add(uUnionRuleDto)
               .build();
        
        Concept cc11 = new Concept(11L);
        Concept cc12 = new Concept(12L);
        Concept cc13 = new Concept(13L);
        Concept cc14 = new Concept(14L);
        Concept cc15 = new Concept(15L);
        Concept cc16 = new Concept(16L);       
        
        ListConceptsRefsetRule uListRuleLeft = new ListConceptsRefsetRule();
        uListRuleLeft.setId(-1L);
        uListRuleLeft.addConcept(cc11);
        uListRuleLeft.addConcept(cc12);
        uListRuleLeft.addConcept(cc13);
        
        ListConceptsRefsetRule uListRuleRight = new ListConceptsRefsetRule();
        uListRuleRight.setId(-2L);
        uListRuleRight.addConcept(cc14);
        uListRuleRight.addConcept(cc15);
        uListRuleRight.addConcept(cc16);
        
        UnionRefsetRule uUnionRule = new UnionRefsetRule();
        uUnionRule.setId(-3L);
        uUnionRule.setLeftRule(uListRuleLeft);
        uUnionRule.setRightRule(uListRuleRight);
        
        RefsetPlan uRefsetPlan = RefsetPlan.getBuilder(uUnionRule).id(REFSET_PLAN_ID).build();
        
        RefsetPlan original = refsetPlan;
        RefsetPlan updated = uRefsetPlan;
        RefsetPlanDto updatedDto = uRefsetPlanDto;
        
        when(refsetPlanRepositoryMock.findOne(updatedDto.getId())).thenReturn(original);
        when(refsetPlanRepositoryMock.save(any(RefsetPlan.class))).thenReturn(updated);
        
        when(refsetRuleServiceMock.create(uListRuleDtoLeft)).thenReturn(listRuleLeft);
        when(refsetRuleServiceMock.create(uListRuleDtoRight)).thenReturn(listRuleRight);
        when(refsetRuleServiceMock.create(uUnionRuleDto)).thenReturn(unionRule);        

//        when(refsetRuleServiceMock.update(listRuleDtoLeft)).thenReturn(listRuleLeft);
//        when(refsetRuleServiceMock.update(listRuleDtoRight)).thenReturn(listRuleRight);
//        when(refsetRuleServiceMock.update(unionRuleDto)).thenReturn(unionRule);       

        RefsetPlan returned = (RefsetPlan) planService.update(updatedDto);
        
        verify(refsetPlanRepositoryMock, times(1)).findOne(updatedDto.getId());
        verify(refsetPlanRepositoryMock, times(1)).save(any(RefsetPlan.class));
        verify(refsetRuleServiceMock, times(3)).create(any(RefsetRuleDto.class));
        //verify(refsetRuleServiceMock, times(3)).update(any(RefsetRuleDto.class));
        verifyNoMoreInteractions(refsetPlanRepositoryMock);
        verifyNoMoreInteractions(refsetRuleServiceMock);
        
        assertRefsetPlan(updatedDto, returned);        
    }
    
    @Test(expected = RefsetPlanNotFoundException.class)
    public void updateWhenRefsetIsNotFound() throws RefsetPlanNotFoundException, UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException {
        RefsetPlanDto updatedDto = refsetPlanDto;
        when(refsetPlanRepositoryMock.findOne(updatedDto.getId())).thenReturn(null);
        planService.update(updatedDto);
    }

    private void assertRefsetPlan(RefsetPlanDto expected, RefsetPlan actual) {
        RefsetPlanDto parsedActual = RefsetPlanDto.parse(actual);
        assertTrue(
                (Objects.equal(expected.getRefsetRules(), parsedActual.getRefsetRules())) &&
                (Objects.equal(expected.getTerminal(), parsedActual.getTerminal())));
    }


}