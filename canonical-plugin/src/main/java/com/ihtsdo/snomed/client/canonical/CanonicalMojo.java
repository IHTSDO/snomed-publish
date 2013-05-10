package com.ihtsdo.snomed.client.canonical;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.CanonicalAlgorithm;
import com.ihtsdo.snomed.service.HibernateDbImporter;
import com.ihtsdo.snomed.service.SerialiserFactory;
import com.ihtsdo.snomed.service.SerialiserFactory.Form;

@Mojo(name="generate-canonical")
public class CanonicalMojo extends AbstractMojo{
    
    @Parameter
    private String conceptFile;
    
    @Parameter
    private String relationshipFile;
    
    @Parameter
    private String outputFile;
    
    @Parameter
    private String databaseLocation;
    
    @Parameter
    private String show;
    
    private static final String DEFAULT_ONTOLOGY_NAME = "LongForm";
    public static final String SHOW_ALL = "ALL";
    
    private   EntityManagerFactory emf           = null;
    private   EntityManager em                   = null;
    private   HibernateDbImporter importer       = new HibernateDbImporter();
    private   CanonicalAlgorithm algorithm       = new CanonicalAlgorithm();
     
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
            Ontology ontology = importer.populateDbFromRf1Form(DEFAULT_ONTOLOGY_NAME, 
                    new FileInputStream(conceptFile), new FileInputStream(relationshipFile), em);

            List<Concept> concepts = em.createQuery("SELECT c FROM Concept c WHERE c.ontology.id=" + ontology.getId(), Concept.class).getResultList();

            writeOut(outputFile, runAlgorithm(show, concepts));
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("File not found: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read/write file: " + e.getMessage(), e);
        }finally{
            closeDb();
        }
        overAllstopwatch.stop();
        getLog().info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");        
    }
    
    private Set<Statement> runAlgorithm(String show, List<Concept> concepts) {
        Set<Statement> resultStatements = null;
        if (show == null){
            resultStatements = algorithm.runAlgorithm(concepts, false, null);
        }
        else if (show.toUpperCase().equals(SHOW_ALL)){
            resultStatements = algorithm.runAlgorithm(concepts, true, null);
        }else{
            resultStatements = algorithm.runAlgorithm(concepts, true, parseShowString(show));
        }
        return resultStatements;
    }
    
    public static Set<Long> parseShowString(String show){
        Set<Long> resultSet = new HashSet<Long>();
        Iterator<String> splitIt = Splitter.on(',').split(show.substring(1, show.length() - 1)).iterator();
        while (splitIt.hasNext()){
            resultSet.add(Long.parseLong(splitIt.next().trim()));
        }
        return resultSet;
    }    
    
    private void writeOut(String outputFile, Set<Statement> statements) throws IOException {
        getLog().info("Writing results to " + outputFile);

        File outFile = new File(outputFile);
        if (!outFile.exists()){
            outFile.createNewFile();
        }
        
        try(FileWriter fw = new FileWriter(outFile); BufferedWriter bw = new BufferedWriter(fw)){
            SerialiserFactory.getSerialiser(Form.CANONICAL, bw).write(statements);
        }
    }    
    
    private void testInputs() throws MojoExecutionException{
        if ((outputFile == null) || (conceptFile == null) || (relationshipFile == null) ||
                (outputFile.isEmpty()) || (conceptFile.isEmpty()) || relationshipFile.isEmpty()){
            throw new MojoExecutionException("Invalid parameter configuration");
        }
        
        if (!new File(conceptFile).isFile()){
            throw new MojoExecutionException("Unable to locate concepts input file '" + conceptFile + "'");
        }
        
        if (!new File(relationshipFile).isFile()){
            throw new MojoExecutionException("Unable to locate triples input file '" + relationshipFile + "'");
        }
        
        try {
            new FileOutputStream(new File(outputFile));
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to write to output file '" + outputFile +"'. Check your permissions and path.");
        }
        
        if (show != null){
            if (show.startsWith("{") && show.endsWith("}")){
                try{
                    parseShowString(show);
                }catch (NumberFormatException e){
                    throw new MojoExecutionException("Value for show option '" + show + "'is not valid");                 
                }
            }
            else if (!show.toUpperCase().equals(SHOW_ALL)){
                throw new MojoExecutionException("Value for show option '" + show + "'is not valid");             
            }
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
