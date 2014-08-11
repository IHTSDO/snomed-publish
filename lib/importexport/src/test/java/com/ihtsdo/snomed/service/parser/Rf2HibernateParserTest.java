package com.ihtsdo.snomed.service.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import javax.persistence.Persistence;
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
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.SnomedFlavours;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.parser.HibernateParser.Mode;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class Rf2HibernateParserTest extends BaseTest{
    private static final Logger LOG = LoggerFactory.getLogger(Rf2HibernateParserTest.class);

    HibernateParser parser = HibernateParserFactory.getParser(Parser.RF2).setParseMode(Mode.STRICT);
    
    
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
    public void dbShouldHave28ConceptsAfterPopulateConcepts() throws IOException{
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, ontologyVersion);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(28, result);
    }
    
    @Test
    public void dbShouldHave71DescriptionsAfterPopulateDescriptions() throws IOException{
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, ontologyVersion);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS), em, ontologyVersion);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Description.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(71, result);
    } 
    
    @Test
    public void dbShouldHave16ConceptsAfterPopulateConceptsFromStatements() throws IOException{
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS), em, ontologyVersion);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(16, result);
    }
    
    @Test
    public void dbShouldHave23ConceptsAfterPopulateConceptsFromStatementsAndDescriptions() throws IOException{
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConceptsFromStatementsAndDescriptions(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS),
                em, ontologyVersion);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(23, result);
    }    
     
    
    @Test
    public void dbShouldHave5StatementsAfterPopulateStatements() throws IOException{
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, ontologyVersion);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS), em, ontologyVersion);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }
    

    @Test
    public void dbShouldStoreAllDataPointsForStatement() throws IOException{
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, ontologyVersion);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS), em, ontologyVersion);
        
        Statement r = em.createQuery(
                "SELECT s FROM Statement s where s.serialisedId=" + 1000000021, 
                Statement.class).getSingleResult();        

        assertNotNull(r);
        assertNotNull(r.getSubject());
        assertNotNull(r.getObject());
        assertNotNull(r.getPredicate());
        assertNotNull(r.getModule());
        assertNotNull(r.getCharacteristicType());
        assertNotNull(r.getModifier());
        assertEquals (1000000021l, r.getSerialisedId());
        assertEquals (20020731, r.getEffectiveTime());
        assertEquals (HibernateParser.DEFAULT_STATEMENT_ACTIVE, r.isActive());
        assertEquals (900000000000207008l, r.getModule().getSerialisedId());
        assertEquals (255116009, r.getSubject().getSerialisedId());
        assertEquals (367639000, r.getObject().getSerialisedId());
        assertEquals (0, r.getGroupId());
        assertEquals (116680003, r.getPredicate().getSerialisedId());
        assertEquals (900000000000011006l, r.getCharacteristicType().getSerialisedId());
        assertEquals (900000000000451002l, r.getModifier().getSerialisedId());
        assertEquals (HibernateParser.DEFAULT_STATEMENT_CHARACTERISTIC_TYPE_IDENTIFIER, r.getCharacteristicTypeIdentifier());
        assertEquals (HibernateParser.DEFAULT_STATEMENT_REFINABILITY, r.getRefinability()); 
    }
    

    @Test
    public void dbShouldStoreAllDataPointsForConcept() throws IOException{
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, ontologyVersion);
        
        Concept c = em.createQuery(
                "SELECT c FROM Concept c where c.serialisedId=" + 609555007, 
                Concept.class).getSingleResult();
        
        assertNotNull(c);
        assertNotNull(c.getModule());
        assertNotNull(c.getStatus());
        assertEquals(609555007, c.getSerialisedId());
        assertEquals(20130731, c.getEffectiveTime());
        assertEquals(HibernateParser.DEFAULT_CONCEPT_ACTIVE, c.isActive());
        assertEquals(900000000000207008l, c.getModule().getSerialisedId());
        assertEquals(900000000000074008l, c.getStatus().getSerialisedId());
        assertEquals(HibernateParser.DEFAULT_VERSION, c.getVersion());
        assertEquals(HibernateParser.DEFAULT_CONCEPT_PRIMITIVE, c.isPrimitive());
        assertEquals(HibernateParser.DEFAULT_CONCEPT_STATUS_ID, c.getStatusId());
    }

    @Test
    public void dbShouldStoreAllDataPointsForDescription() throws IOException{
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, ontologyVersion);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS), em, ontologyVersion);
        
        Description d = em.createQuery(
                "SELECT d FROM Description d where d.serialisedId=" + 181114011, 
                Description.class).getSingleResult();
        
        assertNotNull(d.getModule());
        assertNotNull(d.getAbout());
        assertNotNull(d.getType());
        assertNotNull(d.getCaseSignificance());
        
        assertEquals(181114011, d.getSerialisedId());
        assertEquals(20110131, d.getEffectiveTime());
        assertEquals(true, d.isActive());
        assertEquals(900000000000012004l, d.getModule().getSerialisedId());
        assertEquals(116680003, d.getAbout().getSerialisedId());
        assertEquals("en", d.getLanguageCode());
        assertEquals(900000000000013009l, d.getType().getSerialisedId());
        assertEquals("Is a", d.getTerm());
        assertEquals(900000000000020002l, d.getCaseSignificance().getSerialisedId());
    }    
    
 
    @Test
    public void shouldPopulateDbFromStatementsOnly() throws IOException{
        parser.populateDbFromStatementsOnly(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE,                
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS), em);
        
        {//16 Concepts
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(16, result);
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
            assertEquals(16, o2.getConcepts().size());
            //assertEquals(new Long(1), ontologyVersion.getId());
            assertEquals(DEFAULT_TAGGED_ON_DATE, o2.getTaggedOn());
            Statement r = new Statement(1000000021);
            assertTrue(o2.getConcepts().contains(new Concept(255116009)));
            assertTrue(o2.getStatements().contains(r));
        }
    }
    
    @Test
    public void shouldPopulateDbFromStatementsAndDescriptionsOnly() throws IOException{
        parser.populateDbFromStatementsAndDescriptionsOnly(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE, 
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS),
                em);
        
        {//16 Concepts
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(23, result);
        }
        {//5 Statements
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(5, result);
        }
        {//82 Descriptions
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Description.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(71, result);
        }
        OntologyVersion o2 = em.merge(ontologyVersion);
        {//1 ontology
            assertNotNull(o2);
            assertEquals(5, o2.getStatements().size());
            assertEquals(23, o2.getConcepts().size());
            //assertEquals(new Long(1), ontologyVersion.getId());
            assertEquals(DEFAULT_TAGGED_ON_DATE, o2.getTaggedOn());
            Statement r = new Statement(1000000021);
            assertTrue(o2.getConcepts().contains(new Concept(900000000000012004l)));
            assertTrue(o2.getStatements().contains(r));
        }
    }    
    
    @Test
    public void shouldPopulateDb() throws IOException{
        parser.populateDb(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE, 
                ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS), em);        
        {//28 Concepts
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(28, result);
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
            assertEquals(28, o2.getConcepts().size());
            //assertEquals(new Long(1), ontologyVersion.getId());
            assertEquals(DEFAULT_TAGGED_ON_DATE, o2.getTaggedOn());
            Statement r = new Statement(1000000021);
            assertTrue(o2.getStatements().contains(r));
        }       
    }
    
    @Test
    public void shouldPopulateDbWithDescriptions() throws IOException{
        parser.populateDbWithDescriptions(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE,                
                ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS), em);        
        {//28 Concepts
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(28, result);
        }
        {//5 Statements
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(5, result);
        }
        {//71 Descriptions
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Description.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(71, result);
        }
        OntologyVersion o2 = em.merge(ontologyVersion);
        {//1 ontology
            assertNotNull(o2);
            assertEquals(5, o2.getStatements().size());
            assertEquals(28, o2.getConcepts().size());
            assertEquals(71, o2.getDescriptions().size());
            //assertEquals(new Long(1), ontologyVersion.getId());
            assertEquals(DEFAULT_TAGGED_ON_DATE, o2.getTaggedOn());
            assertTrue(o2.getStatements().contains(new Statement(1000000021)));
            assertTrue(o2.getDescriptions().contains(new Description(181114011)));
            assertTrue(o2.getConcepts().contains(new Concept(900000000000207008l)));
        }      
    }
    
    @Test
    public void shouldSetDisplaynameCache() throws IOException{
        parser.populateDbWithDescriptions(
                SnomedFlavours.INTERNATIONAL,
                DEFAULT_TAGGED_ON_DATE,                
                ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS), em);
        OntologyVersion o2 = em.merge(ontologyVersion);
        Set<Concept> cs = o2.getConcepts();
        Description fsnDescription = null;
        Concept c = null;
        for (Concept concept : cs) {
            for (Description d : concept.getDescription()){
                if (d.isFullySpecifiedName()){
                    fsnDescription = d;
                    c = concept;

                    assertNotNull(fsnDescription);
                    assertEquals(c.getFullySpecifiedName(), fsnDescription.getTerm());

                    break;
                }
            }
		}
        
        //to make sure these assertion has been performed for at least one concept
        assertNotNull(fsnDescription);
        assertEquals(c.getFullySpecifiedName(), fsnDescription.getTerm());
    }    
    
    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseErrorWhenForgivingMode() throws IOException{
        parser.setParseMode(Mode.FORGIVING);
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        
        parser.populateConcepts(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS_ERROR), 
                em, ontologyVersion);
        parser.populateStatements(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS_ERROR),
                em, ontologyVersion);
        parser.populateDescriptions(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS_ERROR),
                em, ontologyVersion);
        
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
        
        assertEquals(25, conceptResult);
        assertEquals(0, statementResult);
        assertEquals(67, descriptionResult);
    }
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictModeForPopulateConcepts() throws IOException{
        parser.setParseMode(Mode.STRICT);
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConcepts(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS_ERROR), 
                em, ontologyVersion);
    }
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictModeForPopulateConceptsFromStatements() throws IOException{
        parser.setParseMode(Mode.STRICT);
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS_ERROR), 
                em, ontologyVersion);
    }    
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictModeForPopulateConceptsFromStatementsAndDescriptions() throws IOException{
        parser.setParseMode(Mode.STRICT);
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConceptsFromStatementsAndDescriptions(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS_ERROR),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS_ERROR),
                em, ontologyVersion);
    }        
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictModeForPopulateStatements() throws IOException{
        parser.setParseMode(Mode.STRICT);
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConcepts(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), 
                em, ontologyVersion);
        parser.populateStatements(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS_ERROR),
                em, ontologyVersion);        
    }
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionAfterParseErrorWhenStrictModeForPopulateDescriptions() throws IOException{
        parser.setParseMode(Mode.STRICT);
        parser.createOntologyVersion(em, SnomedFlavours.INTERNATIONAL, DEFAULT_TAGGED_ON_DATE);
        parser.populateConcepts(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), 
                em, ontologyVersion);
        parser.populateStatements(
                ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS_ERROR),
                em, ontologyVersion);        
    }      
}
