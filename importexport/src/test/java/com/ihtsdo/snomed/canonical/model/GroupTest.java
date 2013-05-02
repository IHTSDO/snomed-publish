package com.ihtsdo.snomed.canonical.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.service.HibernateDatabaseImporterTest;

public class GroupTest extends HibernateDatabaseImporterTest{
    
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

}
