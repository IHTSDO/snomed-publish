package com.ihtsdo.snomed.client.closure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.impl.StaticLoggerBinder;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.HibernateDbImporter;
import com.ihtsdo.snomed.service.SerialiserFactory;
import com.ihtsdo.snomed.service.SerialiserFactory.Form;
import com.ihtsdo.snomed.service.TransitiveClosureAlgorithm;

@Mojo(name="generate-canonical")
public class ClosureMojo extends AbstractMojo{
    private static final String DEFAULT_ONTOLOGY_NAME = "Transitive Closure Input";
    
    @Parameter
    private String inputFile;
    
    @Parameter
    private String outputFile;
    
    @Parameter
    private String databaseLocation;
    
    private   EntityManagerFactory emf             = null;
    private   EntityManager em                     = null;    
    private   TransitiveClosureAlgorithm algorithm = new TransitiveClosureAlgorithm();
    private   HibernateDbImporter importer         = new HibernateDbImporter();
    
    private void initDb(){
        Map<String, Object> overrides = new HashMap<String, Object>();
        
        if ((databaseLocation != null) && (!databaseLocation.isEmpty())){
            overrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + databaseLocation);
            getLog().info("Using file system database at " + databaseLocation);
        }else{
            getLog().info("Using an in-memory database");
        }
        getLog().info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateDbImporter.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
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
        try{
            initDb();
            Ontology o = importer.populateDbFromRf2(DEFAULT_ONTOLOGY_NAME, 
                    new FileInputStream(inputFile), new FileInputStream(inputFile), em);
            
            List<Concept> concepts = em.createQuery("SELECT c FROM Concept c WHERE c.ontology.id=" + o.getId(), Concept.class).getResultList();            
            
            writeOut(outputFile, algorithm.runAlgorithm(concepts));
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("File not found: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read/write file: " + e.getMessage(), e);
        } finally{
            closeDb();
        }
        overAllstopwatch.stop();
        getLog().info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");        
    }  
    
    private void writeOut(String outputFile, Set<Statement> statements) throws IOException {
        getLog().info("Writing results to " + outputFile);

        File outFile = new File(outputFile);
        if (!outFile.exists()){
            outFile.createNewFile();
        }
        
        try(FileWriter fw = new FileWriter(outFile); BufferedWriter bw = new BufferedWriter(fw)){
            SerialiserFactory.getSerialiser(Form.CHILD_PARENT).write(bw, statements);
        }
    }    
    
    private void testInputs() throws MojoExecutionException{
        if ((outputFile == null) || (inputFile == null) ||
                outputFile.isEmpty() || inputFile.isEmpty()){
            throw new MojoExecutionException("Invalid parameter configuration");
        }
        if (!new File(inputFile).isFile()){
            throw new MojoExecutionException("Unable to locate concepts input file '" + inputFile + "'");
        }
        try {
            new FileOutputStream(new File(outputFile));
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to write to output file '" + outputFile +"'. Check your permissions and path.");
        }
        if ((databaseLocation != null) && (!databaseLocation.isEmpty())){
            try {
                new FileOutputStream(new File(databaseLocation));
            } catch (IOException e) {
                throw new MojoExecutionException("Unable to write to database file '" + databaseLocation +"'. Check your permissions and path.");
            }
        } 
    }        
}
