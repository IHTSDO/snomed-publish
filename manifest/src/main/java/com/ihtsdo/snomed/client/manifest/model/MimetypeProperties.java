package com.ihtsdo.snomed.client.manifest.model;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.exception.ProgrammingException;

@Named
public class MimetypeProperties {
    private static final Logger LOG = LoggerFactory.getLogger( MimetypeProperties.class );
    
    private Properties properties;
    
    @PostConstruct
    public void init(){
        try {
            properties = new Properties();
            
            String mimetypes = System.getProperty("mimetypes"); 
            if ((mimetypes == null) || mimetypes.isEmpty()){
                mimetypes = "mimetypes.properties";
                properties.load(this.getClass().getClassLoader().getResourceAsStream(mimetypes));
            }else{
                properties.load(new FileInputStream(new File(mimetypes)));
            }
            
            LOG.info("Loaded properties from " + mimetypes);
            
        } catch (Exception e) {
            throw new ProgrammingException("Unable to read mime types properties file 'mimetypes.properties'");
        }
    }
    
    public static final String APPLICATION_ZIP = "application/zip";
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String APPLICATION_PERL = "application/perl";
    public static final String REFSET_PART = ".refset.";
    public static final String REFSET_CONTENT_PART = ".refset.content";
    public static final String REFSET_CROSSMAP_PART = ".refset.crossmap";
    public static final String REFSET_METADATA_PART = ".refset.metadata";
    public static final String REFSET_LANGUAGE_PART = ".refset.language";
    public static final String REFSET_ORDEREDTYPE_PART = ".refset.orderedtype";
    
    public String getMimetype(String filename){
        return properties.getProperty(filename);
    }
    
    public boolean isRefsetCollectionFile(String filename){
        if (properties.getProperty(filename) == null){
            return false;
        }
        return properties.getProperty(filename).contains(REFSET_PART);
    }
    
    public boolean isContentRefsetCollectionFile(String filename){
        if (properties.getProperty(filename) == null){
            return false;
        }        
        return properties.getProperty(filename).contains(REFSET_CONTENT_PART);
    }
    
    public boolean isCrossmapRefsetCollectionFile(String filename){
        if (properties.getProperty(filename) == null){
            return false;
        }
        return properties.getProperty(filename).contains(REFSET_CROSSMAP_PART);
    }
    
    public boolean isLanguageRefsetCollectionFile(String filename){
        if (properties.getProperty(filename) == null){
            return false;
        }
        return properties.getProperty(filename).contains(REFSET_LANGUAGE_PART);
    }
    public boolean isMetadataRefsetCollectionFile(String filename){
        if (properties.getProperty(filename) == null){
            return false;
        }
        return properties.getProperty(filename).contains(REFSET_METADATA_PART);
    }
    
    public boolean isRefsetCollectionMimetype(String mimetype){
        return mimetype.contains(REFSET_PART);
    }
    
    public boolean isContentRefsetCollectionMimetype(String mimetype){
        return mimetype.contains(REFSET_CONTENT_PART);
    }
    
    public boolean isCrossmapRefsetCollectionMimetype(String mimetype){
        return mimetype.contains(REFSET_CROSSMAP_PART);
    }
    
    public boolean isLanguageRefsetCollectionMimetype(String mimetype){
        return mimetype.contains(REFSET_LANGUAGE_PART);
    }

    public boolean isMetadataRefsetCollectionMimetype(String mimetype){
        return mimetype.contains(REFSET_METADATA_PART);
    }

    public boolean isOrderedTypeRefsetCollectionMimetype(String mimetype) {
        return mimetype.contains(REFSET_ORDEREDTYPE_PART);
    }
    
    
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.refset.content.associationreference+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.refset.content.attributevalue+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.refset.content.simple+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.refset.crossmap.full+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.refset.crossmap.simple+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.refset.language+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.refset.metadata.descriptor+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.refset.metadata.descriptiontype+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.refset.metadata.moduledependency+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.terminology.concept+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.terminology.description+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.terminology.identifier+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.terminology.triple+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.terminology.triple+txt";
//    public static final String application_perl = "application/vnd.ihtsdo.snomed.rf2.terminology.textdefinition+txt";    

}
