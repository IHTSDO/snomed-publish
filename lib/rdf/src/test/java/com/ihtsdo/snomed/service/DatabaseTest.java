package com.ihtsdo.snomed.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.service.parser.HibernateParser;


public class DatabaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseTest.class);
    protected static final String DEFAULT_ONTOLOGY_NAME = "Test";
    
    protected static final String TEST_RF1_GROUP_STATEMENTS = "test.group.statements.rf1";
    protected static final String TEST_RF1_GROUP_CONCEPTS = "test.group.concepts.rf1";

    protected static final String TEST_CANONICAL_STATEMENTS        = "test.statements.canonical";
    protected static final String TEST_CANONICAL_STATEMENTS_ERROR = "test.error.statements.canonical";
    
    protected static final String TEST_RF1_IS_KIND_OF_STATEMENTS = "test.iskindof.statements.rf1";
    protected static final String TEST_RF1_IS_KIND_OF_CONCEPTS   = "test.iskindof.concept.rf1";
    
    protected static final String TEST_RF1_STATEMENTS           = "test.statements.rf1";
    protected static final String TEST_RF1_CONCEPTS             = "test.concepts.rf1";
    protected static final String TEST_RF1_DESCRIPTIONS         = "test.descriptions.rf1";
    protected static final String TEST_RF1_CONCEPTS_ERROR       = "test.error.concepts.rf1";
    protected static final String TEST_RF1_STATEMENTS_ERROR     = "test.error.statements.rf1";
    protected static final String TEST_RF1_DESCRIPTIONS_ERROR   = "test.error.descriptions.rf1";
    
    protected static final String TEST_CP_STATEMENTS            = "test.statements.childparent";
    protected static final String TEST_CP_STATEMENTS_ERROR      = "test.error.statements.childparent";
    
    protected static final String TEST_RF2_STATEMENTS           = "test.statements.rf2";
    protected static final String TEST_RF2_CONCEPTS             = "data/test.concepts.rf2";
    protected static final String TEST_RF2_DESCRIPTIONS         = "data/test.descriptions.rf2";
    protected static final String TEST_RF2_STATEMENTS_ERROR     = "data/test.error.statements.rf2";
    protected static final String TEST_RF2_CONCEPTS_ERROR       = "test.error.concepts.rf2";
    protected static final String TEST_RF2_DESCRIPTIONS_ERROR   = "test.error.descriptions.rf2";

    protected static EntityManagerFactory emf = null;
    protected static EntityManager em = null;

    @Before
    public void setUp() throws Exception {
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML);
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
