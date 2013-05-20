IHTSDO Snomed Publication Tools
===============================

Database Import
---------------

Utility for importing an ontology to a database, e.g. for use with the web application.

    -h, --help          Print this help menu
    -t. --triples       File containing relationships
    -c, --concepts      File containing concepts
    -d, --descriptions  File containing descriptions
    -f, --format        File format of input files. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'
    -n, --name          Ontology name
    -p, --properties    Properties file with database configuration
    
Sample configuration
    
    java -Xmx6000m -jar target/import.jar -t sct2_Relationship_Snapshot_INT_20130731.txt 
    -d sct2_Description_Snapshot-en_INT_20130731.txt -c sct2_Concept_Snapshot_INT_20130731.txt 
    -f RF2 -n testing -p src/main/resources/database.properties
    
with this database.properties configuration:

    hibernate.dialect=org.hibernate.dialect.MySQLDialect
    driver=com.mysql.jdbc.Driver
    url=jdbc:mysql://localhost/snomed
    username=root
    password=

produces this output

