package com.ihtsdo.snomed.client.closure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.commons.cli.ParseException;
import org.hibernate.CacheMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.InvalidInputException;
import com.ihtsdo.snomed.service.TransitiveClosureAlgorithm;
import com.ihtsdo.snomed.service.parser.HibernateParser;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;
import com.ihtsdo.snomed.service.serialiser.OntologySerialiser;
import com.ihtsdo.snomed.service.serialiser.SerialiserFactory;
import com.ihtsdo.snomed.service.serialiser.SerialiserFactory.Form;

public class ClosureMain {
    
    private static final Logger LOG = LoggerFactory.getLogger( ClosureMain.class );
    
    private static final String DEFAULT_ONTOLOGY_NAME = "Transitive Closure Input";
    public static final int DEFAULT_PAGE_SIZE = 450000;
    
    private   EntityManagerFactory emf              = null;
    private   EntityManager em                      = null;
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
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
        em = emf.createEntityManager();
        //em.getTransaction().begin();
    }
    
//    private void initMysqlDb(){
//        Map<String, Object> overrides = new HashMap<String, Object>();
//        
//        overrides.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        overrides.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
//        overrides.put("javax.persistence.jdbc.url", "jdbc:mysql://localhost/snomed");
//        overrides.put("javax.persistence.jdbc.user", "root");
//        overrides.put("javax.persistence.jdbc.password", "");
//        overrides.put("hibernate.hbm2ddl.auto", "update");
//        
//        LOG.info("Initialising database");
//        emf = Persistence.createEntityManagerFactory(Rf1HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
//        em = emf.createEntityManager();
//    }    

    public void closeDb(){
        LOG.info("Closing database");
        //em.getTransaction().commit();
        emf.close();
    }

    public static void main(String[] args) throws IOException, ParseException{
        Stopwatch overAllstopwatch = new Stopwatch().start();
        ClosureCliParser cli = new ClosureCliParser();
        cli.parse(args, new ClosureMain()); 
        overAllstopwatch.stop();
        LOG.info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }    
    


    public void runProgram(File conceptsFile, File triplesFile,
            //File descriptionsFile, 
            Parser type, File outputFile,
            int pageSize, String dbLocation) throws IOException 
    {
        try {
            initDb(dbLocation);
            Ontology o = null;
            HibernateParser hibParser = HibernateParserFactory.getParser(type);
            
           //statement + description: not implemented
//            if (descriptionsFile != null){
//                //statement + concept + description
//                //not really required. Algorithm no longer need descriptions file
//                o = hibParser.populateDbWithDescriptions(
//                        DEFAULT_ONTOLOGY_NAME, 
//                        new FileInputStream(conceptsFile), 
//                        new FileInputStream(triplesFile), 
//                        new FileInputStream(descriptionsFile), 
//                        em);
//            } else 
            if (conceptsFile != null){
                //statement + concept
                o = hibParser.populateDb(
                        DEFAULT_ONTOLOGY_NAME, 
                        new FileInputStream(conceptsFile), 
                        new FileInputStream(triplesFile), 
                        em);                        
            } else {
                //statement
                o = hibParser.populateDbFromStatementsOnly(
                        DEFAULT_ONTOLOGY_NAME, 
                        new FileInputStream(triplesFile), 
                        new FileInputStream(triplesFile), 
                        em);
            }             
            
            if (o == null){
                throw new InvalidInputException("Parsing failed");
            }
            
            TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c WHERE c.ontology.id=:ontologyId", Concept.class);
            query.setParameter("ontologyId", o.getId());
            query.setHint("org.hibernate.cacheable", Boolean.TRUE);
            query.setHint("org.hibernate.readOnly", Boolean.TRUE);
            query.setHint("org.hibernate.cacheMode", CacheMode.GET);

            int firstResult = 0;
            int counter = 0;
            query.setFirstResult(firstResult);
            query.setMaxResults(pageSize);            
            List<Concept> concepts = query.getResultList();

            try(FileWriter fw = new FileWriter(outputFile); BufferedWriter bw = new BufferedWriter(fw)){
                OntologySerialiser serialiser = SerialiserFactory.getSerialiser(Form.CHILD_PARENT, bw);
                Stopwatch stopwatch = new Stopwatch().start();
                LOG.info("Running algorithm");
                boolean done = false;
                while (!done){
                    LOG.info("Running concept batch with pagesize {}", pageSize);
                    Stopwatch stopwatchBatch = new Stopwatch().start();
                    if (concepts.size() < pageSize) {
                        counter += concepts.size();
                        done = true;
                    }                    
                    algorithm.runAlgorithm(concepts, serialiser);
                    em.clear();
                    firstResult = firstResult + pageSize;
                    if (!done){
                        counter += pageSize;
                        query.setFirstResult(firstResult);
                        concepts = query.getResultList();
                    }
                    stopwatchBatch.stop();
                    LOG.info("Batch completed in {} seconds", stopwatch.elapsed(TimeUnit.SECONDS));
                }
                stopwatch.stop();
                LOG.info("Completed algorithm in {} seconds with {} concepts", stopwatch.elapsed(TimeUnit.SECONDS), counter);
            }
        } finally{
            closeDb();
        }
    }
}

