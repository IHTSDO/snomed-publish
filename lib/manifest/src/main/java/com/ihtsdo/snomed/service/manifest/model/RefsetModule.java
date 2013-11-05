package com.ihtsdo.snomed.service.manifest.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Concept;

@XmlRootElement(name="module")
public class RefsetModule {
    
    @XmlElement(name="refset")
    private Set<Refset> refsets = new HashSet<>();
    
    @XmlTransient
    private Concept concept;
    
    public RefsetModule(Concept concept){
        this.concept = concept;
    }
    
    public RefsetModule(){}
    
    public Set<Refset> getRefsets() {
        return refsets;
    }

    public void addRefset(Refset refset){
        refsets.add(refset);
    }
    public Concept getConcept() {
        return concept;
    }
    
    public boolean equals(Object o){
        if (o instanceof RefsetModule){
            RefsetModule r = (RefsetModule)o;
            if (r.getSid() == getSid()){
                return true;
            }
        }
        return false;
    }
    
    public int hashCode(){
        return Longs.hashCode(getSid());
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("sid", getSid())
                .add("name", getName())
                .add("refsets", getRefsets())
                .toString();
    }
    
    public Refset getRefset(long sid){
        for (Refset refset : refsets){
            if (refset.getSid() == sid){
                return refset;
            }
        }
        return null;
    }
    
    @XmlAttribute
    public String getName(){
        return getConcept().getFullySpecifiedName();
    }
    
    @XmlAttribute
    public long getSid(){
        return getConcept().getSerialisedId();
    }
    
}
