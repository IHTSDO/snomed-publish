package com.ihtsdo.snomed.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StatementTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldSetStatementInConcept() {
        Concept s = new Concept(1);
        Concept p = new Concept(2);
        Concept o = new Concept(3);
        
        Statement st = new Statement(1, s, p, o);
        
        assertTrue(s.getSubjectOfStatements().contains(st));
        assertTrue(p.getPredicateOfStatements().contains(st));
        assertTrue(o.getObjectOfStatements().contains(st));
    }
    
    @Test
    public void shouldSetIsKindOfRelationship(){
        Concept s = new Concept(1);
        Concept p = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o = new Concept(3);
        
        Statement st = new Statement(1, s, p, o);
        assertTrue(st.isKindOfStatement());
    }
    
    @Test
    public void shouldSetIsKindOfRelationshipFail(){
        Concept s = new Concept(1);
        Concept p = new Concept(2);
        Concept o = new Concept(3);
        
        Statement st = new Statement(1, s, p, o);
        assertTrue(!st.isKindOfStatement());
    }    

    @Test
    public void shouldReturnIsDefiningCharacteristicType(){
        Concept s = new Concept(1);
        Concept p = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o = new Concept(3);
        
        Statement st = new Statement(1, s, p, o, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);
        assertTrue(st.isDefiningCharacteristic());
    }
    
    @Test
    public void shouldReturnIsDefiningCharacteristicTypeFail(){
        Concept s = new Concept(1);
        Concept p = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o = new Concept(3);
        
        Statement st = new Statement(1, s, p, o, 2, 0);
        assertTrue(!st.isDefiningCharacteristic());
    }   
    
    @Test
    public void shouldReturnIsMemberOfGroup(){
        Concept s = new Concept(1);
        Concept p = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o = new Concept(3);
        
        Statement st = new Statement(1, s, p, o, 2, 1);
        assertTrue(st.isMemberOfGroup());
    }
    
    @Test
    public void shouldReturnIsMemberOfGroupFail(){
        Concept s = new Concept(1);
        Concept p = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o = new Concept(3);
        
        Statement st = new Statement(1, s, p, o, 2, 0);
        assertTrue(!st.isMemberOfGroup());
    } 
    
    @Test
    public void shouldReturnEqualStatementOnSerialisedId(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept o1 = new Concept(3);
        Statement st1 = new Statement(1, s1, p1, o1);
        
        Concept s2 = new Concept(1);
        Concept p2 = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Concept o2 = new Concept(3);
        Statement st2 = new Statement(1, s2, p2, o2);
        
        assertEquals(st1, st2);
    }
    
    @Test
    public void shouldReturnEqualStatementOnMissingSerialisedId(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept o1 = new Concept(3);
        Statement st1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o1);
        Statement st2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o1);
        
        assertEquals(st1, st2);
    }    
    
    @Test
    public void shouldReturnEqualStatementOnMissingSerialisedIdFail1(){
        Concept s1 = new Concept(1);
        Concept s2 = new Concept(2);
        Concept p1 = new Concept(3);
        Concept o1 = new Concept(4);
        Statement st1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o1);
        Statement st2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s2, p1, o1);
        
        assertNotEquals(st1, st2);
    }      
    
    @Test
    public void shouldReturnEqualStatementOnMissingSerialisedIdFail2(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept o1 = new Concept(3);
        Concept o2 = new Concept(4);
        Statement st1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o1);
        Statement st2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o2);
        
        assertNotEquals(st1, st2);
    }    
    
    @Test
    public void shouldReturnEqualStatementOnMissingSerialisedIdFail3(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept p2 = new Concept(3);
        Concept o1 = new Concept(4);
        Statement st1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o1);
        Statement st2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p2, o1);
        
        assertNotEquals(st1, st2);
    }    
    
    @Test
    public void shouldHaveEqualHashCode1(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept p2 = new Concept(3);
        Concept o1 = new Concept(4);
        Statement st1 = new Statement(1, s1, p1, o1);
        Statement st2 = new Statement(1, s1, p2, o1);
        
        assertEquals(st1.hashCode(), st2.hashCode());
    }

    @Test
    public void shouldHaveEqualHashCode1Fail(){
        Concept s1 = new Concept(1);
        Concept s2 = new Concept(11);
        Concept p1 = new Concept(2);
        Concept p2 = new Concept(3);
        Concept o1 = new Concept(4);
        Statement st1 = new Statement(1, s1, p1, o1);
        Statement st2 = new Statement(2, s2, p2, o1);
        
        assertNotEquals(st1.hashCode(), st2.hashCode());
    }    
    
    @Test
    public void shouldHaveEqualHashCode2(){
        Concept s1 = new Concept(1);
        Concept p1 = new Concept(2);
        Concept p2 = new Concept(3);
        Concept o1 = new Concept(4);
        Statement st1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p1, o1);
        Statement st2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED, s1, p2, o1);
        
        assertEquals(st1.hashCode(), st2.hashCode());
    }  
    
    @Test
    public void shouldHaveEqualHashCode2Fail(){
        Concept s1 = new Concept(1);
        Concept s2 = new Concept(11);
        Concept p1 = new Concept(2);
        Concept p2 = new Concept(3);
        Concept o1 = new Concept(4);
        Statement st1 = new Statement(1, s1, p1, o1);
        Statement st2 = new Statement(2, s2, p2, o1);
        
        assertNotEquals(st1.hashCode(), st2.hashCode());
    }    
    
}
