IHTSDO Snomed Publication Tools
===============================

Canonical Form Library
----------------------

This library provides an API for creating the canonical form of an ontology.

The API has two public methods in class [CanonicalAlgorithm](src/main/java/com/ihtsdo/snomed/service/CanonicalAlgorithm.java) with this signature:

    public Set<Statement> runAlgorithm(Collection<Concept> concepts, boolean showDetails, Set<Long> showDetailsConceptIds)
    public Set<Statement> runAlgorithm(Collection<Concept> concepts, boolean showDetails)

This method will generate the set of canonical form statements for a set of input concepts. 

If showDetails is true, and showDetailsConceptIds is null or empty, the method will log to info detailed information about the reasoning for each concept. WARNING! This could be a lot of data, so use with care. 

If showDetails is true, and showDetailsConceptIds contains one or more concept ids, the method will log detailed reasoning information for these concepts only, and is probably how you would like to use this feature.

Example usage:

    Set<Statement> resultStatements = new CanonicalAlgorithm().runAlgorithm(concepts, false);


The rules for the transformation taking place can be found in [this PDF document](http://goo.gl/Oh1RJX) [PDF], with an updated section to be found on [this wiki](https://sites.google.com/a/ihtsdo.org/snomed-documentation/algorithm/canonical/algorithm).

You will need to have the Java 7 JDK and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the root project directory (two up from this folder) and type:

    mvn clean install package
    
You can find the distribution jar file at lib/canonical/target/canonical.jar.
