package com.ihtsdo.snomed.service.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.InvalidInputException;
import com.ihtsdo.snomed.service.parser.HibernateParser.Mode;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class Rf1HibernateParserTest extends BaseTest{
    private static final Logger LOG = LoggerFactory.getLogger(Rf1HibernateParserTest.class);

    HibernateParser parser = HibernateParserFactory.getParser(Parser.RF1).setParseMode(Mode.STRICT);
    
    
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
    public void dbShouldHave5StatementsAfterPopulateStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }
  
    @Test
    public void dbShouldHave8ConceptsAfterPopulateConcepts() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(8, result);
    }
    
    @Test
    public void dbShouldHave8ConceptsAfterPopulateConceptsFromStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(8, result);
    } 
    
    @Test(expected=UnsupportedOperationException.class)
    public void dbShouldThrowEXceptionForPopulateConceptsFromStatementsAndDescriptions() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatementsAndDescriptions(
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_DESCRIPTIONS),
                em, o);
    }     
    
    @Test
    public void dbShouldHave23DescriptionsAfterPopulateDescriptions() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS), em, o);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_RF1_DESCRIPTIONS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Description.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(23, result);
    }     
    

    @Test
    public void dbShouldStoreAllStatementDataPoints() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS), em, o);

        Statement r = em.createQuery(
                "SELECT r FROM Statement r where r.ontology.id=1 AND r.serialisedId=" + 100000028, 
                Statement.class).getSingleResult();        

        assertNull(r.getCharacteristicType());
        assertNotNull(r);
        assertNotNull(r.getSubject());
        assertNotNull(r.getObject());
        assertEquals (100000028l, r.getSerialisedId());
        assertEquals (280844000, r.getSubject().getSerialisedId());
        assertEquals (116680003, r.getPredicate().getSerialisedId());
        assertEquals (71737002, r.getObject().getSerialisedId());
        assertEquals (0, r.getCharacteristicTypeIdentifier());
        assertEquals (0, r.getRefinability());
        assertEquals (0, r.getGroupId());
        assertEquals(HibernateParser.DEFAULT_STATEMENT_ACTIVE, r.isActive());
        assertEquals(HibernateParser.DEFAULT_STATEMENT_EFFECTIVE_TIME, r.getEffectiveTime());
    }
    

    @Test
    public void dbShouldStoreAllDataPointsForConcepts() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS), em, o);
        
        TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c where c.ontology.id=1 AND c.serialisedId=" + 280844000, Concept.class);
        Concept c = query.getSingleResult();

        assertNotNull(c);
        assertEquals (280844000, c.getSerialisedId());
        assertEquals (0, c.getStatusId());
        assertEquals ("Entire body of seventh thoracic vertebra (body structure)", c.getFullySpecifiedName());
//        assertEquals ("body structure", c.getType());
        assertEquals ("Xa1Y9", c.getCtv3id());
        assertEquals ("T-11875", c.getSnomedId());
        assertEquals (true, c.isPrimitive());
        assertEquals (HibernateParser.DEFAULT_VERSION, c.getVersion());
        assertEquals (HibernateParser.DEFAULT_CONCEPT_ACTIVE, c.isActive());
        assertEquals (HibernateParser.DEFAULT_CONCEPT_EFFECTIVE_TIME, c.getEffectiveTime());
        assertEquals (HibernateParser.DEFAULT_CONCEPT_PRIMITIVE, c.isPrimitive());
        assertEquals (1, c.getOntology().getId());
    }
    
    @Test
    public void dbShouldStoreAllDataPointsForDescription() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS), em, o);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_RF1_DESCRIPTIONS), em, o);
        
        Description d = em.createQuery(
                "SELECT d FROM Description d where d.ontology.id=1 AND d.serialisedId=" + 418668015, 
                Description.class).getSingleResult();
        
        assertNull(d.getModule());
        assertNotNull(d.getAbout());
        assertNull(d.getType());
        assertNull(d.getCaseSignificance());
        
        assertEquals(418668015, d.getSerialisedId());
        assertEquals(HibernateParser.DEFAULT_DESCRIPTION_EFFECTIVETIME, d.getEffectiveTime());
        assertEquals(HibernateParser.DEFAULT_DESCRIPTION_ACTIVE, d.isActive());
        assertEquals("en", d.getLanguageCode());
        assertEquals("T7 vertebral body", d.getTerm());
        assertEquals(0, d.getStatus());
        assertEquals(280844000, d.getAbout().getSerialisedId());
        assertEquals(1, d.getInitialCapitalStatus());
        assertEquals(2, d.getDescriptionTypeId());
    }      
    
    @Test
    public void shouldPopulateDb() throws IOException{
        Ontology ontology = parser.populateDb(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS), em);
        
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
        {//1 ontology
            assertNotNull(ontology);
            assertEquals(5, ontology.getStatements().size());
            assertEquals(8, ontology.getConcepts().size());
            assertEquals(1, ontology.getId());
            assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
            assertTrue(ontology.getStatements().contains(new Statement(100000028)));
        }
    }
    
    
    @Test
    public void shouldPopulateDbFromStatementsOnly() throws IOException{
        Ontology ontology = parser.populateDbFromStatementsOnly(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS), em);
        
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
        {//1 ontology
            assertNotNull(ontology);
            assertEquals(5, ontology.getStatements().size());
            assertEquals(8, ontology.getConcepts().size());
            assertEquals(1, ontology.getId());
            assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
            Statement r = new Statement(100000028);
            assertTrue(ontology.getStatements().contains(r));
        }
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionsForPopulateDbFromStatementsAndDescriptionsOnly() throws IOException{
        parser.populateDbFromStatementsAndDescriptionsOnly(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_DESCRIPTIONS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_DESCRIPTIONS),
                em);
    }    
    
    @Test
    public void shouldPopulateDbWithDescriptions() throws IOException{
        Ontology ontology = parser.populateDbWithDescriptions(DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_DESCRIPTIONS), em);        
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
        {//23 Descriptions
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Description.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(23, result);
        }
        {//1 ontology
            assertNotNull(ontology);
            assertEquals(5, ontology.getStatements().size());
            assertEquals(8, ontology.getConcepts().size());
            assertEquals(23, ontology.getDescriptions().size());
            assertEquals(1, ontology.getId());
            assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
            assertTrue(ontology.getStatements().contains(new Statement(100000028)));
            assertTrue(ontology.getDescriptions().contains(new Description(418668015)));
            assertTrue(ontology.getConcepts().contains(new Concept(280844000)));
        }      
    }       

    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseErrorWhenForgivingMode() throws IOException{
        parser.setParseMode(Mode.FORGIVING);
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS_ERROR), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS_ERROR), em, o);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_RF1_DESCRIPTIONS_ERROR), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> conceptCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        conceptCriteriaQuery.select(criteriaBuilder.count(conceptCriteriaQuery.from(Concept.class)));
        long conceptResult = em.createQuery(conceptCriteriaQuery).getSingleResult();

        CriteriaQuery<Long> statementsCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        statementsCriteriaQuery.select(criteriaBuilder.count(statementsCriteriaQuery.from(Statement.class)));
        long statementResult = em.createQuery(statementsCriteriaQuery).getSingleResult();

        CriteriaQuery<Long> descriptionsCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        descriptionsCriteriaQuery.select(criteriaBuilder.count(descriptionsCriteriaQuery.from(Description.class)));
        long descriptionResult = em.createQuery(descriptionsCriteriaQuery).getSingleResult();        
        
        assertEquals(4, conceptResult);
        assertEquals(1, statementResult);
        assertEquals(6, descriptionResult);
    }
    

    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictModeForPopulateConcepts() throws IOException{
        parser.setParseMode(Mode.STRICT);
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(
                ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS_ERROR), 
                em, o);
    }
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictModeForPopulateConceptsFromStatements() throws IOException{
        parser.setParseMode(Mode.STRICT);
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS_ERROR), 
                em, o);
    }    

    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictModeForPopulateStatements() throws IOException{
        parser.setParseMode(Mode.STRICT);
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(
                ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS), 
                em, o);
        parser.populateStatements(
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS_ERROR),
                em, o);        
    }
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictModeForPopulateDescriptions() throws IOException{
        parser.setParseMode(Mode.STRICT);
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(
                ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS), 
                em, o);
        parser.populateStatements(
                ClassLoader.getSystemResourceAsStream(TEST_RF1_DESCRIPTIONS_ERROR),
                em, o);        
    }      
}
