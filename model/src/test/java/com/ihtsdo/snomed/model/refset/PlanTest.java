package com.ihtsdo.snomed.model.refset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.exception.ConceptsCacheNotBuiltException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.rule.BaseSetOperationRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.DifferenceRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.IntersectionRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.SymmetricDifferenceRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.UnionRefsetRule;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:spring-data.xml"})
public class PlanTest {
    
    @PersistenceContext(unitName="testdb")
    EntityManager em;
    
    private Plan plan;
    private ListConceptsRefsetRule list1Rule;
    private ListConceptsRefsetRule list2Rule;
    private UnionRefsetRule unionRefsetRule;
    private DifferenceRefsetRule differenceRefsetRule;
    private IntersectionRefsetRule intersectionRefsetRule;
    private SymmetricDifferenceRefsetRule symmetricDifferenceRefsetRule;
    
    Concept c1, c2, c3, c4, c5;

    @Before
    public void setup(){
        plan = new Plan();
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);
        c5 = new Concept(5);
        
        list1Rule = new ListConceptsRefsetRule();
        list1Rule.getConcepts().add(c1);
        list1Rule.getConcepts().add(c2);
        list1Rule.getConcepts().add(c3);
        
        list2Rule = new ListConceptsRefsetRule();
        list2Rule.getConcepts().add(c2);
        list2Rule.getConcepts().add(c3);
        list2Rule.getConcepts().add(c4);
        list2Rule.getConcepts().add(c5);
        
        unionRefsetRule = new UnionRefsetRule();
        differenceRefsetRule = new DifferenceRefsetRule();
        intersectionRefsetRule = new IntersectionRefsetRule();
        symmetricDifferenceRefsetRule = new SymmetricDifferenceRefsetRule();
        
        em.flush();
        em.clear();
    }
    
    @Test
    public void testEquals(){
        Plan refsetPlan2 = new Plan();
        ListConceptsRefsetRule list3Rule = new ListConceptsRefsetRule();
        list3Rule.getConcepts().add(new Concept(1));
        list3Rule.getConcepts().add(new Concept(2));
        list3Rule.getConcepts().add(new Concept(3));
        
        ListConceptsRefsetRule list4Rule = new ListConceptsRefsetRule();
        list4Rule.getConcepts().add(new Concept(2));
        list4Rule.getConcepts().add(new Concept(3));
        list4Rule.getConcepts().add(new Concept(4));
        list4Rule.getConcepts().add(new Concept(5));
        
        UnionRefsetRule unionRefsetRule2 = new UnionRefsetRule();
        list3Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule2);
        list4Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule2);
        refsetPlan2.setTerminal(unionRefsetRule2);
        
        list1Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule);
        plan.setTerminal(unionRefsetRule);

        assertTrue(plan.equals(refsetPlan2));
        assertTrue(refsetPlan2.equals(plan));        
    }
    
    @Test
    public void testNotEquals1(){
        Plan refsetPlan2 = new Plan();
        ListConceptsRefsetRule list3Rule = new ListConceptsRefsetRule();
        list3Rule.getConcepts().add(new Concept(1));
        //list3Rule.getConcepts().add(new Concept(2));
        list3Rule.getConcepts().add(new Concept(3));
        
        ListConceptsRefsetRule list4Rule = new ListConceptsRefsetRule();
        list4Rule.getConcepts().add(new Concept(2));
        list4Rule.getConcepts().add(new Concept(3));
        list4Rule.getConcepts().add(new Concept(4));
        list4Rule.getConcepts().add(new Concept(5));
        
        UnionRefsetRule unionRefsetRule2 = new UnionRefsetRule();
        list3Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule2);
        list4Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule2);
        refsetPlan2.setTerminal(unionRefsetRule2);
        
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule);
        plan.setTerminal(unionRefsetRule);

        assertTrue(!plan.equals(refsetPlan2));
        assertTrue(!refsetPlan2.equals(plan));        
    }
    
    @Test
    public void testNotEquals2(){
        Plan refsetPlan2 = new Plan();
        ListConceptsRefsetRule list3Rule = new ListConceptsRefsetRule();
        list3Rule.getConcepts().add(new Concept(1));
        list3Rule.getConcepts().add(new Concept(2));
        list3Rule.getConcepts().add(new Concept(3));
        
        ListConceptsRefsetRule list4Rule = new ListConceptsRefsetRule();
        list4Rule.getConcepts().add(new Concept(2));
        list4Rule.getConcepts().add(new Concept(3));
        list4Rule.getConcepts().add(new Concept(4));
        list4Rule.getConcepts().add(new Concept(5));
        
        //UnionRefsetRule unionRefsetRule2 = new UnionRefsetRule();
        IntersectionRefsetRule intersectionRefsetRule = new IntersectionRefsetRule();
        
        list3Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, intersectionRefsetRule);
        list4Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, intersectionRefsetRule);
        refsetPlan2.setTerminal(intersectionRefsetRule);
        
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule);
        plan.setTerminal(unionRefsetRule);

        assertTrue(!plan.equals(refsetPlan2));
        assertTrue(!refsetPlan2.equals(plan));        
    }    

    @Test
    @Rollback(true)
    public void shouldStoreRefsetPlan() throws ConceptsCacheNotBuiltException {
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);
        em.persist(c4);
        em.persist(c5);

        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule);
        plan.setTerminal(unionRefsetRule);
        plan.refreshConceptsCache();
        em.persist(plan);
        
        em.flush();
        em.clear();
        
        Plan newRefsetPlan = em.createQuery("Select r FROM Plan r WHERE id=:id", Plan.class)
                .setParameter("id", plan.getId())
                .getSingleResult();
        
        assertEquals(plan.getId(), newRefsetPlan.getId());
        assertTrue(plan.getTerminal().equals(newRefsetPlan.getTerminal()));
        assertTrue(plan.getConcepts().equals(newRefsetPlan.getConcepts()));
    }    
    
    
    @Test
    public void shouldGenerateConceptsFromList() throws ConceptsCacheNotBuiltException{
        plan.setTerminal(list1Rule);
        plan.refreshConceptsCache();
        assertEquals(3, plan.getConcepts().size());
        Iterator<Concept> conceptsIt = plan.getConcepts().iterator();
        assertEquals(1, conceptsIt.next().getSerialisedId());
        assertEquals(2, conceptsIt.next().getSerialisedId());
        assertEquals(3, conceptsIt.next().getSerialisedId());        
    }
    
    @Test
    public void shouldAppendRules() throws ConceptsCacheNotBuiltException{
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule);
        plan.setTerminal(unionRefsetRule);
        plan.refreshConceptsCache();
        assertEquals(5, plan.getConcepts().size());
        assertTrue(plan.getConcepts().contains(new Concept(1)));
        assertTrue(plan.getConcepts().contains(new Concept(2)));
        assertTrue(plan.getConcepts().contains(new Concept(3)));
        assertTrue(plan.getConcepts().contains(new Concept(4)));
        assertTrue(plan.getConcepts().contains(new Concept(5)));
    }
    
    @Test
    public void shouldPrependRules() throws ConceptsCacheNotBuiltException{
        unionRefsetRule.prepend(BaseSetOperationRefsetRule.RIGHT_OPERAND, list1Rule);
        unionRefsetRule.prepend(BaseSetOperationRefsetRule.LEFT_OPERAND, list2Rule);
        plan.setTerminal(unionRefsetRule);
        plan.refreshConceptsCache();
        assertEquals(5, plan.getConcepts().size());
        assertTrue(plan.getConcepts().contains(new Concept(1)));
        assertTrue(plan.getConcepts().contains(new Concept(2)));
        assertTrue(plan.getConcepts().contains(new Concept(3)));
        assertTrue(plan.getConcepts().contains(new Concept(4)));
        assertTrue(plan.getConcepts().contains(new Concept(5)));
    }
    
    @Test
    public void shouldDoDifference1() throws ConceptsCacheNotBuiltException{
        list1Rule.append(DifferenceRefsetRule.LEFT_OPERAND, differenceRefsetRule);
        list2Rule.append(DifferenceRefsetRule.RIGHT_OPERAND, differenceRefsetRule);
        plan.setTerminal(differenceRefsetRule);
        plan.refreshConceptsCache();
        assertEquals(1, plan.getConcepts().size());
        assertTrue(plan.getConcepts().contains(new Concept(1)));    
    }
    
    @Test
    public void shouldDoDifference2() throws ConceptsCacheNotBuiltException{
        list1Rule.append(DifferenceRefsetRule.RIGHT_OPERAND, differenceRefsetRule);
        list2Rule.append(DifferenceRefsetRule.LEFT_OPERAND, differenceRefsetRule);
        plan.setTerminal(differenceRefsetRule);
        plan.refreshConceptsCache();
        assertEquals(2, plan.getConcepts().size());
        assertTrue(plan.getConcepts().contains(new Concept(4)));
        assertTrue(plan.getConcepts().contains(new Concept(5)));        
    }
    
    @Test
    public void shouldDoIntersection() throws ConceptsCacheNotBuiltException{
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, intersectionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, intersectionRefsetRule);
        plan.setTerminal(intersectionRefsetRule);
        plan.refreshConceptsCache();
        assertEquals(2, plan.getConcepts().size());
        assertTrue(plan.getConcepts().contains(new Concept(2)));
        assertTrue(plan.getConcepts().contains(new Concept(3)));
    }

    @Test
    public void shouldDoSymmetricDifference() throws ConceptsCacheNotBuiltException{
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, symmetricDifferenceRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, symmetricDifferenceRefsetRule);
        plan.setTerminal(symmetricDifferenceRefsetRule);
        plan.refreshConceptsCache();
        assertEquals(3, plan.getConcepts().size());
        assertTrue(plan.getConcepts().contains(new Concept(1)));
        assertTrue(plan.getConcepts().contains(new Concept(4)));
        assertTrue(plan.getConcepts().contains(new Concept(5)));
    }         
    
    @Test
    public void shouldDoUnion() throws ConceptsCacheNotBuiltException{
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule);
        plan.setTerminal(unionRefsetRule);
        plan.refreshConceptsCache();
        assertEquals(5, plan.getConcepts().size());
        assertTrue(plan.getConcepts().contains(new Concept(1)));
        assertTrue(plan.getConcepts().contains(new Concept(2)));
        assertTrue(plan.getConcepts().contains(new Concept(3)));
        assertTrue(plan.getConcepts().contains(new Concept(4)));
        assertTrue(plan.getConcepts().contains(new Concept(5)));    
    }   
    
    @Test(expected=ConceptsCacheNotBuiltException.class)
    public void shouldThrowExceptionIfCacheNotBuilt() throws ConceptsCacheNotBuiltException{
        plan.setTerminal(list1Rule);
        assertNull(plan.getConcepts());
    }    

}
