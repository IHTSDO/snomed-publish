package com.ihtsdo.snomed.model.refset.rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import com.ihtsdo.snomed.model.Concept;

/*https://code.google.com/p/guava-libraries/wiki/CollectionUtilitiesExplained#Sets*/
@Entity
@DiscriminatorValue("listConcepts")
public class ListConceptsRefsetRule extends SourceRefsetRule{ 
    public static final String NAME = ListConceptsRefsetRule.class.getName();
    
    @ManyToMany(fetch=FetchType.EAGER)
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
            if ((r.getId() == this.getId()) && (r.getConcepts().equals(this.getConcepts()))){
                return true;
            }
        }
        return false;
    }
    
    @Transient
    public List<Long> getConceptIds(){
        List<Long> concepts = new ArrayList<>();
        for (Concept c : getConcepts()){
            concepts.add(c.getSerialisedId());
        }
        return concepts;
    }
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("incomingRules", getIncomingRules())
                .add("concepts", getConceptIds())
                .add("class", this.getClass().getSimpleName())
                .toString();
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
