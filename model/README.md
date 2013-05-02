IHTSDO Snomed Publication Tools
===============================

Domain Model Library
--------------------

This library contains the domain model for the snomed publication applications, complete with JPA annotations. The classes are:
- [Concept](src/main/java/com/ihtsdo/snomed/canonical/model/Concept.java)
- [Statement] (src/main/java/com/ihtsdo/snomed/canonical/model/Statement.java)
- [Ontology](src/main/java/com/ihtsdo/snomed/canonical/model/Ontology.java)

To build the distribution, enter the root project directory (on up from this folder) and type:

    mvn clean package

The distribution jar file can be found at model/target/model.jar after this. No other file is required in order to run the program, and can be distributed as this single file.
