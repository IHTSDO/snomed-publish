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
        Concept c1 = new Concept();
        c1.setId((long)100523006);
        Concept c2 = new Concept();
        c2.setId((long)100523007);

        Concept c3 = new Concept();
        c3.setId((long)100523008);
        Concept c4 = new Concept();
        c4.setId((long)100523009);

        RelationshipStatement r1 = new RelationshipStatement();
        r1.setId(0);
        r1.setSubject(c1);
        r1.setRelationshipType((long)116680003);
        r1.setObject(c2);
        r1.setRelationShipGroup(0);

        RelationshipStatement r2 = new RelationshipStatement();
        r2.setId(1);
        r2.setSubject(c3);
        r2.setRelationshipType((long)116680004);
        r2.setObject(c4);
        r2.setRelationShipGroup(1);

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
