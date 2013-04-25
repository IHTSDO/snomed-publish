IHTSDO Snomed Publication Tools
===============================

Canonical Form Library
----------------------

This library provides an API for creating the canonical form of an ontology.

The API has a single method in class [CanonicalAlgorithm](src/main/java/com/ihtsdo/snomed/canonical/CanonicalAlgorithm.java) with this signature:

    public Set<RelationshipStatement> runAlgorithm(Collection<Concept> concepts, boolean showDetails, Set<Long> showDetailsConceptIds)

This method will generate the set of canonical form statements for a set of input concepts. 

If showDetails is true, and showDetailsConceptIds is null or empty, the method will log to info detailed information about the reasoning for each concept. WARNING! This could be a lot of data, so use with care. If showDetails is true, and showDetailsConceptIds contains one or more concept ids, the method will log detailed reasoning information for these concepts only.

Example usage:

    Set<RelationshipStatement> resultStatements = new CanonicalAlgorithm().runAlgorithm(concepts, false, null);


The rules for the transformation taking place can be found in [this PDF document](https://github.com/sparkling/snomed-publish/blob/master/doc/doc1_CanonicalTableGuide_Current-en-US_INT_20130131.pdf?raw=true) [PDF].

You will need to have the Java 7 JDK and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the root project directory (one up from this folder) and type:

    mvn clean package
    
The distribution jar file can be found at canonical/target/canonical.jar after this. This library can be distributed as this single file.
