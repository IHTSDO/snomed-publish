package com.ihtsdo.snomed.canonical;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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
import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger( Main.class );
    
    private static final String ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML = "persistenceManager";
    
    private   EntityManagerFactory emf           = null;
    protected EntityManager em                   = null;
    private   HibernateDatabaseImporter importer = new HibernateDatabaseImporter();
    private   CanonicalOutputWriter writer       = new CanonicalOutputWriter();
    private   CanonicalAlgorithm algorithm       = new CanonicalAlgorithm();

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

    public static void main(String[] args) throws IOException, ParseException{
        Stopwatch overAllstopwatch = new Stopwatch().start();
        CliParser cli = new CliParser();
        cli.parse(args, new Main()); 
        overAllstopwatch.stop();
        LOG.info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }    
    
    protected void runProgram(String conceptFile, String triplesFile, String outputFile, String db) throws IOException{
        try{
            initDb(db);  
            
            List<Concept> concepts = importer.populateDb(new FileInputStream(conceptFile),
                new FileInputStream(triplesFile), em);
            
            writeOut(outputFile, algorithm.runAlgorithm(concepts));
        }finally{
            closeDb();
        }
    }

    private void writeOut(String outputFile, Set<RelationshipStatement> statements) throws IOException {
        LOG.info("Writing results to " + outputFile);

        File outFile = new File(outputFile);
        if (!outFile.exists()){
            outFile.createNewFile();
        }
        
        try(FileWriter fw = new FileWriter(outFile); BufferedWriter bw = new BufferedWriter(fw)){
            writer.write(bw, statements);
        }
    }
}

