IHTSDO Snomed Publication Tools
===============================

Database Import
---------------

Utility for importing an ontology to a database, e.g. for use with the web application.

This is the syntax:

    -h, --help          Print this help menu
    -t. --triples       File containing relationships
    -c, --concepts      File containing concepts
    -d, --descriptions  File containing descriptions
    -f, --format        File format of input files. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'
    -n, --name          Ontology name
    -p, --properties    Properties file with database configuration
    
You will need to have the Java 7 JDK and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the root project directory (on up from this folder) and type:

    mvn clean package
    
The distribution jar file can be found at import-main/target/import.jar after this. No other file is required in order to run the program, and can be distributed as this single file.

For help on how to run the program, type:

    java -jar import.jar -h
    
Sample configuration
    
    java -Xmx6000m -jar target/import.jar --triples sct2_Relationship_Snapshot_INT_20130731.txt 
    --descriptions sct2_Description_Snapshot-en_INT_20130731.txt --concepts sct2_Concept_Snapshot_INT_20130731.txt 
    --format RF2 --name testing --properties src/main/resources/database.properties
    
with this database.properties configuration:

    hibernate.dialect=org.hibernate.dialect.MySQLDialect
    driver=com.mysql.jdbc.Driver
    url=jdbc:mysql://localhost/snomed
    username=root
    password=

produces this output

    Connecting to database jdbc:mysql://localhost/snomed
    Importing ontology "testing"
    Populating concepts
    Populated 399086 concepts
    Completed concepts import in 202 seconds
    Populating descriptions
    Populated 1187334 descriptions
    Completed descriptions import in 277 seconds
    Populating statements
    Populated 2359102 statements
    Creating isA hierarchy
    Created 733421 isA statements
    Completed import in 1309 seconds
    Closing database
    Overall program completion in 1312 seconds
