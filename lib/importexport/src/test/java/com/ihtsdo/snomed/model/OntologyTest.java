package com.ihtsdo.snomed.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.service.parser.DatabaseTest;

public class OntologyTest extends DatabaseTest{

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
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
