package com.ihtsdo.snomed.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.TransitiveClosureAlgorithm;

public class TransitiveClosureAlgorithmTest {

    private static TransitiveClosureAlgorithm algorithm;
    
    Set<Concept> concepts = new HashSet<Concept>();
    Concept c1,c2,c3,c4,c5,cp;
    Statement s12,s13,s14,s15,s23,s24,s25,s34,s35,s45;

    @Before
    public void setUp() throws Exception {
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);
        c5 = new Concept(5);
        cp = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);        
        
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);
        c4.addKindOf(c5);

        s12 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c1, cp, c2);
        s13 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c1, cp, c3);
        s14 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c1, cp, c4);
        s15 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c1, cp, c5);
        s23 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c2, cp, c3);
        s24 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c2, cp, c4);
        s25 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c2, cp, c5);
        s34 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c3, cp, c4);
        s35 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c3, cp, c5);
        s45 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c4, cp, c5);

        concepts.addAll(Arrays.asList(c1,c2,c3,c4,c5,cp));
        algorithm = new TransitiveClosureAlgorithm();
    }

    @Test
    public void shouldReturnAllParentsForC1(){
        Set<Statement> results = algorithm.runAlgorithm(Arrays.asList(c1));
        assertEquals(4, results.size());
        assertTrue(results.contains(s12));
        assertTrue(results.contains(s13));
        assertTrue(results.contains(s14));
        assertTrue(results.contains(s15));
    }
    
    @Test
    public void shouldReturnAllParentsForC1C2(){
        Set<Statement> results = algorithm.runAlgorithm(Arrays.asList(c1,c2));
        assertEquals(7, results.size());
        assertTrue(results.contains(s12));
        assertTrue(results.contains(s13));
        assertTrue(results.contains(s14));
        assertTrue(results.contains(s15));
        assertTrue(results.contains(s23));
        assertTrue(results.contains(s24));
        assertTrue(results.contains(s25));
    }
    @Test
    public void shouldReturnAllParentsForC1C2C3(){
        Set<Statement> results = algorithm.runAlgorithm(Arrays.asList(c1,c2,c3));
        assertEquals(9, results.size());
        assertTrue(results.contains(s12));
        assertTrue(results.contains(s13));
        assertTrue(results.contains(s14));
        assertTrue(results.contains(s15));
        assertTrue(results.contains(s23));
        assertTrue(results.contains(s24));
        assertTrue(results.contains(s25));
        assertTrue(results.contains(s34));
        assertTrue(results.contains(s35));
    }
    
    @Test
    public void shouldReturnAllParentsForC1C2C3C4(){
        Set<Statement> results = algorithm.runAlgorithm(Arrays.asList(c1,c2,c3,c4));
        assertEquals(10, results.size());
        assertTrue(results.contains(s12));
        assertTrue(results.contains(s13));
        assertTrue(results.contains(s14));
        assertTrue(results.contains(s15));
        assertTrue(results.contains(s23));
        assertTrue(results.contains(s24));
        assertTrue(results.contains(s25));
        assertTrue(results.contains(s34));
        assertTrue(results.contains(s35));
        assertTrue(results.contains(s45));
    }
    
    @Test
    public void shouldReturnAllParentsForC1C2C3C4C5(){
        Set<Statement> results = algorithm.runAlgorithm(Arrays.asList(c1,c2,c3,c4,c5));
        assertEquals(10, results.size());
        assertTrue(results.contains(s12));
        assertTrue(results.contains(s13));
        assertTrue(results.contains(s14));
        assertTrue(results.contains(s15));
        assertTrue(results.contains(s23));
        assertTrue(results.contains(s24));
        assertTrue(results.contains(s25));
        assertTrue(results.contains(s34));
        assertTrue(results.contains(s35));
        assertTrue(results.contains(s45));
    } 
    
    @Test
    public void shouldReturnAllParentsForC2(){
        Set<Statement> results = algorithm.runAlgorithm(Arrays.asList(c2));
        assertEquals(3, results.size());
        assertTrue(results.contains(s23));
        assertTrue(results.contains(s24));
        assertTrue(results.contains(s25));
    }  
}
