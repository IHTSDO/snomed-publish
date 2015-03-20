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

public class CanonicalHibernateParserTest extends BaseTest{
    private static final Logger LOG = LoggerFactory.getLogger(CanonicalHibernateParserTest.class);

    protected static HibernateParser parser = HibernateParserFactory.getParser(Parser.CANONICAL).setParseMode(Mode.STRICT);    
    
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
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, ontologyVersion);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(8, result);
    } 
    
    @Test
    public void dbShouldHave6StatementsAfterPopulateStatements() throws IOException{
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, ontologyVersion);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, ontologyVersion);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }
    
    @Test
    public void dbShouldStoreAllStatementDataPointsForPopulateStatements() throws IOException{
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, ontologyVersion);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, ontologyVersion);

        TypedQuery<Statement> query = em.createQuery(
                "SELECT r FROM Statement r where r.subject.serialisedId=" + 280844000 + 
                " AND r.object.serialisedId=" + 71737002, 
                Statement.class);
        
        Statement r = query.getSingleResult();        

        assertNotNull(r);
        assertNotNull(r.getSubject());
        assertNotNull(r.getPredicate());
        assertNotNull(r.getObject());
        assertEquals (Statement.SERIALISED_ID_NOT_DEFINED, r.getSerialisedId());
        assertEquals (280844000, r.getSubject().getSerialisedId());
        assertEquals (116680003, r.getPredicate().getSerialisedId());
        assertEquals (71737002, r.getObject().getSerialisedId());
        assertEquals(1, r.getGroupId());
        assertEquals(HibernateParser.DEFAULT_STATEMENT_ACTIVE, r.isActive());
        assertEquals(HibernateParser.DEFAULT_STATEMENT_EFFECTIVE_TIME, r.getEffectiveTime());
        assertEquals (HibernateParser.DEFAULT_STATEMENT_CHARACTERISTIC_TYPE_IDENTIFIER, r.getCharacteristicTypeIdentifier());
        assertEquals (HibernateParser.DEFAULT_STATEMENT_REFINABILITY, r.getRefinability());
    }
    
    @Test
    public void dbShouldStoreAllDataPointsForPopulateConceptFromStatements() throws IOException {
        parser.populateDbFromStatementsOnly(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE, 
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), 
                em);
                
        TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c where c.serialisedId=" + 280844000, Concept.class);
        Concept c = query.getSingleResult();

        assertNotNull(c);
        assertEquals (280844000, c.getSerialisedId());
        assertEquals (HibernateParser.DEFAULT_VERSION, c.getVersion());
        assertEquals (HibernateParser.DEFAULT_CONCEPT_ACTIVE, c.isActive());
        assertEquals (HibernateParser.DEFAULT_CONCEPT_EFFECTIVE_TIME, c.getEffectiveTime());
        assertEquals(HibernateParser.DEFAULT_CONCEPT_ACTIVE, c.isActive());
        assertEquals(HibernateParser.DEFAULT_CONCEPT_PRIMITIVE, c.isPrimitive());
        assertEquals(HibernateParser.DEFAULT_CONCEPT_STATUS_ID, c.getStatusId());
        //assertEquals (new Long(1), c.getOntologyVersion().getId());
    }
    
    @Test
    public void shouldPopulateDbWithNoConcept() throws IOException {
        parser.populateDbFromStatementsOnly(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE,
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), 
                em);
        
        {//8 Concepts
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(8, result);
        }
        {//5 Statements
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(5, result);
        }
        OntologyVersion o2 = em.merge(ontologyVersion);
        {//1 ontology
            assertNotNull(o2);
            assertEquals(5, o2.getStatements().size());
            assertEquals(8, o2.getConcepts().size());
            //assertEquals(new Long(1), ontologyVersion.getId());
            assertEquals(DEFAULT_TAGGED_ON_DATE, o2.getTaggedOn());
            assertTrue(o2.getConcepts().contains(new Concept(280845004)));
            assertTrue(o2.getStatements().contains(
                    new Statement(Statement.SERIALISED_ID_NOT_DEFINED, 
                            new Concept(280845004), 
                            o2.getIsKindOfPredicate(), 
                            new Concept(280737002))));
        }
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDb() throws IOException {
        parser.populateDb(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE, 
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), 
                em);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDbWithNoConceptsAndDescriptions() throws IOException {
        parser.populateDbFromStatementsAndDescriptionsOnly(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE, 
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                null,
                null,
                em);
    }    
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDbWithDescriptions() throws IOException {
        parser.populateDbWithDescriptions(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE,
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                em);
    }    
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionOnPopulateDescriptions() throws IOException {
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, ontologyVersion);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, ontologyVersion);
    }    

    @Test(expected=UnsupportedOperationException.class)
    public void dbShouldThrowExceptionForPopulateConcepts() throws IOException {
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, ontologyVersion);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void dbShouldThrowExceptionForPopulateConceptsFromStatementsAndDescriptions() throws IOException {
        parser.populateConceptsFromStatementsAndDescriptions(
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), 
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), 
                em, ontologyVersion);
    }
        
    
    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseErrorWhenForgivingMode() throws IOException {
        parser.setParseMode(Mode.FORGIVING);
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS_ERROR), 
                em, ontologyVersion);
        parser.populateStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS_ERROR),
                em, ontologyVersion);
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> conceptCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        conceptCriteriaQuery.select(criteriaBuilder.count(conceptCriteriaQuery.from(Concept.class)));
        long conceptResult = em.createQuery(conceptCriteriaQuery).getSingleResult();

        CriteriaQuery<Long> statementsCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        statementsCriteriaQuery.select(criteriaBuilder.count(statementsCriteriaQuery.from(Statement.class)));
        long statementResult = em.createQuery(statementsCriteriaQuery).getSingleResult();

        assertEquals(5, conceptResult);
        assertEquals(1, statementResult);
    }
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictMode() throws IOException {
        parser.setParseMode(Mode.STRICT);
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS_ERROR), 
                em, ontologyVersion);
    }
}
