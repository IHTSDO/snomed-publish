IHTSDO Snomed Publication Toolssnomed-publish
=============================================

Canonical Form
--------------

This program takes an an input 2 text files:

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

The rules for the transformation taking place can be found in this PDF document.

When you run this program, you have the option of using either a disk based embedded database (H2), or an in-memory database.
The disk based database is slower to use, but requires a smaller memory footprint. The in-memory database requires about 
2Gb of heap space, to be specified on launch using the Java option '-Xmx2000m'.

You will need to have the Java 7 JRE and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the project directory and type:

    mvn clean package
    
The distribution jar file can be found at target/canonical.jar after this. No other file is required in order to run the program,
and can be distributed as a single file.

For help on how to run the program, type:

    java -jar canonical.jar -h
    
You will then see this output:

    -h, --help      Print this help menu
    -t. --triples   File containing all the relationships that you want to process, aka 'Relationships_Core'
    -c, --concepts  File containing all the concepts referenced in the relationships file, aka 'Concepts_Core'
    -o, --output    Destination file to write the canonical output results to
    -d, --database  Optional. Specify location of database file. If not specified, 
                    defaults to an in-memory database (minium 2Gb of heap space required)

So 
