package com.ihtsdo.snomed.dto.refset;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.exception.UnrecognisedRefsetException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.model.refset.RefsetRule;
import com.ihtsdo.snomed.model.refset.Visitor;
import com.ihtsdo.snomed.model.refset.rule.BaseSetOperationRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;

public class RefsetPlanDto {
    private long id;
    private List<RefsetRuleDto> refsetRules = new ArrayList<>();
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
        if (o instanceof RefsetRuleDto){
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
        return Longs.hashCode(id);
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

    public Long getTerminal() {
        return terminal;
    }

    public void setTerminal(Long terminal) {
        this.terminal = terminal;
    }
    
    
    
    public static Builder getBuilder(RefsetPlan refsetPlan) {
        return new Builder(refsetPlan);
    }
    

    public static class Builder implements Visitor{
        private RefsetPlanDto built;

        Builder(RefsetPlan refsetPlan) {
            built = new RefsetPlanDto();
            built.refsetRules = new ArrayList<>();
            refsetPlan.getTerminal().accept(this);;
        }

        public RefsetPlanDto build() {
            return built;
        }

        @Override
        public void visit(RefsetRule rule) {
            RefsetRuleDto refsetRuleDto = new RefsetRuleDto();
            refsetRuleDto.setType(RefsetRuleDto.CLASS_TYPE_MAP.get(rule.getClass()));
            if (rule instanceof ListConceptsRefsetRule){
                ListConceptsRefsetRule lcRule = (ListConceptsRefsetRule)rule;
                for (Concept c : lcRule.getConcepts()){
                    refsetRuleDto.addConcept(new ConceptDto(c.getSerialisedId()));
                }
            }
            else if (rule instanceof BaseSetOperationRefsetRule){
                BaseSetOperationRefsetRule baseRule = (BaseSetOperationRefsetRule) rule;
                refsetRuleDto.setLeft(((BaseRefsetRule)baseRule.getIncomingRules().get(BaseSetOperationRefsetRule.LEFT_OPERAND)).getId());
                refsetRuleDto.setRight(((BaseRefsetRule)baseRule.getIncomingRules().get(BaseSetOperationRefsetRule.RIGHT_OPERAND)).getId());
            }else{
                throw new UnrecognisedRefsetException("I've not been able to handle RefsetRule of class " + rule.getClass());
            }
            built.refsetRules.add(refsetRuleDto);
        }
    }
    
    
}
