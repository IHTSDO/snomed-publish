IHTSDO Snomed Publication Tools
===============================

Transitive Closure Library
--------------------------

This library provides an API for creating the transitive closure of an ontology's isA statements

The API has a single method in class [TransitiveClosureAlgorithm](src/main/java/com/ihtsdo/snomed/service/TransitiveClosureAlgorithmm.java) with this signature:

    public void runAlgorithm(Collection<Concept> concepts, OntologySerialiser serialiser, EntityManager em)

This method will generate all transitive closures for all isA statements for a set of input concepts. 

The transitive closures are written out to an outputfile using an [OntologySerialiser](/importexport/src/main/java/com/ihtsdo/snomed/service/serialiser/OntologySerialiser.java)

Example usage (Java 7):

    try(FileWriter fw = new FileWriter(outputFile); BufferedWriter bw = new BufferedWriter(fw)){
        new TransitiveClosureAlgorithm().runAlgorithm(concepts, SerialiserFactory.getSerialiser(Form.CHILD_PARENT, bw, em);
    }

Please note that this implementation relies on a JPA 2 [EntityManager](http://docs.oracle.com/javaee/6/api/javax/persistence/EntityManager.html), in order to improve performance.

The rules for the transformation taking place can be found in [this PDF document](https://github.com/sparkling/snomed-publish/blob/master/doc/doc1_CanonicalTableGuide_Current-en-US_INT_20130131.pdf?raw=true) [PDF], with an updated section to be found on [this wiki](https://sites.google.com/a/ihtsdo.org/snomed-publish/canonical/algorithm).

You will need to have the Java 7 JDK and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the root project directory (one up from this folder) and type:

    mvn clean package
    
The distribution jar file can be found at canonical/target/canonical.jar after this. This library can be distributed as this single file.
