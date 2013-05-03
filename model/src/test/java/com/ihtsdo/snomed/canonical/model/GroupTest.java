package com.ihtsdo.snomed.canonical.model;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.service.InvalidInputException;

public class GroupTest{

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldReturnEqual1() {
        Concept subject1 = new Concept(1);
        Concept subject2 = new Concept(2);
        Concept predicate1 = new Concept(3);
        Concept predicate2 = new Concept(4);
        Concept predicate3 = new Concept(5);
        Concept object1 = new Concept(6);
        Concept object2 = new Concept(7);
        Concept object3 = new Concept(8);
        
        Statement s1 = new Statement(101, subject1, predicate1, object1);
        Statement s2 = new Statement(102, subject1, predicate2, object2);
        Statement s3 = new Statement(103, subject1, predicate3, object3);
        Statement s4 = new Statement(104, subject2, predicate1, object1);
        Statement s5 = new Statement(105, subject2, predicate2, object2);
        Statement s6 = new Statement(106, subject2, predicate3, object3);
        
        Group group1 = new Group(s1);
        group1.addStatement(s2);
        group1.addStatement(s3);

        Group group2 = new Group(s4);
        group2.addStatement(s5);
        group2.addStatement(s6);

        assertTrue(group1.equals(group2));        
    }
    
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionWhenSettingWrongStatementInGroup() throws InvalidInputException{
        Statement s1 = new Statement(123);
        Statement s2 = new Statement(1234);
        s1.setSubject(new Concept(1));
        s2.setSubject(new Concept(2));
        s1.setPredicate(new Concept(3));
        s2.setPredicate(new Concept(4));
        
        new Group(Arrays.asList(s1, s2));
    }
    
    @Test
    public void shouldSetWrappedStatements(){
        Statement s1 = new Statement(123);
        s1.setPredicate(new Concept(1));
        Group group = new Group(s1);
        
        assertEquals(1, group.statements.size());
        assertEquals(s1, group.statements.iterator().next().statement);
    }
    
    @Test
    public void shouldEqualStatementWrapperForAttributeCompare(){
        Statement s1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED);
        Statement s2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED);
        s1.setObject(new Concept(1));
        s2.setObject(new Concept(1));
        s1.setPredicate(new Concept(2));
        s2.setPredicate(new Concept(2));
        
        Group.StatementWrapperForAttributeCompare w1 = new Group.StatementWrapperForAttributeCompare(s1);
        Group.StatementWrapperForAttributeCompare w2 = new Group.StatementWrapperForAttributeCompare(s2);
        
        assertEquals(w1, w2);
    }
    
    @Test
    public void shouldNotEqualStatementWrapperForAttributeCompare1(){
        Statement s1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED);
        Statement s2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED);
        s1.setObject(new Concept(1));
        s2.setObject(new Concept(1));
        s1.setPredicate(new Concept(3));
        s2.setPredicate(new Concept(2));
        
        Group.StatementWrapperForAttributeCompare w1 = new Group.StatementWrapperForAttributeCompare(s1);
        Group.StatementWrapperForAttributeCompare w2 = new Group.StatementWrapperForAttributeCompare(s2);
        
        assertNotEquals(w1, w2);
    }
    
    @Test
    public void shouldNotEqualStatementWrapperForAttributeCompare2(){
        Statement s1 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED);
        Statement s2 = new Statement(Statement.SERIALISED_ID_NOT_DEFINED);
        s1.setObject(new Concept(3));
        s2.setObject(new Concept(1));
        s1.setPredicate(new Concept(2));
        s2.setPredicate(new Concept(2));
        
        Group.StatementWrapperForAttributeCompare w1 = new Group.StatementWrapperForAttributeCompare(s1);
        Group.StatementWrapperForAttributeCompare w2 = new Group.StatementWrapperForAttributeCompare(s2);
        
        assertNotEquals(w1, w2);
    }    

}
