package com.ihtsdo.snomed.canonical.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;

@Entity(name="Ontology")
public class Ontology {
	
    
    @Transient
    @XmlTransient private Concept isKindOfPredicate;
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    private String name;

    @OneToMany(mappedBy="ontology")
    private Set<Statement> statements;

    @OneToMany(mappedBy="ontology")
    private Set<Concept> concepts;
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", getId()).
                add("name", getId()).
                add("statements", getStatements() == null ? 0 : getStatements().size()).
                add("concepts", getConcepts() == null ? 0 : getConcepts().size()).
                //HERE
                add("isA", (isKindOfPredicate == null) ? "not set" : "set").toString();
    }

    public void addStatement(Statement r){
        getStatements().add(r);
    }
    //HERE
    public Concept getIsKindOfPredicate(){
        if (isKindOfPredicate == null){
            for (Concept c : concepts){
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

    public void setConcepts(Set<Concept> concepts) {
        this.concepts = concepts;
    }

    //HERE
//    public Concept getIsKindOfPredicate(){
//        if (isKindOfPredicate == null){
//            for (Statement s : getStatements()){
//                if (s.isKindOfRelationship()){
//                    isKindOfPredicate = s.getPredicate();
//                    break;
//                }
//            }
//        }
//        if (isKindOfPredicate == null){
//            throw new IllegalStateException("IsA Concept not found in ontology");
//        }
//        return isKindOfPredicate;
//    }
//
//    public void setIsKindOfPredicate(Concept isKindOfPredicate) {
//        this.isKindOfPredicate = isKindOfPredicate;
//    }

    

}
