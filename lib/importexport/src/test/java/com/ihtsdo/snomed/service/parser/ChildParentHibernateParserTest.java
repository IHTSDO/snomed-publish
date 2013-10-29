package com.ihtsdo.snomed.service.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.InvalidInputException;
import com.ihtsdo.snomed.service.parser.HibernateParser.Mode;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class ChildParentHibernateParserTest extends BaseTest{
    private static final Logger LOG = LoggerFactory.getLogger(ChildParentHibernateParserTest.class);

    HibernateParser parser = HibernateParserFactory.getParser(Parser.CHILD_PARENT).setParseMode(Mode.STRICT);

    @BeforeClass
    public static void beforeClass(){
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML);
        em = emf.createEntityManager();        
    }
    
    @AfterClass
    public static void afterClass(){
        emf.close();
    }    
    
    @After
    public void tearDown() throws Exception {
        em.getTransaction().rollback();
    }
    
    @Before
    public void setUp() throws Exception {
        em.getTransaction().begin();
        em.getTransaction().setRollbackOnly();   
    } 
    
    @Test
    public void dbShouldHave8ConceptsAfterPopulateConceptsFromStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(7, result);
    } 
    
    @Test
    public void dbShouldHave6StatementsAfterPopulateStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(6, result);
    }
    
    @Test
    public void dbShouldStoreAllStatementDataPointsForPopulateStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, o);

        TypedQuery<Statement> query = em.createQuery(
                "SELECT r FROM Statement r where r.ontology.id=1 AND r.subject.serialisedId=" + 609555007l + 
                " AND r.object.serialisedId=" + 161639008l, 
                Statement.class);
        
        Statement r = query.getSingleResult();        

        assertNotNull(r);
        assertNotNull(r.getSubject());
        assertNotNull(r.getObject());
        assertEquals (Statement.SERIALISED_ID_NOT_DEFINED, r.getSerialisedId());
        assertEquals (609555007, r.getSubject().getSerialisedId());
        assertEquals (Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID, r.getPredicate().getSerialisedId());
        assertEquals (161639008, r.getObject().getSerialisedId());
    }
    
    @Test
    public void dbShouldStoreAllDataPointsForPopulateConceptFromStatements() throws IOException{
        parser.populateDbFromStatementsOnly(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), 
                em);
                
        TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c where c.ontology.id=1 AND c.serialisedId=" + 609555007, Concept.class);
        Concept c = query.getSingleResult();

        assertNotNull(c);
        assertEquals (609555007, c.getSerialisedId());
        assertTrue(c.getKindOfs().contains(new Concept(161639008)));
        assertEquals (1, c.getOntology().getId());
    }
    
    @Test
    public void shouldPopulateDbWithNoConcept() throws IOException{
        Ontology ontology = parser.populateDbFromStatementsOnly(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), 
                em);
        
        {//8 Concepts
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(7, result);
        }
        {//6 Statements
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(6, result);
        }
        {//1 ontology
            assertNotNull(ontology);
            assertEquals(6, ontology.getStatements().size());
            assertEquals(7, ontology.getConcepts().size());
            assertEquals(1, ontology.getId());
            assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
            assertTrue(ontology.getConcepts().contains(new Concept(609555007)));
            assertTrue(ontology.getStatements().contains(
                    new Statement(Statement.SERIALISED_ID_NOT_DEFINED, 
                            new Concept(609555007l), 
                            ontology.getIsKindOfPredicate(), 
                            new Concept(161639008l))));
        }
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDb() throws IOException{
        parser.populateDb(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), 
                em);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDbWithNoConceptsAndDescriptions() throws IOException{
        parser.populateDbFromStatementsAndDescriptionsOnly(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                null,
                null,
                em);
    }    
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDbWithDescriptions() throws IOException{
        parser.populateDbWithDescriptions(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                em);
    }    
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionOnPopulateDescriptions() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, o);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, o);
    }    

    @Test(expected=UnsupportedOperationException.class)
    public void dbShouldThrowExceptionForPopulateConcepts() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, o);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void dbShouldThrowExceptionForPopulateConceptsFromStatementsAndDescriptions() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatementsAndDescriptions(
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), 
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), 
                em, o);
    }
        
    
    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseErrorWhenForgivingMode() throws IOException{
        parser.setParseMode(Mode.FORGIVING);
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS_ERROR), 
                em, o);
        parser.populateStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS_ERROR),
                em, o);
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> conceptCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        conceptCriteriaQuery.select(criteriaBuilder.count(conceptCriteriaQuery.from(Concept.class)));
        long conceptResult = em.createQuery(conceptCriteriaQuery).getSingleResult();

        CriteriaQuery<Long> statementsCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        statementsCriteriaQuery.select(criteriaBuilder.count(statementsCriteriaQuery.from(Statement.class)));
        long statementResult = em.createQuery(statementsCriteriaQuery).getSingleResult();

        assertEquals(6, conceptResult);
        assertEquals(4, statementResult);
    }
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictMode() throws IOException{
        parser.setParseMode(Mode.STRICT);
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS_ERROR), 
                em, o);
    }
}
