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

This distrubution does **not** contain the dataset nescessary to run this application. To run this application, you must first create a database with ontologies, concepts and triple statements. See below for instructions on how to create this.

To build this distribution, enter the root project directory (one up from this folder) and type:

    mvn clean package

The distribution war file can be found at web/target/web.war after this, and can be deployed to any Servlet web continer, e.g. Apache Tomcat or Eclipse Jetty.

You can also run the application straight from the checkout, by going into the 'web' folder and typing:

    mvn jetty:run
    
You can then access the application [here](http://localhost:8080/browse/ontology/1/concept/420881009).

The username and password for accessing the maven jetty:run application can be found in [jetty-users.properties](src/main/resources/jetty-users.properties). For configuring the database connection, please see [database.properties](src/main/resources/database.properties).

For populating the database to be used for the web application, we usually modify the configuration for the test application so that the database does not get destroyed at the end of a run. You then end up with the "original", "expected", and "generated" ontologies that you can use for the web application. Modify [persistence.xml](../importexport/src/main/resources/META-INF/persistence.xml), to set this value:

    <property name="hibernate.hbm2ddl.auto" value="update" />
    
and configure the JDBC driver to match the configuration you want for your web application.

Back out to the parent pom, and run [resetm2.sh](../resetme.2sh) (if you are on windows, have a look inside this file - its just deleting a folder from your %HOME%/.m2/repository folder). Then build the test application by running:
   
    mvn clean install -DskipTests
    
After this, enter the 'test' folder, and run the test program with the 3 inputs "original", "expected", and "generated" (see project test [README](../test/README.md) to create the database. Simply point the web application to this database and launch your servlet application server.
