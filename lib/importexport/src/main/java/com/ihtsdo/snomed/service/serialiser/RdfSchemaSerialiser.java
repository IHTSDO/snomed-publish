package com.ihtsdo.snomed.service.serialiser;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.Statement;

public class RdfSchemaSerialiser extends BaseSnomedSerialiser{
    private final Logger LOG = LoggerFactory.getLogger(RdfSchemaSerialiser.class);
    
    private final String DEFAULT_SNOMED_BASE_URI = "http://sct.snomed.info/";
    private final String DEFAULT_SNOMED_NS = "http://sct.snomed.info/#";

    private final String NS_ONTOLOGY_VARIABLE = "__ontologyVersion_id__";
    private final String NS_CONCEPT = DEFAULT_SNOMED_BASE_URI;
    private final String NS_TRIPLE = DEFAULT_SNOMED_BASE_URI;
    private final String NS_DESCRIPTION = DEFAULT_SNOMED_BASE_URI;
    
    /*properties*/
    private final String EFFECTIVE_TIME = "effectiveTime";

    private final String ACTIVE = "active";
    
    private final String STATUS = "status";
    
    private final String MODULE = "module";
    
    private final String GROUP = "group";
    
    private final String CHARACTERISTIC_TYPE = "characteristicType";
    
    private final String MODIFIER = "modifier";
    
    private final String DESCRIPTION = "description";
    
    private final String CASE_SIGNIFICANCE = "caseSignificance";
    
    private final String TYPE = "type";
    
    
    private SimpleDateFormat longTimeParser = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");
    
    
    private Set<Concept> parsedConcepts  = new HashSet<>();
    
    private boolean isParsed(Concept c){
        return parsedConcepts.contains(c);
    }
    
    private Concept parsed(Concept c){
        parsedConcepts.add(c);
        return c;
    }
    
    RdfSchemaSerialiser(Writer writer) throws IOException{
        super(writer);
    }
    
    @Override
    public void write(OntologyVersion o, Collection<Statement> statements) throws IOException, ParseException {
        LOG.debug("Exporting to RDF/XML");
        Stopwatch stopwatch = new Stopwatch().start();

        printBody(o, statements);

        footer();
        stopwatch.stop();
        LOG.info("Completed RDF Schema export in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");          
    }

    @Override
    public void write(Statement statement) throws IOException, ParseException {
        write(statement.getOntologyVersion(), statement);
    }

    @Override
    public SnomedSerialiser header() throws IOException {
        writer.write("<rdf:RDF\n");
        writer.write(" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
        writer.write(" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n");
        writer.write(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n");
        writer.write(" xmlns:sn=\""+ DEFAULT_SNOMED_NS +"\"\n");
        writer.write(" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n");
        writer.write(">\n");
        printStructural();
        return this;
    }

    @Override
    public SnomedSerialiser footer() throws IOException{
        writer.write("</rdf:RDF>\n");
        return this;
    }

    public void printStructural() throws IOException{
        //EffectiveTime
    	printProperty(EFFECTIVE_TIME);
        //writer.write("<rdf:Description rdf:about=\"+ DEFAULT_SNOMED_BASE_URI + "effectiveTime\>\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">EffectiveTime</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");

        //Active
    	printProperty(ACTIVE);
//        writer.write("<rdf:Description rdf:about=\"http://snomed.info/sct/term/active\">\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">Active</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");

        //Status
    	printProperty(STATUS);

//        writer.write("<rdf:Description rdf:about=\"http://snomed.info/sct/term/status\">\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">Status</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");
        
        //Module
    	printProperty(MODULE);

//        writer.write("<rdf:Description rdf:about=\"http://snomed.info/sct/term/module\">\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">Module</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");
        
        //Group
    	printProperty(GROUP);

//        writer.write("<rdf:Description rdf:about=\"http://snomed.info/sct/term/group\">\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">Group</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");

        //CharacteristicType
    	printProperty(CHARACTERISTIC_TYPE);

//        writer.write("<rdf:Description rdf:about=\"http://snomed.info/sct/term/characteristictype\">\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">CharacteristicType</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");
        
        //Modifier
    	printProperty(MODIFIER);

//        writer.write("<rdf:Description rdf:about=\"http://snomed.info/sct/term/modifier\">\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">Modifier</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");
        
        //Description
    	printProperty(DESCRIPTION);

//        writer.write("<rdf:Description rdf:about=\"http://snomed.info/sct/term/description\">\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">Description</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");
        
        //CaseSignificance
    	printProperty(CASE_SIGNIFICANCE);

//        writer.write("<rdf:Description rdf:about=\"http://snomed.info/sct/term/casesignificance\">\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">CaseSignificance</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");
        
        //Type
    	printProperty(TYPE);

//        writer.write("<rdf:Description rdf:about=\"http://snomed.info/sct/term/type\">\n");
//        writer.write("<rdfs:label xml:lang=\"en-gb\">Type</rdfs:label>\n");
//        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
//        writer.write("</rdf:Description>\n");


        
    }
    
    private void printProperty(String propertyName) throws IOException {
    	
    	writer.write("<rdf:Description rdf:about=\""+ DEFAULT_SNOMED_BASE_URI + propertyName + "\">\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">"+ StringUtils.capitalize(propertyName)   + "</rdfs:label>\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        writer.write("</rdf:Description>\n");
    }

    private void printBody(OntologyVersion o, Collection<Statement> statements) throws IOException, ParseException{
        int counter = 1;
        
        for (Statement s : statements){
            if (o == null){
                write(s);
            }else{
                write(o, s);
            }
            counter++;
            if (counter % 10000 == 0){
                LOG.info("Processed {} statements", counter);
            }
        }
    }

    private void write(OntologyVersion o, Statement s) throws IOException, ParseException {
        parse(o, s.getSubject());
        parse(o, s.getObject());
        parse(o, s.getPredicate());
        writeStatement(o, s);
    }

    private void parse(OntologyVersion o, Concept c) throws IOException,
            ParseException {
        if (!isParsed(c)){
            writeConcept(o, parsed(c));
            if (c.getDescription() != null){
	            for (Description d : c.getDescription()){
	                writeDescription(o, d);
	            }
            }
                        
        }
    }

    protected void writeConcept(OntologyVersion o, Concept c)
            throws IOException, ParseException {
        writer.write("<rdf:Description rdf:about=\"" + getConceptName(c.getSerialisedId(), o) + "\">\n");
        if (c.isPredicate()){
            writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        }
        else{
            writer.write("<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n");
        }
        writer.write("<rdfs:label xml:lang=\"en-gb\">" + StringEscapeUtils.escapeXml(c.getFullySpecifiedName()) + "</rdfs:label>\n");
        if (c.getModule() != null) writer.write("<sn:module rdf:resource=\"" + getConceptName(c.getModule().getSerialisedId(), o) + "\"/>\n");
        if (c.getStatus() != null) writer.write("<sn:status rdf:resource=\"" + getConceptName(c.getStatus().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + c.isActive() + "</sn:active>\n");
                
        if (c.getEffectiveTime() != 0) writer.write("<sn:effectiveTime rdf:datatype=\"xsd:date\">" + 
            dateTimeFormatter.format(longTimeParser.parse(String.valueOf(c.getEffectiveTime()))) + 
            "</sn:effectiveTime>\n");
        
        if ((c.getDescription() != null) && !c.getDescription().isEmpty()){
	        for (Description d : c.getDescription()){
	            writer.write("<sn:description rdf:resource=\"" + getDescriptionName(d.getSerialisedId(), o) + "\"/>\n");
	        }
    	}
        if (c.isPredicate()){
            for (Concept p : c.getKindOfs()){
                writer.write("<rdfs:subPropertyOf rdf:resource=\"" + getConceptName(p.getSerialisedId(), o) + "\" />\n");
            }
        }else{
            for (Concept p : c.getKindOfs()){
                writer.write("<rdfs:subClassOf rdf:resource=\"" + getConceptName(p.getSerialisedId(), o) + "\" />\n");
            }            
        }
        writer.write("</rdf:Description>\n");
    }

    protected void writeDescription(OntologyVersion o, Description d) throws IOException, ParseException {
        writer.write("<rdf:Description rdf:about=\"" + getDescriptionName(d.getSerialisedId(), o) + "\">\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n");
        writer.write("<sn:module rdf:resource=\"" + getConceptName(d.getModule().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:type rdf:resource=\"" + getConceptName(d.getType().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:casesignificance rdf:resource=\"" + getConceptName(d.getCaseSignificance().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + d.isActive() + "</sn:active>\n");
        
        writer.write("<sn:effectiveTime rdf:datatype=\"xsd:date\">" + 
                dateTimeFormatter.format(longTimeParser.parse(String.valueOf(d.getEffectiveTime()))) + 
                "</sn:effectiveTime>\n");
        writer.write("<rdfs:label xml:lang=\"en-gb\">" + StringEscapeUtils.escapeXml(d.getTerm()) + "</rdfs:label>\n");
        
        writer.write("</rdf:Description>\n");
    }

    protected void writeStatement(OntologyVersion o, Statement s) throws IOException, ParseException {
        if ( ! isTrue(OPTIONS_RDF_INCLUDE_ISA_STATEMENT)){
            //modelled as subProperty and SubClass. See writeConcept. But you do lose all the 
            //reification data for the isA statements (!)
            return;
        }
        writer.write("<rdf:Description rdf:about=\"" + getTripleName(s.getSerialisedId(), o) + "\">\n");
        writer.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement\"/>\n");
        writer.write("<rdf:subject rdf:resource=\"" + getConceptName(s.getSubject().getSerialisedId(), o) + "\"/>\n");
        writer.write("<rdf:predicate rdf:resource=\"" + getConceptName(s.getPredicate().getSerialisedId(), o) + "\"/>\n");
        writer.write("<rdf:object rdf:resource=\"" + getConceptName(s.getObject().getSerialisedId(), o) + "\"/>\n");
        if (s.getModifier() != null) writer.write("<sn:modifier rdf:resource=\"" + getConceptName(s.getModifier().getSerialisedId(), o) + "\"/>\n");
        if (s.getModule() != null) writer.write("<sn:module rdf:resource=\"" + getConceptName(s.getModule().getSerialisedId(), o) + "\"/>\n");
        if (s.getCharacteristicType() != null) writer.write("<sn:characteristictype rdf:resource=\"" + getConceptName(s.getCharacteristicType().getSerialisedId(), o) + "\"/>\n");
        writer.write("<sn:group rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">" + s.getGroupId() + "</sn:group>\n");
        writer.write("<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + s.isActive() + "</sn:active>\n");
        
        if (s.getEffectiveTime() != 0) writer.write("<sn:effectiveTime rdf:datatype=\"xsd:date\">" + 
                dateTimeFormatter.format(longTimeParser.parse(String.valueOf(s.getEffectiveTime()))) + 
                "</sn:effectiveTime>\n");
        
        writer.write("</rdf:Description>\n");
    }
        

    private String getDescriptionName(long id, OntologyVersion o){
        return NS_DESCRIPTION.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + id;
    }

    private String getConceptName(long id, OntologyVersion o){
        return NS_CONCEPT.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + id;
    }
    
    private String getTripleName(long id, OntologyVersion o) {
        return NS_TRIPLE.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + id;
    }

	@Override
	public void write(Concept c) throws IOException, ParseException {
		writeConcept(c.getOntologyVersion(), c);
		
	}

	@Override
	public void write(Description d) throws IOException, ParseException {
		writeDescription(d.getOntologyVersion(), d);
		
	}


}
