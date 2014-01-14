package com.ihtsdo.snomed.dto.refset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.refset.BaseRule;
import com.ihtsdo.snomed.model.refset.BaseRule.RuleType;

@XmlRootElement(name="rule")
public class RuleDto {
    
    @Transient
    @JsonIgnore
    public boolean isSetOperation(){
        return BaseRule.SET_OPERATIONS.contains(getType());
    }
    
    @Transient
    @JsonIgnore    
    public boolean isListOperation(){
        return getType() == RuleType.LIST;
    }
    
    
    @Transient
    @JsonIgnore    
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
    
    public RuleDto addConcept(ConceptDto concept){
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
        if (o instanceof RuleDto){
            RuleDto dto = (RuleDto) o;
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
        private RuleDto built;

        Builder() {
            built = new RuleDto();
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

        public RuleDto build() {
            return built;
        }
    }
}
