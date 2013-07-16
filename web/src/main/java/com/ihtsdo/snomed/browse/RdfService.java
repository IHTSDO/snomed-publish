package com.ihtsdo.snomed.browse;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.inject.Inject;
import javax.inject.Named;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.jena.RdfSchemaSerialiser;

@Named
public class RdfService {

    @Inject
    private RdfSchemaSerialiser rdfSchemaSerialiser;
    
    public void writeConcept(Concept c, OutputStreamWriter ow, Ontology o) throws IOException{
        rdfSchemaSerialiser.printHeader(ow);
        rdfSchemaSerialiser.writeConcept(ow, o, c);
        rdfSchemaSerialiser.printFooter(ow);
    }
    
    public void writeStatement(Statement s, OutputStreamWriter ow, Ontology o) throws IOException{
        rdfSchemaSerialiser.printHeader(ow);
        rdfSchemaSerialiser.writeStatement(ow, o, s);
        rdfSchemaSerialiser.printFooter(ow);
    }
    
    public void writeDescription(Description d, OutputStreamWriter ow, Ontology o) throws IOException{
        rdfSchemaSerialiser.printHeader(ow);
        rdfSchemaSerialiser.writeDescription(ow, o, d);
        rdfSchemaSerialiser.printFooter(ow);
    }    
}
