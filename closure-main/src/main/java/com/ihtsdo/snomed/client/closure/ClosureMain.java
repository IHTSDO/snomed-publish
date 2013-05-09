package com.ihtsdo.snomed.client.closure;

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
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.HibernateDbImporter;
import com.ihtsdo.snomed.service.SerialiserFactory;
import com.ihtsdo.snomed.service.SerialiserFactory.Form;
import com.ihtsdo.snomed.service.TransitiveClosureAlgorithm;

public class ClosureMain {
    
    private static final Logger LOG = LoggerFactory.getLogger( ClosureMain.class );
    
    private static final String DEFAULT_ONTOLOGY_NAME = "Transitive Closure Input";
    
    private   EntityManagerFactory emf              = null;
    private   EntityManager em                      = null;
    private   HibernateDbImporter importer          = new HibernateDbImporter();
    private   TransitiveClosureAlgorithm algorithm  = new TransitiveClosureAlgorithm();

    private void initDb(String db){
        Map<String, Object> overrides = new HashMap<String, Object>();
        
        if ((db != null) && (!db.isEmpty())){
            overrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + db);
            LOG.info("Using file system database at " + db);
        }else{
            LOG.info("Using an in-memory database");
        }
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateDbImporter.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
        em = emf.createEntityManager();
    }

    public void closeDb(){
        LOG.info("Closing database");
        emf.close();
    }

    public static void main(String[] args) throws IOException, ParseException{
        Stopwatch overAllstopwatch = new Stopwatch().start();
        ClosureCliParser cli = new ClosureCliParser();
        cli.parse(args, new ClosureMain()); 
        overAllstopwatch.stop();
        LOG.info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }    
    
    protected void runProgram(String inputFile, String outputFile, String dbFile) throws IOException{
        try{
            initDb(dbFile);  
            Ontology o = importer.populateDbFromRf2(DEFAULT_ONTOLOGY_NAME, 
                    new FileInputStream(inputFile), new FileInputStream(inputFile), em);
            
            List<Concept> concepts = em.createQuery("SELECT c FROM Concept c WHERE c.ontology.id=" + o.getId(), Concept.class).getResultList();            
            
            writeOut(outputFile, algorithm.runAlgorithm(concepts));
        }finally{
            closeDb();
        }
    }

    private void writeOut(String outputFile, Set<Statement> statements) throws IOException {
        LOG.info("Writing results to " + outputFile);

        File outFile = new File(outputFile);
        if (!outFile.exists()){
            outFile.createNewFile();
        }
        
        try(FileWriter fw = new FileWriter(outFile); BufferedWriter bw = new BufferedWriter(fw)){
            SerialiserFactory.getSerialiser(Form.CANONICAL).write(bw, statements);
        }
    }
}

