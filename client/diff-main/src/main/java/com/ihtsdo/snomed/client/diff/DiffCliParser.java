package com.ihtsdo.snomed.client.diff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.ihtsdo.snomed.service.DiffAlgorithmFactory;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory;

public class DiffCliParser {

    public static final String SHOW_ALL                 = "ALL";
    public static final String DRIVER_KEY               = "driver";
    public static final String URL_KEY                  = "url";
    public static final String PASSWORD_KEY             = "password";
    public static final String USER_KEY                 = "username";
    public static final String HIBERNATE_DIALECT_KEY    = "hibernate.dialect";
    
    public void parse(String[] args, DiffMain callback) throws IOException, java.text.ParseException, ParseException{
        //Create a parser using Commons CLI
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("bt", "base-triples", true, "Base triples input file");
        options.addOption("bc", "base-concepts", true, "Base concepts input file");
        options.addOption("bd", "base-descriptions", true, "Base descriptions input file");
        options.addOption("bf", "base-format", true, "Base input format");
        options.addOption("ct", "compare-triples", true, "Compared triples input file");
        options.addOption("cc", "compare-concepts", true, "Compared concepts input file");
        options.addOption("cd", "compare-descriptions", true, "Compared descriptions input file");
        options.addOption("cf", "compare-format", true, "Compared input format");
        options.addOption("of", "output-format", true, "Output file format for extra and missing triples");
        options.addOption("e", "extra", true, "Output for all extra triples");
        options.addOption("s", "strategy", true, "Diff strategy");
        options.addOption("m", "missing", true, "Output for all missing triples");
        options.addOption("db", "database location", true, "Database file location");
        String helpString = "-bt <base triples input file> -bc <base concepts input file> -bd <base descriptions input file> -bf <base input files format> + +" +
        		"-ct <compare triples input file> -cc <compare concepts input file> -cd <compare descriptions input file> -cf <compare input files format> " +
                "-s <diff strategy> -e <output file for extra triples> -m <output file for missing triples> -of <file format for missing and extra triples>. " +
        		" Optionaly -d <database location>. Try -h for more help";
        
        // Parse the program arguments
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            System.out.println("Unrecognised option. Usage is " + helpString);
            System.exit(1);
        }

        // Set the appropriate variables based on supplied options
        String baseDescriptions = commandLine.getOptionValue("bd");
        String baseConcepts = commandLine.getOptionValue("bc");
        String baseTriples = commandLine.getOptionValue("bt");
        String baseFormat = commandLine.getOptionValue("bf");
        String compareDescriptions = commandLine.getOptionValue("cd");
        String compareConcepts = commandLine.getOptionValue("cc");
        String compareTriples = commandLine.getOptionValue("ct");
        String compareFormat = commandLine.getOptionValue("cf");
        String strategy = commandLine.getOptionValue("s");
        String extra = commandLine.getOptionValue("e");
        String missing = commandLine.getOptionValue("m");
        String outputFormat = commandLine.getOptionValue("of");
        String dbLocation = commandLine.getOptionValue("db");

        testInputs(helpString, commandLine, baseDescriptions, baseConcepts, baseTriples, baseFormat,
                compareDescriptions, compareConcepts, compareTriples, compareFormat, strategy, extra, missing, 
                dbLocation, outputFormat);
        
        File extraFile = new File(extra);
        File missingFile = new File(missing);
        
        File baseTriplesFile = new File(baseTriples);
        File baseConceptsFile = null;
        File baseDescriptionsFile = null;
        if ((baseConcepts != null) && !baseConcepts.isEmpty()){
            baseConceptsFile = new File(baseConcepts);
        }
        if ((baseDescriptions != null) && !baseDescriptions.isEmpty()){
            baseDescriptionsFile = new File(baseDescriptions);
        }
        
        File compareTriplesFile = new File(compareTriples);
        File compareConceptsFile = null;
        File compareDescriptionsFile = null;
        if ((compareConcepts != null) && !compareConcepts.isEmpty()){
            compareConceptsFile = new File(compareConcepts);
        }
        if ((compareDescriptions != null) && !compareDescriptions.isEmpty()){
            compareDescriptionsFile = new File(compareDescriptions);
        } 
        
        callback.runProgram(baseConceptsFile, baseTriplesFile, baseDescriptionsFile, HibernateParserFactory.Parser.valueOf(baseFormat),
                compareConceptsFile, compareTriplesFile, compareDescriptionsFile, HibernateParserFactory.Parser.valueOf(compareFormat),
                DiffAlgorithmFactory.DiffStrategy.valueOf(strategy), extraFile, missingFile, dbLocation, SnomedSerialiserFactory.Form.valueOf(outputFormat));
    }

    private void testInputs(String helpString, CommandLine commandLine,
            String baseDescriptions, String baseConcepts, String baseTriples,
            String baseFormat, String compareDescriptions,
            String compareConcepts, String compareTriples,
            String compareFormat, String strategy, String extra, String missing, 
            String dbLocation, String outputFormat) 
    {
        if (commandLine.hasOption('h')) {
            System.out.println("\n-h,  --help\t\t\tPrint this help menu\n" +
                    "-bt. --base-triples\t\tFile containing relationships baseline for diff\n" +
                    "-bc, --base-concepts\t\tFile containing concepts baseline for diff\n" +
                    "-bd, --base-descriptions\tFile containing descriptions baseline for diff\n" +
                    "-bf, --base-format\t\tFile format of baseline input files. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'\n" +
                    "-ct. --compare-triples\t\tFile containing relationships base for diff\n" +
                    "-cc, --compare-concepts\t\tFile containing concepts to compare to baseline\n" +
                    "-cd, --compare-descriptions\tFile containing descriptions to compare to baseline\n" +
                    "-cf, --compare-format\t\tFile format of input files to compare to baseline. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'\n" +
                    "-of, --output-format\t\tFile format of extra and missing output files. One of 'CANONICAL' or 'CHILD_PARENT'\n" +
                    "-s,  --strategy\t\t\tStrategy for how to compare the ontologies. One of 'SUBJECT_OBJECT', SUBJECT_PREDICATE_OBJECT', or 'SERIALISED_ID'\n" +
                    "-e,  --extra\t\t\tOutput file to write triples that exists in the comparator but not in the baseline to\n" +
                    "-m,  --missing\t\t\tOutput file to write triples that exists in the baseline but not in the comparator to\n" +
                    "-db, --database\t\t\tOptional. Specify location of database file. If not specified, \n\t\t\t\tdefaults to an in-memory database\n"); 
            
            System.exit(0);
        }
        
        if ((baseFormat == null) || baseFormat.isEmpty())
        {
            System.out.println("Baseline format parameter not specified. Usage is: " + helpString);
            System.exit(-1);
        }
        
        if ((compareFormat == null) || compareFormat.isEmpty())
        {
            System.out.println("Comparator format parameter not specified. Usage is: " + helpString);
            System.exit(-1);
        }
        if ((outputFormat == null) || outputFormat.isEmpty())
        {
            System.out.println("Output file format parameter not specified. Usage is: " + helpString);
            System.exit(-1);
        }        
        
        if ((baseTriples == null) || baseTriples.isEmpty())
        {
            System.out.println("Base triples file not specified. Usage is: " + helpString);
            System.exit(-1);
        }

        if ((compareTriples == null) || compareTriples.isEmpty())
        {
            System.out.println("Compare triples file not specified. Usage is: " + helpString);
            System.exit(-1);
        }
        
        if ((strategy == null) || strategy.isEmpty())
        {
            System.out.println("diff strategy not specified. Usage is: " + helpString);
            System.exit(-1);
        }
        
        if ((extra == null) || extra.isEmpty())
        {
            System.out.println("extra output file not specified. Usage is: " + helpString);
            System.exit(-1);
        }
        if ((missing == null) || missing.isEmpty())
        {
            System.out.println("missing output file not specified. Usage is: " + helpString);
            System.exit(-1);
        }
        
        try{
            HibernateParserFactory.Parser.valueOf(baseFormat);            
        }catch (IllegalArgumentException e){
            System.out.println("Base format specified '" + baseFormat + "' not supported. Use 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'");
            System.exit(-1);            
        }        
        try{
            HibernateParserFactory.Parser.valueOf(compareFormat);            
        }catch (IllegalArgumentException e){
            System.out.println("Compare format specified '" + compareFormat + "' not supported. Use 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'");
            System.exit(-1);            
        }
        try{
            SnomedSerialiserFactory.Form.valueOf(outputFormat);
        }catch (IllegalArgumentException e){
            System.out.println("Output file format specified '" + outputFormat + "' not supported. Use 'CANONICAL' or 'CHILD_PARENT'");
            System.exit(-1);
        }
        try{
            DiffAlgorithmFactory.DiffStrategy.valueOf(strategy);            
        }catch (IllegalArgumentException e){
            System.out.println("Diff strategy specified '" + baseFormat + "' not supported. Use 'SUBJECT_OBJECT', 'SUBJECT_PREDICATE_OBJECT', or 'SERIALISED_ID'");
            System.exit(-1);            
        } 
       
        if (!new File(baseTriples).isFile()){
            System.out.println("Unable to locate base triples input file '" + baseTriples + "'");
            System.exit(-1);
        }
        
        if (!new File(compareTriples).isFile()){
            System.out.println("Unable to locate compare triples input file '" + compareTriples + "'");
            System.exit(-1);
        }
        
        try {
            new FileOutputStream(new File(extra));
        } catch (IOException e) {
            System.out.println("Unable to write to extra file '" + extra +"'. Check your permissions and path.");
            System.exit(-1);
        }

        try {
            new FileOutputStream(new File(missing));
        } catch (IOException e) {
            System.out.println("Unable to write to missing file '" + missing +"'. Check your permissions and path.");
            System.exit(-1);
        } 
        
        if (!new File(missing).isFile()){
            System.out.println("Unable to locate output file for missing triples '" + compareTriples + "'");
            System.exit(-1);
        }
        
        if ((baseConcepts != null) && !baseConcepts.isEmpty() && !new File(baseConcepts).isFile()){
            System.out.println("Unable to locate base concepts input file '" + baseConcepts + "'");
            System.exit(-1);
        }
        
        if ((compareConcepts != null) && !compareConcepts.isEmpty() && !new File(compareConcepts).isFile()){
            System.out.println("Unable to locate compare concepts input file '" + compareConcepts + "'");
            System.exit(-1);
        } 
        
        if ((baseDescriptions != null) && !baseDescriptions.isEmpty() && !new File(baseDescriptions).isFile()){
            System.out.println("Unable to locate base descriptions input file '" + baseDescriptions + "'");
            System.exit(-1);
        }    
        
        if ((compareDescriptions != null) && !compareDescriptions.isEmpty() && !new File(compareDescriptions).isFile()){
            System.out.println("Unable to locate compare descriptions input file '" + compareDescriptions + "'");
            System.exit(-1);
        }
        if ((dbLocation != null) && (!dbLocation.isEmpty())){
            try {
                new FileOutputStream(new File(dbLocation));
            } catch (IOException e) {
                System.out.println("Unable to write to database file '" + dbLocation +"'. Check your permissions and path.");
                System.exit(-1);
            }
        }        
    }    
}
