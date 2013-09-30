package com.ihtsdo.snomed.model.refset;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Concept;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name="refsetRuleType",
        discriminatorType=DiscriminatorType.STRING
    )
public abstract class BaseRefsetRule implements RefsetRule{
    

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)    
    protected Long id;
    
    @OneToMany(targetEntity=BaseRefsetRule.class, cascade=CascadeType.ALL)
    @JoinColumn
    protected Map<String, RefsetRule> incomingRules = new HashMap<String, RefsetRule>();
    
    @NotNull
    private Date creationTime;
    
    @NotNull
    private Date modificationTime;
    
    @Version
    private long version = 0;

    public Set<Concept> generateConcepts(){
        Map<String, Set<Concept>> parentResults = new HashMap<>();
        for (String name : incomingRules.keySet()){
            parentResults.put(name, incomingRules.get(name).generateConcepts());
        }
        return apply(parentResults);
    }
    
    protected abstract Set<Concept> apply(Map<String, Set<Concept>> inputs);
    
    @Override
    public RefsetRule append(String inputName, RefsetRule rule){
        ((BaseRefsetRule)rule).getIncomingRules().put(inputName, this);
        return rule;
    }
    
    @Override
    public RefsetRule prepend(String inputName, RefsetRule rule){
        getIncomingRules().put(inputName, rule);
        return rule;
    }    

    @Override
    public void accept(Visitor visitor){
        visitor.visit(this);        
    }
    
    @Override
    public int hashCode(){
        return Longs.hashCode(getId() == null ? 0 : getId());
    }
    
    @Override
    public boolean equals(Object o){
        if (o.getClass().isInstance(this)){
            BaseRefsetRule r = (BaseRefsetRule) o;
            if ((r.getIncomingRules()).equals(this.getIncomingRules())){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("incomingRules", getIncomingRules())
                .add("class", this.getClass().getSimpleName())
                .toString();
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, RefsetRule> getIncomingRules() {
        return incomingRules;
    }

    public void setIncomingRules(Map<String, RefsetRule> incomingRules) {
        this.incomingRules = incomingRules;
    }
    
    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date(Calendar.getInstance().getTime().getTime());
    }
    
    @PrePersist
    public void prePersist() {
        Date now = new Date(Calendar.getInstance().getTime().getTime());
        creationTime = now;
        modificationTime = now;
    }
}
