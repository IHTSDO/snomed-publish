package com.ihtsdo.snomed.service.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class Rf2HibernateParserTest extends DatabaseTest{

    HibernateParser parser = HibernateParserFactory.getParser(Parser.RF2);
    
    @Test
    public void dbShouldHave28ConceptsAfterPopulateConcepts() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(28, result);
    }
    
    @Test
    public void dbShouldHave82DescriptionsAfterPopulateDescriptions() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, o);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Description.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(82, result);
    } 
    
    @Test
    public void dbShouldHave16ConceptsAfterPopulateConceptsFromStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConceptsFromStatements(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(16, result);
    }
     
    
    @Test
    public void dbShouldHave5StatementsAfterPopulateStatements() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }
    

    @Test
    public void dbShouldStoreAllDataPointsForStatement() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, o);
        parser.populateStatements(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS), em, o);
        
        Statement r = em.createQuery(
                "SELECT s FROM Statement s where s.ontology.id=1 AND s.serialisedId=" + 1000000021, 
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
        assertEquals (true, r.isActive());
        assertEquals (900000000000207008l, r.getModule().getSerialisedId());
        assertEquals (255116009, r.getSubject().getSerialisedId());
        assertEquals (367639000, r.getObject().getSerialisedId());
        assertEquals (0, r.getGroupId());
        assertEquals (116680003, r.getPredicate().getSerialisedId());
        assertEquals (900000000000011006l, r.getCharacteristicType().getSerialisedId());
        assertEquals (900000000000451002l, r.getModifier().getSerialisedId());
        assertEquals (-1, r.getCharacteristicTypeIdentifier());
        assertEquals (-1, r.getRefinability()); 
    }
    

    @Test
    public void dbShouldStoreAllDataPointsForConcept() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, o);
        
        Concept c = em.createQuery(
                "SELECT c FROM Concept c where c.ontology.id=1 AND c.serialisedId=" + 609555007, 
                Concept.class).getSingleResult();
        
        assertNotNull(c);
        assertNotNull(c.getModule());
        assertNotNull(c.getStatus());
        assertEquals(609555007, c.getSerialisedId());
        assertEquals(20130731, c.getEffectiveTime());
        assertEquals(true, c.isActive());
        assertEquals(900000000000207008l, c.getModule().getSerialisedId());
        assertEquals(900000000000074008l, c.getStatus().getSerialisedId());
        assertEquals(1, c.getVersion());
        assertEquals(false, c.isPrimitive());
        assertEquals(-1, c.getStatusId());
    }

    @Test
    public void dbShouldStoreAllDataPointsForDescription() throws IOException{
        Ontology o = parser.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        parser.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS), em, o);
        parser.populateDescriptions(ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS), em, o);
        
        Description d = em.createQuery(
                "SELECT d FROM Description d where d.ontology.id=1 AND d.serialisedId=" + 2967732019l, 
                Description.class).getSingleResult();
        
        assertNotNull(d.getModule());
        assertNotNull(d.getAbout());
        assertNotNull(d.getType());
        assertNotNull(d.getCaseSignificance());
        
        assertEquals(2967732019l, d.getSerialisedId());
        assertEquals(20130731, d.getEffectiveTime());
        assertEquals(true, d.isActive());
        assertEquals(900000000000207008l, d.getModule().getSerialisedId());
        assertEquals(609555007l, d.getAbout().getSerialisedId());
        assertEquals("en", d.getLanguageCode());
        assertEquals(900000000000013009l, d.getType().getSerialisedId());
        assertEquals("Diastolic heart failure stage A", d.getTerm());
        assertEquals(900000000000017005l, d.getCaseSignificance().getSerialisedId());
    }    
    
 
    @Test
    public void shouldPopulateDbWithNoConcept() throws IOException{
        Ontology ontology = parser.populateDbWithNoConcepts(DEFAULT_ONTOLOGY_NAME, 
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
        {//1 ontology
            assertNotNull(ontology);
            assertEquals(5, ontology.getStatements().size());
            assertEquals(16, ontology.getConcepts().size());
            assertEquals(1, ontology.getId());
            assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
            Statement r = new Statement(1000000021);
            assertTrue(ontology.getStatements().contains(r));
        }
    }
    
    @Test
    public void shouldPopulateDb() throws IOException{
        Ontology ontology = parser.populateDb(DEFAULT_ONTOLOGY_NAME, 
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
        {//1 ontology
            assertNotNull(ontology);
            assertEquals(5, ontology.getStatements().size());
            assertEquals(28, ontology.getConcepts().size());
            assertEquals(1, ontology.getId());
            assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
            Statement r = new Statement(1000000021);
            assertTrue(ontology.getStatements().contains(r));
        }       
    }
    
    @Test
    public void shouldPopulateDbWithDescriptions() throws IOException{
        Ontology ontology = parser.populateDbWithDescriptions(DEFAULT_ONTOLOGY_NAME, 
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
        {//82 Descriptions
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Description.class)));
    
            long result = em.createQuery(criteriaQuery).getSingleResult();
            assertEquals(82, result);
        }
        {//1 ontology
            assertNotNull(ontology);
            assertEquals(5, ontology.getStatements().size());
            assertEquals(28, ontology.getConcepts().size());
            assertEquals(82, ontology.getDescriptions().size());
            assertEquals(1, ontology.getId());
            assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
            Statement r = new Statement(1000000021);
            assertTrue(ontology.getStatements().contains(r));
        }      
    }   
    
    @Test
    public void shouldSkipRowAndContinueDbPopulationAfterParseError() throws IOException{

    }
}
