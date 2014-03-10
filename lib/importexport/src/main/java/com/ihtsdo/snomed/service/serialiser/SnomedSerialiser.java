package com.ihtsdo.snomed.service.serialiser;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.Statement;

public interface SnomedSerialiser {
    
    public static final String OPTIONS_RDF_INCLUDE_ISA_STATEMENT = "OPTIONS_RDF_INCLUDE_ISA_STATEMENT";
    
    public abstract SnomedSerialiser header() throws IOException;
    public abstract SnomedSerialiser footer() throws IOException;
    public abstract void write (OntologyVersion o, Collection<Statement> statements) throws IOException, ParseException;
    public abstract void write (Collection<Statement> statements) throws IOException, ParseException;
    public abstract void write(Statement statement) throws IOException, ParseException;
    public abstract void write(Concept c) throws IOException, ParseException;
    public abstract void write(Description d) throws IOException, ParseException;
    void write(OntologyVersion o) throws IOException, ParseException;
    public abstract SnomedSerialiser property(String key, Object value);
    
}
