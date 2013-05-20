package com.ihtsdo.snomed.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

@Entity
public class Description {
    
    //SHARED
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private long id;
    private long serialisedId;
    private String term;
    @OneToOne 
    private Concept about;
    private String languageCode;
    @XmlTransient
    @OneToOne 
    private Ontology ontology;
    
    //RF1
    private int status;
    private int initialCapitalStatus;
    private int descriptionTypeId;

    //RF2 
    private int effectiveTime;
    @Column(columnDefinition = "BIT", length = 1) 
    private boolean active;
    @OneToOne 
    private Concept type;
    @OneToOne 
    private Concept caseSignificance;
    @OneToOne 
    private Concept module;
    
    public Description(){}
    public Description(long serialisedId){this.serialisedId = serialisedId;}

    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("internalId", getSerialisedId())
                .add("ontology", getOntology() == null ? null : getOntology().getId())
                .add("term", getTerm())
                .add("about", getAbout() == null ? null : getAbout().getSerialisedId())
                .add("languageCode", getLanguageCode())
                .add("status(rf1)", getStatus())
                .add("initialCapitalStatus(rf1)", getInitialCapitalStatus())
                .add("typeId(rf1)", getDescriptionTypeId())
                .add("effectiveTime(rf2)", getEffectiveTime())
                .add("active(rf2)", isActive())
                .add("type(rf2)", getType() == null ? null : getType().getSerialisedId())
                .add("caseSignificance(rf2)", getCaseSignificance())
                .add("module(rf2)", getModule())
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

    /*
     * Generated Getters and Setters
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public Concept getAbout() {
        return about;
    }

    public void setAbout(Concept about) {
        this.about = about;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getInitialCapitalStatus() {
        return initialCapitalStatus;
    }

    public void setInitialCapitalStatus(int initialCapitalStatus) {
        this.initialCapitalStatus = initialCapitalStatus;
    }


    public int getDescriptionTypeId() {
        return descriptionTypeId;
    }
    public void setDescriptionTypeId(int descriptionTypeId) {
        this.descriptionTypeId = descriptionTypeId;
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

    public Concept getType() {
        return type;
    }

    public void setType(Concept type) {
        this.type = type;
    }

    public Concept getCaseSignificance() {
        return caseSignificance;
    }

    public void setCaseSignificance(Concept caseSignificance) {
        this.caseSignificance = caseSignificance;
    }

    public Concept getModule() {
        return module;
    }

    public void setModule(Concept module) {
        this.module = module;
    }

    public long getSerialisedId() {
        return serialisedId;
    }

    public void setSerialisedId(long serialisedId) {
        this.serialisedId = serialisedId;
    }

    public Ontology getOntology() {
        return ontology;
    }

    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }


}
