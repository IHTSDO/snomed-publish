IHTSDO Snomed Publication Tools
===============================

Canonical Form
--------------

This program takes as an input 2 text files:

1. Set of concepts of this form:

        CONCEPTID  CONCEPTSTATUS	FULLYSPECIFIEDNAME	CTV3ID	SNOMEDID	ISPRIMITIVE
        280844000	0	Entire body of seventh thoracic vertebra (body structure)	Xa1Y9	T-11875	1
        280845004	0	Entire body of eighth thoracic vertebra (body structure)	Xa1YA	T-11876	1
        etc.

2. Set of relationships of this form:

        RELATIONSHIPID  CONCEPTID1	RELATIONSHIPTYPE	CONCEPTID2	CHARACTERISTICTYPE	REFINABILITY	RELATIONSHIPGROUP
        100000028	280844000	116680003	71737002	0	0	0
        100001029	280845004	116680003	280737002	0	0	0
        etc.
    
and produces an output file called 'canonical form' of this form:

    CONCEPTID1  RELATIONSHIPTYPE	CONCEPTID2	RELATIONSHIPGROUP
    280844000	116680003	71737002	0
    280845004	116680003	280737002	0

The rules for the transformation taking place can be found in [this PDF document](https://github.com/sparkling/snomed-publish/blob/master/doc/doc1_CanonicalTableGuide_Current-en-US_INT_20130131.pdf?raw=true) [PDF].

When you run this program, you have the option of using either a disk based embedded database (H2), or an in-memory database.
The disk based database is slower to use, but has a smaller memory footprint. The in-memory database requires about 
3Gb of heap space ('-Xmx3000m').

You will need to have the Java 7 JDK and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the root project directory (on up from this folder) and type:

    mvn clean package
    
The distribution jar file can be found at canonical/target/canonical.jar after this. No other file is required in order to run the program,
and can be distributed as this single file.

For help on how to run the program, type:

    java -jar canonical.jar -h
    
You will then see this output:

    -h, --help      Print this help menu
    -t. --triples   File containing all the relationships that you want to process, aka 'Relationships_Core'
    -c, --concepts  File containing all the concepts referenced in the relationships file, aka 'Concepts_Core'
    -o, --output    Destination file to write the canonical output results to
    -d, --database  Optional. Specify location of database file. If not specified, 
                    defaults to an in-memory database (minium 2Gb of heap space required)
    -s, --show      Optional. Show reasoning details for concept(s). 
                    Either 'all' or a set of concept ids like '{c1id,c2id,etc.}'

For example, to launch with an in-memory database, use this command:

    java -Xmx3000m -jar -t relationships.input.file.txt -c concepts.input.file.txt -o canonical.out.txt

Don't forget the '-Xmx=3000m' option, or the program will not complete. 

Or, to launch with a disk backed database, you can use this command, requiring only a minimum amount of heap space:

    java -jar -t relationships.input.file.txt -c concepts.input.file.txt -o canonical.out.txt -d /tmp/canonical.tmp.db
    
The output from the console will look something like this:

    Using an in-memory database
    Initialising database
    Populating database
    Populating concepts for ontology [LongForm(1)]
    Populated [396109] concepts
    Populating relationships for ontology [LongForm(1)]
    Populated [1446149] relationships
    Creating isA hierarchy
    Created [539711] isA relationships
    Completed import in 28 seconds
    Running algorithm
    Calculating immidiate primitive concepts
    Found [434197] immidiate primitive concepts
    Calculating unshared defining characteristics
    Found [272177] unshared defining characteristics
    Completed algorithm in 106 seconds with [706374] statements
    Writing results to canonical.out.txt
    Wrote 706376 lines
    Closing database
    Overall program completion in 154 seconds

and the results would be stored in canonical.out.txt, as specified above.

For determining the reasoning for why particular concept(s) exists in the canonical output, the program has an optional
switch to display the reasoning behind that concept's inclusion/exclusion. By specifying
    
    -s {conceptid1, conceptid2, etc.}
  
the program will print detailed reasoning information like this

    Running algorithm
    Calculating immidiate primitive concepts
    Attempting to find all proximal primitive isKindOf concepts for concept 229395005
    All primitive super concepts for concept 229395005 are {229392008, 243120004, 229373006, 229391001, 138875005, 229390000, 71388002}
    Found that concept 229392008 isA concept 243120004
    Since concept 229392008 isA concept 243120004, concept 243120004 is not an proximal isA for concept 229395005
    Found that concept 229392008 isA concept 229373006
    Since concept 229392008 isA concept 229373006, concept 229373006 is not an proximal isA for concept 229395005
    Found that concept 229392008 isA concept 138875005
    Since concept 229392008 isA concept 138875005, concept 138875005 is not an proximal isA for concept 229395005
    Found that concept 229392008 isA concept 229391001
    Since concept 229392008 isA concept 229391001, concept 229391001 is not an proximal isA for concept 229395005
    Found that concept 229392008 isA concept 229390000
    Since concept 229392008 isA concept 229390000, concept 229390000 is not an proximal isA for concept 229395005
    Found that concept 229392008 isA concept 71388002
    Since concept 229392008 isA concept 71388002, concept 71388002 is not an proximal isA for concept 229395005
    Found that concept 243120004 isA concept 138875005
    Since concept 243120004 isA concept 138875005, concept 138875005 is not an proximal isA for concept 229395005
    Found that concept 243120004 isA concept 71388002
    Since concept 243120004 isA concept 71388002, concept 71388002 is not an proximal isA for concept 229395005
    Found that concept 229373006 isA concept 243120004
    Since concept 229373006 isA concept 243120004, concept 243120004 is not an proximal isA for concept 229395005
    Found that concept 229373006 isA concept 138875005
    Since concept 229373006 isA concept 138875005, concept 138875005 is not an proximal isA for concept 229395005
    Found that concept 229373006 isA concept 71388002
    Since concept 229373006 isA concept 71388002, concept 71388002 is not an proximal isA for concept 229395005
    Found that concept 229391001 isA concept 243120004
    Since concept 229391001 isA concept 243120004, concept 243120004 is not an proximal isA for concept 229395005
    Found that concept 229391001 isA concept 229373006
    Since concept 229391001 isA concept 229373006, concept 229373006 is not an proximal isA for concept 229395005
    Found that concept 229391001 isA concept 138875005
    Since concept 229391001 isA concept 138875005, concept 138875005 is not an proximal isA for concept 229395005
    Found that concept 229391001 isA concept 229390000
    Since concept 229391001 isA concept 229390000, concept 229390000 is not an proximal isA for concept 229395005
    Found that concept 229391001 isA concept 71388002
    Since concept 229391001 isA concept 71388002, concept 71388002 is not an proximal isA for concept 229395005
    Concept 138875005 has no isA relationships, continuing
    Found that concept 229390000 isA concept 243120004
    Since concept 229390000 isA concept 243120004, concept 243120004 is not an proximal isA for concept 229395005
    Found that concept 229390000 isA concept 229373006
    Since concept 229390000 isA concept 229373006, concept 229373006 is not an proximal isA for concept 229395005
    Found that concept 229390000 isA concept 138875005
    Since concept 229390000 isA concept 138875005, concept 138875005 is not an proximal isA for concept 229395005
    Found that concept 229390000 isA concept 71388002
    Since concept 229390000 isA concept 71388002, concept 71388002 is not an proximal isA for concept 229395005
    Found that concept 71388002 isA concept 138875005
    Since concept 71388002 isA concept 138875005, concept 138875005 is not an proximal isA for concept 229395005
    Found that concept 229395005 has proximal primitive isA concept(s) of {229392008}
    Found [434197] immidiate primitive concepts
    Calculating unshared defining characteristics
    Attempting to find all unshared defining characteristics for concept [229395005]
    Relationships for concept [229395005] are {[1467951023: 229395005(260870009)272125009 type:1], [3364178029: 229395005(260686004)129409008 type:0], [3364177023: 229395005(405813007)127863007 type:0], [100000000146834: 229395005(116680003)229392008 type:0], [39411024: 229395005(116680003)229392008 type:0]}
    Testing for existence of relationship [3364178029] in parent primitive concepts
    Concept [229395005] has a primitive parent concept of [229392008]
    Found that parent concept [229392008] has relationship [100000000146831: 229392008(116680003)229391001 type:0]
    Found that parent concept [229392008] has relationship [1467945029: 229392008(260870009)272125009 type:1]
    Found that parent concept [229392008] has relationship [39405021: 229392008(116680003)229391001 type:0]
    Found that parent concept [229392008] has relationship [3467953022: 229392008(260686004)129409008 type:0]
    Found that relationship under test [3364178029: 229395005(260686004)129409008 type:0] is also defined in parent concept as [3467953022: 229392008(260686004)129409008 type:0]
    Removing statement [[3364178029: 229395005(260686004)129409008 type:0]] from output because parent concept [229392008] has defined relationship [[3467953022: 229392008(260686004)129409008 type:0]]
    Found that parent concept [229392008] has relationship [3441868023: 229392008(405813007)27949001 type:0]
    Concept [229395005] has a primitive parent concept of [243120004]
    Found that parent concept [243120004] has relationship [60487025: 243120004(116680003)71388002 type:0]
    Found that parent concept [243120004] has relationship [100000000163141: 243120004(116680003)71388002 type:0]
    Found that parent concept [243120004] has relationship [1519147021: 243120004(260870009)272125009 type:1]
    Concept [229395005] has a primitive parent concept of [229373006]
    Found that parent concept [229373006] has relationship [3532559023: 229373006(260686004)129409008 type:0]
    Found that relationship under test [3364178029: 229395005(260686004)129409008 type:0] is also defined in parent concept as [3532559023: 229373006(260686004)129409008 type:0]
    Removing statement [[3364178029: 229395005(260686004)129409008 type:0]] from output because parent concept [229373006] has defined relationship [[3532559023: 229373006(260686004)129409008 type:0]]
    Found that parent concept [229373006] has relationship [1467907025: 229373006(260870009)272125009 type:1]
    Found that parent concept [229373006] has relationship [100000000146809: 229373006(116680003)243120004 type:0]
    Found that parent concept [229373006] has relationship [2531872027: 229373006(116680003)118710009 type:0]
    Found that parent concept [229373006] has relationship [3286995026: 229373006(116680003)229319000 type:0]
    Found that parent concept [229373006] has relationship [3532560029: 229373006(405813007)61685007 type:0]
    Concept [229395005] has a primitive parent concept of [229391001]
    Found that parent concept [229391001] has relationship [100000000146830: 229391001(116680003)229390000 type:0]
    Found that parent concept [229391001] has relationship [1467943020: 229391001(260870009)272125009 type:1]
    Found that parent concept [229391001] has relationship [39404020: 229391001(116680003)229390000 type:0]
    Found that parent concept [229391001] has relationship [3441867029: 229391001(260686004)129409008 type:0]
    Found that relationship under test [3364178029: 229395005(260686004)129409008 type:0] is also defined in parent concept as [3441867029: 229391001(260686004)129409008 type:0]
    Removing statement [[3364178029: 229395005(260686004)129409008 type:0]] from output because parent concept [229391001] has defined relationship [[3441867029: 229391001(260686004)129409008 type:0]]
    Found that parent concept [229391001] has relationship [3417080026: 229391001(405813007)26552008 type:0]
    Concept [229395005] has a primitive parent concept of [138875005]
    Concept [138875005] is not the subject of any relationship statements. Continuing
    Concept [229395005] has a primitive parent concept of [229390000]
    Found that parent concept [229390000] has relationship [100000000146828: 229390000(116680003)229373006 type:0]
    Found that parent concept [229390000] has relationship [3437317023: 229390000(405813007)26552008 type:0]
    Found that parent concept [229390000] has relationship [39402024: 229390000(116680003)129182005 type:0]
    Found that parent concept [229390000] has relationship [3437318029: 229390000(260686004)129409008 type:0]
    Found that relationship under test [3364178029: 229395005(260686004)129409008 type:0] is also defined in parent concept as [3437318029: 229390000(260686004)129409008 type:0]
    Removing statement [[3364178029: 229395005(260686004)129409008 type:0]] from output because parent concept [229390000] has defined relationship [[3437318029: 229390000(260686004)129409008 type:0]]
    Found that parent concept [229390000] has relationship [39403025: 229390000(116680003)118716003 type:0]
    Found that parent concept [229390000] has relationship [39401028: 229390000(116680003)229373006 type:0]
    Found that parent concept [229390000] has relationship [3216456029: 229390000(116680003)74251004 type:0]
    Found that parent concept [229390000] has relationship [1467941022: 229390000(260870009)272125009 type:1]
    Concept [229395005] has a primitive parent concept of [71388002]
    Found that parent concept [71388002] has relationship [1903382025: 71388002(260507000)309795001 type:1]
    Found that parent concept [71388002] has relationship [1903383024: 71388002(260870009)272125009 type:1]
    Found that parent concept [71388002] has relationship [100000000401529: 71388002(116680003)138875005 type:0]
    Found that parent concept [71388002] has relationship [146315028: 71388002(116680003)138875005 type:0]
    Found that concept 229395005 has defining characteristic relationships {[3364177023: 229395005(405813007)127863007 type:0], [100000000146834: 229395005(116680003)229392008 type:0], [39411024: 229395005(116680003)229392008 type:0]}
    Testing for existence of relationship [3364177023] in parent primitive concepts
    Concept [229395005] has a primitive parent concept of [229392008]
    Found that parent concept [229392008] has relationship [100000000146831: 229392008(116680003)229391001 type:0]
    Found that parent concept [229392008] has relationship [1467945029: 229392008(260870009)272125009 type:1]
    Found that parent concept [229392008] has relationship [39405021: 229392008(116680003)229391001 type:0]
    Found that parent concept [229392008] has relationship [3467953022: 229392008(260686004)129409008 type:0]
    Found that parent concept [229392008] has relationship [3441868023: 229392008(405813007)27949001 type:0]
    Concept [229395005] has a primitive parent concept of [243120004]
    Found that parent concept [243120004] has relationship [60487025: 243120004(116680003)71388002 type:0]
    Found that parent concept [243120004] has relationship [100000000163141: 243120004(116680003)71388002 type:0]
    Found that parent concept [243120004] has relationship [1519147021: 243120004(260870009)272125009 type:1]
    Concept [229395005] has a primitive parent concept of [229373006]
    Found that parent concept [229373006] has relationship [3532559023: 229373006(260686004)129409008 type:0]
    Found that parent concept [229373006] has relationship [1467907025: 229373006(260870009)272125009 type:1]
    Found that parent concept [229373006] has relationship [100000000146809: 229373006(116680003)243120004 type:0]
    Found that parent concept [229373006] has relationship [2531872027: 229373006(116680003)118710009 type:0]
    Found that parent concept [229373006] has relationship [3286995026: 229373006(116680003)229319000 type:0]
    Found that parent concept [229373006] has relationship [3532560029: 229373006(405813007)61685007 type:0]
    Concept [229395005] has a primitive parent concept of [229391001]
    Found that parent concept [229391001] has relationship [100000000146830: 229391001(116680003)229390000 type:0]
    Found that parent concept [229391001] has relationship [1467943020: 229391001(260870009)272125009 type:1]
    Found that parent concept [229391001] has relationship [39404020: 229391001(116680003)229390000 type:0]
    Found that parent concept [229391001] has relationship [3441867029: 229391001(260686004)129409008 type:0]
    Found that parent concept [229391001] has relationship [3417080026: 229391001(405813007)26552008 type:0]
    Concept [229395005] has a primitive parent concept of [138875005]
    Concept [138875005] is not the subject of any relationship statements. Continuing
    Concept [229395005] has a primitive parent concept of [229390000]
    Found that parent concept [229390000] has relationship [100000000146828: 229390000(116680003)229373006 type:0]
    Found that parent concept [229390000] has relationship [3437317023: 229390000(405813007)26552008 type:0]
    Found that parent concept [229390000] has relationship [39402024: 229390000(116680003)129182005 type:0]
    Found that parent concept [229390000] has relationship [3437318029: 229390000(260686004)129409008 type:0]
    Found that parent concept [229390000] has relationship [39403025: 229390000(116680003)118716003 type:0]
    Found that parent concept [229390000] has relationship [39401028: 229390000(116680003)229373006 type:0]
    Found that parent concept [229390000] has relationship [3216456029: 229390000(116680003)74251004 type:0]
    Found that parent concept [229390000] has relationship [1467941022: 229390000(260870009)272125009 type:1]
    Concept [229395005] has a primitive parent concept of [71388002]
    Found that parent concept [71388002] has relationship [1903382025: 71388002(260507000)309795001 type:1]
    Found that parent concept [71388002] has relationship [1903383024: 71388002(260870009)272125009 type:1]
    Found that parent concept [71388002] has relationship [100000000401529: 71388002(116680003)138875005 type:0]
    Found that parent concept [71388002] has relationship [146315028: 71388002(116680003)138875005 type:0]
    Found that concept 229395005 has defining characteristic relationships {[3364177023: 229395005(405813007)127863007 type:0], [100000000146834: 229395005(116680003)229392008 type:0], [39411024: 229395005(116680003)229392008 type:0]}
    Found [272177] unshared defining characteristics
    Completed algorithm in 86 seconds with [706374] statements
    Writing results to /Users/henrikpettersen/Downloads/output.20120731.txt
    Wrote 706376 lines
    Closing database


Please note that there exists also a 'nuclear' option of where you can specify

    -s all
    
**WARNING!** This option will print detailed reasoning information for each and every concept/triple in the inputs, 
and dump about 8Gb (!) of text to your console. Please use carefully.
