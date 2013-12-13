package com.ihtsdo.snomed.model.refset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.UnionRefsetRule;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:spring-data.xml"})
public class RuleTest {
    
    @PersistenceContext(unitName="testdb")
    EntityManager em;
    
    //private Plan refsetPlan;
    private ListConceptsRefsetRule list1Rule;
    private ListConceptsRefsetRule list2Rule;
    private UnionRefsetRule unionRefsetRule;
//    private DifferenceRefsetRule differenceRefsetRule;
//    private IntersectionRefsetRule intersectionRefsetRule;
//    private SymmetricDifferenceRefsetRule symmetricDifferenceRefsetRule;
    
    Concept c1, c2, c3, c4, c5, c6;

    @Before
    public void setup(){
        //refsetPlan = new Plan();
        
        c1 = new Concept(1L);
        c2 = new Concept(2L);
        c3 = new Concept(3L);
        c4 = new Concept(4L);
        c5 = new Concept(5L);
        c6 = new Concept(6L);
        
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);
        em.persist(c4);
        em.persist(c5);
        em.persist(c6);
                
        list1Rule = new ListConceptsRefsetRule();
        list1Rule.getConcepts().add(c1);
        list1Rule.getConcepts().add(c2);
        list1Rule.getConcepts().add(c3);
        
        list2Rule = new ListConceptsRefsetRule();
        list2Rule.getConcepts().add(c2);
        list2Rule.getConcepts().add(c3);
        list2Rule.getConcepts().add(c4);
        list2Rule.getConcepts().add(c5);
        
        em.persist(list1Rule);
        em.persist(list2Rule);
        
        unionRefsetRule = new UnionRefsetRule();
        unionRefsetRule.setLeftRule(list1Rule);
        unionRefsetRule.setRightRule(list2Rule);
        
        em.persist(unionRefsetRule);
        
//        differenceRefsetRule = new DifferenceRefsetRule();
//        intersectionRefsetRule = new IntersectionRefsetRule();
//        symmetricDifferenceRefsetRule = new SymmetricDifferenceRefsetRule();
        
        em.flush();
        em.clear();
    }
    
    @Test
    public void shouldLoadAllData(){
        BaseRule r = em.createQuery("SELECT r FROM BaseRule r WHERE id=:id", BaseRule.class)
                .setParameter("id", unionRefsetRule.getId())
                .getSingleResult();
        
        assertTrue(r instanceof UnionRefsetRule);
        UnionRefsetRule uRule = (UnionRefsetRule) r;
        assertEquals(uRule.getLeft(), list1Rule);
        assertEquals(uRule.getRight(), list2Rule);
        
        assertEquals(((ListConceptsRefsetRule)uRule.getLeft()).getConcepts().size(), 3);
        assertTrue(((ListConceptsRefsetRule)uRule.getLeft()).getConcepts().contains(c1));
        assertTrue(((ListConceptsRefsetRule)uRule.getLeft()).getConcepts().contains(c2));
        assertTrue(((ListConceptsRefsetRule)uRule.getLeft()).getConcepts().contains(c3));
        
        assertEquals(((ListConceptsRefsetRule)uRule.getRight()).getConcepts().size(), 4);
        assertTrue(((ListConceptsRefsetRule)uRule.getRight()).getConcepts().contains(c2));
        assertTrue(((ListConceptsRefsetRule)uRule.getRight()).getConcepts().contains(c3));
        assertTrue(((ListConceptsRefsetRule)uRule.getRight()).getConcepts().contains(c4));
        assertTrue(((ListConceptsRefsetRule)uRule.getRight()).getConcepts().contains(c5));
        
    }
    

}
