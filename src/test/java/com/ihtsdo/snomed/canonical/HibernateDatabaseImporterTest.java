package com.ihtsdo.snomed.canonical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class HibernateDatabaseImporterTest {

    private static HibernateDatabaseImporter importer;
    private static Main main;

    private static final String TEST_CONCEPTS = "test_concepts.txt";
    private static final String TEST_CONCEPTS_WITH_PARSE_ERROR = "test_concepts_with_parse_error.txt";
    private static final String TEST_RELATIONSHIPS = "test_relationships.txt";
    private static final String TEST_RELATIONSHIPS_WITH_PARSE_ERROR = "test_relationships_with_parse_error.txt";
    private static final String TEST_IS_KIND_OF_RELATIONSHIPS = "test_is_kind_of_relationships.txt";
    private static final String TEST_IS_KIND_OF_CONCEPTS = "test_is_kind_of_concepts.txt";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        importer = new HibernateDatabaseImporter();
        main = new Main();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        main.initDb(null);
    }

    @After
    public void tearDown() throws Exception {
        main.closeDb();
    }
    
    @Test
    public void shouldPopulateConcepts() throws IOException {
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
    }

    @Test
    public void shouldPopulateRelationships() throws IOException {
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
        importer.populateRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);
    }

    @Test
    public void dbShouldHave5RelationshipsAfterPopulation() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
        importer.populateRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);

        CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(RelationshipStatement.class)));

        long result = main.em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }

    @Test
    public void dbShouldHave7ConceptsAfterPopulation() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);

        CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = main.em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(7, result);
    }

    @Test
    public void dbShouldStoreAllDataPointsForRelationship() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
        importer.populateRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);

        RelationshipStatement r = main.em.find(RelationshipStatement.class, (long)100000028);

        assertNotNull(r);
        assertNotNull(r.getSubject());
        assertNotNull(r.getObject());
        assertEquals (100000028l, r.getId());
        assertEquals (280844000, r.getSubject().getId());
        assertEquals (116680003, r.getRelationshipType(), 116680003);
        assertEquals (71737002, r.getObject().getId());
        assertEquals (0, r.getCharacteristicType());
        assertEquals (0, r.getRefinability());
        assertEquals (0, r.getRelationShipGroup());
    }

    @Test
    public void dbShouldStoreAllDataPointsForConcept() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);

        Concept c = main.em.find(Concept.class, (long)280844000);

        assertNotNull(c);
        assertEquals (280844000, c.getId());
        assertEquals (0, c.getStatus());
        assertEquals ("Entire body of seventh thoracic vertebra (body structure)", c.getFullySpecifiedName());
        assertEquals ("Xa1Y9", c.getCtv3id());
        assertEquals ("T-11875", c.getSnomedId());
        assertEquals (true, c.isPrimitive());
    }

    @Test
    public void shouldPopulateSubjectOfRelationshipStatementBidirectionalField() throws IOException{
        importer.populateDb(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);

        Query query = main.em.createQuery("SELECT r FROM RelationshipStatement r");
        
        @SuppressWarnings("unchecked")
        List<RelationshipStatement> statements = (List<RelationshipStatement>) query.getResultList();

        Iterator<RelationshipStatement> stIt = statements.iterator();
        while (stIt.hasNext()){
            RelationshipStatement statement = stIt.next();
            assertTrue(statement.getSubject().getSubjectOfRelationShipStatements().contains(statement));
        }
    }

    @Test
    public void shouldPopulateKindOfAndParentOfBidirectionalFieldsForIsARelationships() throws IOException{
        importer.populateDb(ClassLoader.getSystemResourceAsStream(TEST_IS_KIND_OF_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_IS_KIND_OF_RELATIONSHIPS), main.em);

        Query query = main.em.createQuery("SELECT r FROM RelationshipStatement r");
        @SuppressWarnings("unchecked")
        List<RelationshipStatement> statements = (List<RelationshipStatement>) query.getResultList();

        RelationshipStatement r1000 = null;
        RelationshipStatement r2000 = null;
        RelationshipStatement r3000 = null;
        Iterator<RelationshipStatement> rIt = statements.iterator();
        while (rIt.hasNext()){
            RelationshipStatement r = rIt.next();
            if (r.getId() == 1000){
                r1000 = r;
            }
            if (r.getId() == 2000){
                r2000 = r;
            }
            if (r.getId() == 3000){
                r3000 = r;
            }
        }
        assertEquals(1, r1000.getSubject().getId());
        assertEquals(2, r2000.getSubject().getId());
        assertEquals(3, r3000.getSubject().getId());
        assertTrue(r1000.getSubject().getKindOfs().contains(r2000.getSubject()));
        assertTrue(r2000.getSubject().getParentOf().contains(r1000.getSubject()));
    }

    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseError() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS_WITH_PARSE_ERROR), main.em);
        importer.populateRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_WITH_PARSE_ERROR), main.em);

        CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
        CriteriaQuery<Long> conceptCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        conceptCriteriaQuery.select(criteriaBuilder.count(conceptCriteriaQuery.from(Concept.class)));
        long conceptResult = main.em.createQuery(conceptCriteriaQuery).getSingleResult();

        CriteriaQuery<Long> relationshipCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        relationshipCriteriaQuery.select(criteriaBuilder.count(relationshipCriteriaQuery.from(RelationshipStatement.class)));
        long relationshipResult = main.em.createQuery(relationshipCriteriaQuery).getSingleResult();

        assertEquals(9, conceptResult);
        assertEquals(9, relationshipResult);
    }



////  @SuppressWarnings("static-access")
////  @Test
////  public void shouldCreateOntologyDatabaseEntryWithAllDataPointsOnImport() throws IOException{
////      importer.populateDb(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
////              ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);
////
////      Ontology ontology = main.em.find(Ontology.class, HibernateDatabaseImporter.IMPORTED_LONG_INPUT_ONTOLOGY_ID);
////      assertNotNull(ontology);
////      assertEquals(10, ontology.getRelationshipStatements().size());
////      assertEquals(HibernateDatabaseImporter.IMPORTED_LONG_INPUT_ONTOLOGY_ID, ontology.getId());
////      RelationshipStatement r = new RelationshipStatement();
////      r.setId(100000028);
////      assertTrue(ontology.getRelationshipStatements().contains(r));
////  }
}
