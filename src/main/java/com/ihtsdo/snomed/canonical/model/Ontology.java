package com.ihtsdo.snomed.canonical.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.google.common.base.Objects;

@Entity(name="Ontology")
public class Ontology {
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    private String name;

    @OneToMany(mappedBy="ontology")
    private Set<RelationshipStatement> relationshipStatements;

    @OneToMany(mappedBy="ontology")
    private Set<Concept> concepts;
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", getId()).
                add("name", getId()).
                add("statements", getRelationshipStatements() == null ? 0 : getRelationshipStatements().size()).
                add("concepts", getConcepts() == null ? 0 : getConcepts().size()).
                toString();
    }

    public void addRelationshipStatement(RelationshipStatement r){
        getRelationshipStatements().add(r);
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

    public Set<RelationshipStatement> getRelationshipStatements() {
        return relationshipStatements;
    }

    public void setRelationshipStatements(Set<RelationshipStatement> relationshipStatements) {
        this.relationshipStatements = relationshipStatements;
    }

    public Set<Concept> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<Concept> concepts) {
        this.concepts = concepts;
    }


}
