package com.ihtsdo.snomed.client.manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.ihtsdo.snomed.service.parser.HibernateParserFactory;


public class ManifestCliParser {

    public static final String DRIVER_KEY               = "driver";
    public static final String URL_KEY                  = "url";
    public static final String PASSWORD_KEY             = "password";
    public static final String USER_KEY                 = "username";
    public static final String HIBERNATE_DIALECT_KEY    = "hibernate.dialect";
    
    public void parse(String[] args, ManifestMain callback) throws IOException, ParseException, TransformerException{
        //Create a parser using Commons CLI
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("c", "concepts", true, "Concepts input file");
        options.addOption("d", "descriptions", true, "Descriptions input file");
        options.addOption("f", "format", true, "Input format");
        options.addOption("r", "root", true, "Tag folder"); 
//        options.addOption("o", "output", true, "Input format"); //optional
        options.addOption("db", "database", true, "Database location"); //optional    

        String helpString = "-c <concepts input file> -d <descriptions input file> -f <input files format> -r <release root folder>. Optionally, -db <database file>. Try -h for more help";
        
        // Parse the program arguments
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            System.out.println("Unrecognised option. Usage is " + helpString);
            System.exit(1);
        }

        // Set the appropriate variables based on supplied options
        String descriptions = commandLine.getOptionValue('d');
        String concepts = commandLine.getOptionValue('c');
        String root = commandLine.getOptionValue('r');
        String format = commandLine.getOptionValue('f');
        //String output = commandLine.getOptionValue('o');
        String db = commandLine.getOptionValue("db");

        testInputs(helpString, commandLine, descriptions, concepts, root, format, db);
        
        
        
        File conceptsFile = new File(concepts);
        File descriptionsFile = new File(descriptions);
        File rootFile = new File(root);
        //File outputFile = ((output != null) && !output.isEmpty()) ? new File(output) : new File(ManifestMain.DEFAULT_TARGET_FILENAME);
        if ((concepts != null) && !concepts.isEmpty()){
            conceptsFile = new File(concepts);
        }
        if ((descriptions != null) && !descriptions.isEmpty()){
            descriptionsFile = new File(descriptions);
        }
        
        callback.runProgram(conceptsFile, descriptionsFile, rootFile, HibernateParserFactory.Parser.valueOf(format), db);
    }
    
    private static void testInputs(String helpString, CommandLine commandLine,
            String descriptions, String concepts, String root, String format, String db)
    {
        if (commandLine.hasOption('h')) {
            System.out.println(
                    "\nYou need to specify mimetype.properties location as a system property like this:\n" +
                    "  java -Dmimetype.properties=mimetype.properties -Xmx2000m -jar manifest.jar ...\n\n" +
                    "-h, --help\t\tPrint this help menu\n" +
                    "-c, --concepts\t\tFile containing concepts\n" +
                    "-d, --descriptions\tFile containing descriptions\n" +
                    "-f, --format\t\tFile format of input files. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'\n" +
                    "-r,  --root\t\tRoot folder of the release files to create a manifest for\n" +
                    //"-o, --output\t\tOptional. Destination file to write the manifest xml file to\n" +
                    "-db, --database\t\tOptional. Specify location of database file. If not specified, \n\t\t\tdefaults to an in-memory database (minimum 3Gb of heap space required)\n"
                    );
            System.exit(0);
        }
        
        if ((format == null) || format.isEmpty()){
            System.out.println("Format parameter not specified. Usage is: " + helpString);
            System.exit(-1);
        }        
        
        try {
            HibernateParserFactory.Parser.valueOf(format);
        } catch (Exception e1) {
            System.out.println("Format unknown. Usage is: " + helpString);
            System.exit(-1);
        }
        
        if ((concepts == null) || (descriptions == null) || (root == null) ||
                concepts.isEmpty() || descriptions.isEmpty() || root.isEmpty())
        {
            System.out.println("Invalid parameter configuration. Usage is: " + helpString);
            System.exit(-1);
        }
        
        if (!new File(concepts).isFile()){
            System.out.println("Unable to locate concepts input file '" + concepts + "'");
            System.exit(-1);
        }
        if (!new File(descriptions).isFile()){
            System.out.println("Unable to locate descriptions input file '" + descriptions + "'");
            System.exit(-1);
        }    
        if (!new File(root).isDirectory()){
            System.out.println("Unable to locate root directory '" + root + "'");
            System.exit(-1);
        }
        
        String mimetypes = System.getProperty("mimetypes"); 
        
        if ((mimetypes == null) || mimetypes.isEmpty()){
            System.out.println("You need to specify location of mimetype.properties, like this -Dmimetypes=mimetype.properties");
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

//        if ((output != null) && (!output.isEmpty())){
//            new File(output).delete();
//            try {
//                if (!new File(output).createNewFile()){
//                    System.out.println("Unable to create output file '" + output + "'");
//                    System.exit(-1);    
//                }
//            } catch (IOException e) {
//                System.out.println("Unable to create output file '" + output + "'");
//                e.printStackTrace();
//                System.exit(-1);  
//            }
//        }
    }    
}
