package com.ihtsdo.snomed.client.rdfs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import com.ihtsdo.snomed.service.jena.RdfSchemaSerialiser;
import com.ihtsdo.snomed.service.parser.HibernateParser;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;

public class RdfsExportMain {
    private static final Logger LOG = LoggerFactory.getLogger( RdfsExportMain.class );
        
    private EntityManagerFactory emf  = null;
    private EntityManager em          = null;
    
//    private JenaRdfSchemaSerialiser jena = new JenaRdfSchemaSerialiser();
    private RdfSchemaSerialiser export = new RdfSchemaSerialiser();

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
        em.getTransaction().begin();
    }

    public void closeDb(){
        LOG.info("Closing database");
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    public static void main(String[] args) throws IOException, ParseException{
        Stopwatch overAllstopwatch = new Stopwatch().start();
        RdfsExportCliParser cli = new RdfsExportCliParser();
        cli.parse(args, new RdfsExportMain()); 
        overAllstopwatch.stop();
        LOG.info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }    

    public void runProgram(File conceptsFile, File triplesFile, File descriptionsFile, Parser inputFormat, 
            File tdbFolder, RdfsExportCliParser.RdfFormat outputFormat, File outputFile, String db) throws IOException
    {
        try{
            initDb(db);
            HibernateParser hibParser = HibernateParserFactory.getParser(inputFormat);
            Ontology o = null;
            if (descriptionsFile != null){
                o = hibParser.populateDbWithDescriptions(
                        "Jena import", 
                        new FileInputStream(conceptsFile), 
                        new FileInputStream(triplesFile), 
                        new FileInputStream(descriptionsFile), 
                        em);
            } else if (conceptsFile != null){
                o = hibParser.populateDb(
                        "Jena import", 
                        new FileInputStream(conceptsFile), 
                        new FileInputStream(triplesFile), 
                        em);                        
            } else {
                o = hibParser.populateDbFromStatementsOnly(
                        "Jena import",
                        new FileInputStream(triplesFile), 
                        new FileInputStream(triplesFile), 
                        em);
            }
            
//            OntModel model = jena.importJenaModel(o, em, tdbFolder);
//            
//            if (outputFile != null){
//                try (FileOutputStream fos = new FileOutputStream(outputFile); BufferedOutputStream bos = new BufferedOutputStream(fos)){
//                    model.write(bos, outputFormat.toString());
//                }
//            }
            
            if (outputFile != null){
                try (OutputStreamWriter ow = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)))){
                    export.exportToRdfXml(o, em, ow);
                }
            }
            
        }finally{
            closeDb();
        }
        
    }
}
