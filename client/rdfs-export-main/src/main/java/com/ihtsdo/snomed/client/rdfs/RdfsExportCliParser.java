package com.ihtsdo.snomed.client.rdfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.ihtsdo.snomed.service.parser.HibernateParserFactory;

public class RdfsExportCliParser {
    
    public enum RdfFormat{
        RDFXML("RDF/XML"), RDFXMLABBREV("RDF/XML-ABBREV"), NTRIPLE("N-TRIPLE"), N3("N3"), TURTLE("TURTLE");
        
        private String format;
        
        RdfFormat(String format){
            this.format = format;
        }
        
        @Override
        public String toString() {
            return format;
        }
        
        public static RdfFormat getFormat(String format){
            for (RdfFormat e : values()) {
                if (e.toString().equals(format)) {
                    return e;
                }
            }
            return null;
                
        }
    }    

    public static final String DRIVER_KEY               = "driver";
    public static final String URL_KEY                  = "url";
    public static final String PASSWORD_KEY             = "password";
    public static final String USER_KEY                 = "username";
    public static final String HIBERNATE_DIALECT_KEY    = "hibernate.dialect";
    
    public void parse(String[] args, RdfsExportMain callback) throws IOException, ParseException, java.text.ParseException{
        //Create a parser using Commons CLI
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("t", "triples", true, "Triples input file");
        options.addOption("c", "concepts", true, "Concepts input file");
        options.addOption("d", "descriptions", true, "Descriptions input file");
        options.addOption("if", "inputformat", true, "Input format");
        options.addOption("of", "outputformat", true, "Optional. Format to serialise RDF Schema to. One of 'RDF/XML', 'RDF/XML-ABBREV', 'N-TRIPLE', 'N3' or 'TURTLE'");
        options.addOption("o", "output", true, "Optional. File to serialise RDF Schema to. Must be used with '-of'");
        options.addOption("db", "database", true, "Optional. Database location"); //optional    

        //TODO: ADD OUTPUT FILE PARAM!!!!
        
        String helpString = "-t <triples input file> -c <concepts input file> -d <descriptions input file> " + 
                "-if <input files format>. Optionally, -db <database file>, " + 
                "-of <RDF Schema output format> -o <RDF Sschema output file>. Try -h for more help";
        
        // Parse the program arguments
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            System.out.println("Unrecognised option. Usage is " + helpString);
            System.exit(1);
        }

        // Set the appropriate variables based on supplied options
        String descriptions = commandLine.getOptionValue('d');//done
        String concepts = commandLine.getOptionValue('c');//done
        String triples = commandLine.getOptionValue('t');//done
        String inputFormat = commandLine.getOptionValue("if");//done
        String outputFormat = commandLine.getOptionValue("of");//done
        String output = commandLine.getOptionValue("o");//done
        String db = commandLine.getOptionValue("db"); //done
        
        testInputs(helpString, commandLine, descriptions, concepts, triples, 
                inputFormat, outputFormat, output, db);
                
        File conceptsFile = null;
        File triplesFile = new File(triples);
        File descriptionsFile = null;
        if ((concepts != null) && !concepts.isEmpty()){
            conceptsFile = new File(concepts);
        }
        if ((descriptions != null) && !descriptions.isEmpty()){
            descriptionsFile = new File(descriptions);
        }

        File outputFile = null;
        if ((output != null) && !output.isEmpty()){
            outputFile = new File(output);
        }
        
        
        callback.runProgram(conceptsFile, triplesFile, descriptionsFile, HibernateParserFactory.Parser.valueOf(inputFormat),
                RdfFormat.getFormat(outputFormat), outputFile, db);
    }
    
    private static void testInputs(String helpString, CommandLine commandLine,
            String descriptions, String concepts, String triples, String inputFormat, 
            String outputFormat, String output, String db) {
        
        if (commandLine.hasOption('h')) {
            System.out.println(
                    "\n" + 
                    "-h,   --help\t\tPrint this help menu\n" +
                    "-t.   --triples\t\tFile containing relationships\n" +
                    "-c,   --concepts\tOptional. File containing concepts\n" +
                    "-d,   --descriptions\tOptional. File containing descriptions\n" +
                    "-if,  --inputformat\tFile format of input files. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'\n" +
                    "-of,  --outputformat\tFormat to serialise RDF Schema to. One of 'RDF/XML', 'RDF/XML-ABBREV', 'N-TRIPLE', 'N3' or 'TURTLE'\n" +
                    "-o,   --output\t\tOptional. File to serialise RDF Schema to. Must be used together with '-of' option\n" +
                    "-db,  --database\tOptional. Specify location of database file. If not specified, \n\t\t\tdefaults to an in-memory database (minimum 3Gb of heap space required)\n");
                    
            System.exit(0);
        }
        
        if ((inputFormat == null) || inputFormat.isEmpty())
        {
            System.out.println("Input format parameter not specified. Usage is: " + helpString + "");
            System.exit(-1);
        }
        
        if ((output == null) || output.isEmpty()){
            System.out.println("Output file not specified. Usage is: " + helpString + "");
            System.exit(-1);            
        }
        
        try{
            HibernateParserFactory.Parser.valueOf(inputFormat);            
        }catch (IllegalArgumentException e){
            System.out.println("Input format specified '" + inputFormat + "' not supported. Use 'RF1', 'RF2', 'CHILD_PARENT', or 'CANONICAL'\n");
            System.exit(-1);
        }
        
        if ((outputFormat != null) && (!outputFormat.isEmpty())){
            try{
                HibernateParserFactory.Parser.valueOf(inputFormat);            
            }catch (IllegalArgumentException e){
                System.out.println("Output format specified '" + outputFormat + "' not supported. Use 'RDF/XML', 'RDF/XML-ABBREV', 'N-TRIPLE', 'N3' or 'TURTLE'. Usage is: " + helpString + "");
                System.exit(-1);            
            }            
        }
        
        if ((output != null) && !output.isEmpty() && ((outputFormat == null) || (outputFormat.isEmpty()))){
            System.out.println("If you specify an outputfile, you must also specify the output file format with the '-of' option. Usage is: " + helpString + "");
            System.exit(-1);            
        }
        
        if ((triples == null) || triples.isEmpty() || (concepts == null) || concepts.isEmpty() || 
                (descriptions == null) || descriptions.isEmpty())
        {
            System.out.println("Missing triples, concept, or description file. Usage is: " + helpString + "");
            System.exit(-1);
        }
        
        if (!new File(triples).isFile()){
            System.out.println("Unable to locate triples input file '" + triples + "'\n");
            System.exit(-1);
        }
        if ((concepts != null) && !concepts.isEmpty() && !new File(concepts).isFile()){
            System.out.println("Unable to locate concepts input file '" + concepts + "'");
            System.exit(-1);
        }
        if ((descriptions != null) && !descriptions.isEmpty() && !new File(descriptions).isFile()){
            System.out.println("Unable to locate descriptions input file '" + descriptions + "'\n");
            System.exit(-1);
        }
        
        if ((output != null) && !output.isEmpty()){
            try {
                new FileOutputStream(new File(output));
            } catch (IOException e) {
                System.out.println("Error: Unable to write to output file '" + output +"'. Check your permissions and path.\n");
                System.exit(-1);
            }
        }

        if ((db != null) && (!db.isEmpty())){
            try {
                new FileOutputStream(new File(db));
            } catch (IOException e) {
                System.out.println("Error: Unable to write to database file '" + db +"'. Check your permissions and path.\n");
                System.exit(-1);
            }
        }
    }    
}
