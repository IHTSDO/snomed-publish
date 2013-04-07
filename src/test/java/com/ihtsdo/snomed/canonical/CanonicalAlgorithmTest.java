package com.ihtsdo.snomed.canonical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class CanonicalAlgorithmTest {

    //private static HibernateDatabaseImporter importer;
    //private static Main main;
    private static CanonicalAlgorithm algorithm;

    RelationshipStatement rs1, rs2, rs3, rs4, rs5;
    Concept c1, c2, c3, c4, c5, cA, cB;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
//        importer = new HibernateDatabaseImporter();
//        main = new Main();
        algorithm = new CanonicalAlgorithm();
    }

    @Before
    public void setUp() throws Exception {
        //main.initDb(null);
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);
        c5 = new Concept(5);
        cA = new Concept(10);
        cB = new Concept(11);

        c1.setPrimitive(true);
        c2.setPrimitive(true);
        c3.setPrimitive(true);
        c4.setPrimitive(true);
        c5.setPrimitive(true);
        cA.setPrimitive(true);
        cB.setPrimitive(true);
    }

    @After
    public void tearDown() throws Exception {
        //main.closeDb();
    }


    /*
     * Test case 1:
     * ------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }
    @Test
    public void shouldPassTestCase1_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }

    /*
     * Test case 2:
     * ------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c3));
    }
    @Test
    public void shouldPassTestCase2_WithoutCache() {
        c2.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c3));
    }    

    /*
     * Test case 3:
     * ------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c4));
    }    

    /*
     * Test case 4:
     * ------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }
    @Test
    public void shouldPassTestCase4_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }   

    /*
     * Test case 5:
     * ------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false);
        assertEquals(2, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
        assertTrue(proximalPrimitives.contains(c3));
    }   

    /*
     * Test case 6:
     * ------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }

    @Test
    public void shouldPassTestCase6_WithoutCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }   

    /*
     * Test case 7:
     * ------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c4));
    }    

    /*
     * Test case 8:
     * ------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, false);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }   

    /*
     * Test case 5:
     * ------------
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
    public void shouldCreateNewProximalPrimitiveIsAStatementsForConcept(){
        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<RelationshipStatement> statements = algorithm.createProximalPrimitiveStatementsForConcept(c1, true);
        assertEquals(2, statements.size());
        
        for (RelationshipStatement r : statements){
            assertEquals(c1, r.getSubject());
            assertTrue(r.isKindOfRelationship());
            assertTrue(new HashSet<Concept>(Arrays.asList(c2,c3)).contains(r.getObject()));
            assertTrue(r.isDefiningCharacteristic());
            assertTrue(new HashSet<Long>(Arrays.asList(
                    CanonicalAlgorithm.RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS, 
                    CanonicalAlgorithm.RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS + 1)).
                    contains(r.getId()));
        }
    }
    
    /*
     * Test case 9:
     * ------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
        assertEquals(1, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
    }  
    
    /*
     * Test case 10:
     * -------------
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

        Set<Concept> proximalPrimitives = algorithm.getProximalPrimitiveConcepts(c1, true);
        assertEquals(2, proximalPrimitives.size());
        assertTrue(proximalPrimitives.contains(c2));
        assertTrue(proximalPrimitives.contains(c3));
    }      

    
    /* ---------------------------------------
     * Unshared Defining Characteristics [UDC]
     * --------------------------------------- */
    
    /*
     * Test case 1:
     * {c1,cA} are primitive types
     * Triple (c1,r1,cA) are a defining characteristics
     */
    @Test
    public void shouldPassUDCTestCase1(){ 
        RelationshipStatement r1 = new RelationshipStatement(101, c1, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(r1));
    }
    
    /*
     * Test case 2:
     * {c1,c2,cA} are primitive types
     * Triple (c1,r1,cA) is a defining characteristic
     * Triple (c2,r1,cA) is a defining characteristic
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase2(){ 
        new RelationshipStatement(100, c1, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        new RelationshipStatement(101, c2, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        c1.addKindOf(c2);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(0, foundUDC.size());
    }
    
    /*
     * Test case 3:
     * {c1,c2,c3,cA} are primitive types
     * Triple (c1,r1,cA) is a defining characteristic
     * Triple (c3,r1,cA) is a defining characteristic
     * c1 isKindOf c2
     * c2 isKindOf c3
     */
    @Test
    public void shouldPassUDCTestCase3(){ 
        new RelationshipStatement(100, c1, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        new RelationshipStatement(101, c3, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(0, foundUDC.size());
    }    

    
    /*
     * Test case 4:
     * {c1,c3,cA} are primitive types. {c2} is not a primitive type
     * Triple (c1,r1,cA) is a defining characteristic
     * Triple (c3,r1,cA) is a defining characteristic
     * c1 isKindOf c2
     * c2 isKindOf c3
     */
    @Test
    public void shouldPassUDCTestCase4(){ 
        c2.setPrimitive(false);
        RelationshipStatement r = new RelationshipStatement(100, c1, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        new RelationshipStatement(101, c2, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(r));
    }    
    
    /*
     * Test case 5:
     * {c1,c2,cA,cB} are primitive types
     * Triple (c1,r1,cA) is a defining characteristic
     * Triple (c2,r1,cB) is a defining characteristic
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase5(){ 
        RelationshipStatement r = new RelationshipStatement(100, c1, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        new RelationshipStatement(101, c2, 1, cB, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        c1.addKindOf(c2);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(r));
    }    
    
    /*
     * Test case 6:
     * {c1,c3,cA} are primitive types. {c2} is not a primitive type 
     * Triple (c1,r1,cA) is a defining characteristic
     * Triple (c3,r1,cA) is a defining characteristic
     * c1 isKindOf c2
     * c2 isKindOf c3
     */
    @Test
    public void shouldPassUDCTestCase6(){ 
        c2.setPrimitive(false);
        new RelationshipStatement(100, c1, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        new RelationshipStatement(101, c3, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(0, foundUDC.size());
    }  
    
    /*
     * Test case 7:
     * {c1,c2,cA} are primitive types 
     * Triple (c2,r1,cA) is a defining characteristic
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase7(){ 
        new RelationshipStatement(100, c2, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        c1.addKindOf(c2);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(0, foundUDC.size());
    }
    
    /*
     * Test case 8:
     * {c1,cA} are primitive types 
     * Triple (c2,r1,cA) is not a defining characteristic
     */
    @Test
    public void shouldPassUDCTestCase8(){ 
        new RelationshipStatement(100, c2, 1, cA, RelationshipStatement.NOT_DEFINING_CHARACTERISTIC_TYPE);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(0, foundUDC.size());
    }  
    
    /*
     * Test case 9:
     * {c1,c2,cA} are primitive types
     * Triple (c1,r1,cA) is a defining characteristic
     * Triple (c2,r1,cA) is not a defining characteristic
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase9(){ 
        RelationshipStatement r = new RelationshipStatement(100, c1, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        new RelationshipStatement(101, c2, 1, cA, RelationshipStatement.NOT_DEFINING_CHARACTERISTIC_TYPE);
        c1.addKindOf(c2);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(1, foundUDC.size());
        assertTrue(foundUDC.contains(r));
    }  
    
    /*
     * Test case 10:
     * {c1,c2,cA} are primitive types
     * Triple (c1,r1,cA) is not a defining characteristic
     * Triple (c2,r1,cA) is a defining characteristic
     * c1 isKindOf c2
     */
    @Test
    public void shouldPassUDCTestCase10(){ 
        new RelationshipStatement(100, c1, 1, cA, RelationshipStatement.NOT_DEFINING_CHARACTERISTIC_TYPE);
        new RelationshipStatement(101, c2, 1, cA, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE);
        c1.addKindOf(c2);
        
        Set<RelationshipStatement> foundUDC = algorithm.getUnsharedDefiningCharacteristicsForConcept(c1, true); 
        
        assertEquals(0, foundUDC.size());
    }  
    
    /*
     * Test case 1:
     * {r(1000)} is a defining characteristic
     * c1 isKindOf c2
     * c1 r(1000) c3
     * c2 r(1000) c3
     * 
     * should return:
     * c1 has no relationships
     * c2 r(1000) c3 [103]
     * c3 has no relationships
     */
  

    /*
     * Test case 2:
     * {r(1000)} is a defining characteristic 
     * c1 isKindOf c2
     * c2 isKindOf c3
     * c1 r(1000) c4
     * c3 r(1000) c4
     * 
     * should return:
     * c1 has no relationships
     * c2 has no relationships
     * c3 r(1000) c4 [103]
     * c4 has no relationships
     */


    /*
     * Test case 3:
     * {r(1000)} is NOT a defining characteristic
     * c1 isKindOf c2
     * c1 r(1000) c3
     * c2 r(1000) c3
     * 
     * should return:
     * c1 has no relationships
     * c2 has no relationships
     * c3 has no relationships
     */

}
