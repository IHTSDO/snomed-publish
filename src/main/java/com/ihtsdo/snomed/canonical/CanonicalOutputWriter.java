package com.ihtsdo.snomed.canonical;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class CanonicalOutputWriter {

    protected static final char DELIMITER = '\t';
    private static final Logger LOG = LoggerFactory.getLogger( CanonicalOutputWriter.class );
    
    public void write (Writer w, Collection<RelationshipStatement> statements) throws IOException{
        printHeading(w);
        Iterator<RelationshipStatement> rIt = statements.iterator();
        int counter = 2;
        while (rIt.hasNext()){
            w.write("\r\n");
            printRelationship(w, rIt.next());
            counter++;
        }
        LOG.info("Wrote " + counter + " lines");
    }

    protected void printHeading(Writer w) throws IOException{
        w.write("CONCEPTID1" + DELIMITER + "RELATIONSHIPTYPE" + DELIMITER +
                "CONCEPTID2" + DELIMITER + "RELATIONSHIPGROUP");
    }

    protected void printRelationship(Writer w, RelationshipStatement r) throws IOException{
        w.write(Long.toString(r.getSubject().getId())
                + DELIMITER + Long.toString(r.getRelationshipType())
                + DELIMITER + Long.toString(r.getObject().getId())
                + DELIMITER + Integer.toString(r.getRelationShipGroup()));
    }
}
