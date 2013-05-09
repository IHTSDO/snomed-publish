package com.ihtsdo.snomed.browse;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseTest.class);
    protected static final String DEFAULT_ONTOLOGY_NAME = "Test";
    
    @Autowired
    protected static EntityManagerFactory emf = null;
    
    protected static EntityManager em = null;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        LOG.info("Initialising database");
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        LOG.info("Closing database");
    }
}
