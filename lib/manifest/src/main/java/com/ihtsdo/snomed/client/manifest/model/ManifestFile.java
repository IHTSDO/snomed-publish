package com.ihtsdo.snomed.client.manifest.model;

import java.io.File;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="file")
@XmlType(propOrder={""})
public class ManifestFile {

    public enum MyEnum{
        @XmlEnumValue("simple") SIMPLE, 
        @XmlEnumValue("refset") REFSET;
    }
    
    @XmlTransient private MimetypeProperties mimetypeProperties;
    @XmlTransient private BaseRefsetCollection refsetCollection;   
    @XmlTransient private File file;    

    ManifestFile(File file, BaseRefsetCollection refsetCollection){
        this.file = file;
        this.refsetCollection = refsetCollection;
    }
    
    ManifestFile(File file){
        this.file = file;
    }
    
    public ManifestFile(){}
    
    @XmlAttribute(name="type")
    public MyEnum getType(){
        if (refsetCollection != null){
            return MyEnum.REFSET;
        }
        return MyEnum.SIMPLE; 
    }
    
    @XmlAttribute
    public String getMimetype(){
        return mimetypeProperties.getMimetype(file.getName());
    }

    @XmlElement(name="module")
    private Set<RefsetModule> getRefsetModules(){
        if (getRefsetCollection() == null){
            return null;
        }
        return getRefsetCollection().getModules();
    }    
    
    @XmlAttribute
    public String getName(){
        return file.getName();
    }    
    
    @XmlAttribute
    public long getSize(){
        //return 2;
        return file.length();
    }
    
    public boolean isRefsetCollection(){
        return refsetCollection != null;
    }

    public BaseRefsetCollection getRefsetCollection() {
        return refsetCollection;
    }

    public void setRefsetCollection(BaseRefsetCollection refsetCollection) {
        this.refsetCollection = refsetCollection;
    }

    public ManifestFile setMimetypeProperties(MimetypeProperties mimetypeProperties) {
        this.mimetypeProperties = mimetypeProperties;
        return this;
    }   
}