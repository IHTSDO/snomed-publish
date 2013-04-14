package com.ihtsdo.snomed.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
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

import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.Ontology;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class HibernateDatabaseImporterTest {
    private static final Logger LOG = LoggerFactory.getLogger( HibernateDatabaseImporterTest.class );

    private static final String DEFAULT_ONTOLOGY_NAME = "Test";
    private static HibernateDbImporter importer;
    

    private static final String TEST_CONCEPTS = "test_concepts.txt";
    private static final String TEST_CONCEPTS_WITH_PARSE_ERROR = "test_concepts_with_parse_error.txt";
    private static final String TEST_RELATIONSHIPS_LONG_FORM = "test_relationships_longform.txt";
    private static final String TEST_RELATIONSHIPS_LONG_FORM_WITH_PARSE_ERROR = "test_relationships_longform_with_parse_error.txt";

    private static final String TEST_RELATIONSHIPS_SHORT_FORM = "test_relationships_shortform.txt";
    //private static final String TEST_RELATIONSHIPS_SHORT_FORM_WITH_PARSE_ERROR = "test_relationships_shortform_with_parse_error.txt";
    private static final String TEST_IS_KIND_OF_RELATIONSHIPS = "test_is_kind_of_relationships.txt";
    private static final String TEST_IS_KIND_OF_CONCEPTS = "test_is_kind_of_concepts.txt";

    private   EntityManagerFactory emf = null;
    private   EntityManager em = null;
    
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
    }

    @After
    public void tearDown() throws Exception {
        LOG.info("Closing database");
        emf.close();
    }

    @Test
    public void shouldPopulateConcepts() throws IOException {
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
    }

    @Test
    public void shouldPopulateLongFormRelationships() throws IOException {
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        importer.populateLongFormRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_LONG_FORM), em, o);
    }
    
    @Test
    public void shouldPopulateShortFormRelationships() throws IOException {
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        importer.populateShortFormRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_SHORT_FORM), em, o);
    } 
    
    

    @Test
    public void dbShouldHave5RelationshipsAfterPopulation() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        importer.populateLongFormRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_LONG_FORM), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(RelationshipStatement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }

    @Test
    public void dbShouldHave8ConceptsAfterPopulation() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(8, result);
    }

    @Test
    public void dbShouldStoreAllDataPointsForRelationship() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        importer.populateLongFormRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_LONG_FORM), em, o);

        TypedQuery<RelationshipStatement> query = em.createQuery(
                "SELECT r FROM RelationshipStatement r where r.ontology.id=1 AND r.serialisedId=" + 100000028, 
                RelationshipStatement.class);
        
        RelationshipStatement r = query.getSingleResult();        

        assertNotNull(r);
        assertNotNull(r.getSubject());
        assertNotNull(r.getObject());
        assertEquals (100000028l, r.getSerialisedId());
        assertEquals (280844000, r.getSubject().getSerialisedId());
        assertEquals (116680003, r.getPredicate().getSerialisedId(), 116680003);
        assertEquals (71737002, r.getObject().getSerialisedId());
        assertEquals (0, r.getCharacteristicType());
        assertEquals (0, r.getRefinability());
        assertEquals (0, r.getGroup());
    }

    @Test
    public void dbShouldStoreAllDataPointsForConcept() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        
        TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c where c.ontology.id=1 AND c.serialisedId=" + 280844000, Concept.class);
        Concept c = query.getSingleResult();

        assertNotNull(c);
        assertEquals (280844000, c.getSerialisedId());
        assertEquals (0, c.getStatus());
        assertEquals ("Entire body of seventh thoracic vertebra", c.getFullySpecifiedName());
        assertEquals ("body structure", c.getType());
        assertEquals ("Xa1Y9", c.getCtv3id());
        assertEquals ("T-11875", c.getSnomedId());
        assertEquals (true, c.isPrimitive());
        assertEquals (1, c.getOntology().getId());
    }

    @Test
    public void shouldPopulateSubjectOfRelationshipStatementBidirectionalField() throws IOException{
        importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_LONG_FORM), em);

        Query query = em.createQuery("SELECT r FROM RelationshipStatement r");

        @SuppressWarnings("unchecked")
        List<RelationshipStatement> statements = (List<RelationshipStatement>) query.getResultList();

        Iterator<RelationshipStatement> stIt = statements.iterator();
        while (stIt.hasNext()){
            RelationshipStatement statement = stIt.next();
            assertTrue(statement.getSubject().getSubjectOfRelationshipStatements().contains(statement));
        }
    }

    @Test
    public void shouldPopulateKindOfAndParentOfBidirectionalFieldsForIsARelationships() throws IOException{
        importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_IS_KIND_OF_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_IS_KIND_OF_RELATIONSHIPS), em);

        TypedQuery<RelationshipStatement> query = em.createQuery("SELECT r FROM RelationshipStatement r WHERE r.ontology.id=1", RelationshipStatement.class);
        List<RelationshipStatement> statements = (List<RelationshipStatement>) query.getResultList();

        RelationshipStatement r1000 = null;
        RelationshipStatement r2000 = null;
        RelationshipStatement r3000 = null;
        Iterator<RelationshipStatement> rIt = statements.iterator();
        while (rIt.hasNext()){
            RelationshipStatement r = rIt.next();
            if (r.getSerialisedId() == 1000){
                r1000 = r;
            }
            if (r.getSerialisedId() == 2000){
                r2000 = r;
            }
            if (r.getSerialisedId() == 3000){
                r3000 = r;
            }
        }
        assertEquals(1, r1000.getSubject().getSerialisedId());
        assertEquals(2, r2000.getSubject().getSerialisedId());
        assertEquals(3, r3000.getSubject().getSerialisedId());
        assertTrue(r1000.getSubject().getKindOfs().contains(r2000.getSubject()));
        assertTrue(r2000.getSubject().getParentOf().contains(r1000.getSubject()));
    }

    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseError() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS_WITH_PARSE_ERROR), em, o);
        importer.populateLongFormRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_LONG_FORM_WITH_PARSE_ERROR), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> conceptCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        conceptCriteriaQuery.select(criteriaBuilder.count(conceptCriteriaQuery.from(Concept.class)));
        long conceptResult = em.createQuery(conceptCriteriaQuery).getSingleResult();

        CriteriaQuery<Long> relationshipCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        relationshipCriteriaQuery.select(criteriaBuilder.count(relationshipCriteriaQuery.from(RelationshipStatement.class)));
        long relationshipResult = em.createQuery(relationshipCriteriaQuery).getSingleResult();

        assertEquals(11, conceptResult);
        assertEquals(9, relationshipResult);
    }

    @Test
    public void shouldCreateOntologyDatabaseEntryWithAllDataPointsOnImport() throws IOException{
        Ontology ontology = importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_LONG_FORM), em);

        assertNotNull(ontology);
        assertEquals(5, ontology.getRelationshipStatements().size());
        assertEquals(1, ontology.getId());
        RelationshipStatement r = new RelationshipStatement();
        r.setSerialisedId((100000028));
        assertTrue(ontology.getRelationshipStatements().contains(r));
    }
    
    //Add test for set operations for all data imported using hibernate
    //to test that the persistence layer is working
    
    //Add test for loading multiple ontologies
    //make sure the serialiseids for concepts don't get confused
    //and that relationships point to the correct concept ids
    
    //add test for createIsRelationship routine
}
