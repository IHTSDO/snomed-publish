#Canonical Maven MOJO Plugin
Maven Mojo plugin for creating the canonical form to go with a release bundle. For more information on the internals of this tool, have a look at the [Canonical library](/lib/canonical).

More Information on [Maven Mojo](http://maven.apache.org/developers/mojo-api-specification.html).

###Configuration Details
The goal for this plugin is called 'generate-canonical'.

The configuration for this plugin looks like this:

    <configuration>
        <conceptFile>/path/to/file</conceptFile>
        <relationshipFile>/path/to/file</relationshipFile>
        <outputFile>/path/to/file</outputfile>
        <databaseLocation>/path/to/file</databaseLocation> <!-- optional -->
        <show>concept_id</show> <!-- Optional -->
    </configuration>

For a description of these parameters, have a look at the documentation for the [runnable jar project](/client/canonical-main). 

The output is generated in the [child-parent format](/lib/importexport), which is a two-column layout of concept identifiers where the second column values are simply the parent concepts of the first column values. 
