package com.ihtsdo.snomed.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;

public class HibernateDatabaseImporterTest extends DatabaseTest{

    protected static final String TEST_CONCEPTS = "test_concepts.txt";
    protected static final String TEST_CONCEPTS_WITH_PARSE_ERROR = "test_concepts_with_parse_error.txt";
    protected static final String TEST_STATEMENTS_LONG_FORM = "test_statements_longform.txt";
    protected static final String TEST_STATEMENTS_LONG_FORM_WITH_PARSE_ERROR = "test_statements_longform_with_parse_error.txt";
    protected static final String TEST_STATEMENTS_SHORT_FORM = "test_statements_shortform.txt";
    protected static final String TEST_IS_KIND_OF_STATEMENTS = "test_is_kind_of_statements.txt";
    protected static final String TEST_IS_KIND_OF_CONCEPTS = "test_is_kind_of_concepts.txt";
    protected static final String TEST_RF2_STATEMENT = "test_rf2_statements.txt";

    @Test
    public void dbShouldHave5StatementsAfterShortFormImport() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        importer.populateShortFormStatements(ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_SHORT_FORM), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }
    
    @Test
    public void dbShouldHave5StatementsAfterLongFormImport() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        importer.populateLongFormStatements(ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_LONG_FORM), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }
    
    @Test
    public void dbShouldHave5StatementsAfterRf2Import() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConceptsFromRf2(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENT), em, o);
        importer.populateStatementsFromRf2(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENT), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Statement.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(5, result);
    }    
    

    @Test
    public void dbShouldHave8ConceptsAfterConceptImport() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(8, result);
    }
    
    @Test
    public void dbShouldHave13ConceptsAfterConceptsFromRf2Import() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConceptsFromRf2(ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENT), em, o);

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.select(criteriaBuilder.count(criteriaQuery.from(Concept.class)));

        long result = em.createQuery(criteriaQuery).getSingleResult();
        assertEquals(13, result);
    }
    

    @Test
    public void dbShouldStoreAllDataPointsForRelationshipFromLongForm() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        importer.populateLongFormStatements(ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_LONG_FORM), em, o);

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
    public void dbShouldStoreAllDataPointsForRelationshipFromShortForm() throws IOException{
        Ontology o = importer.createOntology(em, DEFAULT_ONTOLOGY_NAME);
        importer.populateConcepts(ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS), em, o);
        importer.populateShortFormStatements(ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_SHORT_FORM), em, o);

        TypedQuery<Statement> query = em.createQuery(
                "SELECT r FROM Statement r where r.ontology.id=1 AND r.subject.serialisedId=280844000", 
                Statement.class);
        
        Statement r = query.getSingleResult();        

        assertNotNull(r);
        assertNotNull(r.getSubject());
        assertNotNull(r.getObject());
        assertEquals (-1, r.getSerialisedId());
        assertEquals (280844000, r.getSubject().getSerialisedId());
        assertEquals (116680003, r.getPredicate().getSerialisedId(), 116680003);
        assertEquals (71737002, r.getObject().getSerialisedId());
        assertEquals (0, r.getCharacteristicType());
        assertEquals (0, r.getRefinability());
        assertEquals (1, r.getGroupId());
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
    public void shouldPopulateDbFromLongForm() throws IOException{
        Ontology ontology = importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_LONG_FORM), em);
        
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
    public void shouldPopulateDbFromShortForm() throws IOException{
        Ontology ontology = importer.populateDbFromShortForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_SHORT_FORM), em);
        
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
            Statement r = new Statement(-1);
            r.setSubject(new Concept(280844000));
            r.setPredicate(new Concept(116680003));
            r.setObject(new Concept(71737002));
            assertTrue(ontology.getStatements().contains(r));
        }
    }    

    @Test
    public void shouldPopulateDbFromRf2() throws IOException{
        Ontology ontology = importer.populateDbFromRf2(DEFAULT_ONTOLOGY_NAME, 
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
    public void shouldPopulateSubjectOfStatementBidirectionalField() throws IOException{
        importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_LONG_FORM), em);

        Query query = em.createQuery("SELECT r FROM Statement r");

        @SuppressWarnings("unchecked")
        List<Statement> statements = (List<Statement>) query.getResultList();

        Iterator<Statement> stIt = statements.iterator();
        while (stIt.hasNext()){
            Statement statement = stIt.next();
            assertTrue(statement.getSubject().getSubjectOfStatements().contains(statement));
        }
    }

    @Test
    public void shouldPopulateKindOfAndParentOfBidirectionalFieldsForIsAStatements() throws IOException{
        importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_IS_KIND_OF_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_IS_KIND_OF_STATEMENTS), em);

        TypedQuery<Statement> query = em.createQuery("SELECT r FROM Statement r WHERE r.ontology.id=1", Statement.class);
        List<Statement> statements = (List<Statement>) query.getResultList();

        Statement r1000 = null;
        Statement r2000 = null;
        Statement r3000 = null;
        Iterator<Statement> rIt = statements.iterator();
        while (rIt.hasNext()){
            Statement r = rIt.next();
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
        importer.populateLongFormStatements(ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_LONG_FORM_WITH_PARSE_ERROR), em, o);

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

    @Test
    public void shouldCreateOntologyDatabaseEntryWithAllDataPointsOnImport() throws IOException{
        Ontology ontology = importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_LONG_FORM), em);

        assertNotNull(ontology);
        assertEquals(5, ontology.getStatements().size());
        assertEquals(1, ontology.getId());
        assertEquals(DEFAULT_ONTOLOGY_NAME, ontology.getName());
        Statement r = new Statement();
        r.setSerialisedId((100000028));
        assertTrue(ontology.getStatements().contains(r));
    }
    
    @Test
    public void shouldCreateConceptSerialisedIdMapToDatabaseIdForOntology() throws IOException{
        Ontology ontology = importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_LONG_FORM), em);
        Map<Long, Long> map = importer.createConceptSerialisedIdMapToDatabaseIdForOntology(ontology, em);
        
        assertEquals(8, map.keySet().size());
        for (Long value : map.values()){
            assertNotNull(value);
        }
    }
    
    @Test
    public void shouldConvertStringToBoolean(){
        assertTrue(!importer.stringToBoolean("0"));
        assertTrue(importer.stringToBoolean("1"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldFailToConvertStringToBoolean(){
        importer.stringToBoolean("blah");
    }    
    
    @Test
    public void shouldSetKindOfPredicate() throws IOException{
        Ontology ontology = importer.populateDbFromLongForm(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_STATEMENTS_LONG_FORM), em);
        
        assertNotNull(ontology.getIsKindOfPredicate());
        assertEquals(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID, ontology.getIsKindOfPredicate().getSerialisedId());
    }
}
