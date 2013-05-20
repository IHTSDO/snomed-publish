IHTSDO Snomed Publication Tools
===============================

Transitive Closure 
------------------

Computes the transitive closure for the given input files, and saves the results out in simple child-parent format to the specified output file. 

This is the syntax:

    -h, --help          Print this help menu
    -t. --triples       File containing relationships
    -c, --concepts      Optional. File containing concepts
    -f, --format        File format of input files. One of 'RF1', 'RF2', or 'CANONICAL'
    -o, --output        Destination file to write the transitive closure results to, in simple child-parent format
    -p, --pagesize      Optional. Number of concept records to handle in a single batch.
                        A smaller page size requires less memory, but has poorer performance. Default it 450,000
    -l, --location      Optional. Specify location of database file. If not specified, defaults to an in-memory database
                        with increased memory requirements, but much imnproved performance and lower IO latency

For more information on the transitive closure algorithm, take a look at the [documentation on our wiki](https://sites.google.com/a/ihtsdo.org/snomed-publish/algorithm/transitive-closure). Here you can also find documentation on the various [input formats](https://sites.google.com/a/ihtsdo.org/snomed-publish/formats)

The output is generated in the [child-parent format](https://sites.google.com/a/ihtsdo.org/snomed-publish/formats/child-parent-format)

When you run this program, you have the option of using either a disk based embedded database, or an in-memory database.
The disk based database is slower to use, but has a smaller memory footprint. If you specify the --location parameter, a disk based database will be used, and the data will be stored in the file specified by this --location parameter. Not specifying the --location parameter forces the use of an in-memory database.

If you are having problems with running out of memory, perhaps especially if you are using the in-memory database on a large dataset, you may optionally specify a pagesize. A smaller pagesize forces a smaller batch size when retriving the concepts from the database, clearing the memory between batches. Please note that because concepts are self-referential, there are performance implications of specifying a pageSize smaller than the total number of concepts: This will lead to some concepts being retrieved more than once, and their transitive closure also to be computed more than once. But peak memory usage will be greatly reduced.

You will need to have the Java 7 JDK and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the root project directory (on up from this folder) and type:

    mvn clean package
    
The distribution jar file can be found at closure-main/target/closure.jar after this. No other file is required in order to run the program, and can be distributed as this single file.

For help on how to run the program, type:

    java -jar closure.jar -h
    
You will have to specify the maximum heap size when you run this program. How much you can allocate depends on how much RAM is avaiable, but we recommend setting this value fairly high, if possible. If you are wondering about memory requirements for your particular inputs, launch [>jconsole](http://docs.oracle.com/javase/6/docs/technotes/guides/management/jconsole.html) on the command line, and attach to the running closure process for more information. Memory requirements can be further reduced by following the instructions above for pageSize and location.

Example usage:
    
    java -Xmx10000m -jar target/closure.jar -t sct2_Relationship_Snapshot_INT_20120731.txt -f RF2 -o results.cp

The output from the console will look something like this:

    Using an in-memory database
    Initialising database
    Importing ontology "Transitive Closure Input"
    Populating concepts
    Populated 344928 concepts
    Populating statements
    Populated 2331543 statements
    Creating isA hierarchy
    Created 723099 isA statements
    Completed import in 81 seconds
    Running algorithm
    Running concept batch with pagesize 450000
    Batch completed in 156 seconds
    Completed algorithm in 156 seconds with 344928 concepts
    Overall program completion in 245 seconds

and the results would be stored in results.cp.
