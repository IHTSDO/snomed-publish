package com.ihtsdo.snomed.dto.refset;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.exception.UnrecognisedRefsetException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.model.refset.RefsetRule;
import com.ihtsdo.snomed.model.refset.Visitor;
import com.ihtsdo.snomed.model.refset.rule.BaseSetOperationRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;

@XmlRootElement(name="plan")
public class RefsetPlanDto {
    
    @Transient
    private long id;
    
    public RefsetPlanDto(){}
    
    @XmlElementWrapper(name = "rules")
    @XmlElement(name="rule")
    private List<RefsetRuleDto> refsetRules = new ArrayList<>();
    
    @NotNull(message="validation.refsetplan.terminal.not.null")
    private Long terminal;
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("refsetRules", getRefsetRules())
                .add("terminal", getTerminal())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof RefsetPlanDto){
            RefsetPlanDto dto = (RefsetPlanDto) o;
            if (Objects.equal(dto.getId(), getId()) &&
                    (Objects.equal(dto.getRefsetRules(), getRefsetRules())) &&
                    (Objects.equal(dto.getTerminal(), getTerminal()))){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        return Longs.hashCode(getId());
    } 
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<RefsetRuleDto> getRefsetRules() {
        return refsetRules;
    }

    public void setRefsetRules(List<RefsetRuleDto> refsetRules) {
        this.refsetRules = refsetRules;
    }
    
    public void addRefsetRule(RefsetRuleDto refsetRule){
        this.refsetRules.add(refsetRule);
    }

    public Long getTerminal() {
        return terminal;
    }

    public void setTerminal(Long terminal) {
        this.terminal = terminal;
    }

    public static RefsetPlanDto parse(RefsetPlan plan){
        RefsetPlanParser parser = new RefsetPlanParser();
        RefsetPlanDto planDto = null;
        if (plan.getTerminal() != null){
            plan.getTerminal().accept(parser);
            planDto = parser.getPlanDto();
            planDto.setTerminal(plan.getTerminal().getId());
        }else{
            planDto = new RefsetPlanDto();
        }
        planDto.setId(plan.getId());
        return planDto;
    }
    
    private static class RefsetPlanParser implements Visitor{
        private RefsetPlanDto built = new RefsetPlanDto();
        
        public RefsetPlanDto getPlanDto(){
            return built;
        }
        
        @Override
        public void visit(RefsetRule rule) {
            RefsetRuleDto refsetRuleDto = new RefsetRuleDto();
            refsetRuleDto.setType(RefsetRuleDto.CLASS_TYPE_MAP.get(rule.getClass()));
            refsetRuleDto.setId(rule.getId());
            if (rule instanceof ListConceptsRefsetRule){
                ListConceptsRefsetRule lcRule = (ListConceptsRefsetRule)rule;
                for (Concept c : lcRule.getConcepts()){
                    ConceptDto conceptDto = new ConceptDto(c.getSerialisedId());
                    conceptDto.setTitle(c.getDisplayName());
                    refsetRuleDto.addConcept(conceptDto);
                }
            }
            else if (rule instanceof BaseSetOperationRefsetRule){
                BaseSetOperationRefsetRule setOperationRule = (BaseSetOperationRefsetRule) rule;
                refsetRuleDto.setLeft(setOperationRule.getLeft() == null ? null : setOperationRule.getLeft().getId());
                refsetRuleDto.setRight(setOperationRule.getRight() == null ? null : setOperationRule.getRight().getId());
            }else{
                throw new UnrecognisedRefsetException("I've not been able to handle RefsetRule of class " + rule.getClass());
            }
            built.refsetRules.add(refsetRuleDto);
        }
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }    

    public static class Builder{
        private RefsetPlanDto built;

        Builder() {
            built = new RefsetPlanDto();
        }
        
        public Builder id(long id){
            built.setId(id);
            return this;
        }
        
        public Builder add(RefsetRuleDto ruleDto){
            built.addRefsetRule(ruleDto);
            return this;
        }
        
        public Builder terminal(Long terminalId){
            built.setTerminal(terminalId);
            return this;
        }

        public RefsetPlanDto build() {
            return built;
        }
    }
}
