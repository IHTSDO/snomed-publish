package com.ihtsdo.snomed.canonical.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Ontology {
    @Id private long id;
    private String name;
    private String description;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<RelationshipStatement> relationshipStatements = new HashSet<RelationshipStatement>();

    @Override
    public String toString() {
        return "id: " + id + ", name: " + name + ", description: " + description + ", number of relationshipStatements: " + relationshipStatements.size();
    }

    public void addRelationshipStatement(RelationshipStatement r){
        relationshipStatements.add(r);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<RelationshipStatement> getRelationshipStatements() {
        return relationshipStatements;
    }

    public void setRelationshipStatements(Set<RelationshipStatement> relationshipStatements) {
        this.relationshipStatements = relationshipStatements;
    }


}
