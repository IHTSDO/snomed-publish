IHTSDO Snomed Publication Tools
===============================

Concept browser web application
-------------------------------

A one-page web application for browsing the concept object graph. The page details
- a concept's attributes
- all triples referencing the concept as subject, predicate or object
- all parent (isA) and child concepts
- all primitive super concepts

Functionality we would like to add:
- A google like-search functionality, implemented using Apache Lucene
- Ontology management (import/export/CRUD)

The web application relies on the existince of a database containing all the concepts, triples, and ontologies, to allready be present. This distrubution does **not** contain the data nescessary to run this application.

To build the distribution, enter the root project directory (on up from this folder) and type:

    mvn clean package

The distribution war file can be found at web/target/web.war after this, and can run in any Servlet web continer, e.g. apache tomcat or jetty.

You can also run the application straight from maven, by going into the 'web' folder and typing

    mvn jetty:run
    
This launches the jetty web server, and you can access the application here: http://localhost:8080/browse/ontology/1/concept/420881009

The username and password for accessing the maven jetty:run application can be found in [jetty-users.properties](src/main/resources/jetty-users.properties)

For configuring the database connection, please see [database.properties](src/main/resources/database.properties) and [browse.properties](src/main/resources/browse.properties). 

For generating the database to be used for the web application, we usually modify the configuration for the test application, so that in [persistence.xml](../importexport/src/main/resources/META-INF/persistence.xml) we set this value:

    <property name="hibernate.hbm2ddl.auto" value="update" />
    
and were we also configure the JDBC driver details to use the same database implementation to match what we want the web application to run. *NOTE:* Make sure that the db file does not exists allready, or that the database instance does not exist.

You can then back out to the parent pom, and run [resetm2.sh](../resetme.2sh) (if you are on a windows machine, have a look inside this file - its just deleting a folder from your local m2 repository), before doing a:
   
    mvn clean install -DskipTests
    
After this, enter the 'test' folder, and run the test program with the 3 inputs "original", "expected", and "generated" (see project test [README](../test/README.md).After the completion of the testing program, you should now have a database containing these 3 ontologies, ready to be used for the web application


