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
public class RefsetPlanTest {
    
    @PersistenceContext(unitName="testdb")
    EntityManager em;
    
    private RefsetPlan refsetPlan;
    private ListConceptsRefsetRule list1Rule;
    private ListConceptsRefsetRule list2Rule;
    private UnionRefsetRule unionRefsetRule;
    private DifferenceRefsetRule differenceRefsetRule;
    private IntersectionRefsetRule intersectionRefsetRule;
    private SymmetricDifferenceRefsetRule symmetricDifferenceRefsetRule;
    
    Concept c1, c2, c3, c4, c5;

    @Before
    public void setup(){
        refsetPlan = new RefsetPlan();
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
        RefsetPlan refsetPlan2 = new RefsetPlan();
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
        refsetPlan.setTerminal(unionRefsetRule);

        assertTrue(refsetPlan.equals(refsetPlan2));
        assertTrue(refsetPlan2.equals(refsetPlan));        
    }
    
    @Test
    public void testNotEquals1(){
        RefsetPlan refsetPlan2 = new RefsetPlan();
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
        refsetPlan.setTerminal(unionRefsetRule);

        assertTrue(!refsetPlan.equals(refsetPlan2));
        assertTrue(!refsetPlan2.equals(refsetPlan));        
    }
    
    @Test
    public void testNotEquals2(){
        RefsetPlan refsetPlan2 = new RefsetPlan();
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
        refsetPlan.setTerminal(unionRefsetRule);

        assertTrue(!refsetPlan.equals(refsetPlan2));
        assertTrue(!refsetPlan2.equals(refsetPlan));        
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
        refsetPlan.setTerminal(unionRefsetRule);
        refsetPlan.refreshConceptsCache();
        em.persist(refsetPlan);
        
        em.flush();
        em.clear();
        
        RefsetPlan newRefsetPlan = em.createQuery("Select r FROM RefsetPlan r WHERE id=:id", RefsetPlan.class)
                .setParameter("id", refsetPlan.getId())
                .getSingleResult();
        
        assertEquals(refsetPlan.getId(), newRefsetPlan.getId());
        assertTrue(refsetPlan.getTerminal().equals(newRefsetPlan.getTerminal()));
        assertTrue(refsetPlan.getConcepts().equals(newRefsetPlan.getConcepts()));
    }    
    
    
    @Test
    public void shouldGenerateConceptsFromList() throws ConceptsCacheNotBuiltException{
        refsetPlan.setTerminal(list1Rule);
        refsetPlan.refreshConceptsCache();
        assertEquals(3, refsetPlan.getConcepts().size());
        Iterator<Concept> conceptsIt = refsetPlan.getConcepts().iterator();
        assertEquals(1, conceptsIt.next().getSerialisedId());
        assertEquals(2, conceptsIt.next().getSerialisedId());
        assertEquals(3, conceptsIt.next().getSerialisedId());        
    }
    
    @Test
    public void shouldAppendRules() throws ConceptsCacheNotBuiltException{
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule);
        refsetPlan.setTerminal(unionRefsetRule);
        refsetPlan.refreshConceptsCache();
        assertEquals(5, refsetPlan.getConcepts().size());
        assertTrue(refsetPlan.getConcepts().contains(new Concept(1)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(2)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(3)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(4)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(5)));
    }
    
    @Test
    public void shouldPrependRules() throws ConceptsCacheNotBuiltException{
        unionRefsetRule.prepend(BaseSetOperationRefsetRule.RIGHT_OPERAND, list1Rule);
        unionRefsetRule.prepend(BaseSetOperationRefsetRule.LEFT_OPERAND, list2Rule);
        refsetPlan.setTerminal(unionRefsetRule);
        refsetPlan.refreshConceptsCache();
        assertEquals(5, refsetPlan.getConcepts().size());
        assertTrue(refsetPlan.getConcepts().contains(new Concept(1)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(2)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(3)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(4)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(5)));
    }
    
    @Test
    public void shouldDoDifference1() throws ConceptsCacheNotBuiltException{
        list1Rule.append(DifferenceRefsetRule.LEFT_OPERAND, differenceRefsetRule);
        list2Rule.append(DifferenceRefsetRule.RIGHT_OPERAND, differenceRefsetRule);
        refsetPlan.setTerminal(differenceRefsetRule);
        refsetPlan.refreshConceptsCache();
        assertEquals(1, refsetPlan.getConcepts().size());
        assertTrue(refsetPlan.getConcepts().contains(new Concept(1)));    
    }
    
    @Test
    public void shouldDoDifference2() throws ConceptsCacheNotBuiltException{
        list1Rule.append(DifferenceRefsetRule.RIGHT_OPERAND, differenceRefsetRule);
        list2Rule.append(DifferenceRefsetRule.LEFT_OPERAND, differenceRefsetRule);
        refsetPlan.setTerminal(differenceRefsetRule);
        refsetPlan.refreshConceptsCache();
        assertEquals(2, refsetPlan.getConcepts().size());
        assertTrue(refsetPlan.getConcepts().contains(new Concept(4)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(5)));        
    }
    
    @Test
    public void shouldDoIntersection() throws ConceptsCacheNotBuiltException{
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, intersectionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, intersectionRefsetRule);
        refsetPlan.setTerminal(intersectionRefsetRule);
        refsetPlan.refreshConceptsCache();
        assertEquals(2, refsetPlan.getConcepts().size());
        assertTrue(refsetPlan.getConcepts().contains(new Concept(2)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(3)));
    }

    @Test
    public void shouldDoSymmetricDifference() throws ConceptsCacheNotBuiltException{
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, symmetricDifferenceRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, symmetricDifferenceRefsetRule);
        refsetPlan.setTerminal(symmetricDifferenceRefsetRule);
        refsetPlan.refreshConceptsCache();
        assertEquals(3, refsetPlan.getConcepts().size());
        assertTrue(refsetPlan.getConcepts().contains(new Concept(1)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(4)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(5)));
    }         
    
    @Test
    public void shouldDoUnion() throws ConceptsCacheNotBuiltException{
        list1Rule.append(BaseSetOperationRefsetRule.RIGHT_OPERAND, unionRefsetRule);
        list2Rule.append(BaseSetOperationRefsetRule.LEFT_OPERAND, unionRefsetRule);
        refsetPlan.setTerminal(unionRefsetRule);
        refsetPlan.refreshConceptsCache();
        assertEquals(5, refsetPlan.getConcepts().size());
        assertTrue(refsetPlan.getConcepts().contains(new Concept(1)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(2)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(3)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(4)));
        assertTrue(refsetPlan.getConcepts().contains(new Concept(5)));    
    }   
    
    @Test(expected=ConceptsCacheNotBuiltException.class)
    public void shouldThrowExceptionIfCacheNotBuilt() throws ConceptsCacheNotBuiltException{
        refsetPlan.setTerminal(list1Rule);
        assertNull(refsetPlan.getConcepts());
    }    

}
