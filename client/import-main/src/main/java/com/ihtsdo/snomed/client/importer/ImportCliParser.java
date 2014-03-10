package com.ihtsdo.snomed.client.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.ihtsdo.snomed.model.SnomedFlavours;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;

public class ImportCliParser {

    public static final String SHOW_ALL                 = "ALL";
    public static final String DRIVER_KEY               = "driver";
    public static final String URL_KEY                  = "url";
    public static final String PASSWORD_KEY             = "password";
    public static final String USER_KEY                 = "username";
    public static final String HIBERNATE_DIALECT_KEY    = "hibernate.dialect";
    
    public void parse(String[] args, ImportMain callback) throws IOException, ParseException{
        //Create a parser using Commons CLI
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h",  "help", false, "Print this usage information");
        options.addOption("t",  "triples", true, "Triples input file");
        options.addOption("c",  "concepts", true, "Concepts input file");
        options.addOption("d",  "descriptions", true, "Descriptions input file");
        options.addOption("f",  "format", true, "Input format");
        options.addOption("fl", "flavour", true, "Snomed Flavour, one of {INTERNATIONAL} (more coming)");
        options.addOption("v",  "version", true, "Version Date, of the format 'yyyyMMdd'");
        options.addOption("p",  "properties", true, "Properties file with db config");

        String helpString = "-t <triples input file> -c <concepts input file> -d <descriptions input file>" + 
                " -f <input files format> -fl <flavour> -v <version date> -p <properties file>. Try -h for more help";
        
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
        String triples = commandLine.getOptionValue('t');
        String format = commandLine.getOptionValue('f');
        String versionString = commandLine.getOptionValue('v');
        String flavour = commandLine.getOptionValue("fl");
        String properties = commandLine.getOptionValue('p');

        testInputs(helpString, commandLine, descriptions, concepts, triples, format, versionString, flavour, properties);
        
        Properties p = new Properties();
        p.load(new FileInputStream(properties));
        
        Date version;
        try {
            version = new Date(new SimpleDateFormat("yyyyMMdd").parse(versionString).getTime());
        } catch (java.text.ParseException e1) {
            throw new ParseException("Should not really happen, the testInputs method should have covered this. Bad programmer! Bad! " + e1.getMessage());
        }
        File conceptsFile = null;
        File triplesFile = new File(triples);
        File descriptionsFile = null;
        if ((concepts != null) && !concepts.isEmpty()){
            conceptsFile = new File(concepts);
        }
        if ((descriptions != null) && !descriptions.isEmpty()){
            descriptionsFile = new File(descriptions);
        }
        
        callback.runProgram(conceptsFile, triplesFile, descriptionsFile, p, 
                HibernateParserFactory.Parser.valueOf(format), version, SnomedFlavours.getFlavour(flavour));
    }
    
    private static void testInputs(String helpString, CommandLine commandLine,
            String descriptions, String concepts, String triples, String format, 
            String version, String flavour, String properties) {
        if (commandLine.hasOption('h')) {
            System.out.println("\n-h, --help\t\tPrint this help menu\n" +
                    "-t. --triples\t\tFile containing relationships\n" +
                    "-c, --concepts\t\tFile containing concepts\n" +
                    "-d, --descriptions\tFile containing descriptions\n" +
                    "-f, --format\t\tFile format of input files. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'\n" +
                    "-v, --version\t\tVersion date, of the form 'yyyyMMdd'\n" +
                    "-fl, --flavour\t\tSnomed Flavour. One of {INTERNATIONAL}\n" +
                    "-p, --properties\tProperties file with database configuration\n"); 
                    
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
        
        if ((triples == null) || (properties == null) || (version == null) || (flavour == null) ||
                triples.isEmpty() || properties.isEmpty() || version.isEmpty() || (flavour.isEmpty()))
        {
            System.out.println("Invalid parameter configuration. Usage is: " + helpString);
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
        if ((descriptions != null) && !descriptions.isEmpty() && !new File(descriptions).isFile()){
            System.out.println("Unable to locate descriptions input file '" + descriptions + "'");
            System.exit(-1);
        }        
        
        if (!new File(properties).isFile()){
            System.out.println("Unable to locate properties file '" + properties + "'");
            System.exit(-1);
        }
        if (!flavour.toUpperCase().equals("INTERNATIONAL")){
            System.out.println("Flavour is not one of {INTERNATIONAL}. Flavour was: " + flavour);
            System.exit(-1);
        }
        try {
            new java.sql.Date(new SimpleDateFormat("yyyyMMdd").parse(version).getTime());
        } catch (java.text.ParseException e1) {
            System.out.println("Version was not parseable as a date, it must be of the format 'yyyyDDmm'. Version was: " + version);
            System.exit(-1);
        }
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(properties));
            if (p.getProperty(URL_KEY) == null || p.getProperty(URL_KEY).isEmpty()){
                System.out.println("Properties file does not contain the jdbc '" + URL_KEY + "' parameter");
                System.exit(-1);
            }
            if (p.getProperty(USER_KEY) == null || p.getProperty(USER_KEY).isEmpty()){
                System.out.println("Properties file does not contain the database '" + USER_KEY + "' parameter");
                System.exit(-1);
            }
            if (p.getProperty(PASSWORD_KEY) == null){
                System.out.println("Properties file does not contain the database '" + PASSWORD_KEY + "' parameter");
                System.exit(-1);
            }
            if (p.getProperty(DRIVER_KEY) == null || p.getProperty(DRIVER_KEY).isEmpty()){
                System.out.println("Properties file does not contain the jdbc '" + DRIVER_KEY + "' parameter");
                System.exit(-1);
            }
            if (p.getProperty(HIBERNATE_DIALECT_KEY) == null || p.getProperty(HIBERNATE_DIALECT_KEY).isEmpty()){
                System.out.println("Properties file does not contain the '" + HIBERNATE_DIALECT_KEY + "' parameter");
                System.exit(-1);
            }
        } catch (Exception e) {
            System.out.println("Unable to read properties file '" + properties + "'");
            System.exit(-1);
        }        
        
    }    
}
