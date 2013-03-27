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

public class MainTests {

	private static Main main;
	private static final String TEST_CONCEPTS = "test_concepts.txt";
	private static final String TEST_CONCEPTS_WITH_PARSE_ERROR = "test_concepts_with_parse_error.txt";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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

	@Test
	public void shouldPopulateConcepts() throws FileNotFoundException, IOException {
		main.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS));
	}
	
	@Test
	public void dbShouldHave10TestConceptsAfterPopulation() throws FileNotFoundException, IOException{
		main.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS));
		
		CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
		
		long result = main.em.createQuery(criteriaQuery).getSingleResult();
		assertEquals(10, result);
	}
	
	@Test
	public void shouldSlipInsertAndContinueDbPopulationAfterParseError() throws FileNotFoundException, IOException{
		main.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS_WITH_PARSE_ERROR));
		
		CriteriaBuilder criteriaBuilder = main.em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
		
		long result = main.em.createQuery(criteriaQuery).getSingleResult();
		assertEquals(9, result);
	}	
	
	@Test
	public void dbShouldHaveStoredAllDataPointsForAConcept() throws FileNotFoundException, IOException{
		main.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS));

		Concept c = main.em.find(Concept.class, (long)100000000); 
	    
		assertEquals (100000000, c.getId());
		assertEquals (10, c.getStatus());
		assertEquals ("BITTER-3 (product)", c.getFullySpecifiedName());
		assertEquals ("XU000", c.getCtv3id());
		assertEquals ("C-D1619", c.getSnomedId());
		assertEquals (true, c.isPrimitive());
	}
	
}
