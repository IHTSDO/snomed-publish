package com.ihtsdo.snomed.canonical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.Ontology;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class HibernateDatabaseImporterTests {

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
        main.initDb();
    }

    @After
    public void tearDown() throws Exception {
        main.closeDb();
    }

    @SuppressWarnings("static-access")
    @Test
    public void shouldPopulateConcepts() throws IOException {
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
    }

    @SuppressWarnings("static-access")
    @Test
    public void shouldPopulateRelationships() throws IOException {
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
        importer.populateRelationshipsAndCreateOntology(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);
    }

    @SuppressWarnings("static-access")
    @Test
    public void dbShouldHave10RelationshipsAfterPopulation() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
        importer.populateRelationshipsAndCreateOntology(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);

        CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(RelationshipStatement.class)));

        long result = main.em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(10, result);
    }

    @SuppressWarnings("static-access")
    @Test
    public void dbShouldHave10TestConceptsAfterPopulation() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);

        CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = main.em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(10, result);
    }

    @SuppressWarnings("static-access")
    @Test
    public void dbShouldStoreAllDataPointsForRelationship() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
        importer.populateRelationshipsAndCreateOntology(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);

        RelationshipStatement r = main.em.find(RelationshipStatement.class, (long)100000028);

        assertEquals (100000028, r.getId());
        assertEquals (100006006, r.getSubject().getId());
        assertEquals (116680003, r.getRelationshipType(), 116680003);
        assertEquals (10000006, r.getObject().getId());
        assertEquals (0, r.getCharacteristicType());
        assertEquals (false, r.isRefinability());
        assertEquals (0, r.getRelationShipGroup());
    }

    @SuppressWarnings("static-access")
    @Test
    public void dbShouldStoreAllDataPointsForConcept() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);

        Concept c = main.em.find(Concept.class, (long)100000000);

        assertEquals (100000000, c.getId());
        assertEquals (10, c.getStatus());
        assertEquals ("BITTER-3 (product)", c.getFullySpecifiedName());
        assertEquals ("XU000", c.getCtv3id());
        assertEquals ("C-D1619", c.getSnomedId());
        assertEquals (true, c.isPrimitive());
    }

    @Test
    public void shouldReturnOntologyAfterPopulatingDatabase() throws IOException{
        @SuppressWarnings("static-access")
        Ontology ontology = importer.populateDb(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);
        assertNotNull(ontology);
        assertEquals(10, ontology.getRelationshipStatements().size());
    }

    @SuppressWarnings("static-access")
    @Test
    public void shouldPopulateSubjectOfRelationshipStatementBidirectionalField() throws IOException{
        importer.populateDb(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);

        Ontology ontology = main.em.find(Ontology.class, HibernateDatabaseImporter.IMPORTED_ONTOLOGY_ID);
        RelationshipStatement r = ontology.getRelationshipStatements().iterator().next();
        assertTrue(r.getSubject().getSubjectOfRelationShipStatements().contains(r));

    }

    @SuppressWarnings("static-access")
    @Test
    public void shouldPopulateKindOfAndParentOfBidirectionalFieldsForIsARelationships() throws IOException{
        importer.populateDb(ClassLoader.getSystemResourceAsStream(TEST_IS_KIND_OF_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_IS_KIND_OF_RELATIONSHIPS), main.em);

        Ontology ontology = main.em.find(Ontology.class, HibernateDatabaseImporter.IMPORTED_ONTOLOGY_ID);

        RelationshipStatement r1000 = null;
        RelationshipStatement r2000 = null;
        RelationshipStatement r3000 = null;
        Iterator<RelationshipStatement> rIt = ontology.getRelationshipStatements().iterator();
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
        assertTrue(r1000.getSubject().getKindOf().contains(r2000.getSubject()));
        assertTrue(r2000.getSubject().getParentOf().contains(r1000.getSubject()));

    }

    @SuppressWarnings("static-access")
    @Test
    public void shouldCreateOntologyDatabaseEntryWithAllDataPointsOnImport() throws IOException{
        importer.populateDb(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);

        Ontology ontology = main.em.find(Ontology.class, HibernateDatabaseImporter.IMPORTED_ONTOLOGY_ID);
        assertNotNull(ontology);
        assertEquals(10, ontology.getRelationshipStatements().size());
        assertEquals(HibernateDatabaseImporter.IMPORTED_ONTOLOGY_ID, ontology.getId());
        RelationshipStatement r = new RelationshipStatement();
        r.setId(100000028);
        assertTrue(ontology.getRelationshipStatements().contains(r));
    }

    @SuppressWarnings("static-access")
    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseError() throws IOException{
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS_WITH_PARSE_ERROR), main.em);
        importer.populateRelationshipsAndCreateOntology(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_WITH_PARSE_ERROR), main.em);

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
}
