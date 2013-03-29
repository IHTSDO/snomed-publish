package com.ihtsdo.snomed.canonical;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    
    protected static final String CONCEPTS_INPUT_FILE_SYSTEM_PROPERTY_NAME = "concept.in";
    protected static final String TRIPLE_INPUT_FILE_SYSTEM_PROPERTY_NAME = "triple.in";
    protected static final String OUTPUT_FILE_SYSTEM_PROPERTY_NAME = "canonical.out";

    private static final Logger LOG = LoggerFactory.getLogger( Main.class );
    protected static final String CONCEPTS_INPUT = "sct1_Concepts_Core_INT_20130131.ont.txt";
    private static final String ENTITY_MANAGER_NAME = "persistenceManager";
    private EntityManagerFactory emf = null;
    protected EntityManager em = null;
    private HibernateDatabaseImporter importer = new HibernateDatabaseImporter();

    protected void initDb(){
        LOG.info("Initialising Database");
        emf = Persistence.createEntityManagerFactory(ENTITY_MANAGER_NAME);
        em = emf.createEntityManager();
    }

    protected void closeDb(){
        LOG.info("Closing database");
        emf.close();
    }

    protected void runMain(String conceptFile, String triplesFile, String outputFile) throws IOException{
        try{
            initDb();
            
            importer.populateDb(ClassLoader.getSystemResourceAsStream(conceptFile),
                ClassLoader.getSystemResourceAsStream(triplesFile), em);

        }finally{
            closeDb();
        }
    }

    public static void main(String[] args) throws IOException, ParseException{
        
        //Create a parser using Commons CLI
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("t", "triples", true, "Triples input file");
        options.addOption("c", "concepts", true, "Concepts input file");
        options.addOption("f", "file", true, "Destination file");

        // Parse the program arguments
        CommandLine commandLine = parser.parse(options, args);

        // Set the appropriate variables based on supplied options
        String file = commandLine.getOptionValue('f');
        String concepts = commandLine.getOptionValue('c');
        String triples = commandLine.getOptionValue('t');
        
        if (commandLine.hasOption('h')) {
            System.out.println( "-t <triples input file> -c <concepts input file> -f <destination file>");
            System.exit(0);
        }
        
        if ((file == null) || (concepts == null) || (triples == null) ||
                (file.isEmpty()) || (concepts.isEmpty()) || triples.isEmpty()){
            System.out.println("Invalid parameters - try the help menu (-h)");
            System.exit(-1);
        }
        new Main().runMain(concepts, triples, file);
    }
}

