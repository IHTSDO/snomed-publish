package com.ihtsdo.snomed.service.serialiser;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory.Form;

public class CanonicalSerialiserTest {

    private static final String EXPECTED_RESULT =
            "CONCEPTID1\tRELATIONSHIPTYPE\tCONCEPTID2\tRELATIONSHIPGROUP\r\n" +
            "100523006\t116680003\t100523007\t0\r\n" +
            "100523008\t116680004\t100523009\t1\r\n";
    
    private Collection<Statement> statements = new HashSet<Statement>();
    
    @Before
    public void setUp() throws Exception {
        Concept c1 = new Concept(100523006l);
        Concept c2 = new Concept(100523007);
        Concept c3 = new Concept(100523008l);
        Concept c4 = new Concept(100523009l);
        
        Concept cp1 = new Concept(116680003l);
        Concept cp2 = new Concept(116680004l);

        Statement r1 = new Statement(0, c1, cp1, c2, Statement.DEFINING_CHARACTERISTIC_TYPE, 0);
        Statement r2 = new Statement(1, c3, cp2, c4, Statement.DEFINING_CHARACTERISTIC_TYPE, 1);

        statements.add(r1);
        statements.add(r2);
    }

    @Test
    public void shouldPrintOntologyInSpecifiedFormat() throws IOException, ParseException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            SnomedSerialiserFactory.getSerialiser(Form.CANONICAL, pw).write(statements);
        }
        baos.flush();
        assertEquals(EXPECTED_RESULT, baos.toString());
    }
}
