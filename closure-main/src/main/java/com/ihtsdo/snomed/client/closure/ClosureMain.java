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
import com.ihtsdo.snomed.service.HibernateDbImporter;
import com.ihtsdo.snomed.service.OntologySerialiser;
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
            Ontology o = importer.populateDbFromRf2FormWithNoConcepts(DEFAULT_ONTOLOGY_NAME, 
                    new FileInputStream(inputFile), new FileInputStream(inputFile), em);
            
            File outFile = new File(outputFile);
            if (!outFile.exists()){
                outFile.createNewFile();
            }
            
            TypedQuery<Concept> query = em.createQuery("SELECT c FROM Concept c WHERE c.ontology.id=:ontologyId", Concept.class);
            query.setParameter("ontologyId", o.getId());
            query.setHint("org.hibernate.cacheable", Boolean.TRUE);
            query.setHint("org.hibernate.readOnly", Boolean.TRUE);
            query.setHint("org.hibernate.cacheMode", CacheMode.GET);
            
            //int pageSize = 250000;
            //int firstResult = 0;
            //int counter = 0;
            //query.setFirstResult(firstResult);
            //query.setMaxResults(pageSize);            
            List<Concept> concepts = query.getResultList();
            
            //boolean done = false;
            try(FileWriter fw = new FileWriter(outFile); BufferedWriter bw = new BufferedWriter(fw)){
                OntologySerialiser serialiser = SerialiserFactory.getSerialiser(Form.CANONICAL, bw);
                Stopwatch stopwatch = new Stopwatch().start();
                LOG.info("Running algorithm");
                algorithm.runAlgorithm(concepts, serialiser);
//                while (!done){
//                    if (concepts.size() < pageSize) {
//                        counter += concepts.size();
//                        done = true;
//                    }                    
//                    algorithm.runAlgorithm(concepts, serialiser);
//                    em.clear();
//                    firstResult = firstResult + pageSize;
//                    if (!done){
//                        counter += pageSize;
//                        query.setFirstResult(firstResult);
//                        concepts = query.getResultList();
//                    }
//                }
                stopwatch.stop();
                LOG.info("Completed algorithm in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");// with [" + counter + "] statements");
            }
        }finally{
            closeDb();
        }
    }
}

