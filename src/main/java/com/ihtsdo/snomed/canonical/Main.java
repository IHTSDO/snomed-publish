package com.ihtsdo.snomed.canonical;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

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
    private CanonicalOutputWriter writer = new CanonicalOutputWriter();

    protected void initDb(String db){
        Map<String, Object> overrides = new HashMap<String, Object>();
        
        if ((db != null) && (!db.isEmpty())){
            overrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + db);
            LOG.info("Using file system database at " + db);
        }else{
            LOG.info("Using an in-memory database");
        }
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(ENTITY_MANAGER_NAME, overrides);
        em = emf.createEntityManager();
    }

    protected void closeDb(){
        LOG.info("Closing database");
        emf.close();
    }

    protected void runMain(String conceptFile, String triplesFile, String outputFile, String db) throws IOException{
        try{
            initDb(db);
            
            Stopwatch stopwatch = new Stopwatch().start();
            
            importer.populateDb(new FileInputStream(conceptFile),
                new FileInputStream(triplesFile), em);
            stopwatch.stop();
            
            LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
            
            writeOut(outputFile);

        }finally{
            closeDb();
        }
    }

    private void writeOut(String outputFile) throws IOException {
        LOG.info("Writing results to " + outputFile);
        Query query = em.createQuery("SELECT r FROM RelationshipStatement r");
        @SuppressWarnings("unchecked")
        List<RelationshipStatement> statements = (List<RelationshipStatement>) query.getResultList();
        
        File outFile = new File(outputFile);
        if (!outFile.exists()) outFile.createNewFile();
        try(FileWriter fw = new FileWriter(outFile); 
            BufferedWriter bw = new BufferedWriter(fw);)
        {
            writer.write(bw, statements);
        }
    }

    public static void main(String[] args) throws IOException, ParseException{
        
        //Create a parser using Commons CLI
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("t", "triples", true, "Triples input file");
        options.addOption("c", "concepts", true, "Concepts input file");
        options.addOption("o", "output", true, "Destination file");
        options.addOption("d", "database", true, "Database location");

        String helpString = "-t <triples input file> -c <concepts input file> -o <output file>. Optionaly -d <database location>. Try -h for more help";
        
        // Parse the program arguments
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            System.out.println("Unrecognised option. Usage is " + helpString);
            System.exit(1);
        }

        // Set the appropriate variables based on supplied options
        String output = commandLine.getOptionValue('o');
        String concepts = commandLine.getOptionValue('c');
        String triples = commandLine.getOptionValue('t');
        String db = commandLine.getOptionValue('d');
        
        
        
        testInputs(helpString, commandLine, output, concepts, triples, db);
        LOG.info("db is " + db);
        new Main().runMain(concepts, triples, output, db);
    }

    private static void testInputs(String helpString, CommandLine commandLine,
            String output, String concepts, String triples, String db) {
        if (commandLine.hasOption('h')) {
            System.out.println("-h, --help\t\tPrint this help menu\n" +
                    "-t. --triples\t\tFile containing all the relationships that you want to process, aka 'Relationships_Core'\n" +
                    "-c, --concepts\t\tFile containing all the concepts referenced in the relationships file, aka 'Concepts_Core'\n" +
                    "-o, --output\t\tDestination file to write the canonical output results to\n" +
                    "-d, --database\t\tOptional. Specify location of database file. If not specified, \n\t\t\tdefaults to an in-memory database (minium 2Gb of heap space required)");
            System.exit(0);
        }
        
        if ((output == null) || (concepts == null) || (triples == null) ||
                (output.isEmpty()) || (concepts.isEmpty()) || triples.isEmpty()){
            System.out.println("Invalid parameter configuration. Usage is: " + helpString);
            System.exit(-1);
        }
        
        if (!new File(concepts).isFile()){
            System.out.println("Unable to locate concepts input file '" + concepts + "'");
            System.exit(-1);
        }
        
        if (!new File(concepts).isFile()){
            System.out.println("Unable to locate triples input file '" + triples + "'");
            System.exit(-1);
        }
        
        try {
            new FileOutputStream(new File(output));
        } catch (IOException e) {
            System.out.println("Unable to write to output file '" + output +"'. Check your permissions and path.");
            System.exit(-1);
        }
        
        if ((db != null) && (!db.isEmpty())){
            try {
                new FileOutputStream(new File(db));
            } catch (IOException e) {
                System.out.println("Unable to write to database file '" + db +"'. Check your permissions and path.");
                System.exit(-1);
            }
        }
    }
}

