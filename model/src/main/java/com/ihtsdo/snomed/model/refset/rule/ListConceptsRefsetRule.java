package com.ihtsdo.snomed.model.refset.rule;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.UniqueConstraint;

import com.ihtsdo.snomed.model.Concept;

/*https://code.google.com/p/guava-libraries/wiki/CollectionUtilitiesExplained#Sets*/
@Entity
@DiscriminatorValue("listConcepts")
public class ListConceptsRefsetRule extends BaseSetOperationRefsetRule{ 
    public static final String NAME = ListConceptsRefsetRule.class.getName();
    
    @ManyToMany
    @JoinTable(
        joinColumns = @JoinColumn(name="addConceptsRefsetRule_id"),
        inverseJoinColumns = @JoinColumn(name="concept_id"),
        uniqueConstraints=@UniqueConstraint(columnNames={"addConceptsRefsetRule_id", "concept_id"}))
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Set<Concept> concepts = new HashSet<>();

    @Override
    protected Set<Concept> apply(Map<String, Set<Concept>> inputs) {
        assert(inputs.size() == 0);
        return getConcepts();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof ListConceptsRefsetRule){
            ListConceptsRefsetRule r = (ListConceptsRefsetRule) o;
            if (r.getConcepts().equals(this.getConcepts())){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString(){
        return "list(" + getConcepts() == null ? "empty" : getConcepts().size() + ")";
    }
    
    public void addConcept(Concept c){
        getConcepts().add(c);
    }

    public Set<Concept> getConcepts() {
        return concepts;
    }
    public void setConcepts(Set<Concept> concepts) {
        this.concepts = concepts;
    }
}