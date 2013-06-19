package com.ihtsdo.snomed.client.canonical;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.google.common.base.Splitter;

public class CanonicalCliParser {

    public static final String SHOW_ALL = "ALL";
    
    public void parse(String[] args, CanonicalMain callback) throws IOException, ParseException{

        //Create a parser using Commons CLI
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("t", "triples", true, "Triples input file");
        options.addOption("c", "concepts", true, "Optional. Concepts input file");
        options.addOption("o", "output", true, "Destination file");
        options.addOption("d", "database", true, "Database location");
        options.addOption("s", "show", true, "Show reasoning details for concept(s)");

        String helpString = "-t <triples input file> -c <concepts input file> -o <output file>. Optionaly -d <database location>, -s <'all' | {concept ids}>. Try -h for more help";
        
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
        String show = commandLine.getOptionValue('s');
        String db = commandLine.getOptionValue('d');

        testInputs(helpString, commandLine, output, concepts, triples, db, show);
        callback.runProgram(concepts, triples, output, db, show);
    }
    
    public static Set<Long> parseShowString(String show){
        Set<Long> resultSet = new HashSet<Long>();
        Iterator<String> splitIt = Splitter.on(',').split(show.substring(1, show.length() - 1)).iterator();
        while (splitIt.hasNext()){
            resultSet.add(Long.parseLong(splitIt.next().trim()));
        }
        return resultSet;
    }
    
    private static void testInputs(String helpString, CommandLine commandLine,
            String output, String concepts, String triples, String db, String show) {
        if (commandLine.hasOption('h')) {
            System.out.println("-h, --help\t\tPrint this help menu\n" +
                    "-t. --triples\t\tFile containing all the relationships that you want to process, aka 'Relationships_Core'\n" +
                    "-c, --concepts\t\tOptional. File containing all the concepts referenced in the relationships file, aka 'Concepts_Core'\n" +
                    "-o, --output\t\tDestination file to write the canonical output results to\n" +
                    "-d, --database\t\tOptional. Specify location of database file. If not specified, \n\t\t\tdefaults to an in-memory database (minimum 3Gb of heap space required)\n" + 
                    "-s, --show\t\tOptional. Show reasoning details for concept(s). \n\t\t\tEither 'all' or a set of concept ids like '{c1id,c2id,etc.}'");
                    
            System.exit(0);
        }
        
        if ((output == null) || (triples == null) ||
                (output.isEmpty()) || triples.isEmpty()){
            System.out.println("Invalid parameter configuration. Usage is: " + helpString);
            System.exit(-1);
        }
        
        if (((concepts != null) && !concepts.isEmpty()) && !new File(concepts).isFile()){
            System.out.println("Unable to locate concepts input file '" + concepts + "'");
            System.exit(-1);
        }
        
        if (!new File(triples).isFile()){
            System.out.println("Unable to locate triples input file '" + triples + "'");
            System.exit(-1);
        }
        
        try {
            new FileOutputStream(new File(output));
        } catch (IOException e) {
            System.out.println("Unable to write to output file '" + output +"'. Check your permissions and path.");
            System.exit(-1);
        }
        
        if (show != null){
            if (show.startsWith("{") && show.endsWith("}")){
                try{
                    parseShowString(show);
                }catch (NumberFormatException e){
                    System.out.println("Value for show option '" + show + "'is not valid");
                    System.exit(-1);                    
                }
            }
            else if (!show.toUpperCase().equals(SHOW_ALL)){
                System.out.println("Value for show option '" + show + "'is not valid");
                System.exit(-1);                
            }
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
