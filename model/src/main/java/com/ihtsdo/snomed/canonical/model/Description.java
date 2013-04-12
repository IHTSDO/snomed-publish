package com.ihtsdo.snomed.canonical.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Description {
    @Id private long id;
    private int status;
    @OneToOne (optional=false)
    private Concept concept;
    private String term;
    private int initialCapitalStatus;
    private int type;
    private String langaugeCode;

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

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLangaugeCode() {
        return langaugeCode;
    }

    public void setLangaugeCode(String langaugeCode) {
        this.langaugeCode = langaugeCode;
    }


}
