package com.ihtsdo.snomed.canonical;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class CanonicalOutputWriterTest {

    private static final String EXPECTED_RESULT =
            "CONCEPTID1\tRELATIONSHIPTYPE\tCONCEPTID2\tRELATIONSHIPGROUP\r\n" +
            "100523006\t116680003\t100523007\t0\r\n" +
            "100523008\t116680004\t100523009\t1";
    
    private Collection<RelationshipStatement> statements = new HashSet<RelationshipStatement>();
    private CanonicalOutputWriter writer = new CanonicalOutputWriter();
    
    @Before
    public void setUp() throws Exception {
        Concept c1 = new Concept(100523006l);
        Concept c2 = new Concept(100523007);
        Concept c3 = new Concept(100523008l);
        Concept c4 = new Concept(100523009l);

        RelationshipStatement r1 = new RelationshipStatement(0, c1, 116680003l, c2, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE, 0);
        RelationshipStatement r2 = new RelationshipStatement(1, c3, 116680004l, c4, RelationshipStatement.DEFINING_CHARACTERISTIC_TYPE, 1);

        statements.add(r1);
        statements.add(r2);
    }

    @Test
    public void shouldPrintOntologyInSpecifiedFormat() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            writer.write(pw, statements);
        }
        baos.flush();
        assertEquals(EXPECTED_RESULT, baos.toString());
    }
}
