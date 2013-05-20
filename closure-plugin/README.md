IHTSDO Snomed Publication Tools
===============================

Canonical Form Maven Mojo Plugin
--------------------------------

This plugin project is a wrapper for the [transitive closure library](/closure), and makes it available as a [Maven Plugin](http://maven.apache.org/guides/mini/guide-configuring-plugins.html).

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

For a description of these parameters, have a look at the documentation for the [runnable jar project](/closure-main). 

For more information on the transitive closure algorithm, take a look at the [documentation on our wiki](https://sites.google.com/a/ihtsdo.org/snomed-publish/algorithm/transitive-closure). Here you can also find documentation on the various [input formats](https://sites.google.com/a/ihtsdo.org/snomed-publish/formats).

The output is generated in the [child-parent format](https://sites.google.com/a/ihtsdo.org/snomed-publish/formats/child-parent-format), which is a two-column layout of concept identifiers where the second column values are simply the parent concepts of the first column values. 
