package com.ihtsdo.snomed.client.closure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.hibernate.CacheMode;
import org.slf4j.impl.StaticLoggerBinder;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.InvalidInputException;
import com.ihtsdo.snomed.service.TransitiveClosureAlgorithm;
import com.ihtsdo.snomed.service.parser.HibernateParser;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiser;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory.Form;

@Mojo(name="generate-transitive-closure")
public class ClosureMojo extends AbstractMojo{
    private static final String DEFAULT_ONTOLOGY_NAME = "Transitive Closure Input";
    
    @Parameter String conceptsFile;
    @Parameter String triplesFile;
    @Parameter String parserType;
    @Parameter String outputFile;
    @Parameter int pageSize;
    @Parameter private String databaseLocation;
    
    private   EntityManagerFactory emf             = null;
    private   EntityManager em                     = null;    
    private   TransitiveClosureAlgorithm algorithm = new TransitiveClosureAlgorithm();
    
    private void initDb(){
        Map<String, Object> overrides = new HashMap<String, Object>();
        
        if ((databaseLocation != null) && (!databaseLocation.isEmpty())){
            overrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + databaseLocation);
            getLog().info("Using file system database at " + databaseLocation);
        }else{
            getLog().info("Using an in-memory database");
        }
        getLog().info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
        em = emf.createEntityManager();
    }

    public void closeDb(){
        getLog().info("Closing database");
        emf.close();
    }   

    public void execute() throws MojoExecutionException{
        Stopwatch overAllstopwatch = new Stopwatch().start();
        
        // bind slf4j to maven log
        ((org.apache.maven.plugin.Mojo) StaticLoggerBinder.getSingleton()).setLog(getLog());        
        
        testInputs();
        try {
            initDb();
            
            Ontology o = null;
            HibernateParser hibParser = HibernateParserFactory.getParser(Parser.valueOf(parserType));
//            if (descriptionsFile != null){
//                o = hibParser.populateDbWithDescriptions(
//                        DEFAULT_ONTOLOGY_NAME, 
//                        new FileInputStream(conceptsFile), 
//                        new FileInputStream(triplesFile), 
//                        new FileInputStream(descriptionsFile), 
//                        em);
//            } else 
                if (conceptsFile != null){
                o = hibParser.populateDb(
                        DEFAULT_ONTOLOGY_NAME, 
                        new FileInputStream(conceptsFile), 
                        new FileInputStream(triplesFile), 
                        em);                        
            } else {
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
                SnomedSerialiser serialiser = SnomedSerialiserFactory.getSerialiser(Form.CHILD_PARENT, bw);
                Stopwatch stopwatch = new Stopwatch().start();
                getLog().info("Running algorithm");
                boolean done = false;
                while (!done){
                    getLog().info("Running concept batch with pagesize " + pageSize);
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
                    getLog().info("Batch completed in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
                }
                stopwatch.stop();
                getLog().info("Completed algorithm in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds with " + counter + " concepts");
            } catch (ParseException e) {
                throw new MojoExecutionException(e.getMessage(), e);
			}
        } catch (FileNotFoundException e) {
            getLog().error("File not found: " + e.getMessage());
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            getLog().error("Unable to read/write file: " + e.getMessage());
            throw new MojoExecutionException(e.getMessage(), e);
        } finally{
            closeDb();
        }
        overAllstopwatch.stop();
        getLog().info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");        
    }  
    
    private void testInputs() throws MojoExecutionException{
        if ((parserType == null) || parserType.isEmpty())
        {
            System.out.println("Parser type parameter not specified");
            System.exit(-1);
        }        
        
        try{
            HibernateParserFactory.Parser.valueOf(parserType);            
        }catch (IllegalArgumentException e){
            System.out.println("Parser type '" + parserType + "' not supported. Use 'RF1', 'RF2', or 'CANONICAL'");
            System.exit(-1);            
        }        
        
        if ((triplesFile == null) || triplesFile.isEmpty())
        {
            System.out.println("Invalid triples file parameter configuration");
            System.exit(-1);
        }
        
        if (!new File(triplesFile).isFile()){
            System.out.println("Unable to locate triples input file '" + triplesFile + "'");
            System.exit(-1);
        }
        
        if ((conceptsFile != null) && !conceptsFile.isEmpty() && !new File(conceptsFile).isFile()){
            System.out.println("Unable to locate concepts input file '" + conceptsFile + "'");
            System.exit(-1);
        }
//        if ((descriptionsFile != null) && !descriptionsFile.isEmpty() && !new File(descriptionsFile).isFile()){
//            System.out.println("Unable to locate descriptions input file '" + descriptionsFile + "'");
//            System.exit(-1);
//        }        
//        
        if ((outputFile == null) || outputFile.isEmpty()){
            System.out.println("Invalid parameter configuration");
            System.exit(-1);
        }

        try {
            new FileOutputStream(new File(outputFile));
        } catch (IOException e) {
            System.out.println("Unable to write to output file '" + outputFile +"'. Check your permissions and path.");
            System.exit(-1);
        }
        
        if ((databaseLocation != null) && (!databaseLocation.isEmpty())){
            try {
                new FileOutputStream(new File(databaseLocation));
            } catch (IOException e) {
                System.out.println("Unable to write to database file '" + databaseLocation +"'. Check your permissions and path.");
                System.exit(-1);
            }
        }
    }        
}
