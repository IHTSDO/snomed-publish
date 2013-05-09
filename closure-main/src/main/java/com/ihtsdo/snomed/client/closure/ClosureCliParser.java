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

public class ClosureCliParser {

    public static final String SHOW_ALL = "ALL";
    
    public void parse(String[] args, ClosureMain callback) throws IOException, ParseException{

        //Create a parser using Commons CLI
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("i", "input", true, "Input file in RF2 format");
        options.addOption("o", "output", true, "Destination file");
        options.addOption("d", "database", true, "Database location");

        String helpString = "-i <input file> -o <output file> . Optionaly -d <database location>. Try -h for more help";
        
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
        String input = commandLine.getOptionValue('i');
        String db = commandLine.getOptionValue('d');

        testInputs(helpString, commandLine, output, input, db);
        callback.runProgram(input, output, db);
    }
    
    
    private static void testInputs(String helpString, CommandLine commandLine,
            String output, String input, String db) {
        if (commandLine.hasOption('h')) {
            System.out.println("-h, --help\t\tPrint this help menu\n" +
                    "-i. --input\t\tInput file in RF2 format, containing statements\n" +
                    "-o, --output\t\tDestination file to write the transitive closure results to, in simple child-parent format\n" +
                    "-d, --database\t\tOptional. Specify location of database file. If not specified, \n\t\t\tdefaults to an in-memory database\n"); 
                    
            System.exit(0);
        }
        
        if ((output == null) || (input == null)|| output.isEmpty() || input.isEmpty()){
            System.out.println("Invalid parameter configuration. Usage is: " + helpString);
            System.exit(-1);
        }
        
        if (!new File(input).isFile()){
            System.out.println("Unable to locate concepts input file '" + input + "'");
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
