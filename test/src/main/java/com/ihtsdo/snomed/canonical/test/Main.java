package com.ihtsdo.snomed.canonical.test;

import java.io.File;
import java.io.FileInputStream;
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
import com.ihtsdo.snomed.canonical.model.Ontology;
import com.ihtsdo.snomed.service.HibernateDbImporter;

public class Main{
    private static final Logger LOG = LoggerFactory.getLogger( Main.class );
    
    private static final String ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML = "persistenceManager";
    public static final String GENERATED_ONTOLOGY_NAME = "Generated";
    public static final String EXPECTED_ONTOLOGY_NAME  = "Expected";
    public static final String ORIGINAL_ONTOLOGY_NAME  = "Original";
    
    private   EntityManagerFactory emf     = null;
    protected EntityManager em             = null;
    private   HibernateDbImporter importer = new HibernateDbImporter();
    private   TestingAlgorithm tester      = new TestingAlgorithm();
    
    protected void initDb(String db){
        Map<String, Object> overrides = new HashMap<String, Object>();
        
        if ((db != null) && (!db.isEmpty())){
            overrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + db);
            LOG.info("Using file system database at " + db);
        }else{
            LOG.info("Using an in-memory database");
        }
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
        em = emf.createEntityManager();
    }

    protected void closeDb(){
        LOG.info("Closing database");
        emf.close();
    }

    protected void runProgram(String conceptFile, String expectedFile, String generatedFile, 
            String extraFile, String missingFile, String originalFile, String db) throws IOException{
        try{
            initDb(db);
            
            Ontology originalOntology = importer.populateDbFromLongForm(ORIGINAL_ONTOLOGY_NAME, new FileInputStream(conceptFile), 
                    new FileInputStream(originalFile), em);
            Ontology expectedOntology = importer.populateDbFromShortForm(EXPECTED_ONTOLOGY_NAME, new FileInputStream(conceptFile), 
                    new FileInputStream(expectedFile), em);
            Ontology generatedOntology = importer.populateDbFromShortForm(GENERATED_ONTOLOGY_NAME, new FileInputStream(conceptFile), 
                    new FileInputStream(generatedFile), em);
            
            tester.findDifference(em, new File(extraFile), new File(missingFile), 
                    originalOntology, 
                    expectedOntology, 
                    generatedOntology);

        }finally{
            closeDb();
        }
    }    
    
    public static void main(String[] args) throws IOException, ParseException{
        Stopwatch overAllstopwatch = new Stopwatch().start();
        CliParser cli = new CliParser();
        cli.parse(args, new Main()); 
        overAllstopwatch.stop();
        LOG.info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }  
    
}
