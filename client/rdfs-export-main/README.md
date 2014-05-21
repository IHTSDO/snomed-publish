#RDF Schema Generator

Command line tool for converting a version of Snomed into RDF Schema RDF/XML. For more information on implementation, see the [Import Export library](/lib/importexport). 

###Building
You will need to have the Java 7 JDK and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it. To build the distribution, enter the root project directory (two up from this folder) and type:

    mvn clean package
    
The distribution jar file can be found at lib/rdfs-export-main/target/rdfs-export.jar after this. No other file is required in order to run the program, and can be distributed as this single file.

###Usage

For help on how to run the program, type:

    java -jar target/rdfs-export.jar -h

which produces

	-h,   --help			Print this help menu
	-t.   --triples			File containing relationships
	-c,   --concepts		Optional. File containing concepts
	-d,   --descriptions	Optional. File containing descriptions
	-if,  --inputformat		File format of input files. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'
	-of,  --outputformat	Optional. Format to serialise RDF Schema to. 
	                       	One of 'RDF/XML', 'RDF/XML-ABBREV', 'N-TRIPLE', 'N3' or 'TURTLE'
	-o,   --output			Optional. File to serialise RDF Schema to. Must be used together with '-of' option
	-db,  --database		Optional. Specify location of database file. If not specified,
							defaults to an in-memory database (minimum 3Gb of heap space required)

When you run this program, you have the option of using either a disk based embedded database, or an in-memory database.
The disk based database is slower to use, but has a smaller memory footprint. If you specify the --location parameter, a disk based database will be used, and the data will be stored in the file specified by this --location parameter. Not specifying the --location parameter forces the use of an in-memory database.

###Example
This sample run
    
	java -Xmx8000m -jar target/rdfs-export.jar -c sct2_Concept_Snapshot_INT_20120731.txt -t sct2_StatedRelationship_Snapshot_INT_20120731.txt -d sct2_Description_Snapshot-en_INT_20120731.txt -if RF2 -o /tmp/out -of RDF/XML

produces this output

	Using an in-memory database
	Initialising database
	Logging Provider: org.jboss.logging.Log4jLoggerProvider
	Importing ontology "Jena import"
	Populating concepts
	Populated 396305 concepts
	396305 concepts to update
	Completed concepts import in 10 seconds
	Populating descriptions
	Populated 1178104 descriptions
	Completed descriptions import in 18 seconds
	Populating statements
	Populated 667180 statements
	Creating isA hierarchy
	Created 400392 isA statements
	Populating display name cache
	Updated 1 display names
	Completed import in 49 seconds
	Exporting to RDF/XML
	Processed 10000 statements
	Processed 20000 statements
	...
	Processed 640000 statements
	Processed 650000 statements
	Processed 660000 statements
	Completed RDF Schema export in 142 seconds
	Closing database
	Overall program completion in 200 seconds
