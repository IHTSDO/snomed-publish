package com.ihtsdo.snomed.canonical.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GroupTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldReturnEqual() {
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

}
