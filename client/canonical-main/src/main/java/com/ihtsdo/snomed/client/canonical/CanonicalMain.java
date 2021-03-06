package com.ihtsdo.snomed.client.canonical;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.SnomedFlavours;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.CanonicalAlgorithm;
import com.ihtsdo.snomed.service.parser.HibernateParser;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory.Form;

public class CanonicalMain {    
    private static final Logger LOG = LoggerFactory.getLogger( CanonicalMain.class );
    
    protected static final Date DEFAULT_TAGGED_ON_DATE;
    
    static{
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();
        DEFAULT_TAGGED_ON_DATE = new Date(utilDate.getTime());        
    }
    
    private   EntityManagerFactory emf           = null;
    private   EntityManager em                   = null;
    private   HibernateParser parser             = HibernateParserFactory.getParser(Parser.RF1);
    private   CanonicalAlgorithm algorithm       = new CanonicalAlgorithm();

    private void initDb(String db){
        Map<String, Object> overrides = new HashMap<String, Object>();
        
        if ((db != null) && (!db.isEmpty())){
            overrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + db);
            LOG.info("Using file system database at " + db);
        }else{
            LOG.info("Using an in-memory database");
        }
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
        em = emf.createEntityManager();
    }

    public void closeDb(){
        LOG.info("Closing database");
        emf.close();
    }

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException{
        Stopwatch overAllstopwatch = new Stopwatch().start();
        CanonicalCliParser cli = new CanonicalCliParser();
        cli.parse(args, new CanonicalMain()); 
        overAllstopwatch.stop();
        LOG.info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }    
    
    protected void runProgram(String conceptFile, String triplesFile, String outputFile, String db, String show) throws IOException, java.text.ParseException{
        try{
            initDb(db);  
            OntologyVersion ontologyVersion = null;
            if (conceptFile != null){
                ontologyVersion = parser.populateDb(
                        SnomedFlavours.INTERNATIONAL,
                        DEFAULT_TAGGED_ON_DATE, 
                        new FileInputStream(conceptFile), new FileInputStream(triplesFile), em);
            }else{
                ontologyVersion = parser.populateDbFromStatementsOnly(
                        SnomedFlavours.INTERNATIONAL,
                        DEFAULT_TAGGED_ON_DATE,                         
                        new FileInputStream(triplesFile), new FileInputStream(triplesFile), em);
            }
                
            List<Concept> concepts = em.createQuery("SELECT c FROM Concept c WHERE c.ontology.id=" + ontologyVersion.getId(), Concept.class).getResultList();

            writeOut(outputFile, runAlgorithm(show, concepts));
        }finally{
            closeDb();
        }
    }

    private Set<Statement> runAlgorithm(String show, List<Concept> concepts) {
        Set<Statement> resultStatements = null;
        if (show == null){
            resultStatements = algorithm.runAlgorithm(concepts, false, null);
        }
        else if (show.toUpperCase().equals(CanonicalCliParser.SHOW_ALL)){
            resultStatements = algorithm.runAlgorithm(concepts, true, null);
        }else{
            resultStatements = algorithm.runAlgorithm(concepts, true, CanonicalCliParser.parseShowString(show));
        }
        return resultStatements;
    }

    private void writeOut(String outputFile, Set<Statement> statements) throws IOException, java.text.ParseException {
        LOG.info("Writing results to " + outputFile);

        File outFile = new File(outputFile);
        if (!outFile.exists()){
            outFile.createNewFile();
        }
        
        try(FileWriter fw = new FileWriter(outFile); BufferedWriter bw = new BufferedWriter(fw)){
            SnomedSerialiserFactory.getSerialiser(Form.CANONICAL, bw).write(statements);
        }
    }
}

