#Import / Export Library
Library for importing and exporting Snomed data in various formats. The parsers and serialisers are available using [HibernateParserFactory](/lib/importexport/src/main/java/com/ihtsdo/snomed/service/parser/HibernateParserFactory.java) and [SnomedSerialiserFactory](/lib/importexport/src/main/java/com/ihtsdo/snomed/service/serialiser/SnomedSerialiserFactory.java)

##Available parsers

<table>
  <tr>
    <th>Enum</th>
    <th>Class</th>
  </tr>
  <tr>
    <td>RF1</td>
    <td><a href="../importexport/src/main/java/com/ihtsdo/snomed/service/parser/Rf1HibernateParser.java">Rf1HibernateParser</a></td>
  </tr>
  <tr>
    <td>RF2</td>
    <td><a href="../importexport/src/main/java/com/ihtsdo/snomed/service/parser/Rf2HibernateParser.java">Rf2HibernateParser</a></td>
  </tr>
  <tr>
    <td>CANONICAL</td>
    <td><a href="../importexport/src/main/java/com/ihtsdo/snomed/service/parser/CanonicalHibernateParser.java">CanonicalHibernateParser</a></td>
  </tr>
  <tr>
    <td>CHILD_PARENT</td>
    <td><a href="../importexport/src/main/java/com/ihtsdo/snomed/service/parser/ChildParentHibernateParser.java">ChildParentHibernateParser</a></td>
  </tr>      
</table>

##Available Serialisers

<table>
  <tr>
    <th>Enum</th>
    <th>Class</th>
  </tr>
  <tr>
    <td>CANONICAL</td>
    <td><a href="../importexport/src/main/java/com/ihtsdo/snomed/service/serialiser/CanonicalSerialiser.java">CanonicalSerialiser</a></td>
  </tr>
  <tr>
    <td>CHILD_PARENT</td>
    <td><a href="../importexport/src/main/java/com/ihtsdo/snomed/service/serialiser/ChildParentSerialiser.java">ChilParentSerialiser</a></td>
  </tr>  
  <tr>
    <td>RDF_SCHEMA_XML</td>
    <td><a href="../importexport/src/main/java/com/ihtsdo/snomed/service/serialiser/RdfSchemaSerialiser.java">RdfSchemaSerialiser</a></td>
  </tr>    
</table>


##Format details

###RF1
Legacy format, superseded by RF2, but still need to produce RF1 format for release. Uses tab-separated-values.

    CONCEPTID1  RELATIONSHIPTYPE  CONCEPTID2    RELATIONSHIPGROUP
    280844000   116680003   71737002    0
    280845004   116680003   280737002   0
    etc.

The above example is for concepts only - there are other, similar RF1 formats for other component types, e.g. statements and descriptions. Since this is an old format, we'll just ommit these. 


###RF2
Current standard format for capturing all of Snomed. Serialisation format consists of 3 files for concepts, descriptions and OAV triples.  Uses tab-separated-values.

####Format for Concepts

    id  effectiveTime active  moduleId  definitionStatusId
    100000000 20090731  0 900000000000207008  900000000000074008
    10000006  20020131  1 900000000000207008  900000000000074008
    etc.

####Format for Descriptions

    id  effectiveTime active  moduleId  conceptId languageCode  typeId  term  caseSignificanceId
    2915278011  20120731  1 900000000000207008  450653003 en  900000000000013009  Open embolisation of suprarenal artery  900000000000020002
    2916665012  20120731  1 900000000000207008  450698009 en  900000000000003001  Repair of retina (procedure)  900000000000020002
    etc.

####Format for Statements

    id  effectiveTime active  moduleId  sourceId  destinationId relationshipGroup typeId  characteristicTypeId  modifierId
    3829433029  20080731  1 900000000000207008  102977005 102976001 0 116680003 900000000000010007  900000000000451002
    3829434024  20080731  1 900000000000207008  413337008 306751006 0 116680003 900000000000010007  900000000000451002
    etc.

###RDF Schema
<a href="http://www.w3.org/TR/rdf-schema/" target="_blank">RDF Schema Format<a/>. Requires a full RF2 import (concepts, descriptions, statements) for conversion. Renders the RDF Schema in <a href="http://www.w3.org/TR/REC-rdf-syntax/" target="_blank">RDF/XML format</a>.

For more detail on how we mapped Snomed to RDF Schema, take at look at the <a href="https://sites.google.com/a/ihtsdo.org/technical-dev/write-ups/mapping-snomed-to-rdf-schema" target="_blank">Snomed Documentation</a>.



###Canonical
A special format for representing the canonical form of Snomed. For more information on the canonical form and how it is calculated, see the [canonical library](/lib/canonical)

    CONCEPTID1  RELATIONSHIPTYPE    CONCEPTID2  RELATIONSHIPGROUP
    280844000   116680003   71737002    1
    280845004   116680003   280737002   0
    280845004   116680003   77435000    0
    280846003   116680003   280737002   0
    280846003   116680003   33766003    0
    etc.


###Child-Parent
A simple format with two columns and no heading, to show parent-child releationships. Used for serialising the results after generating the transitive closure of isA relationships for all concepts. For more information on the algorithm, see the the [closure library](/lib/closure)

    609555007   161639008
    609555007   609388001
    609555007   609386002
    609555007   609387006
    609555007   138875005
    116680003   138875005

##How to use the parser

    File conceptFile, triplesFile, descriptionFile;
    Ontology o = 
        HibernateParserFactory.getParser(HibernateParserFactory.Parser.RF2);
            .populateDbWithDescriptions(
                "ontology name", 
                new FileInputStream(conceptFile), 
                new FileInputStream(triplesFile), 
                new FileInputStream(descriptionFile), 
                em);

The parser relies on a JPA 2 database connection and an open transaction to a relational database being available. Here is an exmaple how to configure this using an H2 in memory database, using hibernate as the JPA implementation:

    private EntityManagerFactory emf  = null;
    private EntityManager em          = null;

    private void initDb(Properties properties){
        Map<String, Object> overrides = new HashMap<String, Object>();
        overrides.put("hibernate.dialect", properties.getProperty(ImportCliParser.HIBERNATE_DIALECT_KEY));
        overrides.put("javax.persistence.jdbc.driver", properties.getProperty(ImportCliParser.DRIVER_KEY));
        overrides.put("javax.persistence.jdbc.url", properties.getProperty(ImportCliParser.URL_KEY));
        overrides.put("javax.persistence.jdbc.user", properties.getProperty(ImportCliParser.USER_KEY));
        overrides.put("javax.persistence.jdbc.password", properties.getProperty(ImportCliParser.PASSWORD_KEY));
        overrides.put("hibernate.hbm2ddl.auto", "update");
        
        LOG.info("Connecting to database " + properties.getProperty(ImportCliParser.URL_KEY));
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    public void closeDb(){
        LOG.info("Closing database");
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

These settings can also be configured in [persistence.xml](/client/import-main/src/main/resources/META-INF/persistence.xml).

For a sample implementation, take a look at the [import client project](/client/import-main).

##How to use the serialiser

    Set<Statement> statements;
    try(FileWriter fw = new FileWriter(outFile); BufferedWriter bw = new BufferedWriter(fw)){
        SerialiserFactory.getSerialiser(Form.CANONICAL, bw).write(statements);
    }

##Hardware

We would recommend using a server with fast IO. You will need a minimum 3Gb of RAM for the snapshot version of Snomed, if you are using an in-memory database (the full version will require more). However, we recommend at least 6Gb of RAM available for optimal performance. 

If you want to conserve memory, you may use a disk backed relational database, such as postgres, instead, although this will normally have a negative effect on the performance.
