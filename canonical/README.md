IHTSDO Snomed Publication Tools
===============================

Canonical Form
--------------

This program takes as an input 2 text files:

1. Set of concepts of this form:

        CONCEPTID  CONCEPTSTATUS	FULLYSPECIFIEDNAME	CTV3ID	SNOMEDID	ISPRIMITIVE
        280844000	0	Entire body of seventh thoracic vertebra (body structure)	Xa1Y9	T-11875	1
        280845004	0	Entire body of eighth thoracic vertebra (body structure)	Xa1YA	T-11876	1
        etc.

2. Set of relationships of this form:

        RELATIONSHIPID  CONCEPTID1	RELATIONSHIPTYPE	CONCEPTID2	CHARACTERISTICTYPE	REFINABILITY	RELATIONSHIPGROUP
        100000028	280844000	116680003	71737002	0	0	0
        100001029	280845004	116680003	280737002	0	0	0
        etc.
    
and produces an output file called 'canonical form' of this form:

    CONCEPTID1  RELATIONSHIPTYPE	CONCEPTID2	RELATIONSHIPGROUP
    280844000	116680003	71737002	0
    280845004	116680003	280737002	0

The rules for the transformation taking place can be found in [this PDF document](https://github.com/sparkling/snomed-publish/blob/master/doc/doc1_CanonicalTableGuide_Current-en-US_INT_20130131.pdf?raw=true) [PDF].

When you run this program, you have the option of using either a disk based embedded database (H2), or an in-memory database.
The disk based database is slower to use, but has a smaller memory footprint. The in-memory database requires about 
3Gb of heap space ('-Xmx3000m').

You will need to have the Java 7 JDK and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the root project directory (on up from this folder) and type:

    mvn clean package
    
The distribution jar file can be found at canonical/target/canonical.jar after this. No other file is required in order to run the program,
and can be distributed as this single file.

For help on how to run the program, type:

    java -jar canonical.jar -h
    
You will then see this output:

    -h, --help      Print this help menu
    -t. --triples   File containing all the relationships that you want to process, aka 'Relationships_Core'
    -c, --concepts  File containing all the concepts referenced in the relationships file, aka 'Concepts_Core'
    -o, --output    Destination file to write the canonical output results to
    -d, --database  Optional. Specify location of database file. If not specified, 
                    defaults to an in-memory database (minium 2Gb of heap space required)
    -s, --show      Optional. Show reasoning details for concept(s). 
                    Either 'all' or a set of concept ids like '{c1id,c2id,etc.}'

For example, to launch with an in-memory database, use this command:

    java -Xmx3000m -jar -t relationships.input.file.txt -c concepts.input.file.txt -o canonical.out.txt

Don't forget the '-Xmx=3000m' option, or the program will not complete. 

Or, to launch with a disk backed database, you can use this command, requiring only a minimum amount of heap space:

    java -jar -t relationships.input.file.txt -c concepts.input.file.txt -o canonical.out.txt -d /tmp/canonical.tmp.db
    
The output from the console will look something like this:

    Using an in-memory database
    Initialising database
    Populating database
    Populating concepts for ontology [LongForm(1)]
    Populated [396109] concepts
    Populating relationships for ontology [LongForm(1)]
    Populated [1446149] relationships
    Creating isA hierarchy
    Created [539711] isA relationships
    Completed import in 28 seconds
    Running algorithm
    Calculating immidiate primitive concepts
    Found [434197] immidiate primitive concepts
    Calculating unshared defining characteristics
    Found [272177] unshared defining characteristics
    Completed algorithm in 106 seconds with [706374] statements
    Writing results to canonical.out.txt
    Wrote 706376 lines
    Closing database
    Overall program completion in 154 seconds

and the results would be stored in canonical.out.txt, as specified above.
