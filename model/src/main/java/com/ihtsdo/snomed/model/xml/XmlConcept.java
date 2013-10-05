package com.ihtsdo.snomed.model.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Statement;

@XmlRootElement(name="concept")
public class XmlConcept {
    @XmlTransient
    private long id;
    
    @XmlAttribute
    private URL href;

    private long serialisedId;
    private String fullySpecifiedName;
    private long effectiveTime;
    private boolean active;
    
    private URL ontology;
    
    @XmlElementWrapper(name = "descriptions")
    @XmlElement(name="description")
    private Set<XmlEmbeddedDescription> descriptions = new HashSet<>();
    
    private XmlEmbeddedConcept statusConcept;
    private XmlEmbeddedConcept moduleConcept;
    
    @XmlElementWrapper(name = "subjectOf")
    @XmlElement(name="statement")
    private Set<XmlEmbeddedStatement> subjectOfStatements = new HashSet<>();
    
    @XmlElementWrapper(name = "objectOf")
    @XmlElement(name="statement")
    private Set<XmlEmbeddedStatement> objectOfStatements = new HashSet<>();
    
    @XmlElementWrapper(name = "predicateOf")
    @XmlElement(name="statement")
    private Set<XmlEmbeddedStatement> predicateOfStatements = new HashSet<>();
    
    @XmlElementWrapper(name = "kindOf")
    @XmlElement(name="concept")
    private Set<XmlShortEmbeddedConcept> kindOfs = new HashSet<>();
    
    @XmlElementWrapper(name = "parentOf")
    @XmlElement(name="concept")
    private Set<XmlShortEmbeddedConcept> parentOf = new HashSet<>();
    
    public XmlConcept(Concept c) throws MalformedURLException{
        setSerialisedId(c.getSerialisedId());
        setId(c.getId());   
        setFullySpecifiedName(c.getFullySpecifiedName());
        setEffectiveTime(c.getEffectiveTime());
        setActive(c.isActive());
        setOntology(UrlBuilder.createOntologyUrl(c));
        setHref(UrlBuilder.createConceptUrl(c));

        setStatusConcept(new XmlEmbeddedConcept(c.getStatus()));
        setModuleConcept(new XmlEmbeddedConcept(c.getModule())); 

        for (Description d : c.getDescription()){
            getDescriptions().add(new XmlEmbeddedDescription(d));
        }
        for (Statement s : c.getObjectOfStatements()){
            getObjectOfStatements().add(new XmlEmbeddedStatement(s));
        }
        for (Statement s : c.getPredicateOfStatements()){
            getPredicateOfStatements().add(new XmlEmbeddedStatement(s));
        }
        for (Statement s : c.getSubjectOfStatements()){
            getSubjectOfStatements().add(new XmlEmbeddedStatement(s));
        }
        for (Concept p : c.getKindOfs()){
            getKindOfs().add(new XmlShortEmbeddedConcept(p));
        }
        for (Concept ch : c.getParentOf()){
            getParentOf().add(new XmlShortEmbeddedConcept(ch));
        }
    }
    
    public XmlConcept(){}

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("serialisedId", getSerialisedId())
                .add("ontology", getOntology())
                .add("descriptions", getDescriptions() == null ? 0 : getDescriptions().size())
                .add("fullySpecifiedName", getFullySpecifiedName())
                .add("effectiveTime", getEffectiveTime())
                .add("active", isActive())
                .add("status", getStatusConcept())
                .add("module", getModuleConcept())
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
    public URL getOntology() {
        return ontology;
    }
    public void setOntology(URL ontology) {
        this.ontology = ontology;
    }
    public Set<XmlEmbeddedDescription> getDescriptions() {
        return descriptions;
    }
    public void setDescriptions(Set<XmlEmbeddedDescription> descriptions) {
        this.descriptions = descriptions;
    }

    public XmlEmbeddedConcept getStatusConcept() {
        return statusConcept;
    }

    public void setStatusConcept(XmlEmbeddedConcept statusConcept) {
        this.statusConcept = statusConcept;
    }

    public XmlEmbeddedConcept getModuleConcept() {
        return moduleConcept;
    }

    public void setModuleConcept(XmlEmbeddedConcept moduleConcept) {
        this.moduleConcept = moduleConcept;
    }

    public void setKindOfs(Set<XmlShortEmbeddedConcept> kindOfs) {
        this.kindOfs = kindOfs;
    }

    public void setParentOf(Set<XmlShortEmbeddedConcept> parentOf) {
        this.parentOf = parentOf;
    }

    public Set<XmlEmbeddedStatement> getSubjectOfStatements() {
        return subjectOfStatements;
    }
    public void setSubjectOfStatements(Set<XmlEmbeddedStatement> subjectOfStatements) {
        this.subjectOfStatements = subjectOfStatements;
    }
    public Set<XmlEmbeddedStatement> getObjectOfStatements() {
        return objectOfStatements;
    }
    public void setObjectOfStatements(Set<XmlEmbeddedStatement> objectOfStatements) {
        this.objectOfStatements = objectOfStatements;
    }
    public Set<XmlEmbeddedStatement> getPredicateOfStatements() {
        return predicateOfStatements;
    }
    public void setPredicateOfStatements(Set<XmlEmbeddedStatement> predicateOfStatements) {
        this.predicateOfStatements = predicateOfStatements;
    }

    public Set<XmlShortEmbeddedConcept> getKindOfs() {
        return kindOfs;
    }

    public Set<XmlShortEmbeddedConcept> getParentOf() {
        return parentOf;
    }

    public URL getHref() {
        return href;
    }

    public void setHref(URL href) {
        this.href = href;
    }
    
}
