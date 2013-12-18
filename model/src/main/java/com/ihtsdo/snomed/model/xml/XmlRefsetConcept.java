package com.ihtsdo.snomed.model.xml;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Concept;

@XmlRootElement(name="concept")
public class XmlRefsetConcept {
    private static final Logger LOG = LoggerFactory.getLogger(XmlRefsetConcept.class);

    @XmlTransient
    private long id;
    
    @XmlAttribute
    private URL href;
    
    @XmlElement(name="id")
    private long serialisedId;
    
    private String title;
    private long effectiveTime;
    private boolean active;
    
    public XmlRefsetConcept(Concept c){
        setId(c.getSerialisedId());   
        setTitle(c.getFullySpecifiedName());
        setEffectiveTime(c.getEffectiveTime());
        setActive(c.isActive());
        try {
            setHref(UrlBuilder.createConceptUrl(c));
        }
        catch (MalformedURLException e) {
            LOG.error("Unable to build concept href url, setting null: " + e.getMessage(), e);
            setHref(null);
        }
    }
    
    public XmlRefsetConcept(){}

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("serialisedId", getSerialisedId())
                .add("title", getTitle())
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
