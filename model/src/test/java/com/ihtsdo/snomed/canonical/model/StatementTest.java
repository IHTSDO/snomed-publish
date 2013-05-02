package com.ihtsdo.snomed.canonical.model;

import org.junit.Before;
import static org.junit.Assert.*;
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
    
    
    
}
