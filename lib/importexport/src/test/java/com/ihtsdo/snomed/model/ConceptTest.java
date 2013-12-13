package com.ihtsdo.snomed.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.exception.InvalidInputException;
import com.ihtsdo.snomed.service.parser.BaseTest;
import com.ihtsdo.snomed.service.parser.HibernateParser;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class ConceptTest  extends BaseTest{
    private static final Logger LOG = LoggerFactory.getLogger(ConceptTest.class);
    
    Concept c1,c2,c3,c4;
    
    HibernateParser parser = HibernateParserFactory.getParser(Parser.RF1);
    
    @BeforeClass
    public static void beforeClass(){
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML);
        em = emf.createEntityManager();        
    }
    
    @AfterClass
    public static void afterClass(){
        emf.close();
    }    
    
    @After
    public void tearDown() throws Exception {
        em.getTransaction().rollback();
    }
    
    @Before
    public void setUp() throws Exception {
        em.getTransaction().begin();
        em.getTransaction().setRollbackOnly();
        
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);

        c1.setPrimitive(true);
        c2.setPrimitive(true);
        c3.setPrimitive(true);
        c4.setPrimitive(true);
        
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);
        em.persist(c4);
        em.flush();
        em.clear();
        
        c1 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=1", Concept.class).getSingleResult();
        c2 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=2", Concept.class).getSingleResult();
        c3 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=3", Concept.class).getSingleResult();
        c4 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=4", Concept.class).getSingleResult();
    }
    
    /*
     * Test case 1:
     * ------------
     * {1,2,3} is primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 
     * All primitive supertypes for concept 1: 
     * {2,3}
     */
    @Test
    public void shouldPassPrimitiveParentTestCase1_WithCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        em.persist(c1);
        em.persist(c2);
        em.flush();
        em.clear();
        
        c1 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=1", Concept.class).getSingleResult();
        c2 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=2", Concept.class).getSingleResult();
        
        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
    }    
    @Test
    public void shouldPassPrimitiveParentTestCase1_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        em.persist(c1);
        em.persist(c2);
        em.flush();
        em.clear();
        
        c1 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=1", Concept.class).getSingleResult();
        c2 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=2", Concept.class).getSingleResult();
        
        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(false);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
    }
    
    /*
     * Test case 3:
     * ------------
     * {1,4} is primitive, {2,3} is not primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 3 isKindOf 4
     * 
     * All primitive supertypes for concept 1: 
     * {4}
     */ 
    @Test
    public void shouldPassPrimitiveParentTestCase3_WithCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);
        
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);
        em.flush();
        em.clear();
        
        c1 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=1", Concept.class).getSingleResult();
        c2 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=2", Concept.class).getSingleResult();
        c3 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=3", Concept.class).getSingleResult();

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(1, allPrimitive.size());
        assertTrue(allPrimitive.contains(c4));
    }        
    
    @Test
    public void shouldReturnTrueForIsKindOfPredicate(){
        Concept c = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        em.persist(c);
        em.flush();
        em.clear();
        c = em.createQuery("SELECT c FROM Concept c where c.serialisedId=" + Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID, Concept.class).getSingleResult();
        
        assertTrue(c.isKindOfPredicate());
    }
    
    @Test
    public void shouldReturnTrueForIsPredicate(){
        Concept c = new Concept(123);
//        c.setType(Concept.ATTRIBUTE);
        Concept subject = new Concept(1000);
        Concept object = new Concept(1001);
        Statement s = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, subject, c, object);
        em.persist(c);
        em.persist(subject);
        em.persist(object);
        em.persist(s);
        em.flush();
        em.clear();
        c = em.createQuery("SELECT c FROM Concept c where c.serialisedId=123", Concept.class).getSingleResult();
        
        assertTrue(c.isPredicate());
    }
    
    @Test
    public void shouldBeEqualConcepts(){
        Concept cl1 = new Concept(123);
        Concept cl2 = new Concept(123);
        
        em.persist(cl1);
        em.persist(cl2);
        em.flush();
        em.clear();
        cl1 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=123", Concept.class).getSingleResult();
        cl2 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=123", Concept.class).getSingleResult();
        
        assertEquals(cl1, cl2);
        assertEquals(cl1.hashCode(), cl2.hashCode());
    }
    
    @Test
    public void shouldCreateGroupFromStatement() throws InvalidInputException{
        Concept c = new Concept(123);
        Concept p = new Concept(999);
        Concept o = new Concept(666);
        Statement s = new Statement(444);
        s.setSubject(c);
        s.setPredicate(p);
        s.setObject(o);
        s.setGroupId(5);
        c.addSubjectOfStatement(s);
        
        em.persist(c);
        em.persist(p);
        em.persist(o);
        em.persist(s);
        em.flush();
        em.clear();
        c = em.createQuery("SELECT c FROM Concept c where c.serialisedId=123", Concept.class).getSingleResult();
        s = em.createQuery("SELECT s FROM Statement s where s.serialisedId=444", Statement.class).getSingleResult();
        
        Group g = new Group(s);
        assertTrue(g.equals(c.getGroup(s)));
    }
    
    @Test
    public void shouldGetCorrectGroupFromMultipleGroupsTrue() throws InvalidInputException{
        Concept c = new Concept(123);
        Concept o1 = new Concept(666);
        Concept p1 = new Concept(999);
        Concept p2 = new Concept(9991);
        Concept o2 = new Concept(6661);
        
        Statement s = new Statement(444);
        s.setSubject(c);
        s.setPredicate(p1);
        s.setObject(o1);
        s.setGroupId(5);

        Statement s1 = new Statement(444);
        s1.setSubject(c);
        s1.setPredicate(p2);
        s1.setObject(o2);
        s1.setGroupId(2);

        c.addSubjectOfStatement(s);
        c.addSubjectOfStatement(s1);
        
        em.persist(c);
        em.persist(p1);
        em.persist(o1);
        em.persist(p2);
        em.persist(o2);
        em.persist(s);
        em.persist(s1);
        em.flush();
        em.clear();
        
        c = em.createQuery("SELECT c FROM Concept c where c.serialisedId=123", Concept.class).getSingleResult();
        s = em.createQuery("SELECT s FROM Statement s where s.id=1", Statement.class).getSingleResult();
                
        
        assertTrue(new Group(s).equals(c.getGroup(s)));
    }

    @Test
    public void shouldGetCorrectGroupFromMultipleStatementsTrue() throws InvalidInputException{
        Concept c = new Concept(123);
        Concept p1 = new Concept(999);
        Concept o1 = new Concept(666);
        Concept p2 = new Concept(9991);
        Concept o2 = new Concept(6661);
        
        Statement s = new Statement(444);
        s.setSubject(c);
        s.setPredicate(p1);
        s.setObject(o1);
        s.setGroupId(2);

        Statement s1 = new Statement(4441);
        s1.setSubject(c);
        s1.setPredicate(p2);
        s1.setObject(o2);
        s1.setGroupId(2);

        c.addSubjectOfStatement(s);
        c.addSubjectOfStatement(s1);
        
        em.persist(c);
        em.persist(p1);
        em.persist(o1);
        em.persist(p2);
        em.persist(o2);
        em.persist(s);
        em.persist(s1);
        em.flush();
        em.clear();
        
        c = em.createQuery("SELECT c FROM Concept c where c.serialisedId=123", Concept.class).getSingleResult();
        s = em.createQuery("SELECT s FROM Statement s where s.id=1", Statement.class).getSingleResult();
        s1 = em.createQuery("SELECT s FROM Statement s where s.id=2", Statement.class).getSingleResult();
        
        assertTrue(new Group(Arrays.asList(s1, s)).equals(c.getGroup(s)));
    }
}
