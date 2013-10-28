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
import com.ihtsdo.snomed.service.InvalidInputException;
import com.ihtsdo.snomed.service.parser.HibernateParser.Mode;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class CanonicalHibernateParserTest extends DatabaseTest{
    
    HibernateParser parser = HibernateParserFactory.getParser(Parser.CANONICAL).setParseMode(Mode.STRICT);

    @Test
    public void dbShouldHave8ConceptsAfterPopulateConceptsFromStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(8, result);
    } 
    
    @Test
    public void dbShouldHave6StatementsAfterPopulateStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }
    
    @Test
    public void dbShouldStoreAllStatementDataPointsForPopulateStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, o);

        TypedQuery<Statement> query = em.createQuery(
                "SELECT r FROM Statement r where r.ontology.id=1 AND r.subject.serialisedId=" + 280844000 + 
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
    public void dbShouldStoreAllDataPointsForPopulateConceptFromStatements() throws IOException{
        parser.populateDbFromStatementsOnly(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), 
                em);
                
        TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c where c.ontology.id=1 AND c.serialisedId=" + 280844000, Concept.class);
        Concept c = query.getSingleResult();

        assertNotNull(c);
        assertEquals (280844000, c.getSerialisedId());
        assertEquals (HibernateParser.DEFAULT_VERSION, c.getVersion());
        assertEquals (HibernateParser.DEFAULT_CONCEPT_ACTIVE, c.isActive());
        assertEquals (HibernateParser.DEFAULT_CONCEPT_EFFECTIVE_TIME, c.getEffectiveTime());
        assertEquals(HibernateParser.DEFAULT_CONCEPT_ACTIVE, c.isActive());
        assertEquals(HibernateParser.DEFAULT_CONCEPT_PRIMITIVE, c.isPrimitive());
        assertEquals(HibernateParser.DEFAULT_CONCEPT_STATUS_ID, c.getStatusId());
        assertEquals (1, c.getOntology().getId());
    }
    
    @Test
    public void shouldPopulateDbWithNoConcept() throws IOException{
        Ontology ontology = parser.populateDbFromStatementsOnly(
                DEFAULT_ONTOLOGY_NAME, 
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
        {//1 ontology
            assertNotNull(ontology);
            assertEquals(5, ontology.getStatements().size());
            assertEquals(8, ontology.getConcepts().size());
            assertEquals(1, ontology.getId());
            assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
            assertTrue(ontology.getConcepts().contains(new Concept(280845004)));
            assertTrue(ontology.getStatements().contains(
                    new Statement(Statement.SERIALISED_ID_NOT_DEFINED, 
                            new Concept(280845004), 
                            ontology.getIsKindOfPredicate(), 
                            new Concept(280737002))));
        }
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDb() throws IOException{
        parser.populateDb(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), 
                em);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDbWithNoConceptsAndDescriptions() throws IOException{
        parser.populateDbFromStatementsAndDescriptionsOnly(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                null,
                null,
                em);
    }    
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionForPopulateDbWithDescriptions() throws IOException{
        parser.populateDbWithDescriptions(
                DEFAULT_ONTOLOGY_NAME, 
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS),
                em);
    }    
    
    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowExceptionOnPopulateDescriptions() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, o);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, o);
    }    

    @Test(expected=UnsupportedOperationException.class)
    public void dbShouldThrowExceptionForPopulateConcepts() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), em, o);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void dbShouldThrowExceptionForPopulateConceptsFromStatementsAndDescriptions() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatementsAndDescriptions(
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), 
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS), 
                em, o);
    }
        
    
    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseErrorWhenForgivingMode() throws IOException{
        parser.setParseMode(Mode.FORGIVING);
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS_ERROR), 
                em, o);
        parser.populateStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS_ERROR),
                em, o);
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
    public void shouldThrowExceptionAfterParseErrorWhenStrictMode() throws IOException{
        parser.setParseMode(Mode.STRICT);
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(
                ClassLoader.getSystemResourceAsStream(TEST_CANONICAL_STATEMENTS_ERROR), 
                em, o);
    }
}
