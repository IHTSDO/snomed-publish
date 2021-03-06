package com.ihtsdo.snomed.model.xml;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Description;

@XmlRootElement(name="description")
public class XmlEmbeddedDescription {

    @XmlTransient
    private long id;
    
    @XmlAttribute
    private URL href;
        
    private long serialisedId;
    private String term;
    private String languageCode;    
    private int effectiveTime; 
    private boolean active;
        
    public XmlEmbeddedDescription(Description d) throws MalformedURLException{
        setId(d.getId());
        setSerialisedId(d.getSerialisedId());
        setTerm(d.getTerm());
        setLanguageCode(d.getLanguageCode());
        setEffectiveTime(d.getEffectiveTime());
        setActive(d.isActive());
        setHref(UrlBuilder.createDescriptionUrl(d));
    }
    
    public XmlEmbeddedDescription(){};
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("internalId", getSerialisedId())
                .add("term", getTerm())
                .add("languageCode", getLanguageCode())
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
        if (o instanceof Description){
            Description d = (Description) o;
            if (d.getSerialisedId() == this.getSerialisedId()){
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
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    public String getLanguageCode() {
        return languageCode;
    }
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    public int getEffectiveTime() {
        return effectiveTime;
    }
    public void setEffectiveTime(int effectiveTime) {
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
