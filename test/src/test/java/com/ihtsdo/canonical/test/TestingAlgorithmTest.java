package com.ihtsdo.canonical.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.canonical.model.Ontology;
import com.ihtsdo.snomed.canonical.test.TestingAlgorithm;
import com.ihtsdo.snomed.service.HibernateDbImporter;

public class TestingAlgorithmTest {
    private static final Logger LOG = LoggerFactory.getLogger( TestingAlgorithmTest.class );

    private static HibernateDbImporter importer;
    private   TestingAlgorithm tester = new TestingAlgorithm();

    private static final String TEST_CONCEPTS = "test_concepts.txt";
    private static final String TEST_RELATIONSHIPS_LONG_FORM = "test_relationships_longform.txt";
    private static final String TEST_RELATIONSHIPS_SHORT_FORM = "test_relationships_shortform.txt";

    private   EntityManagerFactory emf = null;
    private   EntityManager em = null;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        importer = new HibernateDbImporter();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateDbImporter.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML);
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        LOG.info("Closing database");
        emf.close();
    }

    @Test
    public void test() throws FileNotFoundException, IOException {
        Ontology originalOntology = importer.populateDbFromLongForm("original", ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), 
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_LONG_FORM), em);
        Ontology expectedOntology = importer.populateDbFromShortForm("expected", ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), 
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_SHORT_FORM), em);
        Ontology generatedOntology = importer.populateDbFromShortForm("generated", ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), 
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_SHORT_FORM), em);
        
//        tester.findDifference(em, new File("extra"), new File("missing"), 
//                em.find(Ontology.class, originalOntology.getId()), 
//                em.find(Ontology.class, expectedOntology.getId()), 
//                em.find(Ontology.class, generatedOntology.getId()));        
        
        tester.findDifference(em, new File("extra"), new File("missing"), 
                originalOntology, 
                expectedOntology, 
                generatedOntology);
    }


}
