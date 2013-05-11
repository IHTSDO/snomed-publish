package com.ihtsdo.snomed.canonical.test;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.canonical.test.model.StatementForCompareWrapper;
import com.ihtsdo.snomed.model.Statement;

public class CanonicalWrappedStatementWriter{
    Writer writer;
    protected static final char DELIMITER = '\t';
    
    public CanonicalWrappedStatementWriter(Writer writer) throws IOException {
        this.writer = writer;
    }

    private static final Logger LOG = LoggerFactory.getLogger( CanonicalWrappedStatementWriter.class );

    public void writeCompareStatements (Collection<StatementForCompareWrapper> statements) throws IOException{
        Iterator<StatementForCompareWrapper> rIt = statements.iterator();
        int counter = 1;
        while (rIt.hasNext()){
            write(rIt.next().getStatement());
            counter++;
        }
        LOG.info("Wrote " + counter + " lines");
    }
    

    public void write(Statement r) throws IOException{
        writer.write(Long.toString(r.getSubject().getSerialisedId())
                + DELIMITER + Long.toString(r.getPredicate().getSerialisedId())
                + DELIMITER + Long.toString(r.getObject().getSerialisedId())
                + DELIMITER + Integer.toString(r.getGroupId()) + "\r\n");
    }

}
