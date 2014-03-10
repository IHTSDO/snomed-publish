package com.ihtsdo.snomed.model;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

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
        if (emf != null){
            emf.close();
        }
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
        Ontology o = CreateOntologyUtil.createOntology();

        o.getFlavours().iterator().next().getVersions().iterator().next().addConcept(new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID));        
        
        em.persist(o);
        em.flush();
        em.clear();
        
        Concept c = em.createQuery("SELECT c FROM Concept c where c.id=1", Concept.class).getSingleResult();
        OntologyVersion ov = em.createQuery("SELECT o FROM OntologyVersion o where o.id=1", OntologyVersion.class).getSingleResult();
        assertEquals(c, ov.getIsKindOfPredicate());
    }

   

}
