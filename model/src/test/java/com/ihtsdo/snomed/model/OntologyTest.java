package com.ihtsdo.snomed.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;

public class OntologyTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldReturnIsKindOfPredicate() {
        Ontology o = new Ontology();
        Concept c = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        o.addConcept(c);
        assertEquals(c, o.getIsKindOfPredicate());
    }
    
    @Test(expected=IllegalStateException.class)
    public void shouldReturnIsKindOfPredicateFail() {
        Ontology o = new Ontology();
        Concept c = new Concept(1);
        o.addConcept(c);
        assertEquals(c, o.getIsKindOfPredicate());
    }

}
