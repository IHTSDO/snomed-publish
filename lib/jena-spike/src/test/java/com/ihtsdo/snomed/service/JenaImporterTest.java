package com.ihtsdo.snomed.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.parser.HibernateParser;
import com.ihtsdo.snomed.service.parser.HibernateParser.Mode;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class JenaImporterTest {

    Concept c1,c2,c3,c4,cp1,cp2,cp3,ca1,ca2,ca3,ca4;
    Ontology o;
    
    private static final Logger LOG = LoggerFactory.getLogger(JenaImporterTest.class);

    
    protected static EntityManagerFactory emf = null;
    protected static EntityManager em = null;
    
    HibernateParser parser = HibernateParserFactory.getParser(Parser.RF2).setParseMode(Mode.STRICT);
    
    private JenaRdfSchemaSerialiser jena = new JenaRdfSchemaSerialiser();
    
    protected static final String TEST_RF2_STATEMENTS           = "test.statements.rf2";
    protected static final String TEST_RF2_CONCEPTS             = "test.concepts.rf2";
    protected static final String TEST_RF2_DESCRIPTIONS         = "test.descriptions.rf2";
        
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
    
    @Before
    public void setUp() throws Exception {
        em.getTransaction().begin();
        em.getTransaction().setRollbackOnly();
    }

    @After
    public void tearDown() throws Exception {
        em.getTransaction().rollback();
    }
    
    //@Test
    public void shouldCreateJenaModel() throws IOException{
        Ontology ontology = parser.populateDbWithDescriptions("Jena Test", 
                ClassLoader.getSystemResourceAsStream(TEST_RF2_CONCEPTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_STATEMENTS),
                ClassLoader.getSystemResourceAsStream(TEST_RF2_DESCRIPTIONS), em);
        OntModel model = jena.importJenaModel(ontology, em, new File("TDB"));
        assertNotNull(model);
    }
}
