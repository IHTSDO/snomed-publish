#Snomed Diff
Finds the difference between two Snomed ontologies.

This is the syntax:

    -h,  --help                 Print this help menu
    -bt. --base-triples         File containing relationships baseline for diff
    -bc, --base-concepts        File containing concepts baseline for diff
    -bd, --base-descriptions	  File containing descriptions baseline for diff
    -bf, --base-format          File format of baseline input files. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'
    -ct. --compare-triples      File containing relationships base for diff
    -cc, --compare-concepts     File containing concepts to compare to baseline
    -cd, --compare-descriptions	File containing descriptions to compare to baseline
    -cf, --compare-format       File format of input files to compare to baseline. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'
    -of, --output-format        File format of extra and missing output files. One of 'CANONICAL' or 'CHILD_PARENT'
    -s,  --strategy             Strategy for how to compare the ontologies. One of 'SUBJECT_OBJECT', SUBJECT_PREDICATE_OBJECT', or 'SERIALISED_ID'
    -e,  --extra                Output file to write triples that exists in the comparator but not in the baseline to
    -m,  --missing              Output file to write triples that exists in the baseline but not in the comparator to
    -db, --database             Optional. Specify location of database file. If not specified, 
                                defaults to an in-memory database

The program can take any combination of base- and compare- triples, concepts and descriptions, but the support of each configuration of these parameters depends on the format specified. Here you will just have to use try and fail, to see what combination works with which format (e.g. the canonical format only supports a triples file) or refer to the [Import Export library](/lib/importexport) for more information.

The program finds all triples that exists in the base-&#42; ontology, and not in the compare-&#42; ontology, prints out the count, and write the missing triples to the file specified by the --missing parameter. 

Similarly, all triples that exists in the compare-&#42; ontology, but not in the base-&#42; ontology, is written out to the file specified by the --extra parameter. 

Statements are compared for equality using the strategy specified by the --strategy parameter. For example, the SUBJECT_OBJECT strategy compares only the subject and object of triples, such that if the subject and object are equivalent between two triples, the triples are identical for the purpose of finding the ontology differences. 

A description of the various input and output formats can be in the [Import Export library](/lib/importexport)

When you run this program, you have the option of using either a disk based embedded database, or an in-memory database. The disk based database is slower to use, but has a smaller memory footprint. If you specify the --database parameter, a disk based database will be used, and the data will be stored in the file specified by this --location parameter. Not specifying the --database parameter forces the use of an in-memory database.

Example configuration, using an in-memory database:

    java -Xms10000m -jar target/diff.jar -bt ../doc/closure2.cp.perl -bf CHILD_PARENT -ct ../doc/closure2.cp.me 
    -cf CHILD_PARENT -of CHILD_PARENT -e extra -m missing -s SUBJECT_OBJECT

produces this outpu:

    Using an in-memory database
    Initialising database
    Importing ontology "Base Ontology"
    Populating concepts
    Populated 296433 concepts
    Populating statements
    Populated 5106062 statements
    Creating isA hierarchy
    Created 5106063 isA statements
    Completed import in 109 seconds
    Importing ontology "Compare-to Ontology"
    Populating concepts
    Populated 296433 concepts
    Populating statements
    Populated 5106062 statements
    Creating isA hierarchy
    Created 5106063 isA statements
    Completed import in 172 seconds
    Getting all base statements
    Done in 69 seconds
    Getting all compare statements
    Done in 62 seconds
    Finding all missing statements
    Found 0 missing statements in 0 seconds
    Finding all extra statements
    Found 0 extra statements in 0 seconds
    Writing extra statements
    Writing missing statements
    Closing database
    Overall program completion in 421 seconds
