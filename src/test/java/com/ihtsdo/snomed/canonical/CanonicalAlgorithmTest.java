package com.ihtsdo.snomed.canonical;

import java.io.IOException;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class CanonicalAlgorithmTest {
    
    private static HibernateDatabaseImporter importer;
    private static Main main;
    
    RelationshipStatement rs1, rs2, rs3, rs4, rs5;
    
    Concept c1, c2, c3, c4, c5;
    
    CanonicalAlgorithm algorithm = new CanonicalAlgorithm();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        importer = new HibernateDatabaseImporter();
        main = new Main();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        main.initDb(null);
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);
        c5 = new Concept(5);
        
//        rs1 = new RelationshipStatement();
//        rs1.setSubject(c1);
//        rs1.setObject(c2);
//        rs1.setRelationshipType(RelationshipStatement.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
//        c1.addSubjectOfRelationshipStatement(rs1);
//        c1.addKindOf(c2);
//        
//        rs2 = new RelationshipStatement();
//        rs2.setRelationshipType(RelationshipStatement.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
//        c2.addSubjectOfRelationshipStatement(rs2);
//        c2.addKindOf(c3);
//        
//        rs3 = new RelationshipStatement();
//        rs3.setRelationshipType(RelationshipStatement.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
//        c3.addSubjectOfRelationshipStatement(rs3);
//        c3.addKindOf(c4);
//        
//        rs4 = new RelationshipStatement();
//        rs4.setRelationshipType(1);
//        c1.addSubjectOfRelationshipStatement(rs4);
    }

    @After
    public void tearDown() throws Exception {
        main.closeDb();
    }
    
    
    /*
     * Test case 1:
     * ------------
     * {1,2,3} is primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 1 isKindof 3
     * 
     * should return for concept 1:
     * 1 isKindOf 2
     */
    @Test
    public void shouldPassImmidiatePrimitiveTestCase1() {
        c1.setPrimitive(true);
        c2.setPrimitive(true);
        c3.setPrimitive(true);
        
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c1.addKindOf(c3);
        
        Set<Concept> immidiatePrimitive = algorithm.getAllImmidiatePrimitiveConcepts(c1, true);
        assertEquals(1, immidiatePrimitive.size());
        assertTrue(immidiatePrimitive.contains(c2));
    }
    
    /*
     * Test case 2:
     * ------------
     * {1,3} is primitive, {2} is not primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 1 isKindOf 3
     * 
     * should return for concept 1:
     * 1 isKindOf 3
     */ 
    @Test
    public void shouldPassImmidiatePrimitiveTestCase2() {
        c1.setPrimitive(true);
        c2.setPrimitive(false);
        c3.setPrimitive(true);
        
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c1.addKindOf(c3);
        
        Set<Concept> immidiatePrimitive = algorithm.getAllImmidiatePrimitiveConcepts(c1, true);
        assertEquals(1, immidiatePrimitive.size());
        assertTrue(immidiatePrimitive.contains(c3));
    }
    
    /*
    * Test case 3:
    * ------------
    * {1,2,3} is not primitive
    * 1 isKindOf 2
    * 2 isKindOf 3
    * 1 isKindOf 3
    * 
    * should return for concept 1:
    * 1 isKindOf {}
    */ 
   @Test
   public void shouldPassImmidiatePrimitiveTestCase3() {
       c1.setPrimitive(false);
       c2.setPrimitive(false);
       c3.setPrimitive(false);
       
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       c1.addKindOf(c3);
       
       Set<Concept> immidiatePrimitive = algorithm.getAllImmidiatePrimitiveConcepts(c1, true);
       assertEquals(0, immidiatePrimitive.size());
   }    
   
   /*
    * Test case 4:
    * ------------
    * {1,2,3} is primitive
    * 1 isKindOf 2
    * 2 isKindOf 3
    * 
    * should return for concept 1:
    * 1 isKindOf 2
    */
   @Test
   public void shouldPassImmidiatePrimitiveTestCase4() {
       c1.setPrimitive(true);
       c2.setPrimitive(true);
       
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       
       Set<Concept> immidiatePrimitive = algorithm.getAllImmidiatePrimitiveConcepts(c1, true);
       assertEquals(1, immidiatePrimitive.size());
       assertTrue(immidiatePrimitive.contains(c2));
   }   

   /*
    * Test case 5:
    * ------------
    * {1,2,3,4,5} is primitive
    * 1 isKindOf 2
    * 2 isKindOf 3
    * 1 isKindof 4
    * 4 isKindOf 5
    * 
    * should return for concept 1:
    * 1 isKindOf {2,3}
    */
   @Test
   public void shouldPassImmidiatePrimitiveTestCase5() {
       c1.setPrimitive(true);
       c2.setPrimitive(true);
       c3.setPrimitive(true);
       c4.setPrimitive(true);
       c5.setPrimitive(true);
       
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       c1.addKindOf(c4);
       c4.addKindOf(c5);
       
       Set<Concept> immidiatePrimitive = algorithm.getAllImmidiatePrimitiveConcepts(c1, true);
       assertEquals(2, immidiatePrimitive.size());
       assertTrue(immidiatePrimitive.contains(c2));
       assertTrue(immidiatePrimitive.contains(c4));
   }

   /*
    * Test case 6:
    * ------------
    * {1,2,3,4} is primitive
    * 1 isKindOf 2
    * 2 isKindOf 3
    * 3 isKindof 4
    * 1 isKindOf 3
    * 1 isKindOf 4
    * 
    * should return for concept 1:
    * 1 isKindOf {2}
    */
   @Test
   public void shouldPassImmidiatePrimitiveTestCase6() {
       c1.setPrimitive(true);
       c2.setPrimitive(true);
       c3.setPrimitive(true);
       c4.setPrimitive(true);
       
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       c3.addKindOf(c4);
       c1.addKindOf(c3);
       c1.addKindOf(c4);
       
       Set<Concept> immidiatePrimitive = algorithm.getAllImmidiatePrimitiveConcepts(c1, true);
       assertEquals(1, immidiatePrimitive.size());
       assertTrue(immidiatePrimitive.contains(c2));
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
   @Test
   public void shouldPassUnsharedDefiningCharacteristicsTestCase1() throws IOException{
       importer.populateDb(ClassLoader.getSystemResourceAsStream("UnsharedDefiningCharacteristics/test_concepts.txt"),
               ClassLoader.getSystemResourceAsStream("UnsharedDefiningCharacteristics/test_case_1_relationships.txt"), main.em);

       Concept pc1 = main.em.find(Concept.class, (long)1);
       Concept pc2 = main.em.find(Concept.class, (long)2);
       Concept pc3 = main.em.find(Concept.class, (long)3);
       
       assertTrue(algorithm.getAllUnsharedDefiningCharacteristics(pc1, true).isEmpty());
       assertTrue(algorithm.getAllUnsharedDefiningCharacteristics(pc3, true).isEmpty());
       Set<RelationshipStatement> allUnsharedDefiningCharacteristicsC2 = algorithm.getAllUnsharedDefiningCharacteristics(pc2, true);
       assertEquals(1, allUnsharedDefiningCharacteristicsC2.size());
       assertTrue(allUnsharedDefiningCharacteristicsC2.contains(new RelationshipStatement(102)));
   }
   
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
   @Test
   public void shouldPassUnsharedDefiningCharacteristicsTestCase2() throws IOException{
       importer.populateDb(ClassLoader.getSystemResourceAsStream("UnsharedDefiningCharacteristics/test_concepts.txt"),
               ClassLoader.getSystemResourceAsStream("UnsharedDefiningCharacteristics/test_case_2_relationships.txt"), main.em);

       Concept pc1 = main.em.find(Concept.class, (long)1);
       Concept pc2 = main.em.find(Concept.class, (long)2);
       Concept pc3 = main.em.find(Concept.class, (long)3);
       Concept pc4 = main.em.find(Concept.class, (long)4);
       
       assertTrue(algorithm.getAllUnsharedDefiningCharacteristics(pc1, true).isEmpty());
       assertTrue(algorithm.getAllUnsharedDefiningCharacteristics(pc2, true).isEmpty());
       assertTrue(algorithm.getAllUnsharedDefiningCharacteristics(pc4, true).isEmpty());
       Set<RelationshipStatement> allUnsharedDefiningCharacteristicsC3 = algorithm.getAllUnsharedDefiningCharacteristics(pc3, true);
       assertEquals(1, allUnsharedDefiningCharacteristicsC3.size());
       assertTrue(allUnsharedDefiningCharacteristicsC3.contains(new RelationshipStatement(104)));
   }
   
   /*
    * Test case 1:
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
   @Test
   public void shouldPassUnsharedDefiningCharacteristicsTestCase3() throws IOException{
       importer.populateDb(ClassLoader.getSystemResourceAsStream("UnsharedDefiningCharacteristics/test_concepts.txt"),
               ClassLoader.getSystemResourceAsStream("UnsharedDefiningCharacteristics/test_case_3_relationships.txt"), main.em);

       Concept pc1 = main.em.find(Concept.class, (long)1);
       Concept pc2 = main.em.find(Concept.class, (long)2);
       Concept pc3 = main.em.find(Concept.class, (long)2);
       
       assertTrue(algorithm.getAllUnsharedDefiningCharacteristics(pc1, true).isEmpty());
       assertTrue(algorithm.getAllUnsharedDefiningCharacteristics(pc2, true).isEmpty());
       assertTrue(algorithm.getAllUnsharedDefiningCharacteristics(pc3, true).isEmpty());
   }   
}
