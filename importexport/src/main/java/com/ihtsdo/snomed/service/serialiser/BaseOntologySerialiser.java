package com.ihtsdo.snomed.service.serialiser;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import com.ihtsdo.snomed.model.Statement;

public abstract class BaseOntologySerialiser {
    protected static final char DELIMITER = '\t';
    
    protected Writer writer;
    
    public BaseOntologySerialiser(Writer writer) throws IOException{
        this.writer = writer;
        writeHeader();
    }

    public abstract void write (Collection<Statement> statements) throws IOException;
    
    public abstract void write(Statement statement) throws IOException;
    
    protected abstract void writeHeader() throws IOException;

}
