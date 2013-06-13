package com.ihtsdo.snomed.client.manifest.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="folder")
@XmlType(propOrder = { "name", "manifestFolders", "manifestFiles" })
public class ManifestFolder {
    
    @XmlTransient
    protected File file;
    
    @XmlElement(name="file")
    protected List<ManifestFile> manifestFiles = new ArrayList<>();
    
    @XmlElement(name="folder")
    protected List<ManifestFolder> manifestFolders = new ArrayList<>();

    public ManifestFolder(File file){
        this.file = file;
    }
    
    public ManifestFolder(){}

    @XmlAttribute
    public String getName(){
        return file.getName();
    }
    
    public File getFile(){
        return file;
    }
    
    public void setName(String name){
        ;
    }    

    public List<ManifestFile> getManifestFiles() {
        return manifestFiles;
    }
    public List<ManifestFolder> getManifestFolders() {
        return manifestFolders;
    }
    
    public ManifestFolder getManifestFolder(int index){
        return manifestFolders.get(index);
    }
    
    public ManifestFolder getManifestFolder(String name){
        for (ManifestFolder f : getManifestFolders()){
            if (f.getName().equals(name)){
                return f;
            }
        }
        return null;
    }    
    
    public ManifestFile getManifestFile(String name){
        for (ManifestFile f : getManifestFiles()){
            if (f.getName().equals(name)){
                return f;
            }
        }
        return null;
    }    
    
    public ManifestFile getManifestFile(int index){
        return manifestFiles.get(index);
    }
    
    public void addManifestFolder(ManifestFolder folder){
        this.manifestFolders.add(folder);
    }
    
    public void addManifestFile(ManifestFile file){
        this.manifestFiles.add(file);
    }
}
