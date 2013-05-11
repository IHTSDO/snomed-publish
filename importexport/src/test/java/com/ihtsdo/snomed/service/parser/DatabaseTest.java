package com.ihtsdo.snomed.service.parser;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.service.parser.Rf1HibernateParser;

public class DatabaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseTest.class);
    protected static final String DEFAULT_ONTOLOGY_NAME = "Test";

    protected static EntityManagerFactory emf = null;
    protected static EntityManager em = null;

    @Before
    public void setUp() throws Exception {
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(Rf1HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML);
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
