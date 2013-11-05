#IHTSDO Snomed Publication Tools

##Manifest generator

Library for creating a manifest to accompany a release

###Required

- You will need an instance of a Snomed ontology that has entries for every refset referred to in this relases (Each refset has a component identifier in Snomed)
- You will need specify the folder containing the files for the release. The program will parse this folder, and generate Manifest data based on its contents.
- The routine also requires a mimetypes.properties file, which maps filenames to our custom Snomed mime types. A sample configuration file can be found in [/lib/manifest/src/test/resources/mimetypes.properties](https://github.com/IHTSDO/snomed-publish/blob/master/lib/manifest/src/test/resources/mimetypes.properties). You will need to make sure this file is up-to-date for each release version of Snomed.

###Creates

- an instance of a [Manifest](/lib/manifest/src/main/java/com/ihtsdo/snomed/service/manifest/model/Manifest.java) class, which is a wrapper for a [ManifestFolder](/lib/manifest/src/main/java/com/ihtsdo/snomed/service/manifest/model/ManifestFolder.java). A ManifestFolder may contain one or more [ManifestFiles](/lib/manifest/src/main/java/com/ihtsdo/snomed/client/manifest/model/ManifestFile.java), or other ManifestFolders again.
- For each file, the manifest will contain its file size, mimetype (see above for mapping), and java.io.File mappings.
- We use the mimetypes (see above) to determine which files we want to parse further, looking for refset identifiers. For the mimetype rules for this, see [MimetypeProperties.java](/lib/manifest/src/main/java/com/ihtsdo/snomed/service/manifest/model/MimetypeProperties.java).
-- We parse Content, Crossmap, Metadata, Language, and Ordertype files. See link above for more details.
- For each Refset file found 
-- the Refset Component identifier from Snomed is returned, along with its fullySpecifiedName
-- For each refset referenced in the refset file, we also return its snomed component id and fullySpecfifiedName


###How to use

The mimetypes.properties file must be specified using the system variable 'mimetypes'. One way of doing this is to specify this property when you launch your jvm, like this:

	java ... -Dmimetypes=../src/test/resouces/mimetypes.properties ...

Here is how you use this library in your code:

    @Inject
    FileSystemParser fsParser;

    @PersistenceContext
    EntityManager em;    

	private Ontology loadSnomedData(File conceptFile, File descriptionFile) throws Exception{
		Ontology o = HibernateParserFactory.getParser(Parser.RF2).populateConceptAndDescriptions(
		        "manifest ontology", 
		        new FileInputStream(conceptFile), 
		        new FileInputStream(descriptionFile), 
		        em);
		return o;
	}

    public void doIt(){}
    	File releaseFolder, conceptsFile, descriptionsFile;
		Manifest manifest = fsParser.setParsemode(Mode.STRICT).parse(releaseFolder, 
			loadSnomedData(conceptsFile, descriptionsFile), em);
		//Assertion: Manifest data contains parsed release folder data
	...

###Example

For an example in the wild, check out our [command line tool](/client/manifest-main)