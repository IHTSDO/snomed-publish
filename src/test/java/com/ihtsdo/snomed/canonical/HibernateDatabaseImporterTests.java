package com.ihtsdo.snomed.canonical;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.Relationship;

public class HibernateDatabaseImporterTests {

	private static HibernateDatabaseImporter importer;
	private static Main main;
	private static final String TEST_CONCEPTS = "test_concepts.txt";
	private static final String TEST_CONCEPTS_WITH_PARSE_ERROR = "test_concepts_with_parse_error.txt";
	private static final String TEST_RELATIONSHIPS = "test_relationships.txt";
	private static final String TEST_RELATIONSHIPS_WITH_PARSE_ERROR = "test_relationships_with_parse_error.txt";
	
	
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
	public void shouldPopulateConcepts() throws FileNotFoundException, IOException {
		importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void shouldPopulateRelationships() throws FileNotFoundException, IOException {
		importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
		importer.populateRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);
	}	

	@SuppressWarnings("static-access")
	@Test
	public void dbShouldHave10TestRelationshipsAfterPopulation() throws FileNotFoundException, IOException{
		importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
		importer.populateRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);
		
		CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Relationship.class)));
		
		long result = main.em.createQuery(criteriaQuery).getSingleResult();
		assertEquals(10, result);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void dbShouldHave10TestConceptsAfterPopulation() throws FileNotFoundException, IOException{
		importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
		
		CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
		
		long result = main.em.createQuery(criteriaQuery).getSingleResult();
		assertEquals(10, result);
	}	
	
	@SuppressWarnings("static-access")
	@Test
	public void dbShouldStoreAllDataPointsForRelationship() throws FileNotFoundException, IOException{
		importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);
		importer.populateRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS), main.em);
		
		Relationship r = main.em.find(Relationship.class, (long)100000028); 
	    
		assertEquals (100000028, r.getId());
		assertEquals (100006006, r.getConcept1().getId());
		assertEquals (116680003, r.getRelationshipType(), 116680003);
		assertEquals (10000006, r.getConcept2().getId());
		assertEquals (0, r.getCharacteristicType());
		assertEquals (false, r.isRefinability());
		assertEquals (0, r.getRelationShipGroup());
	}	
	
	@SuppressWarnings("static-access")
	@Test
	public void dbShouldStoreAllDataPointsForConcept() throws FileNotFoundException, IOException{
		importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), main.em);

		Concept c = main.em.find(Concept.class, (long)100000000); 
	    
		assertEquals (100000000, c.getId());
		assertEquals (10, c.getStatus());
		assertEquals ("BITTER-3 (product)", c.getFullySpecifiedName());
		assertEquals ("XU000", c.getCtv3id());
		assertEquals ("C-D1619", c.getSnomedId());
		assertEquals (true, c.isPrimitive());
	}

	@SuppressWarnings("static-access")
	@Test
	public void shouldSkipRowAndContinueDbPopulationAfterParseError() throws FileNotFoundException, IOException{
		importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS_WITH_PARSE_ERROR), main.em);
		importer.populateRelationships(ClassLoader.getSystemResourceAsStream(TEST_RELATIONSHIPS_WITH_PARSE_ERROR), main.em);
		
		CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
		
		CriteriaQuery<Long> conceptCriteriaQuery = criteriaBuilder.createQuery(Long.class);
		conceptCriteriaQuery.select(criteriaBuilder.count(conceptCriteriaQuery.from(Concept.class)));
		long conceptResult = main.em.createQuery(conceptCriteriaQuery).getSingleResult();

		CriteriaQuery<Long> relationshipCriteriaQuery = criteriaBuilder.createQuery(Long.class);
		relationshipCriteriaQuery.select(criteriaBuilder.count(relationshipCriteriaQuery.from(Relationship.class)));
		long relationshipResult = main.em.createQuery(relationshipCriteriaQuery).getSingleResult();		
		
		assertEquals(9, conceptResult);
		assertEquals(9, relationshipResult);
	}
}
