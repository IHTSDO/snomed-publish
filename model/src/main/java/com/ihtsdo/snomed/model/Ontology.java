package com.ihtsdo.snomed.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;

@Entity(name="Ontology")
public class Ontology {
    
    public enum Source{
        RF1, RF2, CANONICAL, CHILD_PARENT, UNKNOWN;
    }    

    public Ontology(){}
    public Ontology(long id){
        this.id = id;
    }
    
    @Transient
    @XmlTransient 
    private Concept isKindOfPredicate;
    
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    private String name;

    @Enumerated
    private Source source = Source.UNKNOWN;
    
    @OneToMany(mappedBy="ontology")
    private Set<Statement> statements = new HashSet<>();

    @OneToMany(mappedBy="ontology")
    private Set<Concept> concepts = new HashSet<Concept>();

    @OneToMany(mappedBy="ontology")
    private Set<Description> descriptions = new HashSet<Description>();

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", getId()).
                add("source", getSource()).
                add("name", getName()).
                add("statements", getStatements() == null ? 0 : getStatements().size()).
                add("concepts", getConcepts() == null ? 0 : getConcepts().size()).
                add("isA", (isKindOfPredicate == null) ? "not set" : "set").toString();
    }

    public void addStatement(Statement r){
        getStatements().add(r);
    }
    
    public Concept getIsKindOfPredicate(){
        if (isKindOfPredicate == null){
            for (Concept c : getConcepts()){
                if (c.isKindOfPredicate()){
                    isKindOfPredicate = c;
                    return isKindOfPredicate;
                }
            }
            throw new IllegalStateException("IsA Concept not found in ontology");
        }
        else{
        	return isKindOfPredicate;
        }
    }

    public void setIsKindOfPredicate(Concept isKindOfPredicate) {
        this.isKindOfPredicate = isKindOfPredicate;
    } 
    
    public boolean isRf1(){
        return source.equals(Source.RF1);
    }
    
    public boolean isRf2(){
        return source.equals(Source.RF2);
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

    public Set<Statement> getStatements() {
        return statements;
    }

    public void setStatements(Set<Statement> statements) {
        this.statements = statements;
    }

    public Set<Concept> getConcepts() {
        return concepts;
    }
    
    public void addConcept(Concept c){
        getConcepts().add(c);
    }
    
    public void addDescription(Description d){
        getDescriptions().add(d);
    }

    public void setConcepts(Set<Concept> concepts) {
        this.concepts = concepts;
    }

    public Set<Description> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Set<Description> descriptions) {
        this.descriptions = descriptions;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }  
}
