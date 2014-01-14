package com.ihtsdo.snomed.model.refset;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.dto.refset.RuleDto;
import com.ihtsdo.snomed.exception.ProgrammingException;
import com.ihtsdo.snomed.exception.validation.UnrecognisedRefsetRuleTypeException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.rule.DifferenceRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.IntersectionRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.SymmetricDifferenceRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.UnionRefsetRule;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name="refsetRuleType",
        discriminatorType=DiscriminatorType.STRING
    )
public abstract class BaseRule implements Rule{
    
    public enum RuleType{
        DIFFERENCE, INTERSECTION, LIST, SYMMETRIC_DIFFERENCE, UNION, NOT_SET;
    }    
 

    public static final List<RuleType> SET_OPERATIONS = new ArrayList<>();

    @SuppressWarnings("rawtypes")
    public static final Map<RuleType, Class> TYPE_CLASS_MAP = new HashMap<>();

    //TODO: Wow, this sucks. Refactor
    @SuppressWarnings("rawtypes")
    public static final Map<Class, RuleType> CLASS_TYPE_MAP = new HashMap<>();
    

    
    static{
        CLASS_TYPE_MAP.put(DifferenceRefsetRule.class, RuleType.DIFFERENCE);
        CLASS_TYPE_MAP.put(IntersectionRefsetRule.class, RuleType.INTERSECTION);
        CLASS_TYPE_MAP.put(ListConceptsRefsetRule.class, RuleType.LIST);
        CLASS_TYPE_MAP.put(SymmetricDifferenceRefsetRule.class, RuleType.SYMMETRIC_DIFFERENCE);
        CLASS_TYPE_MAP.put(UnionRefsetRule.class, RuleType.UNION);
        
        TYPE_CLASS_MAP.put(RuleType.DIFFERENCE, DifferenceRefsetRule.class);
        TYPE_CLASS_MAP.put(RuleType.INTERSECTION, IntersectionRefsetRule.class);
        TYPE_CLASS_MAP.put(RuleType.LIST, ListConceptsRefsetRule.class);
        TYPE_CLASS_MAP.put(RuleType.SYMMETRIC_DIFFERENCE, SymmetricDifferenceRefsetRule.class);
        TYPE_CLASS_MAP.put(RuleType.UNION, UnionRefsetRule.class);
        
        SET_OPERATIONS.add(RuleType.DIFFERENCE);
        SET_OPERATIONS.add(RuleType.INTERSECTION);
        SET_OPERATIONS.add(RuleType.SYMMETRIC_DIFFERENCE);
        SET_OPERATIONS.add(RuleType.UNION);
    }       

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)    
    protected Long id;
    
    @OneToMany(targetEntity=BaseRule.class, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn
    protected Map<String, Rule> incomingRules = new HashMap<String, Rule>();
    
    @NotNull
    private Date creationTime;
    
    @NotNull
    private Date modificationTime;
    
    @Version
    private long version = 0;
    
    @Transient
    public static RuleType typeFor(BaseRule rule){
        return CLASS_TYPE_MAP.get(rule.getClass());
    }
    
    public static BaseRule getRuleInstanceFor(RuleDto ruleDto) throws UnrecognisedRefsetRuleTypeException {
        if (ruleDto.getType() == RuleType.NOT_SET){
            throw new UnrecognisedRefsetRuleTypeException(ruleDto, "Rule type not set");            
        }
        if (TYPE_CLASS_MAP.get(ruleDto.getType()) == null){
            throw new ProgrammingException("Failed to find class type for rule type " + ruleDto.getType());
        }
        try {
            return (BaseRule)TYPE_CLASS_MAP.get(ruleDto.getType()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UnrecognisedRefsetRuleTypeException(ruleDto, "I've not been able to handle Rule of type " + ruleDto.getType() + " and class " + TYPE_CLASS_MAP.get(ruleDto.getType()));
        }
    }    
    @Override
    public Set<Concept> generateConcepts(){
        Map<String, Set<Concept>> parentResults = new HashMap<>();
        for (String name : incomingRules.keySet()){
            parentResults.put(name, incomingRules.get(name).generateConcepts());
        }
        return apply(parentResults);
    }
    
    protected abstract Set<Concept> apply(Map<String, Set<Concept>> inputs);
    
    @Override
    public Rule append(String inputName, Rule rule){
        ((BaseRule)rule).getIncomingRules().put(inputName, this);
        return rule;
    }
    
    @Override
    public Rule prepend(String inputName, Rule rule){
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
            BaseRule r = (BaseRule) o;
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
    
    @Override
    public abstract BaseRule clone();
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Rule> getIncomingRules() {
        return incomingRules;
    }

    public void setIncomingRules(Map<String, Rule> incomingRules) {
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
