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
    protected static final String TEST_CONCEPTS = "test_concepts.txt";
    protected static final String TEST_CONCEPTS_WITH_PARSE_ERROR = "test_concepts_with_parse_error.txt";
    protected static final String TEST_STATEMENTS_RF1 = "test_statements_longform.txt";
    protected static final String TEST_STATEMENTS_LONG_FORM_WITH_PARSE_ERROR = "test_statements_longform_with_parse_error.txt";
    protected static final String TEST_STATEMENTS_SHORT_FORM = "test_statements_shortform.txt";
    protected static final String TEST_IS_KIND_OF_STATEMENTS = "test_is_kind_of_statements.txt";
    protected static final String TEST_IS_KIND_OF_CONCEPTS = "test_is_kind_of_concepts.txt";
    protected static final String TEST_CHILD_PARENT_STATEMENTS = "test.childparent";
    protected static final String TEST_RF2_STATEMENTS = "test.statements.rf2";
    protected static final String TEST_RF2_CONCEPTS = "test.concepts.rf2";
    protected static final String TEST_RF2_DESCRIPTIONS = "test.descriptions.rf2";

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
