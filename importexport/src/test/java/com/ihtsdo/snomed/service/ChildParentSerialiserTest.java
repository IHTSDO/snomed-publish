package com.ihtsdo.snomed.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.SerialiserFactory.Form;

public class ChildParentSerialiserTest {

    private static final String EXPECTED_RESULT = 
                    "1\t2\r\n" +
                    "1\t3\r\n" +
                    "1\t4\r\n" +
                    "2\t3\r\n" +
                    "2\t4\r\n" +
                    "3\t4\r\n";
    
    private Collection<Statement> statements = new HashSet<Statement>();
    
    @Before
    public void setUp() throws Exception {
        Concept c1 = new Concept(1);
        Concept c2 = new Concept(2);
        Concept c3 = new Concept(3);
        Concept c4 = new Concept(4);
        Concept cp = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        
        statements.addAll(Arrays.asList(
                new Statement(0, c1, cp, c2),
                new Statement(1, c2, cp, c3),
                new Statement(2, c3, cp, c4),
                new Statement(3, c1, cp, c3),
                new Statement(4, c1, cp, c4),
                new Statement(6, c2, cp, c4)                
                ));
    }

    @Test
    public void shouldPrintOntologyInSpecifiedFormat() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            SerialiserFactory.getSerialiser(Form.CHILD_PARENT, pw).write(statements);
        }
        baos.flush();
        assertEquals(EXPECTED_RESULT, baos.toString());
    }
}
