package com.ihtsdo.snomed.client.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.client.manifest.model.Manifest;
import com.ihtsdo.snomed.client.manifest.parser.FileSystemParser;
import com.ihtsdo.snomed.client.manifest.parser.RefsetCollectionParser.Mode;
import com.ihtsdo.snomed.client.manifest.serialiser.XmlSerialiser;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.parser.HibernateParser;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;

public class ManifestMain {
    private static final Logger LOG = LoggerFactory.getLogger( ManifestMain.class );
    
    private EntityManagerFactory emf  = null;
    private EntityManager em          = null;
    
    public static final String TARGET_FOLDER = "Manifest";
    public static final String TARGET_XML = "manifest.xml";
    public static final String TARGET_HTML = "manifest.html";
    public static final String TARGET_CSS = "screen.css";
    public static final String TARGET_XSL = "screen.xsl";
    
    @Inject
    FileSystemParser fsParser;
    
    @Inject
    XmlSerialiser xmlSerialiser; 
    
    ApplicationContext applicationContext;
    
    private void initDb(String db){
        Map<String, Object> overrides = new HashMap<String, Object>();
        
        if ((db != null) && (!db.isEmpty())){
            overrides.put("javax.persistence.jdbc.url", "jdbc:h2:" + db);
            LOG.info("Using file system database at " + db);
        }else{
            LOG.info("Using an in-memory database");
        }
        LOG.info("Initialising database");
        emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML, overrides);
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }    
    
    private void initSpring(){
        LOG.info("Initializing Spring context.");
        @SuppressWarnings("resource")
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        fsParser = applicationContext.getBean(FileSystemParser.class);
        xmlSerialiser = applicationContext.getBean(XmlSerialiser.class);
        LOG.info("Spring context initialized.");
    }
    
    public void closeDb(){
        LOG.info("Closing database");
        em.getTransaction().commit();
        em.close();
        emf.close();
    }    
        
    public static void main(String[] args) throws IOException, ParseException, TransformerException{
        Stopwatch overAllstopwatch = new Stopwatch().start();
        ManifestCliParser cli = new ManifestCliParser();
        cli.parse(args, new ManifestMain()); 
        overAllstopwatch.stop();
        LOG.info("Overall program completion in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }    
    
    public void createManifestFolder(File manifestFolder) throws IOException{
        LOG.info("Refreshing manifest folder");
        if (manifestFolder.exists()){
            FileUtils.deleteDirectory(manifestFolder);
            manifestFolder.mkdir();
        }
        
        
        
  //      this.getClass().getClassLoader().getResourceAsStream(TARGET_CSS)
        
        FileUtils.copyInputStreamToFile(this.getClass().getClassLoader().getResourceAsStream(TARGET_CSS), new File(manifestFolder.getAbsoluteFile(), TARGET_CSS));
        FileUtils.copyInputStreamToFile(this.getClass().getClassLoader().getResourceAsStream(TARGET_XSL), new File(manifestFolder.getAbsoluteFile(), TARGET_XSL));
//        FileUtils.copyURLToFile(new File(TARGET_XSL).toURI().toURL(), new File(manifestFolder.getAbsoluteFile(), TARGET_XSL));
    }
    
    protected void runProgram(File conceptFile, File descriptionFile, File releaseFolder,
            HibernateParserFactory.Parser parser, String db) throws IOException, TransformerException
    {
        try{
            initDb(db);
            initSpring();
            
            File manifestFolder = new File(releaseFolder.getAbsolutePath(), TARGET_FOLDER);
            createManifestFolder(manifestFolder);
            
            HibernateParser hibParser = HibernateParserFactory.getParser(parser);
            Ontology o = hibParser.populateConceptAndDescriptions(
                    "manifest ontology", 
                    new FileInputStream(conceptFile), 
                    new FileInputStream(descriptionFile), 
                    em);

            fsParser.setParsemode(Mode.STRICT);
            Manifest manifest = fsParser.parse(releaseFolder, o, em);
            File targetXmlFile = new File(releaseFolder.getAbsolutePath(), TARGET_XML);
            try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(targetXmlFile))){
//                outputStreamWriter.write("<?xml-stylesheet type=\"text/xsl\" href=\"manifest/manifest.xsl\">\n");
                xmlSerialiser.serialise(outputStreamWriter, manifest);
            }
            
            TransformerFactory tFactory = TransformerFactory.newInstance();

            Transformer transformer = tFactory.newTransformer(
                    new StreamSource(
                            this.getClass().getClassLoader().getResourceAsStream(TARGET_XSL)));

            transformer.transform(new StreamSource(targetXmlFile),
                    new StreamResult(
                            (new FileOutputStream(new File(releaseFolder.getAbsolutePath(), TARGET_HTML)))));

        }finally{
            closeDb();
        }
    }
}

