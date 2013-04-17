IHTSDO Snomed Publication Tools
===============================

Import / Export Library
-----------------------

This library provides an API for marshalling from the long and short form serialisation formats, and for unmarshalling to the short form serialisation format.

Long form format looks like this:

    RELATIONSHIPID  CONCEPTID1  RELATIONSHIPTYPE	CONCEPTID2	CHARACTERISTICTYPE	REFINABILITY	RELATIONSHIPGROUP
    100000028	280844000	116680003	71737002	0	0	0
    100001029	280845004	116680003	280737002	0	0	0
    etc.
        
and short form format looks like this:

    CONCEPTID1  RELATIONSHIPTYPE  CONCEPTID2	RELATIONSHIPGROUP
    280844000	116680003	71737002	0
    280845004	116680003	280737002	0
    etc.

Along with these two formats for triples, the library also needs an input file for concepts, in this format:

    CONCEPTID  CONCEPTSTATUS  FULLYSPECIFIEDNAME	CTV3ID	SNOMEDID	ISPRIMITIVE
    280844000	0	Entire body of seventh thoracic vertebra (body structure)	Xa1Y9	T-11875	1
    280845004	0	Entire body of eighth thoracic vertebra (body structure)	Xa1YA	T-11876	1
    etc.
        
The library is configured to use an in-memory H2 database by default, which requires about 3Gb of heap space with the current size of the Snomed triples set. However, this default can be overriden like this:

    Map<String, Object> overrides = new HashMap<String, Object>();
    overrides.put("javax.persistence.jdbc.url", "new url");
    overrides.put("javax.persistence.jdbc.driver", "new driver");
    //etc.
    emf = Persistence.createEntityManagerFactory(HibernateDbImporter.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
    em = emf.createEntityManager();
    Ontology ontology1 = new HibernateDbImporter().populateDbFromLongForm("ontology name", 
        new FileInputStream("conceptFile"), new FileInputStream("triplesFile"), em);
    //or
    Ontology ontology2 = new HibernateDbImporter().populateDbFromLongForm("ontology name", 
        new FileInputStream("conceptFile"), new FileInputStream("triplesFile"), em);


Please note that this library has a depency on Hibernate, using its APIs for getting a JDBC connection from the EntityManager, for performance reasons.

For writing out to the short form format:

    try(FileWriter fw = new FileWriter("outFile"); BufferedWriter bw = new BufferedWriter(fw)){
        new CanonicalOutputWriter().write(bw, statements);
    }
