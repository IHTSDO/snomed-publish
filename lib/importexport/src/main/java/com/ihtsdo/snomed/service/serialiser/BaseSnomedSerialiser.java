package com.ihtsdo.snomed.service.serialiser;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;

abstract class BaseSnomedSerialiser implements SnomedSerialiser{
    protected static final char DELIMITER = '\t';
    
    protected Writer writer;
    protected Map<String, Object> properties = new HashMap<>();
    
    private void defaultValues(){
        properties.put(SnomedSerialiser.OPTIONS_RDF_INCLUDE_ISA_STATEMENT, true);
    }

    @Override
    public void write(Ontology o) throws IOException, ParseException {
        write (o, o.getStatements());
    }
    
    public void write (Collection<Statement> statements) throws IOException, ParseException{
        write(null, statements);
    }
    
    BaseSnomedSerialiser(Writer writer) throws IOException{
        defaultValues();
        this.writer = writer;
        header();
    }
    
    @Override
    public SnomedSerialiser property(String key, Object value){
        properties.put(key, value);
        return this;
    }

    protected boolean isTrue(String property){
        Object value = properties.get(property) ;
        return (value != null) && (value instanceof Boolean) && (boolean)value;
    }    
    
}
