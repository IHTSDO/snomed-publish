package com.ihtsdo.snomed.canonical.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class ConceptTest {
    
    Concept c1,c2,c3,c4;

    @Before
    public void setUp() throws Exception {
        c1 = new Concept(1);
        c2 = new Concept(2);
        c3 = new Concept(3);
        c4 = new Concept(4);
        
        c1.setPrimitive(true);
        c2.setPrimitive(true);
        c3.setPrimitive(true);
        c4.setPrimitive(true);
    }

    /*
     * Test case 1:
     * ------------
     * {1,2,3} is primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 
     * All primitive supertypes for concept 1: 
     * {2,3}
     */
    @Test
    public void shouldPassTestCase1_WithCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(true);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
    }

    @Test
    public void shouldPassTestCase1_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(false);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
    } 
    
    /*
     * Test case 2:
     * ------------
     * {1,3} is primitive, {2} is not primitive
     * 1 isKindOf 2
     * 2 isKindOf 3
     * 
     * All primitive supertypes for concept 1: 
     * {3}
     */ 
    @Test
    public void shouldPassTestCase2_WithCache() {
        c2.setPrimitive(false);
        
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(true);
        assertEquals(1, allPrimitive.size());
        assertTrue(allPrimitive.contains(c3));
    }
    @Test
    public void shouldPassTestCase2_WithoutCache() {
        c2.setPrimitive(false);
        
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        
        Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(false);
        assertEquals(1, allPrimitive.size());
        assertTrue(allPrimitive.contains(c3));
    }
    
    /*
    * Test case 3:
    * ------------
    * {1,4} is primitive, {2,3} is not primitive
    * 1 isKindOf 2
    * 2 isKindOf 3
    * 3 isKindOf 4
    * 
    * All primitive supertypes for concept 1: 
    * {4}
    */ 
   @Test
   public void shouldPassTestCase3_WithCache() {
       c2.setPrimitive(false);
       c3.setPrimitive(false);
       
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       c3.addKindOf(c4);
       
       Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(true);
       assertEquals(1, allPrimitive.size());
       assertTrue(allPrimitive.contains(c4));
   }    
    
   @Test
   public void shouldPassTestCase3_WithoutCache() {
       c2.setPrimitive(false);
       c3.setPrimitive(false);
       
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       c3.addKindOf(c4);
       
       Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(false);
       assertEquals(1, allPrimitive.size());
       assertTrue(allPrimitive.contains(c4));
   }      
   
   
   /*
    * Test case 4:
    * ------------
    * {1,2,3,4} is primitive
    * 1 isKindOf 2
    * 2 isKindOf 3
    * 3 isKindOf 4 
    * 
    * All primitive supertypes for concept 1: 
    * {2,3,4}
    */
   @Test
   public void shouldPassTestCase4_WithCache() {
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       c3.addKindOf(c4);
       
       Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(true);
       assertEquals(3, allPrimitive.size());
       assertTrue(allPrimitive.contains(c2));
       assertTrue(allPrimitive.contains(c3));
       assertTrue(allPrimitive.contains(c4));
   } 
   
   @Test
   public void shouldPassTestCase4_WithoutCache() {
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       c3.addKindOf(c4);
       
       Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(false);
       assertEquals(3, allPrimitive.size());
       assertTrue(allPrimitive.contains(c2));
       assertTrue(allPrimitive.contains(c3));
       assertTrue(allPrimitive.contains(c4));
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
    * All primitive supertypes for concept 1: 
    * {2,3,4}
    */
   @Test
   public void shouldPassTestCase5_WithCache() {
       c1.addKindOf(c2);
       c1.addKindOf(c3);
       c2.addKindOf(c4);
       c3.addKindOf(c4);
       
       Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(true);
       assertEquals(3, allPrimitive.size());
       assertTrue(allPrimitive.contains(c2));
       assertTrue(allPrimitive.contains(c3));
       assertTrue(allPrimitive.contains(c4));
   }
   
   @Test
   public void shouldPassTestCase5_WithoutCache() {
       c1.addKindOf(c2);
       c1.addKindOf(c3);
       c2.addKindOf(c4);
       c3.addKindOf(c4);
       
       Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(false);
       assertEquals(3, allPrimitive.size());
       assertTrue(allPrimitive.contains(c2));
       assertTrue(allPrimitive.contains(c3));
       assertTrue(allPrimitive.contains(c4));
   }   
   
   /*
    * Test case 6:
    * ------------
    * {1,2,4} is primitive, {3} is not primitive
    * 1 isKindOf 2
    * 2 isKindOf 3
    * 3 isKindof 4
    * 
    * All primitive supertypes for concept 1: 
    * {2,4}
    */
   @Test
   public void shouldPassTestCase6_WithCache() {
       c3.setPrimitive(false);
       
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       c3.addKindOf(c4);
       
       Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(true);
       assertEquals(2, allPrimitive.size());
       assertTrue(allPrimitive.contains(c2));
       assertTrue(allPrimitive.contains(c4));
   }
   
   @Test
   public void shouldPassTestCase6_WithoutCache() {
       c3.setPrimitive(false);
       
       c1.addKindOf(c2);
       c2.addKindOf(c3);
       c3.addKindOf(c4);
       
       Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(false);
       assertEquals(2, allPrimitive.size());
       assertTrue(allPrimitive.contains(c2));
       assertTrue(allPrimitive.contains(c4));
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
   * All primitive supertypes for concept 1: 
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
      
      Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(true);
      assertEquals(1, allPrimitive.size());
      assertTrue(allPrimitive.contains(c4));
  }   
   
  @Test
  public void shouldPassTestCase7_WithoutCache() {
      c2.setPrimitive(false);
      c3.setPrimitive(false);
      
      c1.addKindOf(c2);
      c2.addKindOf(c3);
      c3.addKindOf(c4);
      c1.addKindOf(c4);
      
      Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(false);
      assertEquals(1, allPrimitive.size());
      assertTrue(allPrimitive.contains(c4));
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
  * All primitive supertypes for concept 1: 
  * {2,4}
  */
 @Test
 public void shouldPassTestCase8_WithCache() {
     c3.setPrimitive(false);
     
     c1.addKindOf(c2);
     c1.addKindOf(c3);
     c2.addKindOf(c4);
     c3.addKindOf(c4);
     
     Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(true);
     assertEquals(2, allPrimitive.size());
     assertTrue(allPrimitive.contains(c4));
     assertTrue(allPrimitive.contains(c2));
 }   
  
 @Test
 public void shouldPassTestCase8_WithoutCache() {
     c3.setPrimitive(false);
     
     c1.addKindOf(c2);
     c1.addKindOf(c3);
     c2.addKindOf(c4);
     c3.addKindOf(c4);
     
     Set<Concept> allPrimitive = c1.getKindOfProximalConcepts(false);
     assertEquals(2, allPrimitive.size());
     assertTrue(allPrimitive.contains(c4));
     assertTrue(allPrimitive.contains(c2));
 }    
  
//    
//    /*
//     * Test case 1:
//     * {c1,c3,c5} are primitives, {c2,c4} are not primitives
//     * 
//     * c1 isKindOf c2
//     * c2 isKindOf c3
//     * c3 isKinddOf c4
//     * c4 isKindOf c5
//     * 
//     * should return for c1:
//     * c1 has primitive supertypes {c3,c5}
//     * 
//     */
//    private void setupTestCase1() {
//        c1.setPrimitive(true);
//        c2.setPrimitive(false);
//        c3.setPrimitive(true);
//        c4.setPrimitive(false);
//        c5.setPrimitive(true);
//        
//        c1.addKindOf(c2);
//        c2.addKindOf(c3);
//        c3.addKindOf(c4);
//        c4.addKindOf(c5);
//    } 
//    
//    @Test
//    public void shouldPassTestCase1WithCache() {
//        setupTestCase1();
//        Set<Concept> results = c1.getKindOfPrimitiveConcepts(true);
//        assertEquals(2, results.size());
//        assertTrue(results.contains(c3));
//        assertTrue(results.contains(c5));
//    }
//    
//    @Test
//    public void shouldPassTestCase1WithoutCache() {
//        setupTestCase1();
//        Set<Concept> results = c1.getKindOfPrimitiveConcepts(false);
//        assertEquals(2, results.size());
//        assertTrue(results.contains(c3));
//        assertTrue(results.contains(c5));
//    }

   

}
