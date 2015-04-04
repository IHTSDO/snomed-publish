package com.ihtsdo.snomed.service.serialiser;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.Statement;

public class SnapshotSnomedSerialiser extends BaseSnomedSerialiser{
    private static final String LINE_ENDING = " .\n";

    private final Logger LOG = LoggerFactory.getLogger(SnapshotSnomedSerialiser.class);
    
    private static final String LANGUAGE = "@en-gb";
    
    private final static String NS_SNOMED_BASE = "http://sct.snomed.info#";
    private final static String DEFAULT_SNOMED_BASE_URI = "http://sct.snomed.info/";


    private final static String GRAPH_NAME = NS_SNOMED_BASE + "graph#snomed";
    
    private static final String NS_XML_SCHEMA_DATATYPE = "http://www.w3.org/2001/XMLSchema#";
    private static final String NS_XML_SCHEMA_DATATYPE_IDENTIFIER = "xsd";
    private static final String XML_SCHEMA_DATATYPE_BOOLEAN = "^^" + NS_XML_SCHEMA_DATATYPE_IDENTIFIER + ":boolean";
    private static final String XML_SCHEMA_DATATYPE_DATE = "^^" + NS_XML_SCHEMA_DATATYPE_IDENTIFIER + ":date";
    private static final String XML_SCHEMA_DATATYPE_INTEGER = "^^" + NS_XML_SCHEMA_DATATYPE_IDENTIFIER + ":int";
    
    private final static String NS_SNOMED_TERM = NS_SNOMED_BASE;
    private final static String NS_SNOMED_TERM_IDENTIFIER = "sn";
    
    private final static String NS_SNOMED_CONCEPT = NS_SNOMED_BASE;
    private final static String NS_SNOMED_CONCEPT_IDENTIFIER = "c";
    
    private final static String NS_SNOMED_DESCRIPTION = NS_SNOMED_BASE;
    private final static String NS_SNOMED_DESCRIPTION_IDENTIFIER = "d";
    
    private final static String NS_SNOMED_STATEMENT = NS_SNOMED_BASE;
    private final static String NS_SNOMED_STATEMENT_IDENTIFIER = "s";    
    
    private final static String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static String NS_RDF_IDENTIFIER = "rdf";
    
    private final static String NS_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    private final static String NS_RDFS_IDENTIFIER = "rdfs";
    
    private final static String PROPERTY_RDFS_LABEL = NS_RDFS_IDENTIFIER + ":label";
    private final static String PROPERTY_RDF_TYPE = NS_RDF_IDENTIFIER + ":type";
    private final static String CLASS_RDF_STATEMENT = NS_RDF_IDENTIFIER + ":Statement";
    private final static String PROPERTY_RDF_SUBJECT = NS_RDF_IDENTIFIER + ":subject";
    private final static String PROPERTY_RDF_PREDICATE = NS_RDF_IDENTIFIER + ":predicate";
    private final static String PROPERTY_RDF_OBJECT = NS_RDF_IDENTIFIER + ":object";
    
    
    private final static String PROPERTY_SNOMED_STATUS = NS_SNOMED_TERM_IDENTIFIER + ":status";
    private final static String PROPERTY_SNOMED_ACTIVE = NS_SNOMED_TERM_IDENTIFIER + ":active";
    private final static String PROPERTY_SNOMED_GROUP = NS_SNOMED_TERM_IDENTIFIER + ":group";
    private final static String PROPERTY_SNOMED_MODULE = NS_SNOMED_TERM_IDENTIFIER + ":module";
    private final static String PROPERTY_SNOMED_MODIFIER = NS_SNOMED_TERM_IDENTIFIER + ":modifier";
    private final static String PROPERTY_SNOMED_CHARACTERISTIC_TYPE = NS_SNOMED_TERM_IDENTIFIER + ":characteristicType";
    private final static String PROPERTY_SNOMED_DESCRIPTION = NS_SNOMED_TERM_IDENTIFIER + ":description";
    private final static String PROPERTY_SNOMED_EFFECTIVE_TIME = NS_SNOMED_TERM_IDENTIFIER + ":effectiveTime";
    private final static String PROPERTY_SNOMED_CASE_SIGNIFICANCE = NS_SNOMED_TERM_IDENTIFIER + ":caseSignificance";
    private final static String PROPERTY_SNOMED_DESCRIPTION_TYPE = NS_SNOMED_TERM_IDENTIFIER + ":descriptionType";
    //private final static String PROPERTY_SNOMED_TRIPLE_HASH = NS_SNOMED_TERM_IDENTIFIER + ":tripleHash";
    private final static String PROPERTY_SNOMED_HISTORY_ENTRY = NS_SNOMED_TERM_IDENTIFIER + ":historyEntry";
        
    private SimpleDateFormat longTimeParser = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");
    

    SnapshotSnomedSerialiser(Writer writer) throws IOException{
        super(writer);
    }
    
    @Override
    public void write(OntologyVersion o, Collection<Statement> statements) throws IOException, ParseException {
        LOG.debug("Exporting to Snapshot Snomed Model in TriG format. \nGraph name is <" + GRAPH_NAME + ">");
        Stopwatch stopwatch = new Stopwatch().start();

        int counter = 1;
        for (Concept c : o.getConcepts()){
            parse(c);
            counter++;
            if (counter % 10000 == 0){
                LOG.info("Processed {} concepts", counter);
            }            
        }
        
        counter = 1;
        for (Statement s : statements){
            write(s);
            counter++;
            if (counter % 10000 == 0){
                LOG.info("Processed {} statements", counter);
            }
        }
        footer();

        stopwatch.stop();
        LOG.info("Completed Meta Snomed export in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");          
    }

    @Override
    public SnomedSerialiser header() throws IOException {
        writer.write("@prefix " + NS_SNOMED_TERM_IDENTIFIER + ": <" + NS_SNOMED_TERM + ">" + LINE_ENDING);
        writer.write("@prefix " + NS_SNOMED_CONCEPT_IDENTIFIER + ": <" + NS_SNOMED_CONCEPT + ">" + LINE_ENDING);
        writer.write("@prefix " + NS_SNOMED_DESCRIPTION_IDENTIFIER + ": <" + NS_SNOMED_DESCRIPTION + ">" + LINE_ENDING);
        writer.write("@prefix " + NS_SNOMED_STATEMENT_IDENTIFIER + ": <" + NS_SNOMED_STATEMENT + ">" + LINE_ENDING);
        writer.write("@prefix " + NS_RDF_IDENTIFIER + ": <" + NS_RDF + ">" + LINE_ENDING);
        writer.write("@prefix " + NS_XML_SCHEMA_DATATYPE_IDENTIFIER + ": <" + NS_XML_SCHEMA_DATATYPE + ">" + LINE_ENDING);
        writer.write("@prefix " + NS_RDFS_IDENTIFIER + ": <" + NS_RDFS + ">" + LINE_ENDING);
        writer.write("<" + GRAPH_NAME + "> {\n");
        return this;
    }

    @Override
    public SnomedSerialiser footer() throws IOException{
        writer.write("}");
        return this;
    }
    
    private void parse(Concept c) throws IOException, ParseException {
        write(c);
        if (c.getDescription() != null){
            for (Description d : c.getDescription()){
                write(d);
            }
        }
    }
    
    private String safe(final String label){
        return label.replace('\"', '\'');
    }

//    private void writeTripleHash(final Statement s, final String id) throws IOException{
//        
//        writer.write(id + ' ' + PROPERTY_SNOMED_TRIPLE_HASH + " \"" + 
//                s.getSubject().getSerialisedId() + 
//                s.getPredicate().getSerialisedId() + 
//                s.getObject().getSerialisedId() + 
//                '\"' + LINE_ENDING);        
//    }

    @Override
    public void write(final Concept c) throws IOException, ParseException {    
        final String id = NS_SNOMED_CONCEPT_IDENTIFIER + ':' + c.getSerialisedId();
        
        //Master
        writeDatatypeProperties (c, id);
        
        //Description
        if ((c.getDescription() != null) && !c.getDescription().isEmpty()){
            for (Description d : c.getDescription()){
                writer.write(id + ' ' + PROPERTY_SNOMED_DESCRIPTION + ' ' +
                        NS_SNOMED_DESCRIPTION_IDENTIFIER + ':' + d.getSerialisedId() +
                        LINE_ENDING);
            }
        }
        
        //History
        if ((c.getHistory() != null) && !c.getHistory().isEmpty()){
            int counter = 1;
            for (Concept hc : c.getHistory()){
                String hid = id + "_h" + counter++;
                
                writeDatatypeProperties (hc, hid);
                
                //History Entry
                writer.write(id + ' ' + PROPERTY_SNOMED_HISTORY_ENTRY + ' ' + hid + LINE_ENDING);              
            }
        }
    }
    
	
	public void writeDatatypeProperties(final Concept c, String id) throws IOException, ParseException {
	    //Label
	    writer.write(id + ' ' + PROPERTY_RDFS_LABEL + 
	            " \"" + safe(c.getFullySpecifiedName()) + '\"' + LANGUAGE + 
	            LINE_ENDING);
	    
	    //Active
	    writer.write(id + ' ' + PROPERTY_SNOMED_ACTIVE + 
	            " \"" + (c.isActive() == true ? "true" : "false") + '\"' + XML_SCHEMA_DATATYPE_BOOLEAN +
	            LINE_ENDING);
	    
	    //Module
	    if (c.getModule() != null){
	        writer.write(id + ' ' + PROPERTY_SNOMED_MODULE + ' ' + 
	                NS_SNOMED_CONCEPT_IDENTIFIER + ':' + c.getModule().getSerialisedId() +
	                LINE_ENDING);
	    }
	    
        //Status
        if (c.getStatus() != null){
            writer.write(id + ' ' + PROPERTY_SNOMED_STATUS + ' ' + 
                    NS_SNOMED_CONCEPT_IDENTIFIER + ':' + c.getStatus().getSerialisedId() +
                    LINE_ENDING);
        }	    
        
        //Effective Time
        writer.write(id + ' ' + PROPERTY_SNOMED_EFFECTIVE_TIME + " \"" + 
                dateTimeFormatter.format(longTimeParser.parse(String.valueOf(c.getEffectiveTime()))) 
                + '\"' + XML_SCHEMA_DATATYPE_DATE +
                LINE_ENDING);
	}

	@Override
	public void write(Description d) throws IOException, ParseException {
	    final String id = NS_SNOMED_DESCRIPTION_IDENTIFIER + ':' + d.getSerialisedId();
	    
        //Label
        writer.write(id + ' ' + PROPERTY_RDFS_LABEL + 
                " \"" + safe(d.getTerm()) + '\"' + LANGUAGE +
                LINE_ENDING);
        
        //Active
        writer.write(id + ' ' + PROPERTY_SNOMED_ACTIVE + 
                " \"" + (d.isActive() == true ? "true" : "false") + '\"' + XML_SCHEMA_DATATYPE_BOOLEAN +
                LINE_ENDING);
        
        //Module
        if (d.getModule() != null){
            writer.write(id + ' ' + PROPERTY_SNOMED_MODULE + ' ' + 
                    NS_SNOMED_CONCEPT_IDENTIFIER + ':' + d.getModule().getSerialisedId() + 
                    LINE_ENDING);
        }
        
        //Case Significance
        if (d.getCaseSignificance() != null){
            writer.write(id + ' ' + PROPERTY_SNOMED_CASE_SIGNIFICANCE + ' ' + 
                    NS_SNOMED_CONCEPT_IDENTIFIER + ':' + d.getCaseSignificance().getSerialisedId() +
                    LINE_ENDING);
        }
        
        //Description(s)
        if (d.getType() != null){
            writer.write(id + ' ' + PROPERTY_SNOMED_DESCRIPTION_TYPE + ' ' + 
            		NS_SNOMED_CONCEPT_IDENTIFIER + ':' + d.getType().getSerialisedId() +
                    LINE_ENDING);
        }        
        
        //Effective Time
        writer.write(id + ' ' + PROPERTY_SNOMED_EFFECTIVE_TIME + " \"" + 
                dateTimeFormatter.format(longTimeParser.parse(String.valueOf(d.getEffectiveTime()))) 
                + '\"' + XML_SCHEMA_DATATYPE_DATE +
                LINE_ENDING);
	    
	}

    @Override
    public void write(Statement s) throws IOException, ParseException {
        final String id = NS_SNOMED_STATEMENT_IDENTIFIER + ':' + s.getSerialisedId();
        
        //Type is rdf:Statement
        writer.write(id + ' ' + PROPERTY_RDF_TYPE + ' ' + CLASS_RDF_STATEMENT +
                LINE_ENDING);
        
        //Active
        writer.write(id + ' ' + PROPERTY_SNOMED_ACTIVE + 
                " \"" + (s.isActive() == true ? "true" : "false") + '\"' + XML_SCHEMA_DATATYPE_BOOLEAN +
                LINE_ENDING);
        
        //Group
        writer.write(id + ' ' + PROPERTY_SNOMED_GROUP + 
                " \"" + s.getGroupId() + '\"' + XML_SCHEMA_DATATYPE_INTEGER +
                LINE_ENDING);
        
        //Effective Time
        writer.write(id + ' ' + PROPERTY_SNOMED_EFFECTIVE_TIME + " \"" + 
                dateTimeFormatter.format(longTimeParser.parse(String.valueOf(s.getEffectiveTime()))) 
                + '\"' + XML_SCHEMA_DATATYPE_DATE +
                LINE_ENDING);
        
        //Subject
        writer.write(id + ' ' + PROPERTY_RDF_SUBJECT + ' ' + 
                NS_SNOMED_CONCEPT_IDENTIFIER + ':' + s.getSubject().getSerialisedId() + 
                LINE_ENDING);
        
        //Predicate
        writer.write(id + ' ' + PROPERTY_RDF_PREDICATE + ' ' + 
                NS_SNOMED_CONCEPT_IDENTIFIER + ':' + s.getPredicate().getSerialisedId() +
                LINE_ENDING);
        
        //Object
        writer.write(id + ' ' + PROPERTY_RDF_OBJECT + ' ' + 
                NS_SNOMED_CONCEPT_IDENTIFIER + ':' + s.getObject().getSerialisedId() +
                LINE_ENDING);
        
        //Modifier
        if (s.getModifier() != null){
            writer.write(id + ' ' + PROPERTY_SNOMED_MODIFIER + ' ' + 
                    NS_SNOMED_CONCEPT_IDENTIFIER + ':' + s.getModifier().getSerialisedId() +
                    LINE_ENDING);
        }        

        //Module
        if (s.getModule() != null){
            writer.write(id + ' ' + PROPERTY_SNOMED_MODULE + ' ' + 
                    NS_SNOMED_CONCEPT_IDENTIFIER + ':' + s.getModule().getSerialisedId() +
                    LINE_ENDING);
        }
        
        //Characteristic Type
        if (s.getCharacteristicType() != null){
            writer.write(id + ' ' + PROPERTY_SNOMED_CHARACTERISTIC_TYPE + ' ' + 
                    NS_SNOMED_CONCEPT_IDENTIFIER + ':' + s.getCharacteristicType().getSerialisedId() +
                    LINE_ENDING);
        }
        
        //Triples
        writeTriples(s);
        
        //Triple Hash
        //writeTripleHash(s, id);
    }	
    
    private void writeTriples(Statement s) throws IOException, ParseException {
        writer.write(
                (NS_SNOMED_CONCEPT_IDENTIFIER + ':' + s.getSubject().getSerialisedId()) + ' ' + 
                (NS_SNOMED_CONCEPT_IDENTIFIER + ':' + s.getPredicate().getSerialisedId()) + ' ' + 
                (NS_SNOMED_CONCEPT_IDENTIFIER + ':' + s.getObject().getSerialisedId()) +
                LINE_ENDING);
    }
}
