package com.ihtsdo.snomed.canonical.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.service.HibernateDbImporter;

public class ConceptTest {
    
    private static final String TEST_CONCEPTS = "test_concepts.txt";
    private static final String TEST_RELATIONSHIPS_LONG_FORM = "test_relationships_longform.txt";
    private static final String TEST_IS_KIND_OF_RELATIONSHIPS = "test_is_kind_of_relationships.txt";
    private static final String TEST_IS_KIND_OF_CONCEPTS = "test_is_kind_of_concepts.txt";    

    private static HibernateDbImporter importer;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        importer = new HibernateDbImporter();
    }    
    
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void should(){
        
    }

}
