package com.ihtsdo.snomed.client.manifest.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="")
@XmlType(name="")
public abstract class BaseRefsetCollection {

    @XmlElement(name="module")
    protected Set<RefsetModule> refsetModules = new HashSet<>();

    public BaseRefsetCollection(final List<RefsetModule> refsetModules){
        for (RefsetModule module : refsetModules){
            this.refsetModules.add(module);
        }
    }
    
    public BaseRefsetCollection(){
    }
    
    public Set<RefsetModule> getModules() {
        return refsetModules;
    }
    
    public RefsetModule getModule(long sid){
        for (RefsetModule r : refsetModules){
            if (r.getSid() == sid){
                return r;
            }
        }
        return null;
    }  
    
    public void addModule(RefsetModule refsetModule){
        refsetModules.add(refsetModule);
        
    }
}
