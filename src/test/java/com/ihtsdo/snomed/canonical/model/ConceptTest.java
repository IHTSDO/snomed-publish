package com.ihtsdo.snomed.canonical.model;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConceptTest {
    
    Concept c1,c2,c3,c4,c5,c6;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);
        c5 = new Concept(5);
        c6 = new Concept(6);
    }

    @After
    public void tearDown() throws Exception {
    }

    /*
     * Test case 1:
     * {c1,c3,c5} are primitives, {c2,c4} are not primitives
     * 
     * c1 isKindOf c2
     * c2 isKindOf c3
     * c2 isKinddOf c4
     * c4 isKindOf c5
     * 
     * should return for c1:
     * c1 has primitive supertypes {c3,c5}
     * 
     */
    @Test
    public void test() {
        c1.setPrimitive(true);
        c2.setPrimitive(false);
        c3.setPrimitive(true);
        c4.setPrimitive(false);
        c5.setPrimitive(true);
        
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c2.addKindOf(c4);
        c4.addKindOf(c5);
        
        Set<Concept> results = c1.getKindOfPrimitiveConcepts(true);
        assertEquals(2, results.size());
        assertTrue(results.contains(c3));
        assertTrue(results.contains(c5));
    }

}
