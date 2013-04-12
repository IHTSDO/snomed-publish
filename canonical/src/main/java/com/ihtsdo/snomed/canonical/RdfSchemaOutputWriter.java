package com.ihtsdo.snomed.canonical;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class RdfSchemaOutputWriter {

    protected static final char DELIMITER = '\t';
    private static final Logger LOG = LoggerFactory.getLogger( RdfSchemaOutputWriter.class );
    
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
        w.write("@prefix :     <http://ihtsdo.org/snomed#> .\n" +
                "@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n");
    }

    protected void printRelationship(Writer w, RelationshipStatement r) throws IOException{
        if (r.isKindOfRelationship()){
            w.write("");
        }
    }
}
