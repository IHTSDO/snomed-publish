package com.ihtsdo.snomed.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.CanonicalAlgorithm;

public class CanonicalAlgorithmTest {

    //private static HibernateDbImporter importer;
    //private static Main main;
    private static CanonicalAlgorithm algorithm;
    
    public static final int NOT_DEFINING_CHARACTERISTIC_TYPE = 1;

    Statement rs1, rs2, rs3, rs4, rs5;
    Concept c1, c2, c3, c4, c5, cp1, cp2, cp3, cA, cB, cC;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception { }

    @Before
    public void setUp() throws Exception {
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);
        c5 = new Concept(5);
        cA = new Concept(10);
        cB = new Concept(11);
        cC = new Concept(12);
        
        cp1 = new Concept(100);
        cp2 = new Concept(101);
        cp3 = new Concept(103);

        c1.setPrimitive(true);
        c2.setPrimitive(true);
        c3.setPrimitive(true);
        c4.setPrimitive(true);
        c5.setPrimitive(true);
        cA.setPrimitive(true);
        cB.setPrimitive(true);
        cC.setPrimitive(true);
        
        Ontology o = new Ontology();
        o.setConcepts(new HashSet<Concept>(Arrays.asList(c2, c2, c3, c4, c5, cA, cB, cC, cp1, cp2, cp3)));
        o.setIsKindOfPredicate(new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID));
        c1.setOntology(o);
        c2.setOntology(o);
        c3.setOntology(o);
        c4.setOntology(o);
        c5.setOntology(o);
        cA.setOntology(o);
        cB.setOntology(o);
        cC.setOntology(o);
        cp1.setOntology(o);
        cp2.setOntology(o);
        cp3.setOntology(o);

        algorithm = new CanonicalAlgorithm();
    }

    @After
    public void tearDown() throws Exception {
        //main.closeDb();
    }


    /*
     * PP test case 1:
     * ---------------
     * {1,2,3} is primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 
     * Proximial primitive supertypes for concept 1: 
     * {2}
     */
    @Test
    public void shouldPassTestCase1_WithCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }
    @Test
    public void shouldPassTestCase1_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }

    /*
     * PP test case 2:
     * ---------------
     * {1,3} is primitive, {2} is not primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 
     * Proximial primitive supertypes for concept 1: 
     * {3}
     */ 
    @Test
    public void shouldPassTestCase2_WithCache() {
        c2.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c3));
    }
    @Test
    public void shouldPassTestCase2_WithoutCache() {
        c2.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c3));
    }    

    /*
     * PP test case 3:
     * ---------------
     * {1,4} is primitive, {2,3} is not primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 3 isKindOf 4
     * 
     * Proximial primitive supertypes for concept 1: 
     * {4}
     */ 
    @Test
    public void shouldPassTestCase3_WithCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c4));
    }

    @Test
    public void shouldPassTestCase3_WithoutCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c4));
    }    

    /*
     * PP test case 4:
     * ---------------
     * {1,2,3,4} is primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 3 isKindOf 4 
     * 
     * Proximial primitive supertypes for concept 1: 
     * {2}
     */
    @Test
    public void shouldPassTestCase4_WithCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }
    @Test
    public void shouldPassTestCase4_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }   

    /*
     * PP test case 5:
     * ---------------
     * {1,2,3,4,5} is primitive
     * 1 isKindOf 2
     * 1 isKindOf 3
     * 2 isKindof 4
     * 3 isKindOf 4
     * 
     * Proximial primitive supertypes for concept 1: 
     * {2,3}
     */
    @Test
    public void shouldPassTestCase5_WithCache() {
        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(2, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
        assertTrue(proximalPrimitives.contains(c3));
    }

    @Test
    public void shouldPassTestCase5_WithoutCache() {
        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false, false, null);
        assertEquals(2, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
        assertTrue(proximalPrimitives.contains(c3));
    }   
    
    @Test
    public void shouldCreateNewProximalPrimitiveIsAStatementsForConcept(){
        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);
        
        Set<Statement> statements = algorithm.createProximalPrimitiveStatementsForConcept(c1, true, false, null);
        assertEquals(2, statements.size());
        
        for (Statement r : statements){
            assertEquals(c1, r.getSubject());
            assertTrue(r.isKindOfStatement());
            assertTrue(new HashSet<Concept>(Arrays.asList(c2,c3)).contains(r.getObject()));
            assertTrue(r.isDefiningCharacteristic());
            assertTrue(new HashSet<Long>(Arrays.asList(
                    CanonicalAlgorithm.RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS, 
                    CanonicalAlgorithm.RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS + 1)).
                    contains(r.getSerialisedId()));
        }
    }    

    /*
     * PP test case 6:
     * ---------------
     * {1,2,4} is primitive, {3} is not primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 3 isKindof 4
     * 
     * Proximial primitive supertypes for concept 1: 
     * {2}
     */
    @Test
    public void shouldPassTestCase6_WithCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }

    @Test
    public void shouldPassTestCase6_WithoutCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }   

    /*
     * PP test case 7:
     * ---------------
     * {1,4} is primitive, {2,3} is not primitive
     * 1 isKindOf 2
     * 1 isKindOf 3
     * 2 isKindof 4
     * 3 isKindOf 4
     * 
     * Proximial primitive supertypes for concept 1: 
     * {4}
     */
    @Test
    public void shouldPassTestCase7_WithCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);
        c1.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c4));
    }   

    @Test
    public void shouldPassTestCase7_WithoutCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);
        c1.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c4));
    }    

    /*
     * PP test case 8:
     * ---------------
     * {1,2,4} is primitive, {3} is not primitive
     * 1 isKindOf 2
     * 1 isKindOf 3
     * 2 isKindof 4
     * 3 isKindOf 4
     * 
     * Proximial primitive supertypes for concept 1: 
     * {2}
     */
    @Test
    public void shouldPassTestCase8_WithCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    } 

    @Test
    public void shouldPassTestCase8_WithoutCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }   
    
    /*
     * PP test case 9:
     * ---------------
     * {2,4} is primitive, {1,3} is not primitive
     * 1 isKindOf 2
     * 1 isKindOf 3
     * 2 isKindof 4
     * 3 isKindOf 4
     * 
     * Proximial primitive supertypes for concept 1: 
     * {2}
     */
    @Test
    public void shouldPassTestCase9_WithCache() {
        c3.setPrimitive(false);
        c1.setPrimitive(false);

        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }  
    
    /*
     * PP test case 10:
     * ----------------
     * {2,3,4} is primitive, {1} is not primitive
     * 1 isKindOf 2
     * 1 isKindOf 3
     * 2 isKindof 4
     * 3 isKindOf 4
     * 
     * Proximial primitive supertypes for concept 1: 
     * {2,3}
     */
    @Test
    public void shouldPassTestCase10_WithCache() {
        c1.setPrimitive(false);

        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true, false, null);
        assertEquals(2, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
        assertTrue(proximalPrimitives.contains(c3));
    }      

    
    /* ---------------------------------------
     * Unshared Defining Characteristics [UDC]
     * --------------------------------------- */
    
    /*
     * UDC test case 1:
     * ----------------
     * {c1,cA} are primitive types
     * Triple (c1,r1,cA) are a defining characteristics
     */
    @Test
    public void shouldPassUDCTestCase1(){ 
        Statement r1 = new Statement(101, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(r1));
    }
    
    /*
     * UDC test case 2:
     * ----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r1,cA) is a defining characteristic
     * t1 has group 1
     * t2 has group 1
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase2(){ 
        new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(0, foundUDC.size());
    }
    
    /*
     * UDC test case 3:
     * ----------------
     * {c1,c2,c3,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c3,r1,cA) is a defining characteristic
     * t1 has group 1
     * t2 has group 1
     * c1 isKindOf c2
     * c2 isKindOf c3
     */
    @Test
    public void shouldPassUDCTestCase3(){ 
        new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c3, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(0, foundUDC.size());
    }    

    
    /*
     * UDC test case 4:
     * ----------------
     * {c1,c3,cA} are primitive types. {c2} is not a primitive type
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c3,r1,cA) is a defining characteristic
     * t1 has group 1
     * t2 has group 1
     * c1 isKindOf c2
     * c2 isKindOf c3
     */
    @Test
    public void shouldPassUDCTestCase4(){ 
        c2.setPrimitive(false);
        Statement r = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(r));
    }    
    
    /*
     * UDC test case 5:
     * ----------------
     * {c1,c2,cA,cB} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r1,cB) is a defining characteristic
     * t1 has group 1
     * t2 has group 1
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase5(){ 
        Statement r = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp1, cB, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(r));
    }    
    
    /*
     * UDC test case 6:
     * ----------------
     * {c1,c3,cA} are primitive types. {c2} is not a primitive type 
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c3,r1,cA) is a defining characteristic
     * t1 has group 1
     * t2 has group 1
     * c1 isKindOf c2
     * c2 isKindOf c3
     */
    @Test
    public void shouldPassUDCTestCase6(){ 
        c2.setPrimitive(false);
        new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c3, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(0, foundUDC.size());
    }  
    
    /*
     * UDC test case 7:
     * ----------------
     * {c1,c2,cA} are primitive types 
     * Triple (c2,r1,cA) is a defining characteristic
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase7(){ 
        new Statement(100, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(0, foundUDC.size());
    }
    
    /*
     * UDC test case 8:
     * ----------------
     * {c1,cA} are primitive types 
     * Triple (c1,r1,cA) is not a defining characteristic
     */
    @Test
    public void shouldPassUDCTestCase8(){ 
        new Statement(100, c1, cp1, cA, NOT_DEFINING_CHARACTERISTIC_TYPE, 0);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(0, foundUDC.size());
    }  
    
    /*
     * UDC test case 9:
     * ----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r1,cA) is not a defining characteristic
     * t1 has group 1
     * t2 has group 1
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase9(){ 
        Statement r = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp1, cA, NOT_DEFINING_CHARACTERISTIC_TYPE, 1);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(r));
    }  
    
    /*
     * USC test case 10:
     * -----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is not a defining characteristic
     * Triple t2(c2,r1,cA) is a defining characteristic
     * t1 has group 1
     * t2 has group 1
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase10(){ 
        new Statement(100, c1, cp1, cA, NOT_DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(0, foundUDC.size());
    } 
    
    /*
     * UDC test case 11:
     * -----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r1,cA) is a defining characteristic
     * t1 has group 1
     * t2 has group 1
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase11(){ 
        new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(0, foundUDC.size());
    }  
    
    /*
     * UDC test case 12:
     * -----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r1,cA) is a defining characteristic
     * t1 has group 0
     * t2 has group 0
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase12(){ 
        new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);
        new Statement(101, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(0, foundUDC.size());
    }
    
    /*
     * UDC test case 13:
     * -----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r1,cA) is a defining characteristic
     * t1 has group 0
     * t2 has group 1
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase13(){ 
        Statement s = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);
        new Statement(101, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(s));
    }
    
    /*
     * UDC test case 14:
     * -----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r1,cA) is a defining characteristic
     * t1 has group 1
     * t2 has group 0
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase14(){ 
        Statement s = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(s));
    }
    
    /*
     * UDC test case 15:
     * -----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c1,r2,cB) is a defining characteristic
     * Triple t3(c2,r1,cA) is a defining characteristic
     * Triple t4(c2,r2,cB) is a defining characteristic
     * t1, t2 has group 1
     * t3, t4 has group 2
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase15(){ 
        new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c1, cp2, cB, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(102, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 2);
        new Statement(103, c2, cp2, cB, Statement.DEFINING_CHARACTERISTIC_TYPE, 2);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(0, foundUDC.size());
    }    
    
    /*
     * UDC test case 16:
     * -----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c1,r2,cB) is a defining characteristic
     * Triple t3(c2,r1,cA) is a defining characteristic
     * t1, t2 has group 1
     * t3 has group 2
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase16(){ 
        Statement s1 = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        Statement s2 = new Statement(101, c1, cp2, cB, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(102, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 2);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, true, null); 
        
        assertEquals(2, foundUDC.size());
        assertTrue(foundUDC.contains(s1));
        assertTrue(foundUDC.contains(s2));
    }
    
    /*
     * UDC test case 17:
     * -----------------
     * {c1,c2,cA} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c1,r2,cB) is a defining characteristic
     * Triple t3(c2,r1,cA) is a defining characteristic
     * Triple t4(c2,r2,cB) is a defining characteristic
     * Triple t5(c2,r3,cC) is a defining characteristic 
     * t1, t2 has group 1
     * t3, t4, t5 has group 2
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase17(){ 
        Statement s1 = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        Statement s2 = new Statement(101, c1, cp2, cB, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(102, c2, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 2);
        new Statement(103, c2, cp2, cB, Statement.DEFINING_CHARACTERISTIC_TYPE, 2);
        new Statement(104, c2, cp3, cC, Statement.DEFINING_CHARACTERISTIC_TYPE, 2);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(2, foundUDC.size());
        assertTrue(foundUDC.contains(s1));
        assertTrue(foundUDC.contains(s2));
    }  
    
    /*
     * UDC test case 18:
     * -----------------
     * {c1,c2,cA, cB} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r1,cB) is a defining characteristic
     * t1 has group 1
     * t2 has group 2
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase18(){ 
        Statement s = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp1, cB, Statement.DEFINING_CHARACTERISTIC_TYPE, 2);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(s));
    }
    
    /*
     * UDC test case 19:
     * -----------------
     * {c1,c2,cA, cB} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r2,cA) is a defining characteristic
     * t1 has group 1
     * t2 has group 2
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase19(){ 
        Statement s = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp2, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 2);
        c1.addKindOf(c2);
        
        Set<Statement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true, false, null); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(s));
    }    
    
    @Test
    public void shouldGetNewId(){
        assertEquals(CanonicalAlgorithm.RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS, algorithm.getNewId());
        assertEquals(CanonicalAlgorithm.RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS + 1, algorithm.getNewId());
    }
    
    /*
     * PP test case 5:
     * ---------------
     * {1,2,3,4,5} is primitive
     * 1 isKindOf 2
     * 1 isKindOf 3
     * 2 isKindof 4
     * 3 isKindOf 4

     * PLUS
     * 
     * UDC test case 19:
     * -----------------
     * {c1,c2,cA, cB} are primitive types
     * Triple t1(c1,r1,cA) is a defining characteristic
     * Triple t2(c2,r2,cA) is a defining characteristic
     * t1 has group 1
     * t2 has group 2
     * c1 isKindOf c2
     */
    @Test
    public void shouldReturnCorrectStatementsForRunAlgorithm() {
        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);
        Statement s = new Statement(100, c1, cp1, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);
        new Statement(101, c2, cp2, cA, Statement.DEFINING_CHARACTERISTIC_TYPE, 2);

        Set<Statement> foundStatements = algorithm.runAlgorithm(Arrays.asList(c1, c2, c3), false);
        assertEquals(6, foundStatements.size());
        assertTrue(foundStatements.contains(s));
    }
}
