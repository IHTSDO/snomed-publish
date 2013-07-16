package com.ihtsdo.snomed.model.xml;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Concept;

@XmlRootElement(name="concept")
public class XmlEmbeddedConcept {
    @XmlTransient
    private long id;
    
    @XmlAttribute
    private URL href;

    private long serialisedId;
    private String fullySpecifiedName;
    private long effectiveTime;
    private boolean active;
        
    public XmlEmbeddedConcept(Concept c) throws MalformedURLException{
        setSerialisedId(c.getSerialisedId());
        setId(c.getId());   
        setFullySpecifiedName(c.getFullySpecifiedName());
        setEffectiveTime(c.getEffectiveTime());
        setActive(c.isActive());
        setHref(UrlBuilder.createConceptUrl(c));
    }
    
    public XmlEmbeddedConcept(){}

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("serialisedId", getSerialisedId())
                .add("fullySpecifiedName", getFullySpecifiedName())
                .add("effectiveTime", getEffectiveTime())
                .add("active", isActive())
                .add("href", getHref())
                .toString();
    }
    
    @Override
    public int hashCode(){
        return Longs.hashCode(getSerialisedId());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Concept){
            Concept c = (Concept) o;
            if (c.getSerialisedId() == this.getSerialisedId()){
                return true;
            }
        }
        return false;
    } 
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getSerialisedId() {
        return serialisedId;
    }
    public void setSerialisedId(long serialisedId) {
        this.serialisedId = serialisedId;
    }
    public String getFullySpecifiedName() {
        return fullySpecifiedName;
    }
    public void setFullySpecifiedName(String fullySpecifiedName) {
        this.fullySpecifiedName = fullySpecifiedName;
    }
    public long getEffectiveTime() {
        return effectiveTime;
    }
    public void setEffectiveTime(long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public URL getHref() {
        return href;
    }

    public void setHref(URL href) {
        this.href = href;
    }
    
}
