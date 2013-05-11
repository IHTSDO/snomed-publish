package com.ihtsdo.snomed.service.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class Rf1HibernateParserTest extends HibernateParserTest{
    
    HibernateParser parser = HibernateParserFactory.getParser(Parser.RF1);

    @Test
    public void dbShouldHave5StatementsAfterPopulateStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_RF1), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }
  
    @Test
    public void dbShouldHave8ConceptsAfterPopulateConcepts() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(8, result);
    }
    
    @Test
    public void dbShouldHave8ConceptsAfterPopulateConceptsFromStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_RF1), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(8, result);
    } 
    

    @Test
    public void dbShouldStoreAllStatementDataPointsForPopulateStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_RF1), em, o);

        TypedQuery<Statement> query = em.createQuery(
                "SELECT r FROM Statement r where r.ontology.id=1 AND r.serialisedId=" + 100000028, 
                Statement.class);
        
        Statement r = query.getSingleResult();        

        assertNotNull(r);
        assertNotNull(r.getSubject());
        assertNotNull(r.getObject());
        assertEquals (100000028l, r.getSerialisedId());
        assertEquals (280844000, r.getSubject().getSerialisedId());
        assertEquals (116680003, r.getPredicate().getSerialisedId(), 116680003);
        assertEquals (71737002, r.getObject().getSerialisedId());
        assertEquals (0, r.getCharacteristicType());
        assertEquals (0, r.getRefinability());
        assertEquals (0, r.getGroupId());
    }
    

    @Test
    public void dbShouldStoreAllDataPointsForPopulateConcepts() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        
        TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c where c.ontology.id=1 AND c.serialisedId=" + 280844000, Concept.class);
        Concept c = query.getSingleResult();

        assertNotNull(c);
        assertEquals (280844000, c.getSerialisedId());
        assertEquals (0, c.getStatusId());
        assertEquals ("Entire body of seventh thoracic vertebra", c.getFullySpecifiedName());
        assertEquals ("body structure", c.getType());
        assertEquals ("Xa1Y9", c.getCtv3id());
        assertEquals ("T-11875", c.getSnomedId());
        assertEquals (true, c.isPrimitive());
        assertEquals (1, c.getOntology().getId());
    }
    
    @Test
    public void shouldPopulateDb() throws IOException{
        Ontology ontology = parser.populateDb(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_RF1), em);
        
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
    
    
    @Test
    public void shouldPopulateDbWithNoConcept() throws IOException{
        Ontology ontology = parser.populateDbWithNoConcepts(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_RF1),
                ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_RF1), em);
        
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

    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseError() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS_WITH_PARSE_ERROR), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_LONG_FORM_WITH_PARSE_ERROR), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> conceptCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        conceptCriteriaQuery.select(criteriaBuilder.count(conceptCriteriaQuery.from(Concept.class)));
        long conceptResult = em.createQuery(conceptCriteriaQuery).getSingleResult();

        CriteriaQuery<Long> statementsCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        statementsCriteriaQuery.select(criteriaBuilder.count(statementsCriteriaQuery.from(Statement.class)));
        long statementResult = em.createQuery(statementsCriteriaQuery).getSingleResult();

        assertEquals(11, conceptResult);
        assertEquals(9, statementResult);
    }
}
