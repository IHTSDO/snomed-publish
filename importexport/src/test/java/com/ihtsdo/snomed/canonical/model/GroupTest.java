package com.ihtsdo.snomed.canonical.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.service.DatabaseTest;
import com.ihtsdo.snomed.canonical.service.HibernateDatabaseImporterTest;
import com.ihtsdo.snomed.canonical.service.InvalidInputException;

public class GroupTest extends DatabaseTest{
    
    private static final String TEST_GROUP_RELATIONSHIPS_LONG_FORM = "group_relationships_longform.txt";
    private static final String TEST_GROUP_CONCEPTS = "test_group_concepts.txt";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        HibernateDatabaseImporterTest.setUpBeforeClass();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        HibernateDatabaseImporterTest.tearDownAfterClass();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }


    @Test
    public void shouldReturnEqual() throws IOException {
        
        importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_GROUP_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_GROUP_RELATIONSHIPS_LONG_FORM), em);       
        
        Concept c1 = em.createQuery("SELECT c FROM Concept c where c.ontology.id=1 AND c.serialisedId=1", Concept.class).getSingleResult();
        Concept c2 = em.createQuery("SELECT c FROM Concept c where c.ontology.id=1 AND c.serialisedId=2", Concept.class).getSingleResult();
        
        assertEquals(c1.getGroup(c1.getSubjectOfStatements().iterator().next()),
                c2.getGroup(c2.getSubjectOfStatements().iterator().next()));
    }
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionWhenSettingWrongStatementInGroup() throws InvalidInputException{
        Statement s1 = new Statement(123);
        Statement s2 = new Statement(1234);
        Concept c1 = new Concept(1);
        Concept c2 = new Concept(2);
        Concept c3 = new Concept(3);
        Concept c4 = new Concept(4);
        s1.setSubject(c1);
        s2.setSubject(c2);
        s1.setPredicate(c3);
        s2.setPredicate(c4);
       
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);
        em.persist(c4);
        em.persist(s1);
        em.persist(s2);
        em.flush();
        em.clear();
        
        s1 = em.createQuery("SELECT s FROM Statement s where s.serialisedId=123", Statement.class).getSingleResult();
        s2 = em.createQuery("SELECT s FROM Statement s where s.serialisedId=1234", Statement.class).getSingleResult();
        
        new Group(Arrays.asList(s1, s2));
    }    
    
    @Test
    public void shouldEqualStatementWrapperForAttributeCompare(){
        Statement s1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED);
        Statement s2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED);
        Concept c1 = new Concept(1);
        Concept c2 = new Concept(2);
        s1.setObject(c1);
        s2.setObject(c1);
        s1.setPredicate(c2);
        s2.setPredicate(c2);
        
        em.persist(c1);
        em.persist(c2);
        em.persist(s1);
        em.persist(s2);
        em.flush();
        em.clear();
        
        s1 = em.createQuery("SELECT s FROM Statement s where s.id=1", Statement.class).getSingleResult();
        s2 = em.createQuery("SELECT s FROM Statement s where s.id=2", Statement.class).getSingleResult();
        
        Group.StatementWrapperForAttributeCompare w1 = new Group.StatementWrapperForAttributeCompare(s1);
        Group.StatementWrapperForAttributeCompare w2 = new Group.StatementWrapperForAttributeCompare(s2);
        
        assertEquals(w1, w2);
    }    

}
