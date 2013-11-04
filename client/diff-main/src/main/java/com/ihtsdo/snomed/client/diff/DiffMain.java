package com.ihtsdo.snomed.client.diff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.DiffAlgorithmFactory;
import com.ihtsdo.snomed.service.DiffAlgorithmFactory.DiffStrategy;
import com.ihtsdo.snomed.service.parser.HibernateParser;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory.Form;

public class DiffMain {
    private static final Logger LOG = LoggerFactory.getLogger( DiffMain.class );

    private EntityManagerFactory emf  = null;
    private EntityManager em          = null;

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
        em.close();
        emf.close();
    }

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException{
        Stopwatch overAllstopwatch = new Stopwatch().start();
        DiffCliParser cli = new DiffCliParser();
        cli.parse(args, new DiffMain()); 
        overAllstopwatch.stop();
        LOG.info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }    
    
    public void runProgram(File baseConceptsFile, File baseTriplesFile,
            File baseDescriptionsFile, Parser baseParserFormat,
            File compareConceptsFile, File compareTriplesFile,
            File compareDescriptionsFile, Parser compareParserFormat, DiffStrategy strategy, 
            File extraFile, File missingFile, String dbLocation, Form outputFormat)  throws IOException, java.text.ParseException
    {        
        try{
            initDb(dbLocation);
            HibernateParser baseParser = HibernateParserFactory.getParser(baseParserFormat);
            HibernateParser compareParser = HibernateParserFactory.getParser(compareParserFormat);

            Ontology baseOntology = null;
            if (baseDescriptionsFile != null){
                baseOntology = baseParser.populateDbWithDescriptions(
                        "Base Ontology", 
                        new FileInputStream(baseConceptsFile), 
                        new FileInputStream(baseTriplesFile), 
                        new FileInputStream(baseDescriptionsFile), 
                        em);
            } else if (baseConceptsFile != null){
                baseOntology = baseParser.populateDb(
                        "Base Ontology", 
                        new FileInputStream(baseConceptsFile), 
                        new FileInputStream(baseTriplesFile), 
                        em);                        
            } else {
                baseOntology = baseParser.populateDbFromStatementsOnly(
                        "Base Ontology",
                        new FileInputStream(baseTriplesFile), 
                        new FileInputStream(baseTriplesFile), 
                        em);
            }
            
            Ontology compareOntology = null;
            if (compareDescriptionsFile != null){
                compareOntology = compareParser.populateDbWithDescriptions(
                        "Compare-to Ontology", 
                        new FileInputStream(compareConceptsFile), 
                        new FileInputStream(compareTriplesFile), 
                        new FileInputStream(compareDescriptionsFile), 
                        em);
            } else if (baseConceptsFile != null){
                compareOntology = compareParser.populateDb(
                        "Compare-to Ontology", 
                        new FileInputStream(compareConceptsFile), 
                        new FileInputStream(compareTriplesFile), 
                        em);                        
            } else {
                compareOntology = compareParser.populateDbFromStatementsOnly(
                        "Compare-to Ontology",
                        new FileInputStream(compareTriplesFile), 
                        new FileInputStream(compareTriplesFile), 
                        em);
            }
            
            try(FileWriter extraFw = new FileWriter(extraFile); BufferedWriter extraBw = new BufferedWriter(extraFw);
                    FileWriter missingFw = new FileWriter(missingFile); BufferedWriter missingBw = new BufferedWriter(missingFw);){

                DiffAlgorithmFactory.getAlgorithm(strategy).diff(
                        baseOntology, 
                        compareOntology, 
                        SnomedSerialiserFactory.getSerialiser(outputFormat, extraFw), 
                        SnomedSerialiserFactory.getSerialiser(outputFormat, missingFw), em);
            }            
        }finally{
            closeDb();
        }
    }   
}
