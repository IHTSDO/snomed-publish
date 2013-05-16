package com.ihtsdo.snomed.service.serialiser;

import java.io.IOException;
import java.util.Collection;

import com.ihtsdo.snomed.model.Statement;

public interface OntologySerialiser {

    public abstract void write (Collection<Statement> statements) throws IOException;
    
    public abstract void write(Statement statement) throws IOException;
    
}
