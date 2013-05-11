package com.ihtsdo.snomed.service.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class Rf2HibernateParserTest extends HibernateParserTest{

    HibernateParser parser = HibernateParserFactory.getParser(Parser.RF2);
    
    @Test
    public void dbShouldHave5StatementsAfterPopulateStatemnts() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENT), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENT), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }    
    

    @Test
    public void dbShouldHave13ConceptsAfterPopulateConcepts() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENT), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(13, result);
    }

    @Test
    public void dbShouldStoreAllDataPointsForStatement() throws IOException{

    }
    

    @Test
    public void dbShouldStoreAllDataPointsForConcept() throws IOException{
     
    }
    
 
    @Test
    public void shouldPopulateDbWithNoConcept() throws IOException{
        Ontology ontology = parser.populateDbWithNoConcepts(DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENT),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENT), em);
        
        {//13 Concepts
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(13, result);
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
            assertEquals(13, ontology.getConcepts().size());
            assertEquals(1, ontology.getId());
            assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
            Statement r = new Statement(1000000021);
            assertTrue(ontology.getStatements().contains(r));
        }
    }
    
    @Test
    public void shouldPopulateDb() throws IOException{
       
    }    
    
    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseError() throws IOException{

    }
}
