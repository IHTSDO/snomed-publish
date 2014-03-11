package com.ihtsdo.snomed.service.parser;

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

import com.ihtsdo.snomed.exception.InvalidInputException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.SnomedFlavours;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.parser.HibernateParser.Mode;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ChildParentHibernateParserTest extends BaseTest{
    private static final Logger LOG = LoggerFactory.getLogger(ChildParentHibernateParserTest.class);

    protected static HibernateParser parser = HibernateParserFactory.getParser(Parser.CHILD_PARENT).setParseMode(Mode.STRICT);
    
    @BeforeClass
    public static void beforeClass() throws IOException{
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML);
        em = emf.createEntityManager();
    }
    
    @AfterClass
    public static void afterClass(){
        emf.close();
    }
    
    @Before
    public void setUp() throws Exception {
        em.getTransaction().begin();
        ontologyVersion = parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        em.getTransaction().commit();        
        
        em.getTransaction().begin();
        //em.getTransaction().setRollbackOnly();   
    } 
    
    @After
    public void tearDown() throws Exception {
        em.getTransaction().rollback();
        
        em.getTransaction().begin();
        Ontology o = em.merge(ontologyVersion.getFlavour().getOntology());
        em.remove(o);
        em.getTransaction().commit();  
    }
    
    
    
    @Test
    public void dbShouldHave8ConceptsAfterPopulateConceptsFromStatements() throws IOException{
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, ontologyVersion);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(7, result);
    } 
    
    @Test
    public void dbShouldHave6StatementsAfterPopulateStatements() throws IOException{
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, ontologyVersion);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, ontologyVersion);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(6, result);
    }
    
    @Test
    public void dbShouldStoreAllStatementDataPointsForPopulateStatements() throws IOException{
        //OntologyVersion o = parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, ontologyVersion);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, ontologyVersion);

        TypedQuery<Statement> query = em.createQuery(
                "SELECT r FROM Statement r where r.subject.serialisedId=" + 609555007l + 
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
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE,
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), 
                em);
                
        TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c where c.serialisedId=" + 609555007, Concept.class);
        Concept c = query.getSingleResult();

        assertNotNull(c);
        assertEquals (609555007, c.getSerialisedId());
        assertTrue(c.getKindOfs().contains(new Concept(161639008)));
        //assertEquals (new Long(1), c.getOntology().getId());
    }
    
    @Test
    public void shouldPopulateDbWithNoConcept() throws IOException{
        parser.populateDbFromStatementsOnly(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE,
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
        OntologyVersion o2 = em.merge(ontologyVersion);
        {//1 ontology
            assertNotNull(ontologyVersion);
            assertEquals(6, o2.getStatements().size());
            assertEquals(7, o2.getConcepts().size());
            //assertEquals(new Long(1), o2.getId());
            assertEquals(DEFAULT_TAGGED_ON_DATE, o2.getTaggedOn());
            assertTrue(o2.getConcepts().contains(new Concept(609555007)));
            assertTrue(o2.getStatements().contains(
                    new Statement(Statement.SERIALISED_ID_NOT_DEFINED, 
                            new Concept(609555007l), 
                            o2.getIsKindOfPredicate(), 
                            new Concept(161639008l))));
        }
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDb() throws IOException{
        parser.populateDb(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE, 
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), 
                em);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDbWithNoConceptsAndDescriptions() throws IOException{
        parser.populateDbFromStatementsAndDescriptionsOnly(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE,
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                null,
                null,
                em);
    }    
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDbWithDescriptions() throws IOException{
        parser.populateDbWithDescriptions(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE,
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS),
                em);
    }    
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionOnPopulateDescriptions() throws IOException{
        //ontologyVersion = parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        //em.lock(ontologyVersion, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, ontologyVersion);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, ontologyVersion);
    }    

    @Test(expected=UnsupportedOperationException.class)
    public void dbShouldThrowExceptionForPopulateConcepts() throws IOException{
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), em, ontologyVersion);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void dbShouldThrowExceptionForPopulateConceptsFromStatementsAndDescriptions() throws IOException{
        parser.populateConceptsFromStatementsAndDescriptions(
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), 
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS), 
                em, ontologyVersion);
    }
        
    
    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseErrorWhenForgivingMode() throws IOException{
        parser.setParseMode(Mode.FORGIVING);
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS_ERROR), 
                em, ontologyVersion);
        parser.populateStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS_ERROR),
                em, ontologyVersion);
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
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CP_STATEMENTS_ERROR), 
                em, ontologyVersion);
    }
}
