#Manifest generator
Utility for creating a manifest file to go with a release bundle. For more information on the internals of this tool, have a look at the [Manifest library](/lib/manifest).

At the end, this program will create a new folder called 'Manifest' and 4 new manifest files in the root of the distribution folder (specified by -r in the inputs, see below):

    manifest.html        A human readable HTML version of the manifest. Transient artefact that can be generated using 
                         'manifest.xml' and 'Manifest/manifest.xsl'. References 'Manifest/screen.css'.
    manifest.xml         A machine-readable XML form of the manifest. Non-transient authority.
    Manifest/screen.css  Stylesheet referenced by manifest.html
    Manifest/screen.xsl  XML Stylesheet for generating manifest.html from manifest.xml

This is the syntax:

    You need to specify mimetype.properties location as a system property like this:
        java -Dmimetype.properties=mimetype.properties -Xmx2000m -jar manifest.jar ...
        
    -h, --help          Print this help menu
    -c, --concepts      File containing concepts
    -d, --descriptions  File containing descriptions
    -f, --format        File format of input files. One of 'RF1', 'RF2', 'CANONICAL', or 'CHILD_PARENT'
    -r,  --root         Root folder of the release files to create a manifest for
    -db, --database     Optional. Specify location of database file. If not specified, 
                        defaults to an in-memory database (minimum 3Gb of heap space required)

    
You will need to have the Java 7 JDK and Maven 3 to build the distribution jar file, and Java 7 JRE in order to run it.

To build the distribution, enter the root project directory (two up from this folder) and type:

    mvn clean package
    
The distribution jar file can be found at lib/mainfest-main/target/manifest.jar after this. No other file is required in order to run the program, and can be distributed as this single file.

For help on how to run the program, type:

    java -jar manifest.jar -h
    
This sample run
    
    java -Xmx6000m -Dmimetypes=src/main/resources/mimetypes.properties -jar target/manifest.jar 
    -c sct2_Concept_Snapshot_INT_20120731.txt -d sct2_Description_Snapshot-en_INT_20120731.txt 
    -f RF2 -r ../doc/release/SnomedCT_Release_INT_20120731/

produces this output

    Using an in-memory database
    Initialising database
    Initializing Spring context.
    Loaded properties from src/main/resources/mimetypes.properties
    Spring context initialized.
    Refreshing manifest folder
    Importing ontology "manifest ontology"
    Populating concepts
    Populated 396305 concepts
    Completed concepts import in 10 seconds
    Populating descriptions
    Populated 1178104 descriptions
    Completed descriptions import in 17 seconds
    Populating display name cache
    Updated 1 display names
    Completed import in 34 seconds
    Parsing folder 'SnomedCT_Release_INT_20120731'
    Adding file '.DS_Store'
    Parsing folder 'Documentation'
    Adding file 'doc_EditorialGuide_Current-en-US_INT_20120731.pdf'
    Adding file 'doc_EditorialGuide_Current-en-US_INT_20120731.zip'
    Adding file 'doc_IhtsdoGlossary_Current-en-US_INT_20120731.pdf'
    Adding file 'doc_IhtsdoGlossary_Current-en-US_INT_20120731.zip'
    Adding file 'doc_NamespaceIdentifierRegistry_Current-en-US_INT_20120731.pdf'
    Adding file 'doc_NonHumanRefsetGuide_Current-en-US_INT_20120731.pdf'
    Adding file 'doc_RF1Guide_Current-en-US_INT_20120731.pdf'
    Adding file 'doc_RF1Guide_Current-en-US_INT_20120731.zip'
    Adding file 'doc_ScopeMemo_Current-en-US_INT_20120731.pdf'
    Adding file 'doc_TechnicalImplementationGuide_Current-en-US_INT_20120731.pdf'
    Adding file 'doc_TechnicalImplementationGuide_Current-en-US_INT_20120731.zip'
    Adding file 'doc_UserGuide_Current-en-US_INT_20120731.pdf'
    Adding file 'doc_UserGuide_Current-en-US_INT_20120731.zip'
    Parsing folder 'Manifest'
    Adding file 'screen.css'
    Adding file 'screen.xsl'
    Adding file 'manifest.html'
    Adding file 'manifest.xml'
    Parsing folder 'manifest2'
    Adding file 'manifest.html'
    Adding file 'manifest.xml'
    Adding file 'manifest.zip'
    Adding file 'screen.css'
    Adding file 'screen.xsl'
    Parsing folder 'release'
    Adding file '.DS_Store'
    Parsing folder 'manifest'
    Adding file 'screen.css'
    Adding file 'screen.xsl'
    Adding file 'manifest.html'
    Adding file 'manifest.xml'
    Parsing folder 'Resources'
    Parsing folder 'OrderedTypeReferenceSetExemplar'
    Parsing refset file 'zres2_icRefset_OrderedTypeFull_INT_20110731.txt'
    Parsed 263122 lines
    Parsing refset file 'zres2_icRefset_OrderedTypeSnapshot_INT_20110731.txt'
    Parsed 191670 lines
    Parsing folder 'StatedRelationshipsToOwlKRSS'
    Adding file 'tls2_StatedRelationshipsToOwlKRSS_INT_20120731.pl'
    Parsing folder 'RF1Release'
    Parsing folder 'CrossMaps'
    Parsing folder 'ICD9'
    Adding file 'der1_CrossMaps_ICD9_INT_20120731.txt'
    Adding file 'der1_CrossMapSets_ICD9_INT_20120731.txt'
    Adding file 'der1_CrossMapTargets_ICD9_INT_20120731.txt'
    Parsing folder 'ICDO'
    Adding file 'der1_CrossMaps_ICDO_INT_20120731.txt'
    Adding file 'der1_CrossMapSets_ICDO_INT_20120731.txt'
    Adding file 'der1_CrossMapTargets_ICDO_INT_20120731.txt'
    Parsing folder 'OtherResources'
    Parsing folder 'BridgeFiles'
    Parsing folder 'zres_BridgeFile_Snomed2ToSnomedRT_INT_20020131'
    Adding file 'SNO2-SRT10_Bridge.txt'
    Adding file 'SNO2_not_in_SRT10.txt'
    Adding file 'zres_BridgeFile_Snomed2ToSnomedRT_INT_20020131.zip'
    Adding file 'zres_BridgeFile_Snomed35ToSnomedRT_INT_20020131.zip'
    Parsing folder 'Canonical Table'
    Adding file 'doc1_CanonicalTableGuide_Current-en-US_INT_20120731.pdf'
    Adding file 'res1_Canonical_Core_INT_20120731.txt'
    Parsing folder 'DeveloperToolkit'
    Adding file 'doc_DeveloperToolkitGuide_Current-en-US_INT_20120731.pdf'
    Parsing folder 'Indexes'
    Adding file 'res_DualKeyIndex_Concepts-en-US_INT_20120731.txt'
    Adding file 'res_DualKeyIndex_Descriptions-en-US_INT_20120731.txt'
    Adding file 'res_WordKeyIndex_Concepts-en-US_INT_20120731.txt'
    Adding file 'res_WordKeyIndex_Descriptions-en-US_INT_20120731.txt'
    Adding file 'tls1_Index_Generator_INT_20100731.zip'
    Adding file 'zres_ExcludedWords_en-US_INT_20070731.txt'
    Parsing folder 'Subsets'
    Parsing folder 'DuplicateTerms'
    Adding file 'der1_SubsetMembers_DuplicateTerms_INT_20110731.txt'
    Adding file 'der1_Subsets_DuplicateTerms_INT_20110731.txt'
    Parsing folder 'Navigation'
    Adding file 'zder1_SubsetMembers_Navigation_INT_20030731.txt'
    Adding file 'zder1_Subsets_Navigation_INT_20030731.txt'
    Adding file 'zres_WordEquivalents_en-US_INT_20020731.txt'
    Parsing folder 'LOINCIntegration'
    Adding file 'zder1_Integration_LOINC_INT_20050731.txt'
    Parsing folder 'StatedRelationships'
    Adding file 'res1_StatedRelationships_Core_INT_20120731.txt'
    Parsing folder 'TextDefinitions'
    Adding file 'sct1_TextDefinitions_en-US_INT_20120731.txt'
    Parsing folder 'Subsets'
    Parsing folder 'Language-en-GB'
    Adding file 'der1_SubsetMembers_en-GB_INT_20120731.txt'
    Adding file 'der1_Subsets_en-GB_INT_20120731.txt'
    Parsing folder 'Language-en-US'
    Adding file 'der1_SubsetMembers_en-US_INT_20120731.txt'
    Adding file 'der1_Subsets_en-US_INT_20120731.txt'
    Parsing folder 'NonHumanSubset'
    Adding file 'der1_SubsetMembers_NonHuman_INT_20120731.txt'
    Adding file 'der1_Subsets_NonHuman_INT_20120731.txt'
    Parsing folder 'VTMVMP'
    Adding file 'der1_SubsetMembers_VTMVMP_INT_20120731.txt'
    Adding file 'der1_Subsets_VTMVMP_INT_20120731.txt'
    Parsing folder 'Terminology'
    Parsing folder 'Content'
    Adding file 'sct1_Concepts_Core_INT_20120731.txt'
    Adding file 'sct1_Descriptions_en_INT_20120731.txt'
    Adding file 'sct1_Relationships_Core_INT_20120731.txt'
    Parsing folder 'History'
    Adding file 'sct1_ComponentHistory_Core_INT_20120731.txt'
    Adding file 'sct1_References_Core_INT_20120731.txt'
    Parsing folder 'RF2Release'
    Parsing folder 'Delta'
    Parsing folder 'Refset'
    Parsing folder 'Content'
    Parsing refset file 'der2_cRefset_AssociationReferenceDelta_INT_20120731.txt'
    Parsed 562 lines
    Parsing refset file 'der2_cRefset_AttributeValueDelta_INT_20120731.txt'
    Parsed 345 lines
    Parsing refset file 'der2_Refset_SimpleDelta_INT_20120731.txt'
    Parsed 6 lines
    Parsing folder 'Crossmap'
    Parsing refset file 'der2_iissscRefset_ComplexMapDelta_INT_20120731.txt'
    Parsed 266 lines
    Parsing refset file 'der2_sRefset_SimpleMapDelta_INT_20120731.txt'
    Parsed 1637 lines
    Parsing folder 'Language'
    Parsing refset file 'der2_cRefset_LanguageDelta-en_INT_20120731.txt'
    Parsed 5561 lines
    Parsing folder 'Metadata'
    Parsing refset file 'der2_cciRefset_RefsetDescriptorDelta_INT_20120731.txt'
    Parsed 0 lines
    Parsing refset file 'der2_ciRefset_DescriptionTypeDelta_INT_20120731.txt'
    Parsed 0 lines
    Parsing refset file 'der2_ssRefset_ModuleDependencyDelta_INT_20120731.txt'
    Parsed 1 lines
    Parsing folder 'Terminology'
    Adding file 'sct2_Concept_Delta_INT_20120731.txt'
    Adding file 'sct2_Description_Delta-en_INT_20120731.txt'
    Adding file 'sct2_Identifier_Delta_INT_20120731.txt'
    Adding file 'sct2_Relationship_Delta_INT_20120731.txt'
    Adding file 'sct2_StatedRelationship_Delta_INT_20120731.txt'
    Adding file 'sct2_TextDefinition_Delta-en_INT_20120731.txt'
    Adding file 'delta.zip'
    Parsing folder 'Full'
    Parsing folder 'Refset'
    Parsing folder 'Content'
    Parsing refset file 'der2_cRefset_AssociationReferenceFull_INT_20120731.txt'
    Parsed 135190 lines
    Parsing refset file 'der2_cRefset_AttributeValueFull_INT_20120731.txt'
    Parsed 521230 lines
    Parsing refset file 'der2_Refset_SimpleFull_INT_20120731.txt'
    Parsed 17659 lines
    Parsing folder 'Crossmap'
    Parsing refset file 'der2_iissscRefset_ComplexMapFull_INT_20120731.txt'
    Parsed 249358 lines
    Parsing refset file 'der2_sRefset_SimpleMapFull_INT_20120731.txt'
    Parsed 820421 lines
    Parsing folder 'Language'
    Parsing refset file 'der2_cRefset_LanguageFull-en_INT_20120731.txt'
    Parsed 2604052 lines
    Parsing folder 'Metadata'
    Parsing refset file 'der2_cciRefset_RefsetDescriptorFull_INT_20120731.txt'
    Parsed 52 lines
    Parsing refset file 'der2_ciRefset_DescriptionTypeFull_INT_20120731.txt'
    Parsed 3 lines
    Parsing refset file 'der2_ssRefset_ModuleDependencyFull_INT_20120731.txt'
    Parsed 22 lines
    Parsing folder 'Terminology'
    Adding file 'sct2_Concept_Full_INT_20120731.txt'
    Adding file 'sct2_Description_Full-en_INT_20120731.txt'
    Adding file 'sct2_Identifier_Full_INT_20120731.txt'
    Adding file 'sct2_Relationship_Full_INT_20120731.txt'
    Adding file 'sct2_StatedRelationship_Full_INT_20120731.txt'
    Adding file 'sct2_TextDefinition_Full-en_INT_20120731.txt'
    Adding file 'full.zip'
    Parsing folder 'Snapshot'
    Parsing folder 'Refset'
    Parsing folder 'Content'
    Parsing refset file 'der2_cRefset_AssociationReferenceSnapshot_INT_20120731.txt'
    Parsed 125671 lines
    Parsing refset file 'der2_cRefset_AttributeValueSnapshot_INT_20120731.txt'
    Parsed 404610 lines
    Parsing refset file 'der2_Refset_SimpleSnapshot_INT_20120731.txt'
    Parsed 16279 lines
    Parsing folder 'Crossmap'
    Parsing refset file 'der2_iissscRefset_ComplexMapSnapshot_INT_20120731.txt'
    Parsed 168355 lines
    Parsing refset file 'der2_sRefset_SimpleMapSnapshot_INT_20120731.txt'
    Parsed 818584 lines
    Parsing folder 'Language'
    Parsing refset file 'der2_cRefset_LanguageSnapshot-en_INT_20120731.txt'
    Parsed 2286374 lines
    Parsing folder 'Metadata'
    Parsing refset file 'der2_cciRefset_RefsetDescriptorSnapshot_INT_20120731.txt'
    Parsed 52 lines
    Parsing refset file 'der2_ciRefset_DescriptionTypeSnapshot_INT_20120731.txt'
    Parsed 3 lines
    Parsing refset file 'der2_ssRefset_ModuleDependencySnapshot_INT_20120731.txt'
    Parsed 1 lines
    Parsing folder 'Terminology'
    Adding file 'sct2_Concept_Snapshot_INT_20120731.txt'
    Adding file 'sct2_Description_Snapshot-en_INT_20120731.txt'
    Adding file 'sct2_Identifier_Snapshot_INT_20120731.txt'
    Adding file 'sct2_Relationship_Snapshot_INT_20120731.txt'
    Adding file 'sct2_StatedRelationship_Snapshot_INT_20120731.txt'
    Adding file 'sct2_TextDefinition_Snapshot-en_INT_20120731.txt'
    Adding file 'snapshot.zip'
    Closing database
    Overall program completion in 54 seconds

