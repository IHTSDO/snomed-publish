package com.ihtsdo.snomed.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.service.HibernateDbImporter;

public class DatabaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseTest.class);
    protected static final String DEFAULT_ONTOLOGY_NAME = "Test";
    
    protected static HibernateDbImporter importer;
    protected static EntityManagerFactory emf = null;
    protected static EntityManager em = null;
    
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
        em.getTransaction().begin();
        em.getTransaction().setRollbackOnly();
    }

    @After
    public void tearDown() throws Exception {
        LOG.info("Closing database");
        em.getTransaction().rollback();
        emf.close();
    }
}
