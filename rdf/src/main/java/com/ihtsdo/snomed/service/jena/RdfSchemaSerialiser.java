package com.ihtsdo.snomed.service.jena;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;

@Named
public class RdfSchemaSerialiser{
    private final Logger LOG = LoggerFactory.getLogger(RdfSchemaSerialiser.class);
    
    private final String NS_ONTOLOGY_VARIABLE = "__ONTOLOGY_ID__";
    private final String NS_CONCEPT = "http://browser.sparklingideas.co.uk/ontology/" + NS_ONTOLOGY_VARIABLE + "/concept/rdfs/";
    private final String NS_TRIPLE = "http://browser.sparklingideas.co.uk/ontology/" + NS_ONTOLOGY_VARIABLE + "/statement/rdfs/";
    private final String NS_DESCRIPTION = "http://browser.sparklingideas.co.uk/ontology/" + NS_ONTOLOGY_VARIABLE + "/description/rdfs/";
    
    private SimpleDateFormat longTimeParser = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public void exportToRdfXml(Ontology o, EntityManager em, OutputStreamWriter ow) throws IOException, ParseException{
        LOG.info("Exporting ontology '{}' to RDF/XML", o.getName());
        Stopwatch stopwatch = new Stopwatch().start();
        printHeader(ow);
        printStructural(ow);
        printBody(ow, em, o);
        printFooter(ow);
        stopwatch.stop();
        LOG.info("Completed RDF Schema export in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");        
    }
    
    public void printHeader(OutputStreamWriter ow) throws IOException{
        ow.write("<rdf:RDF\n");
        ow.write(" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
        ow.write(" xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n");
        ow.write(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n");
        ow.write(" xmlns:sn=\"http://browser.sparklingideas.co.uk/term/\"\n");
        ow.write(" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n");
        ow.write(">\n");
    }
    
    public void printFooter(OutputStreamWriter ow) throws IOException{
        ow.write("</rdf:RDF>\n");
    }
    
    public void printStructural(OutputStreamWriter ow) throws IOException{
        //EffectiveTime
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/effectiveTime\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">EffectiveTime</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
        
        //Active
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/active\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">Active</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
        
        //Status
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/status\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">Status</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
        
        //Module
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/module\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">Module</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
        
        //Group
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/group\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">Group</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
        
        //CharacteristicType
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/characteristictype\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">CharacteristicType</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
        
        //Modifier
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/modifier\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">Modifier</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
        
        //Description
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/description\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">Description</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
        
        //CaseSignificance
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/casesignificance\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">CaseSignificance</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
        
        //Type
        ow.write("<rdf:Description rdf:about=\"http://browser.sparklingideas.co.uk/term/type\">\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">Type</rdfs:label>\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        ow.write("</rdf:Description>\n");
    }

    public void printBody(OutputStreamWriter ow, EntityManager em, Ontology o) throws IOException, ParseException{
        LOG.info("Loading concepts");
        Stopwatch stopwatch = new Stopwatch().start();
        TypedQuery<Concept> conceptsQuery = em.createQuery("SELECT c FROM Concept c " +
                //"LEFT JOIN FETCH c.subjectOfStatements " +
                //"LEFT JOIN FETCH c.predicateOfStatements " + 
                //"LEFT JOIN FETCH c.kindOfs " + 
                //"LEFT JOIN FETCH c.objectOfStatements " + 
                //"LEFT JOIN FETCH c.parentOf " +
                "LEFT JOIN FETCH c.description " 
                //"WHERE c.ontology.id=:ontologyId", 
                ,Concept.class);
        //conceptsQuery.setParameter("ontologyId", o.getId());
        List<Concept> concepts = conceptsQuery.getResultList();
        LOG.info("Loaded " + concepts.size() + " concepts in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        
        int counter = 1;
        for (Concept c : concepts){
            
            //Concept
            writeConcept(ow, o, c);
            
            //Descriptions
            for (Description d : c.getDescription()){
                writeDescription(ow, o, d);
            }
            
            //Statements
            for (Statement s : c.getSubjectOfStatements()){
                writeStatement(ow, o, s);
            }
            
            counter++;
            if (counter % 10000 == 0){
                LOG.info("Processed {} concepts", counter);
            }
        }
    }

    public void writeConcept(OutputStreamWriter ow, Ontology o, Concept c)
            throws IOException, ParseException {
        ow.write("<rdf:Description rdf:about=\"" + getConceptName(c.getSerialisedId(), o) + "\">\n");
        if (c.isPredicate()){
            ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Property\"/>\n");
        }
        else{
            ow.write("<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n");
        }
        ow.write("<rdfs:label xml:lang=\"en-gb\">" + StringEscapeUtils.escapeXml(c.getFullySpecifiedName()) + "</rdfs:label>\n");
        ow.write("<sn:module rdf:resource=\"" + getConceptName(c.getModule().getSerialisedId(), o) + "\"/>\n");
        ow.write("<sn:status rdf:resource=\"" + getConceptName(c.getStatus().getSerialisedId(), o) + "\"/>\n");
        ow.write("<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + c.isActive() + "</sn:active>\n");
                
        ow.write("<sn:effectiveTime rdf:datatype=\"xsd:date\">" + 
            dateTimeFormatter.format(longTimeParser.parse(String.valueOf(c.getEffectiveTime()))) + 
            "</sn:effectiveTime>\n");
        
        for (Description d : c.getDescription()){
            ow.write("<sn:description rdf:resource=\"" + getDescriptionName(d.getSerialisedId(), o) + "\"/>\n");
        }
        if (c.isPredicate()){
            for (Concept p : c.getKindOfs()){
                ow.write("<rdfs:subPropertyOf rdf:resource=\"" + getConceptName(p.getSerialisedId(), o) + "\" />\n");
            }
        }else{
            for (Concept p : c.getKindOfs()){
                ow.write("<rdfs:subClassOf rdf:resource=\"" + getConceptName(p.getSerialisedId(), o) + "\" />\n");
            }            
        }
        ow.write("</rdf:Description>\n");
    }

    public void writeDescription(OutputStreamWriter ow, Ontology o, 
            Description d) throws IOException, ParseException {
        ow.write("<rdf:Description rdf:about=\"" + getDescriptionName(d.getSerialisedId(), o) + "\">\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>\n");
        ow.write("<sn:module rdf:resource=\"" + getConceptName(d.getModule().getSerialisedId(), o) + "\"/>\n");
        ow.write("<sn:type rdf:resource=\"" + getConceptName(d.getType().getSerialisedId(), o) + "\"/>\n");
        ow.write("<sn:casesignificance rdf:resource=\"" + getConceptName(d.getCaseSignificance().getSerialisedId(), o) + "\"/>\n");
        ow.write("<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + d.isActive() + "</sn:active>\n");
        
        ow.write("<sn:effectiveTime rdf:datatype=\"xsd:date\">" + 
                dateTimeFormatter.format(longTimeParser.parse(String.valueOf(d.getEffectiveTime()))) + 
                "</sn:effectiveTime>\n");
        ow.write("<rdfs:label xml:lang=\"en-gb\">" + StringEscapeUtils.escapeXml(d.getTerm()) + "</rdfs:label>\n");
        
        ow.write("</rdf:Description>\n");
    }

    public void writeStatement(OutputStreamWriter ow, Ontology o, Statement s) throws IOException, ParseException {
        ow.write("<rdf:Description rdf:about=\"" + getTripleName(s.getSerialisedId(), o) + "\">\n");
        ow.write("<rdf:type rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement\"/>\n");
        ow.write("<rdf:subject rdf:resource=\"" + getConceptName(s.getSubject().getSerialisedId(), o) + "\"/>\n");
        ow.write("<rdf:predicate rdf:resource=\"" + getConceptName(s.getPredicate().getSerialisedId(), o) + "\"/>\n");
        ow.write("<rdf:object rdf:resource=\"" + getConceptName(s.getObject().getSerialisedId(), o) + "\"/>\n");
        ow.write("<sn:modifier rdf:resource=\"" + getConceptName(s.getModifier().getSerialisedId(), o) + "\"/>\n");
        ow.write("<sn:module rdf:resource=\"" + getConceptName(s.getModule().getSerialisedId(), o) + "\"/>\n");
        ow.write("<sn:characteristictype rdf:resource=\"" + getConceptName(s.getCharacteristicType().getSerialisedId(), o) + "\"/>\n");
        ow.write("<sn:group rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">" + s.getGroupId() + "</sn:group>\n");
        ow.write("<sn:active rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">" + s.isActive() + "</sn:active>\n");
        
        ow.write("<sn:effectiveTime rdf:datatype=\"xsd:date\">" + 
                dateTimeFormatter.format(longTimeParser.parse(String.valueOf(s.getEffectiveTime()))) + 
                "</sn:effectiveTime>\n");
        
        ow.write("</rdf:Description>\n");
    }
        

    private String getDescriptionName(long id, Ontology o){
        return NS_DESCRIPTION.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + id;
    }

    private String getConceptName(long id, Ontology o){
        return NS_CONCEPT.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + id;
    }
    
    private String getTripleName(long id, Ontology o) {
        return NS_TRIPLE.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + id;
    }   
}
