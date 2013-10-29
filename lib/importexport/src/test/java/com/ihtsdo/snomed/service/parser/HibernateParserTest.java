package com.ihtsdo.snomed.service.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class HibernateParserTest extends BaseTest{
    private static final Logger LOG = LoggerFactory.getLogger(HibernateParserTest.class);

    private HibernateParser parser = HibernateParserFactory.getParser(Parser.RF1);

    @BeforeClass
    public static void beforeClass(){
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML);
        em = emf.createEntityManager();        
    }
    
    @AfterClass
    public static void afterClass(){
        em.close();
        emf.close();
    }    
    
    @After
    public void tearDown() throws Exception {
        em.getTransaction().rollback();
    }
    
    @Before
    public void setUp() throws Exception {
        em.getTransaction().begin();
        em.getTransaction().setRollbackOnly();   
    }       
    
    @Test
    public void shouldPopulateSubjectOfStatementBidirectionalField() throws IOException{
        parser.populateDb(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS), em);

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
        parser.populateDb(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_RF1_IS_KIND_OF_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_IS_KIND_OF_STATEMENTS), em);

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
    public void shouldCreateOntologyDatabaseEntryWithAllDataPointsOnImport() throws IOException{
        Ontology ontology = parser.populateDb(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS), em);

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
        Ontology ontology = parser.populateDb(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS), em);
        Map<Long, Long> map = parser.createConceptSerialisedIdToDatabaseIdMap(ontology, em);
        
        assertEquals(8, map.keySet().size());
        for (Long value : map.values()){
            assertNotNull(value);
        }
    }
    
    @Test
    public void shouldConvertStringToBoolean(){
        assertTrue(!parser.stringToBoolean("0"));
        assertTrue(parser.stringToBoolean("1"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldFailToConvertStringToBoolean(){
        parser.stringToBoolean("blah");
    }    
    
    @Test
    public void shouldSetKindOfPredicate() throws IOException{
        Ontology ontology = parser.populateDb(DEFAULT_ONTOLOGY_NAME, ClassLoader.getSystemResourceAsStream(TEST_RF1_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF1_STATEMENTS), em);
        
        assertNotNull(ontology.getIsKindOfPredicate());
        assertEquals(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID, ontology.getIsKindOfPredicate().getSerialisedId());
    }
    

}
