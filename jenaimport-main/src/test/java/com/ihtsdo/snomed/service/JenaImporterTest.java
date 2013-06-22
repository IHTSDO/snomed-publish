package com.ihtsdo.snomed.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.jena.JenaImporter;

public class JenaImporterTest extends DatabaseTest {

    Concept c1,c2,c3,c4,cp1,cp2,cp3,ca1,ca2,ca3,ca4;
    Ontology o;
    
    private JenaImporter jena = new JenaImporter();
        
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Concept kindOfConcept = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        kindOfConcept.setFullySpecifiedName("isA");
        
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);

        cp1 = new Concept(11);
        cp2 = new Concept(12);
        cp3 = new Concept(13);
        
        ca1 = new Concept(21);
        ca2 = new Concept(22);
        ca3 = new Concept(23);
        ca4 = new Concept(24);
        
        c1.setPrimitive(true);
        c2.setPrimitive(true);
        c3.setPrimitive(true);
        c4.setPrimitive(true);
        cp1.setPrimitive(true);
        cp2.setPrimitive(true);
        cp3.setPrimitive(true);
        
        c1.setFullySpecifiedName("c1");
        c2.setFullySpecifiedName("c2");
        c3.setFullySpecifiedName("c3");
        c4.setFullySpecifiedName("c4");
        
        cp1.setFullySpecifiedName("cp1");
        cp2.setFullySpecifiedName("cp2");
        cp3.setFullySpecifiedName("cp3");
        
        ca1.setFullySpecifiedName("ca1");
        ca2.setFullySpecifiedName("ca2");
        ca3.setFullySpecifiedName("ca3");
        ca4.setFullySpecifiedName("ca4");
        
        c1.addKindOf(c2);
        c2.addParentOf(c1);
        
        c2.addKindOf(c3);
        c3.addParentOf(c2);
        
        c3.addKindOf(c4);
        c4.addParentOf(c3);
        
        c1.addKindOf(c2);
        c2.addParentOf(c1);
        
        Statement s1 = new Statement(1, c1, cp1, ca1);
        Statement s2 = new Statement(2, c2, cp2, ca2);
        Statement s3 = new Statement(3, c3, cp3, ca3);
        Statement s4 = new Statement(4, c4, cp3, ca4);
        
        Statement sp12 = new Statement(12, c1, kindOfConcept, c2);
        Statement sp23 = new Statement(23, c2, kindOfConcept, c3);
        Statement sp34 = new Statement(34, c3, kindOfConcept, c4);
        
        o = new Ontology();
        o.addConcept(c1);
        o.addConcept(c2);
        o.addConcept(c3);
        o.addConcept(c4);
        o.addConcept(cp1);
        o.addConcept(cp2);
        o.addConcept(cp3);
        o.addConcept(ca1);
        o.addConcept(ca2);
        o.addConcept(ca3);
        o.addConcept(ca4);
        o.addStatement(s1);
        o.addStatement(s2);
        o.addStatement(s3);
        o.addStatement(s4);
        o.addStatement(sp12);
        o.addStatement(sp23);
        o.addStatement(sp34);
        o.addConcept(kindOfConcept);
        
        c1.setOntology(o);
        c2.setOntology(o);
        c3.setOntology(o);
        c4.setOntology(o);
        cp1.setOntology(o);
        cp2.setOntology(o);
        cp3.setOntology(o);
        ca1.setOntology(o);
        ca2.setOntology(o);
        ca3.setOntology(o);
        ca4.setOntology(o);
        s1.setOntology(o);
        s2.setOntology(o);
        s3.setOntology(o);
        s4.setOntology(o);
        sp12.setOntology(o);
        sp23.setOntology(o);
        sp34.setOntology(o);
        kindOfConcept.setOntology(o);
        
        em.persist(o);
        
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);
        em.persist(c4);
        em.persist(cp1);
        em.persist(cp2);
        em.persist(cp3);
        em.persist(ca1);
        em.persist(ca2);
        em.persist(ca3);
        em.persist(ca4);
        em.persist(s1);
        em.persist(s2);
        em.persist(s3);
        em.persist(s4);
        em.persist(sp12);
        em.persist(sp23);
        em.persist(sp34);
        em.persist(kindOfConcept);
        
        em.flush();
        em.clear();
        
//        c1 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=1", Concept.class).getSingleResult();
//        c2 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=2", Concept.class).getSingleResult();
//        c3 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=3", Concept.class).getSingleResult();
//        c4 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=4", Concept.class).getSingleResult();
//        cp1 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=11", Concept.class).getSingleResult();
//        cp2 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=12", Concept.class).getSingleResult();
//        cp3 = em.createQuery("SELECT c FROM Concept c where c.serialisedId=13", Concept.class).getSingleResult();
    }
    
    @Test
    public void shouldCreateJenaModel(){
        OntModel model = jena.importJenaModel(o, em);
        assertNotNull(model);
        
        model.write(System.out, "RDF/XML");
        
    }

}
