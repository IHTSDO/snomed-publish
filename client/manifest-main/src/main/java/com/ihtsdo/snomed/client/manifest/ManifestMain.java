package com.ihtsdo.snomed.client.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.SnomedFlavours;
import com.ihtsdo.snomed.service.manifest.model.Manifest;
import com.ihtsdo.snomed.service.manifest.parser.FileSystemParser;
import com.ihtsdo.snomed.service.manifest.parser.RefsetCollectionParser.Mode;
import com.ihtsdo.snomed.service.manifest.serialiser.XmlSerialiser;
import com.ihtsdo.snomed.service.parser.HibernateParser;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory;

public class ManifestMain {
    private static final Logger LOG = LoggerFactory.getLogger( ManifestMain.class );

    protected static final Date DEFAULT_TAGGED_ON_DATE;
    
    static{
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();
        DEFAULT_TAGGED_ON_DATE = new Date(utilDate.getTime());        
    }
    
    
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
    
    protected void runProgram(File conceptFile, File descriptionFile, File releaseFolder,
            HibernateParserFactory.Parser parser, String db) throws IOException, TransformerException
    {
        try{
            initDb(db);
            initSpring();
            File targetXmlFile = new File(releaseFolder.getAbsolutePath(), TARGET_XML);
            File manifestFolder = new File(releaseFolder.getAbsolutePath(), TARGET_FOLDER);
            
            createManifestFolder(manifestFolder);
            
            OntologyVersion o = loadSnomedData(conceptFile, descriptionFile, parser);

            Manifest manifest = fsParser.setParsemode(Mode.STRICT).parse(releaseFolder, o, em);
            
            writeTargetXml(manifest, targetXmlFile);
            
            convertToHtmlUsingXslAndWrite(releaseFolder, targetXmlFile);

        }catch (Exception e){
            LOG.error(e.getMessage(), e);
        }finally{
            closeDb();
        }
    }
    
    public void createManifestFolder(File manifestFolder) throws IOException{
        LOG.info("Refreshing manifest folder");
        if (manifestFolder.exists()){
            FileUtils.deleteDirectory(manifestFolder);
            manifestFolder.mkdir();
        }
        FileUtils.copyInputStreamToFile(this.getClass().getClassLoader().getResourceAsStream(TARGET_CSS), new File(manifestFolder.getAbsoluteFile(), TARGET_CSS));
        FileUtils.copyInputStreamToFile(this.getClass().getClassLoader().getResourceAsStream(TARGET_XSL), new File(manifestFolder.getAbsoluteFile(), TARGET_XSL));
    }
    

	private void writeTargetXml(Manifest manifest, File targetXmlFile) throws IOException,
			FileNotFoundException {
		try(OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(targetXmlFile))){
		    xmlSerialiser.serialise(outputStreamWriter, manifest);
		}
	}

	private OntologyVersion loadSnomedData(File conceptFile, File descriptionFile,
			HibernateParserFactory.Parser parser) throws IOException,
			FileNotFoundException {
	    LOG.info("Populating concepts and descriptions");
		HibernateParser hibParser = HibernateParserFactory.getParser(parser);
		OntologyVersion o = hibParser.populateConceptAndDescriptions(
		        SnomedFlavours.INTERNATIONAL,
		        DEFAULT_TAGGED_ON_DATE,
		        new FileInputStream(conceptFile), 
		        new FileInputStream(descriptionFile), 
		        em);
		return o;
	}

	private void convertToHtmlUsingXslAndWrite(File releaseFolder, File targetXmlFile)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException,
			FileNotFoundException 
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(
		        new StreamSource(
		                this.getClass().getClassLoader().getResourceAsStream(TARGET_XSL)));
		transformer.transform(new StreamSource(targetXmlFile),
		        new StreamResult(
		                (new FileOutputStream(new File(releaseFolder.getAbsolutePath(), TARGET_HTML)))));
	}
}

