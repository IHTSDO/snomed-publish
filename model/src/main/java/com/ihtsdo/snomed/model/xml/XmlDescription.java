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
public class XmlDescription {

    @XmlTransient
    private long id;
    
    @XmlAttribute(name="href")
    private URL descriptionUrl;
    
    private long serialisedId;
    private String term;
    private String languageCode;
    private XmlEmbeddedConcept about;
    private URL ontology;
    
    private int effectiveTime; 
    private boolean active;
    
    private XmlEmbeddedConcept type; 
    private XmlEmbeddedConcept caseSignificance;
    private XmlEmbeddedConcept module;
    
    public XmlDescription(Description d) throws MalformedURLException{
        setId(d.getId());
        setSerialisedId(d.getSerialisedId());
        setTerm(d.getTerm());
        setLanguageCode(d.getLanguageCode());
        setAbout(new XmlEmbeddedConcept(d.getAbout()));
        setOntology(UrlBuilder.createOntologyUrl(d));
        setEffectiveTime(d.getEffectiveTime());
        setActive(d.isActive());
        setType(new XmlEmbeddedConcept(d.getType()));
        setCaseSignificance(new XmlEmbeddedConcept(d.getCaseSignificance()));
        setModule(new XmlEmbeddedConcept(d.getModule()));
        setDescriptionUrl(UrlBuilder.createDescriptionUrl(d));
    }
    
    public XmlDescription(){};
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("internalId", getSerialisedId())
                .add("ontology", getOntology())
                .add("term", getTerm())
                .add("about", getAbout())
                .add("languageCode", getLanguageCode())
                .add("effectiveTime(rf2)", getEffectiveTime())
                .add("active(rf2)", isActive())
                .add("type", getType())
                .add("caseSignificance", getCaseSignificance())
                .add("module", getModule())
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

    public XmlEmbeddedConcept getAbout() {
        return about;
    }

    public void setAbout(XmlEmbeddedConcept about) {
        this.about = about;
    }

    public URL getOntology() {
        return ontology;
    }

    public void setOntology(URL ontology) {
        this.ontology = ontology;
    }

    public XmlEmbeddedConcept getType() {
        return type;
    }

    public void setType(XmlEmbeddedConcept type) {
        this.type = type;
    }

    public XmlEmbeddedConcept getCaseSignificance() {
        return caseSignificance;
    }

    public void setCaseSignificance(XmlEmbeddedConcept caseSignificance) {
        this.caseSignificance = caseSignificance;
    }

    public XmlEmbeddedConcept getModule() {
        return module;
    }

    public void setModule(XmlEmbeddedConcept module) {
        this.module = module;
    }

    public URL getDescriptionUrl() {
        return descriptionUrl;
    }

    public void setDescriptionUrl(URL descriptionUrl) {
        this.descriptionUrl = descriptionUrl;
    }
}
