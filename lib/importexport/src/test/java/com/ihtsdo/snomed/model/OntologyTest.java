package com.ihtsdo.snomed.model;

import static org.junit.Assert.assertEquals;

import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.service.parser.BaseTest;
import com.ihtsdo.snomed.service.parser.HibernateParser;

public class OntologyTest extends BaseTest{
    private static final Logger LOG = LoggerFactory.getLogger(OntologyTest.class);

    @BeforeClass
    public static void beforeClass(){
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML);
        em = emf.createEntityManager();        
    }
    
    @AfterClass
    public static void afterClass(){
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
    public void shouldReturnIsKindOfPredicate() {
        Ontology o = new Ontology();
        Concept c = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        o.addConcept(c);
        c.setOntology(o);
        em.persist(c);
        em.persist(o);
        em.flush();
        em.clear();
        
        c = em.createQuery("SELECT c FROM Concept c where c.id=1", Concept.class).getSingleResult();
        o = em.createQuery("SELECT o FROM Ontology o where o.id=1", Ontology.class).getSingleResult();
        
        assertEquals(c, o.getIsKindOfPredicate());
    }    

}
