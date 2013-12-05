package com.ihtsdo.snomed.dto.refset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.exception.UnrecognisedRefsetRuleTYpeException;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.DifferenceRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.IntersectionRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.SymmetricDifferenceRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.UnionRefsetRule;

@XmlRootElement(name="rule")
public class RefsetRuleDto {
    
    public enum RuleType{
        DIFFERENCE, INTERSECTION, LIST, SYMMETRIC_DIFFERENCE, UNION;
    }
    
    //TODO: Wow, this sucks. Refactor
    @Transient
    @SuppressWarnings("rawtypes")
    public static final Map<Class, RuleType> CLASS_TYPE_MAP = new HashMap<>();
    
    @Transient
    @SuppressWarnings("rawtypes")
    public static final Map<RuleType, Class> TYPE_CLASS_MAP = new HashMap<>();
    
    @Transient
    @SuppressWarnings("rawtypes")
    public static final List<RuleType> SET_OPERATIONS = new ArrayList<>();
    
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

    public static BaseRefsetRule getRuleInstanceFor(RefsetRuleDto rule) throws UnrecognisedRefsetRuleTYpeException {
        try {
            return (BaseRefsetRule)RefsetRuleDto.TYPE_CLASS_MAP.get(rule.getType()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UnrecognisedRefsetRuleTYpeException(rule.getId(), "I've not been able to handle RefsetRule of type " + rule.getType() + " and class " + RefsetRuleDto.TYPE_CLASS_MAP.get(rule.getType()));
        }
    }    
    
    @Transient
    public boolean isSetOperation(){
        return SET_OPERATIONS.contains(getType());
    }
    
    @Transient
    public boolean isListOperation(){
        return getType() == RuleType.LIST;
    }
    
    
    @Transient
    public static boolean isPersisted(long id){
        if (id > 0){
            return true;
        }
        return false;
    }
    
    private Long id;
    protected RuleType type;
    private Long left;
    private Long right;
    
    @XmlElementWrapper(name = "concepts")
    @XmlElement(name="concept")
    private List<ConceptDto> concepts = new ArrayList<>();
    
    public RefsetRuleDto addConcept(ConceptDto concept){
        getConcepts().add(concept);
        return this;
    }
    
    @Transient
    public List<Long> conceptIds(){
        List<Long> ids = new ArrayList<>(getConcepts().size());
        for (ConceptDto c : getConcepts()){
            ids.add(c.getId());
        }
        return ids;
    } 
 
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("type", getType())
                .add("left", getLeft() == null ? "none" : getLeft())
                .add("right", getRight() == null ? "none" : getRight())
                .add("concepts", getConcepts())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof RefsetRuleDto){
            RefsetRuleDto dto = (RefsetRuleDto) o;
            if (Objects.equal(dto.getId(), getId()) &&
                    (Objects.equal(dto.getType(), getType())) &&
                    (Objects.equal(dto.getLeft(), getLeft())) &&
                    (Objects.equal(dto.getRight(), getRight())) &&
                    (Objects.equal(new HashSet<>(dto.getConcepts()), new HashSet<>(getConcepts())))){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        if (getId() != null){
            return Longs.hashCode(getId());
        }else{
            return 0; //delegate to equals method
        }
    }    

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public RuleType getType() {
        return type;
    }
    public void setType(RuleType type) {
        this.type = type;
    }
    public Long getLeft() {
        return left;
    }
    public void setLeft(Long left) {
        this.left = left;
    }
    public Long getRight() {
        return right;
    }
    public void setRight(Long right) {
        this.right = right;
    }
    public List<ConceptDto> getConcepts() {
        return concepts;
    }
    public void setConcepts(List<ConceptDto> concepts) {
        this.concepts = concepts;
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }
    
    public static class Builder {
        private RefsetRuleDto built;

        Builder() {
            built = new RefsetRuleDto();
        }

        public Builder id(Long id){
            built.setId(id);
            return this;
        }
        
        public Builder type(RuleType type){
            built.setType(type);
            return this;
        }
        
        public Builder left(Long left){
            built.setLeft(left);
            return this;
        }
        
        public Builder right(Long right){
            built.setRight(right);
            return this;
        }
        
        public Builder add(ConceptDto conceptDto){
            if (built.getConcepts() == null){
                built.setConcepts(new ArrayList<ConceptDto>());
            }
            built.getConcepts().add(conceptDto);
            return this;
        }
        
        public Builder concepts(List<ConceptDto> conceptDtos){
            built.setConcepts(conceptDtos);
            return this;
        }

        public RefsetRuleDto build() {
            return built;
        }
    }
}
