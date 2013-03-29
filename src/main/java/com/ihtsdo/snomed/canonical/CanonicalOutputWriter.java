package com.ihtsdo.snomed.canonical;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class CanonicalOutputWriter {

    private static final char DELIMITER = '\t';

    public static void write (Writer w, Collection<RelationshipStatement> statements) throws IOException{
        printHeading(w);
        Iterator<RelationshipStatement> rIt = statements.iterator();
        while (rIt.hasNext()){
            w.write("\n");
            printRelationship(w, rIt.next());
        }
    }

    private static void printHeading(Writer w) throws IOException{
        w.write("CONCEPTID1" + DELIMITER + "RELATIONSHIPTYPE" + DELIMITER +
                "CONCEPTID2" + DELIMITER + "RELATIONSHIPGROUP");
    }

    private static void printRelationship(Writer w, RelationshipStatement r) throws IOException{
        w.write(Long.toString(r.getSubject().getId())
                + DELIMITER + Long.toString(r.getRelationshipType())
                + DELIMITER + Long.toString(r.getObject().getId())
                + DELIMITER + Integer.toString(r.getRelationShipGroup()));
    }
}
