package com.ihtsdo.snomed.model.xml;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Statement;

@XmlRootElement(name="statement")
public class XmlStatement {

    @XmlTransient
    private long id;
    
    @XmlAttribute
    private URL href;
    
    private URL ontology;    
    private XmlEmbeddedConcept subject;
    private XmlEmbeddedConcept predicate;
    private XmlEmbeddedConcept object;
    
    private long serialisedId = Statement.SERIALISED_ID_NOT_DEFINED;
    private int groupId;
    private boolean active;
    private int effectiveTime;
    
    private XmlEmbeddedConcept characteristicType;
    private XmlEmbeddedConcept module;
    private XmlEmbeddedConcept modifier;
    
    public XmlStatement(Statement s) throws MalformedURLException{
        setId(s.getId());
        setOntology(UrlBuilder.createOntologyUrl(s));
        setSubject(new XmlEmbeddedConcept(s.getSubject()));
        setPredicate(new XmlEmbeddedConcept(s.getPredicate()));
        setObject(new XmlEmbeddedConcept(s.getObject()));
        setSerialisedId(s.getSerialisedId());
        setGroupId(s.getGroupId());
        setActive(s.isActive());
        setEffectiveTime(s.getEffectiveTime());
        setCharacteristicType(new XmlEmbeddedConcept(s.getCharacteristicType()));
        setModule(new XmlEmbeddedConcept(s.getModule()));
        setModifier(new XmlEmbeddedConcept(s.getModifier()));
        setHref(UrlBuilder.createStatementUrl(s));
    }
    
    public XmlStatement(){}
    
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("id", getId())
            .add("serialisedId", getSerialisedId())
            .add("ontology", getOntology())
            .add("subject", getSubject() == null ? null : getSubject().getSerialisedId())
            .add("predicate", getPredicate() == null ? null : getPredicate().getSerialisedId())
            .add("object", getObject() == null ? null : getObject().getSerialisedId())
            .add("groupId", getGroupId())
            .add("characteristicType", getCharacteristicType())
            .add("active", isActive())
            .add("module", getModule())
            .add("modifier", getModifier())
            .toString();
    }    
    
    @Override
    public int hashCode(){
        if (this.getSerialisedId() == Statement.SERIALISED_ID_NOT_DEFINED){
            return Longs.hashCode((this.getSubject() == null) ? -1 : this.getSubject().getSerialisedId());
        }
        return Longs.hashCode(getSerialisedId());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Statement){
            Statement r = (Statement) o;
            
            if ((r.getSerialisedId() == Statement.SERIALISED_ID_NOT_DEFINED) || (this.getSerialisedId() == Statement.SERIALISED_ID_NOT_DEFINED)){
                return (r.getSubject().equals(this.getSubject())
                        && r.getObject().equals(this.getObject())
                        && r.getPredicate().equals(this.getPredicate()));
            }
            
            if (r.getSerialisedId() == this.getSerialisedId()){
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
    public URL getOntology() {
        return ontology;
    }
    public void setOntology(URL ontology) {
        this.ontology = ontology;
    }
    public XmlEmbeddedConcept getSubject() {
        return subject;
    }
    public void setSubject(XmlEmbeddedConcept subject) {
        this.subject = subject;
    }
    public XmlEmbeddedConcept getPredicate() {
        return predicate;
    }
    public void setPredicate(XmlEmbeddedConcept predicate) {
        this.predicate = predicate;
    }
    public XmlEmbeddedConcept getObject() {
        return object;
    }
    public void setObject(XmlEmbeddedConcept object) {
        this.object = object;
    }
    public long getSerialisedId() {
        return serialisedId;
    }
    public void setSerialisedId(long serialisedId) {
        this.serialisedId = serialisedId;
    }
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public int getEffectiveTime() {
        return effectiveTime;
    }
    public void setEffectiveTime(int effectiveTime) {
        this.effectiveTime = effectiveTime;
    }
    public XmlEmbeddedConcept getCharacteristicType() {
        return characteristicType;
    }
    public void setCharacteristicType(XmlEmbeddedConcept characteristicType) {
        this.characteristicType = characteristicType;
    }
    public XmlEmbeddedConcept getModule() {
        return module;
    }
    public void setModule(XmlEmbeddedConcept module) {
        this.module = module;
    }
    public XmlEmbeddedConcept getModifier() {
        return modifier;
    }
    public void setModifier(XmlEmbeddedConcept modifier) {
        this.modifier = modifier;
    }
    
    public URL getHref() {
        return href;
    }

    public void setHref(URL href) {
        this.href = href;
    }    
    
}
