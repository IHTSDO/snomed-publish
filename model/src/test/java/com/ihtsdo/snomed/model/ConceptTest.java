package com.ihtsdo.snomed.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Group;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.InvalidInputException;

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
     * 
     * All supertypes for concept 1:
     * {2,3} 
     */
    @Test
    public void shouldPassPrimitiveParentTestCase1_WithCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
    }

    @Test
    public void shouldPassPrimitiveParentTestCase1_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(false);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
    } 
    
    @Test
    public void shouldPassParentTestCase1_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(false);
        assertEquals(2, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
    }     
    
    @Test
    public void shouldPassParentTestCase1_WithCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(true);
        assertEquals(2, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
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
     * 
     * All supertypes for concept 1:
     * {2,3}
     */ 
    @Test
    public void shouldPassPrimitiveParentTestCase2_WithCache() {
        c2.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(1, allPrimitive.size());
        assertTrue(allPrimitive.contains(c3));
    }
    @Test
    public void shouldPassPrimitiveParentTestCase2_WithoutCache() {
        c2.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(false);
        assertEquals(1, allPrimitive.size());
        assertTrue(allPrimitive.contains(c3));
    }
    
    @Test
    public void shouldPassParentTestCase2_WithCache() {
        c2.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(true);
        assertEquals(2, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
    }
    @Test
    public void shouldPassParentTestCase2_WithoutCache() {
        c2.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(false);
        assertEquals(2, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
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
     * 
     * All supertypes for concept 1:
     * {2,3,4}
     */ 
    @Test
    public void shouldPassPrimitiveParentTestCase3_WithCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(1, allPrimitive.size());
        assertTrue(allPrimitive.contains(c4));
    }    

    @Test
    public void shouldPassPrimitiveParentTestCase3_WithoutCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(false);
        assertEquals(1, allPrimitive.size());
        assertTrue(allPrimitive.contains(c4));
    }      
    
    @Test
    public void shouldPassParentTestCase3_WithCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(true);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c4));
    }    

    @Test
    public void shouldPassPrimitiveTestCase3_WithoutCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(false);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c4));
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
     * 
     * All supertypes for concept 1: 
     * {2,3,4}
     */
    @Test
    public void shouldPassPrimitiveParentTestCase4_WithCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(3, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
        assertTrue(allPrimitive.contains(c4));
    } 

    @Test
    public void shouldPassPrimitiveParentTestCase4_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(false);
        assertEquals(3, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
        assertTrue(allPrimitive.contains(c4));
    } 
    
    @Test
    public void shouldPassParentTestCase4_WithCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(true);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c4));
    } 

    @Test
    public void shouldPassParentTestCase4_WithoutCache() {
        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(false);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c4));
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
     * 
     * All supertypes for concept 1: 
     * {2,3,4}
     */
    @Test
    public void shouldPassPrimitiveParentTestCase5_WithCache() {
        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(3, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
        assertTrue(allPrimitive.contains(c4));
    }

    @Test
    public void shouldPassPrimitiveParentTestCase5_WithoutCache() {
        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(false);
        assertEquals(3, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c3));
        assertTrue(allPrimitive.contains(c4));
    }   
    
    @Test
    public void shouldPassParentTestCase5_WithCache() {
        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(true);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c4));
    }

    @Test
    public void shouldPassParentTestCase5_WithoutCache() {
        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(false);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c4));
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
     * 
     * All supertypes for concept 1: 
     * {2,3,4}
     */
    @Test
    public void shouldPassPrimitiveParentTestCase6_WithCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c4));
    }

    @Test
    public void shouldPassPrimitiveParentTestCase6_WithoutCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(false);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c2));
        assertTrue(allPrimitive.contains(c4));
    }  
    
    @Test
    public void shouldPassParentTestCase6_WithCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(true);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c4));
    }

    @Test
    public void shouldPassParentTestCase6_WithoutCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(false);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c4));
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
     * 
     * All supertypes for concept 1: 
     * {2,3,4}
     */
    @Test
    public void shouldPassPrimitiveParentTestCase7_WithCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);
        c1.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(1, allPrimitive.size());
        assertTrue(allPrimitive.contains(c4));
    }   

    @Test
    public void shouldPassPrimitiveParentTestCase7_WithoutCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);
        c1.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(false);
        assertEquals(1, allPrimitive.size());
        assertTrue(allPrimitive.contains(c4));
    }   
    
    @Test
    public void shouldPassParentTestCase7_WithCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);
        c1.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(true);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c4));
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
    }   

    @Test
    public void shouldPassParentTestCase7_WithoutCache() {
        c2.setPrimitive(false);
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c2.addKindOf(c3);
        c3.addKindOf(c4);
        c1.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(false);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c4));
        assertTrue(allKindOfs.contains(c2));
        assertTrue(allKindOfs.contains(c3));
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
     * 
     * All supertypes for concept 1: 
     * {2,3,4}
     */
    @Test
    public void shouldPassPrimitiveParentTestCase8_WithCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(true);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c4));
        assertTrue(allPrimitive.contains(c2));
    }   

    @Test
    public void shouldPassPrimitiveParentTestCase8_WithoutCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> allPrimitive = c1.getAllKindOfPrimitiveConcepts(false);
        assertEquals(2, allPrimitive.size());
        assertTrue(allPrimitive.contains(c4));
        assertTrue(allPrimitive.contains(c2));
    }    
    @Test
    public void shouldPassParentTestCase8_WithCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(true);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c4));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c2));
    }   

    @Test
    public void shouldPassParentTestCase8_WithoutCache() {
        c3.setPrimitive(false);

        c1.addKindOf(c2);
        c1.addKindOf(c3);
        c2.addKindOf(c4);
        c3.addKindOf(c4);

        Set<Concept> allKindOfs = c1.getAllKindOfConcepts(false);
        assertEquals(3, allKindOfs.size());
        assertTrue(allKindOfs.contains(c4));
        assertTrue(allKindOfs.contains(c3));
        assertTrue(allKindOfs.contains(c2));
    }
    
    
    

    @Test
    public void shouldReturnTrueForIsKindOfPredicate(){
        assertTrue(new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID).isKindOfPredicate());
    }
    
    @Test
    public void shouldReturnTrueForIsPredicate(){
        Concept c = new Concept(123);
        c.setType(Concept.ATTRIBUTE);
        assertTrue(c.isPredicate());
    }
    
    @Test
    public void shouldBeEqualConcepts(){
        Concept c1 = new Concept(123);
        Concept c2 = new Concept(123);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }
    
    @Test
    public void shouldBeUnequalConcepts(){
        Concept c1 = new Concept(123);
        Concept c2 = new Concept(1234);
        assertNotEquals(c1, c2);
        assertNotEquals(c1.hashCode(), c2.hashCode());
    }
    
    @Test
    public void shouldCreateGroupFromStatement() throws InvalidInputException{
        Concept c = new Concept(123);
        Concept p = new Concept(999);
        Concept o = new Concept(666);
        Statement s = new Statement(444);
        s.setSubject(c);
        s.setPredicate(p);
        s.setObject(o);
        s.setGroupId(5);
        c.addSubjectOfStatement(s);
        Group g = new Group(s);
        assertTrue(g.equals(c.getGroup(s)));
    }
    
    @Test
    public void shouldGetGroupFromStatement() throws InvalidInputException{
        Concept c = new Concept(123);
        Concept p = new Concept(999);
        Concept o = new Concept(666);
        Statement s = new Statement(444);
        s.setSubject(c);
        s.setPredicate(p);
        s.setObject(o);
        s.setGroupId(5);
        c.addSubjectOfStatement(s);
        Group g = new Group(s);
        //create group
        assertTrue(g.equals(c.getGroup(s)));
        //get group
        assertTrue(g.equals(c.getGroup(s)));
    }
    
    @Test
    public void shouldGetCorrectGroupFromMultipleGroupsTrue() throws InvalidInputException{
        Concept c = new Concept(123);
        
        Statement s = new Statement(444);
        s.setSubject(c);
        s.setPredicate(new Concept(999));
        s.setObject(new Concept(666));
        s.setGroupId(5);

        Statement s1 = new Statement(444);
        s1.setSubject(c);
        s1.setPredicate(new Concept(9991));
        s1.setObject(new Concept(6661));
        s1.setGroupId(2);

        c.addSubjectOfStatement(s);
        c.addSubjectOfStatement(s1);
        
        assertTrue(new Group(s).equals(c.getGroup(s)));
    }

    @Test
    public void shouldGetCorrectGroupFromMultipleStatementsTrue() throws InvalidInputException{
        Concept c = new Concept(123);
        
        Statement s = new Statement(444);
        s.setSubject(c);
        s.setPredicate(new Concept(999));
        s.setObject(new Concept(666));
        s.setGroupId(2);

        Statement s1 = new Statement(4441);
        s1.setSubject(c);
        s1.setPredicate(new Concept(9991));
        s1.setObject(new Concept(6661));
        s1.setGroupId(2);

        c.addSubjectOfStatement(s);
        c.addSubjectOfStatement(s1);
        
        assertTrue(new Group(Arrays.asList(s1, s)).equals(c.getGroup(s)));
    }
    
    @Test
    public void shouldGetCorrectGroupFromMultipleStatementsFalse() throws InvalidInputException{
        Concept c = new Concept(123);
        
        Statement s = new Statement(444);
        s.setSubject(c);
        s.setPredicate(new Concept(999));
        s.setObject(new Concept(666));
        s.setGroupId(2);

        Statement s1 = new Statement(4441);
        s1.setSubject(c);
        s1.setPredicate(new Concept(9991));
        s1.setObject(new Concept(6661));
        s1.setGroupId(2);

        c.addSubjectOfStatement(s);
        c.addSubjectOfStatement(s1);
        
        assertTrue(!new Group(Arrays.asList(s1)).equals(c.getGroup(s)));
    }    
    
    @Test
    public void shouldGetCorrectGroupFromMultipleGroupsFalse() throws InvalidInputException{
        Concept c = new Concept(123);

        Statement s = new Statement(444);
        s.setSubject(c);
        s.setPredicate(new Concept(999));
        s.setObject(new Concept(666));
        s.setGroupId(5);

        Statement s1 = new Statement(444);
        s1.setSubject(c);
        s1.setPredicate(new Concept(9991));
        s1.setObject(new Concept(6661));
        s1.setGroupId(2);

        c.addSubjectOfStatement(s);
        c.addSubjectOfStatement(s1);

        assertTrue(!new Group(s1).equals(c.getGroup(s)));
    }
    
    

}
