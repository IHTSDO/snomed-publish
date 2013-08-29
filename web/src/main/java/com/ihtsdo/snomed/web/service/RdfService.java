package com.ihtsdo.snomed.web.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.jena.RdfSchemaSerialiser;

@Named
@Transactional (value = "transactionManager", readOnly = true)
public class RdfService {

    @Inject
    private RdfSchemaSerialiser rdfSchemaSerialiser;
    
    public void writeConcept(Concept c, OutputStreamWriter ow, Ontology o) throws IOException, ParseException{
        rdfSchemaSerialiser.printHeader(ow);
        rdfSchemaSerialiser.writeConcept(ow, o, c);
        rdfSchemaSerialiser.printFooter(ow);
    }
    
    public void writeStatement(Statement s, OutputStreamWriter ow, Ontology o) throws IOException, ParseException{
        rdfSchemaSerialiser.printHeader(ow);
        rdfSchemaSerialiser.writeStatement(ow, o, s);
        rdfSchemaSerialiser.printFooter(ow);
    }
    
    public void writeDescription(Description d, OutputStreamWriter ow, Ontology o) throws IOException, ParseException{
        rdfSchemaSerialiser.printHeader(ow);
        rdfSchemaSerialiser.writeDescription(ow, o, d);
        rdfSchemaSerialiser.printFooter(ow);
    }    
}
