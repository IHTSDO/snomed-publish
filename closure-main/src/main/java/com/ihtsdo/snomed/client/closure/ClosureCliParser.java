package com.ihtsdo.snomed.client.closure;

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

public class ClosureCliParser {

    public static final String SHOW_ALL                 = "ALL";
    
    public void parse(String[] args, ClosureMain callback) throws IOException, ParseException{
        //Create a parser using Commons CLI
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("t", "triples", true, "Triples input file");
        options.addOption("c", "concepts", true, "Optional. Concepts input file");
        //options.addOption("d", "descriptions", true, "Descriptions input file");
        options.addOption("f", "format", true, "Input format");
        options.addOption("o", "output", true, "Destination file");
        options.addOption("p", "pagesize", true, "Optional. Size of database concept pagination");
        options.addOption("l", "location", true, "Optional. Database location");

        String helpString = "-t <triples input file> -c <concepts input file> " +
        //"-d <descriptions input file> " + 
        "-f <input file format> -o <output file>. Optionaly -l <database location> -p <page size>. Try -h for more help";
        
        // Parse the program arguments
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            System.out.println("Unrecognised option. Usage is " + helpString);
            System.exit(1);
        }

        // Set the appropriate variables based on supplied options
        //String descriptions = commandLine.getOptionValue('d');
        String concepts = commandLine.getOptionValue('c');
        String triples = commandLine.getOptionValue('t');
        String format = commandLine.getOptionValue('f');
        String output = commandLine.getOptionValue('o');
        String db = commandLine.getOptionValue('l');
        String pageSize = commandLine.getOptionValue('p');


        testInputs(helpString, commandLine, 
                //descriptions, 
                concepts, triples, format, output, db, pageSize);
        
        File conceptsFile = null;
        File triplesFile = new File(triples);
        //File descriptionsFile = null;
        if ((concepts != null) && !concepts.isEmpty()){
            conceptsFile = new File(concepts);
        }
//        if ((descriptions != null) && !descriptions.isEmpty()){
//            descriptionsFile = new File(descriptions);
//        }
        
        File outputFile = new File(output);
        if (!outputFile.exists()){
            outputFile.createNewFile();
        }
        
        int pageSizeInt = ClosureMain.DEFAULT_PAGE_SIZE;
        if ((pageSize != null) && (!pageSize.isEmpty())){
            pageSizeInt = Integer.parseInt(pageSize);
        }
        
        callback.runProgram(conceptsFile, triplesFile, 
                //descriptionsFile, 
                HibernateParserFactory.Parser.valueOf(format),
                outputFile, pageSizeInt, db);

    }
    
    private static void testInputs(String helpString, CommandLine commandLine,
            //String descriptions, 
            String concepts, String triples, String format, String output, String db, String pageSize) {
        if (commandLine.hasOption('h')) {
            System.out.println("\n-h, --help\t\tPrint this help menu\n" +
                    "-t. --triples\t\tFile containing relationships\n" +
                    "-c, --concepts\t\tOptional. File containing concepts\n" +
                    //"-d, --descriptions\tFile containing descriptions\n" +
                    "-f, --format\t\tFile format of input files. One of 'RF1', 'RF2', or 'CANONICAL'\n" +
                    "-o, --output\t\tDestination file to write the transitive closure results to, in simple child-parent format\n" +
                    "-p, --pagesize\t\tNumber of concept records to handle in a single batch.\n\t\t\tA smaller page size requires less memory, but has poorer performance. Default it 450,000\n" +
                    "-l, --location\t\tOptional. Specify location of database file. If not specified, defaults to an in-memory database\n\t\t\twith increased memory requirements, but much imnproved performance and lower IO latency\n");                     
            System.exit(0);
        }
        
        if ((format == null) || format.isEmpty())
        {
            System.out.println("Format parameter not specified. Usage is: " + helpString);
            System.exit(-1);
        }        
        
        try{
            HibernateParserFactory.Parser.valueOf(format);            
        }catch (IllegalArgumentException e){
            System.out.println("Format specified '" + format + "' not supported. Use 'RF1', 'RF2', or 'CANONICAL'");
            System.exit(-1);            
        }        
        
        if ((triples == null) || triples.isEmpty())
        {
            System.out.println("Invalid triples file parameter configuration. Usage is: " + helpString);
            System.exit(-1);
        }
        
        if (!new File(triples).isFile()){
            System.out.println("Unable to locate triples input file '" + triples + "'");
            System.exit(-1);
        }
        
        if ((concepts != null) && !concepts.isEmpty() && !new File(concepts).isFile()){
            System.out.println("Unable to locate concepts input file '" + concepts + "'");
            System.exit(-1);
        }
//        if ((descriptions != null) && !descriptions.isEmpty() && !new File(descriptions).isFile()){
//            System.out.println("Unable to locate descriptions input file '" + descriptions + "'");
//            System.exit(-1);
//        }        
        
        if ((output == null) || output.isEmpty()){
            System.out.println("Invalid parameter configuration. Usage is: " + helpString);
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
        
        if ((pageSize != null) && (!pageSize.isEmpty())){
            try {
                Integer.parseInt(pageSize);
            } catch (NumberFormatException e) {
                System.out.println("Unable to parse pagesize '" + pageSize +"'. pageSize must be an integer");
                System.exit(-1);
            }
        }        
    }    
}
