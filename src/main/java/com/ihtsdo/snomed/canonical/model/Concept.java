package com.ihtsdo.snomed.canonical.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.primitives.Longs;

@Entity
public class Concept {
    @Id private long id;
    private int status;
    private String fullySpecifiedName;
    private String ctv3id;
    private String snomedId;
    private boolean isPrimitive;

    @OneToMany(mappedBy="subject", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Set<RelationshipStatement> subjectOfRelationShipStatements = new HashSet<RelationshipStatement>();

    @ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinTable(name = "KIND_OF", 
    joinColumns = @JoinColumn(name="child_id"),
    inverseJoinColumns = @JoinColumn(name="parent_id"))
    private Set<Concept> kindOf = new HashSet<Concept>();

    @ManyToMany(mappedBy="kindOf", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Set<Concept> parentOf = new HashSet<Concept>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    @Override
    public int hashCode(){
        return Longs.hashCode(id);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Concept){
            Concept c = (Concept) o;
            if (c.id == this.id){
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
    public String getFullySpecifiedName() {
        return fullySpecifiedName;
    }
    public void setFullySpecifiedName(String fullySpecifiedName) {
        this.fullySpecifiedName = fullySpecifiedName;
    }
    public String getCtv3id() {
        return ctv3id;
    }
    public void setCtv3id(String ctv3id) {
        this.ctv3id = ctv3id;
    }
    public String getSnomedId() {
        return snomedId;
    }
    public void setSnomedId(String snomedId) {
        this.snomedId = snomedId;
    }
    public boolean isPrimitive() {
        return isPrimitive;
    }
    public void setPrimitive(boolean isPrimitive) {
        this.isPrimitive = isPrimitive;
    }

    public Set<Concept> getKindOf() {
        return kindOf;
    }

    public void setKindOf(Set<Concept> kindOf) {
        this.kindOf = kindOf;
    }

    public Set<Concept> getParentOf() {
        return parentOf;
    }

    public void setParentOf(Set<Concept> parentOf) {
        this.parentOf = parentOf;
    }

    public void addKindOf(Concept concept){
        this.kindOf.add(concept);
    }

    public void addParentOf(Concept concept){
        this.parentOf.add(concept);
    }

    public Set<RelationshipStatement> getSubjectOfRelationShipStatements(){
        return subjectOfRelationShipStatements;
    }

    public void addSubjectOfRelationShipStatements(RelationshipStatement statement){
        this.subjectOfRelationShipStatements.add(statement);
    }
}
