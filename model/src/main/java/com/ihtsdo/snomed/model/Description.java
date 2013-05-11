package com.ihtsdo.snomed.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Description {
    
    //SHARED
    @Id private long id;
    private String term;
    @OneToOne(mappedBy="description") private Concept aboutConcept;
    private String langaugeCode;
    
    //RF1
    private int status;
    private int initialCapitalStatus;
    private int typeId;

    //RF2
    @Column(nullable=true) private Date effectiveTime;
    @Column(nullable=true, columnDefinition = "BIT", length = 1) private boolean active;
    @OneToOne private Concept type;
    @OneToOne private Concept caseSignificance;
    @OneToOne private Concept module;

    @Override
    public String toString() {
        return "not implemented";
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


    public Concept getAboutConcept() {
        return aboutConcept;
    }

    public void setAboutConcept(Concept aboutConcept) {
        this.aboutConcept = aboutConcept;
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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getLangaugeCode() {
        return langaugeCode;
    }

    public void setLangaugeCode(String langaugeCode) {
        this.langaugeCode = langaugeCode;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
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


}
