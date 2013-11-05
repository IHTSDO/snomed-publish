#Canonical Form Maven Mojo Plugin

Maven Mojo plugin for creating the transitive closure of isA relationships. For more information on the internals of this tool, have a look at the [Closure library](/lib/closure).

More Information on [Maven Mojo](http://maven.apache.org/developers/mojo-api-specification.html).

###Configuration Details
The goal for this plugin is called 'generate-transitive-closure'.

The configuration for this plugin looks like this:

    <configuration>
        <conceptsFile>/path/to/file</conceptsFile> <!-- optional -->
        <triplesFile>/path/to/file</triplesFile>
        <parserType>RF1, RF2, CANONICAL, or CHILD_PARENT</parserType>
        <outputFile>/path/to/file</outputfile>
        <pageSize>450000</pageSize> <-- Optional. Default is 450000 -->
        <databaseLocation>/path/to/file</databaseLocation> <!-- optional -->
    </configuration>

For a description of these parameters, have a look at the documentation for the [runnable jar project](/client/closure-main). 

The output is generated in the [child-parent format](/lib/importexport), which is a two-column layout of concept identifiers where the second column values are simply the parent concepts of the first column values. 
