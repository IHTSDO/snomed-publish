package com.ihtsdo.snomed.service.manifest.model;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ManifestFileFactory {

    @Inject
    private MimetypeProperties mimetypeProperties;
    
//    @Inject
//    private RefsetCollectionParser refsetCollectionParser;
//    
//    @Inject
//    private RefsetCollectionFactory refsetCollectionFactory;
        
    public ManifestFile createManifestFile(File file){
        return new ManifestFile(file).setMimetypeProperties(mimetypeProperties);
    }    
    
    public ManifestFile createManifestFile(File file, BaseRefsetCollection refsetCollection){
        return new ManifestFile(file, refsetCollection).setMimetypeProperties(mimetypeProperties);
    }
    

}
