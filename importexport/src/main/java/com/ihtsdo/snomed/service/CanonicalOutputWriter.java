package com.ihtsdo.snomed.service;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.canonical.model.Statement;

public class CanonicalOutputWriter {

    protected static final char DELIMITER = '\t';
    private static final Logger LOG = LoggerFactory.getLogger( CanonicalOutputWriter.class );
    
    public void write (Writer w, Collection<Statement> statements) throws IOException{
        printHeading(w);
        Iterator<Statement> rIt = statements.iterator();
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

    protected void printRelationship(Writer w, Statement r) throws IOException{
        w.write(Long.toString(r.getSubject().getSerialisedId())
                + DELIMITER + Long.toString(r.getPredicate().getSerialisedId())
                + DELIMITER + Long.toString(r.getObject().getSerialisedId())
                + DELIMITER + Integer.toString(r.getGroup()));
    }
}
