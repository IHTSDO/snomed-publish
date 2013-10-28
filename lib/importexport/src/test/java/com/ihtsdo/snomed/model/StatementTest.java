package com.ihtsdo.snomed.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.service.parser.DatabaseTest;

public class StatementTest extends DatabaseTest{

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void shouldSetStatementInConcept() {
        Concept s = new Concept(1);
        Concept p = new Concept(2);
        Concept o = new Concept(3);
        Statement st = new Statement(1, s, p, o);
        
        em.persist(s);
        em.persist(p);
        em.persist(o);
        em.persist(st);
        em.flush();
        em.clear();
        
        st = em.createQuery("SELECT s FROM Statement s where s.serialisedId=1", Statement.class).getSingleResult();
        s = em.createQuery("SELECT c FROM Concept c where c.serialisedId=1", Concept.class).getSingleResult();
        p = em.createQuery("SELECT c FROM Concept c where c.serialisedId=2", Concept.class).getSingleResult();
        o = em.createQuery("SELECT c FROM Concept c where c.serialisedId=3", Concept.class).getSingleResult();

        assertTrue(s.getSubjectOfStatements().contains(st));
        assertTrue(p.getPredicateOfStatements().contains(st));
        assertTrue(o.getObjectOfStatements().contains(st));
    }
    
    @Test
    public void shouldSetIsKindOfRelationship(){
        Concept s = new Concept(1);
        Concept p = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o = new Concept(3);
        Statement st = new Statement(1, s, p, o);
        
        em.persist(s);
        em.persist(p);
        em.persist(o);
        em.persist(st);
        em.flush();
        em.clear();
        
        st = em.createQuery("SELECT s FROM Statement s where s.serialisedId=1", Statement.class).getSingleResult();

        assertTrue(st.isKindOfStatement());
    }
    
    @Test
    public void shouldReturnIsDefiningCharacteristicType(){
        Concept s = new Concept(1);
        Concept p = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o = new Concept(3);
        Statement st = new Statement(1, s, p, o, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);

        em.persist(s);
        em.persist(p);
        em.persist(o);
        em.persist(st);
        em.flush();
        em.clear();
        
        st = em.createQuery("SELECT s FROM Statement s where s.serialisedId=1", Statement.class).getSingleResult();
        
        assertTrue(st.isDefiningCharacteristic());
    } 
    
    @Test
    public void shouldReturnIsMemberOfGroup(){
        Concept s = new Concept(1);
        Concept p = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o = new Concept(3);
        Statement st = new Statement(1, s, p, o, 2, 1);
        
        em.persist(s);
        em.persist(p);
        em.persist(o);
        em.persist(st);
        em.flush();
        em.clear();
        
        st = em.createQuery("SELECT s FROM Statement s where s.serialisedId=1", Statement.class).getSingleResult();
        
        assertTrue(st.isMemberOfGroup());
    }
    
    @Test
    public void shouldReturnEqualStatementOnSerialisedId(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept o1 = new Concept(3);
        Statement st1 = new Statement(1, s1, p1, o1);
        
        Concept s2 = new Concept(1);
        Concept p2 = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o2 = new Concept(3);
        Statement st2 = new Statement(1, s2, p2, o2);
        
        em.persist(s1);
        em.persist(p1);
        em.persist(o1);
        em.persist(st1);
        em.persist(s2);
        em.persist(p2);
        em.persist(o2);
        em.persist(st2);
        em.flush();
        em.clear();
        
        st1 = em.createQuery("SELECT s FROM Statement s where s.id=1", Statement.class).getSingleResult();
        st2 = em.createQuery("SELECT s FROM Statement s where s.id=2", Statement.class).getSingleResult();
        
        assertEquals(st1, st2);
    }
    
    @Test
    public void shouldReturnEqualStatementOnMissingSerialisedId(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept o1 = new Concept(3);
        Statement st1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o1);
        Statement st2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o1);
        
        
        em.persist(s1);
        em.persist(p1);
        em.persist(o1);
        em.persist(st1);
        em.persist(st2);
        em.flush();
        em.clear();
        
        st1 = em.createQuery("SELECT s FROM Statement s where s.id=1", Statement.class).getSingleResult();
        st2 = em.createQuery("SELECT s FROM Statement s where s.id=2", Statement.class).getSingleResult();        
        
        assertEquals(st1, st2);
    }   
    
    @Test
    public void shouldHaveEqualHashCode1(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept p2 = new Concept(3);
        Concept o1 = new Concept(4);
        Statement st1 = new Statement(1, s1, p1, o1);
        Statement st2 = new Statement(1, s1, p2, o1);
        
        em.persist(s1);
        em.persist(p1);
        em.persist(p2);
        em.persist(o1);
        em.persist(st1);
        em.persist(st2);
        em.flush();
        em.clear();
        
        st1 = em.createQuery("SELECT s FROM Statement s where s.id=1", Statement.class).getSingleResult();
        st2 = em.createQuery("SELECT s FROM Statement s where s.id=2", Statement.class).getSingleResult();           
        
        assertEquals(st1.hashCode(), st2.hashCode());
    }

    
    @Test
    public void shouldHaveEqualHashCode2(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept p2 = new Concept(3);
        Concept o1 = new Concept(4);
        Statement st1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o1);
        Statement st2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p2, o1);
        
        em.persist(s1);
        em.persist(p1);
        em.persist(p2);
        em.persist(o1);
        em.persist(st1);
        em.persist(st2);
        em.flush();
        em.clear();
        
        st1 = em.createQuery("SELECT s FROM Statement s where s.id=1", Statement.class).getSingleResult();
        st2 = em.createQuery("SELECT s FROM Statement s where s.id=2", Statement.class).getSingleResult();         
        
        assertEquals(st1.hashCode(), st2.hashCode());
    }      
    
}
