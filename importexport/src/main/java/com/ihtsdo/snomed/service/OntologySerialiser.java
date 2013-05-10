package com.ihtsdo.snomed.service;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import com.ihtsdo.snomed.model.Statement;

public abstract class OntologySerialiser {
    
    protected Writer writer;
    
    public OntologySerialiser(Writer writer) throws IOException{
        this.writer = writer;
        writeHeader();
    }

    public abstract void write (Collection<Statement> statements) throws IOException;
    
    public abstract void write(Statement statement) throws IOException;
    
    protected abstract void writeHeader() throws IOException;

}
