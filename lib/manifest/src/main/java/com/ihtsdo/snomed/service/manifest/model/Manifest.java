package com.ihtsdo.snomed.service.manifest.model;

import java.io.File;

import javax.xml.bind.annotation.XmlRootElement;

//@XmlAccessorType(value = XmlAccessType.NONE)
@XmlRootElement(name="manifest")
public class Manifest extends ManifestFolder{

    public Manifest(File file){
        super(file);
    }
    
    public Manifest(){}
}
