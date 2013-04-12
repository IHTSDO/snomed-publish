package com.ihtsdo.canonical.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.HibernateDbImporter;
import com.ihtsdo.snomed.canonical.Main;
import com.ihtsdo.snomed.canonical.model.Ontology;
import com.ihtsdo.snomed.canonical.test.TestingAlgorithm;

public class TestingAlgorithmTest {

    private static final String DEFAULT_ONTOLOGY_NAME = "Test";
    private static HibernateDbImporter importer;
    private static Main main;
    private   TestingAlgorithm tester = new TestingAlgorithm();

    private static final String TEST_CONCEPTS = "test_concepts.txt";
    private static final String TEST_RELATIONSHIPS_LONG_FORM = "test_relationships_longform.txt";
    private static final String TEST_RELATIONSHIPS_SHORT_FORM = "test_relationships_shortform.txt";

    
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        importer = new HibernateDbImporter();
        main = new Main();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        main.initDb(null);
    }

    @After
    public void tearDown() throws Exception {
        main.closeDb();
    }

    @Test
    public void test() throws FileNotFoundException, IOException {
        Ontology originalOntology = importer.populateDbFromLongForm("original", ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), 
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_LONG_FORM), main.em);
        Ontology expectedOntology = importer.populateDbFromShortForm("expected", ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), 
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_SHORT_FORM), main.em);
        Ontology generatedOntology = importer.populateDbFromShortForm("generated", ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), 
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_SHORT_FORM), main.em);
        
//        tester.findDifference(main.em, new File("extra"), new File("missing"), 
//                main.em.find(Ontology.class, originalOntology.getId()), 
//                main.em.find(Ontology.class, expectedOntology.getId()), 
//                main.em.find(Ontology.class, generatedOntology.getId()));        
        
        tester.findDifference(main.em, new File("extra"), new File("missing"), 
                originalOntology, 
                expectedOntology, 
                generatedOntology);
    }

}
