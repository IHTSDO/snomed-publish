package com.ihtsdo.snomed.dto.refset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.dto.refset.validation.FieldValidationError;
import com.ihtsdo.snomed.dto.refset.validation.GlobalValidationError;
import com.ihtsdo.snomed.dto.refset.validation.ValidationResult;
import com.ihtsdo.snomed.exception.UnrecognisedRefsetException;
import com.ihtsdo.snomed.exception.UnrecognisedRefsetRuleTYpeException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.model.refset.RefsetRule;
import com.ihtsdo.snomed.model.refset.Visitor;
import com.ihtsdo.snomed.model.refset.rule.BaseSetOperationRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;

@XmlRootElement(name="plan")
public class RefsetPlanDto {
    private static final Logger LOG = LoggerFactory.getLogger(RefsetPlanDto.class);

    @Transient
    private long id;
    
    public RefsetPlanDto(){}
    
    public RefsetPlanDto(long id){this.id = id;}
    
    @XmlElementWrapper(name = "rules")
    @XmlElement(name="rule")
    @JsonProperty("rules")
    private List<RefsetRuleDto> refsetRules = new ArrayList<>();
    
    @NotNull(message="A terminal rule must be specified")
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
    
    public ValidationResult validate() {
        LOG.debug("parsing refset plan dto: " + this);
        ValidationResult result = new ValidationResult();
        
        //First instantiate the rules, set the ids, and add these ids to the map
        Map<Long, RefsetRuleDto> ruleIdToRuleMap = new HashMap<>();
        for (RefsetRuleDto rule : this.getRefsetRules()){
            ruleIdToRuleMap.put(rule.getId(), rule);
        }
        //Assertion: The map now contains indexed references to all the rule we will need
        //Now we can populate the rule data
        Map<Long, Long> referencedRefsetRuleIds = new HashMap<>();
        for (RefsetRuleDto rule : this.getRefsetRules()){
            LOG.debug("Validating rule {}", rule);
            if ((rule.getId() == null) || rule.getId() == 0){
                result.addError(ValidationResult.Error.NULL_OR_ZERO_REFSET_RULE_ID, rule, "Null or zero rule id");
            }

            //throws exception if not found
            try {
                RefsetRuleDto.getRuleInstanceFor(rule);
            } catch (UnrecognisedRefsetRuleTYpeException e) {
                result.addError(
                    FieldValidationError.getBuilder(
                            ValidationResult.Error.UNRECOGNISED_REFSET_RULE_TYPE,
                            rule,
                            e.getMessage()).
                        param(ruleIdToRuleMap.get(rule.getType()).toString()).
                        build());
            }
            if (rule.isSetOperation()){
                validateSetOperation(result, ruleIdToRuleMap, referencedRefsetRuleIds, rule);
            } else if (rule.isListOperation()){
                validateListOperation(result, ruleIdToRuleMap, rule);
            } else{
                result.addError(
                    FieldValidationError.getBuilder(
                            ValidationResult.Error.UNRECOGNISED_REFSET_RULE_TYPE,
                            rule,
                            "Internal error: Application did not handle rule type " + rule.getType()).
                        param(rule.getType().toString()).
                        build());
            }
        } 
        SetView<Long> unReferencedRules = Sets.difference(ruleIdToRuleMap.keySet(), referencedRefsetRuleIds.keySet());
        if (unReferencedRules.size() > 1){
            result.addError(
                GlobalValidationError.getBuilder(
                        ValidationResult.Error.MORE_THAN_ONE_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE,
                        "Found more than one unreferenced rule. There can only be one unreferenced rule, as the terminal candidate." + 
                              " Unreferenced rules are: " + unReferencedRules.toString()).
                    param(unReferencedRules.toString()).
                    build());
        }
        else if (unReferencedRules.size() < 1){
            result.addError(
                    ValidationResult.Error.NO_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE, 
                    "Unabled to find unreferenced rule to act as terminal candidate");            
        }else{
            result.setTerminal(ruleIdToRuleMap.get(unReferencedRules.iterator().next()));
        }
        
        return result;
    }

    private void validateListOperation(ValidationResult result,
            Map<Long, RefsetRuleDto> ruleIdToRuleMap, RefsetRuleDto rule) {
        if ((rule.getConcepts() == null) || (rule.getConcepts().size() == 0)){
            result.addError(
                FieldValidationError.getBuilder(
                        ValidationResult.Error.EMPTY_CONCEPT_LIST,
                        rule,
                        "No concepts found").
                    build());
        }
        for (ConceptDto concept : rule.getConcepts()){
            if ((concept.getId() == null) || (concept.getId() <= 0)){
                result.addError(
                    FieldValidationError.getBuilder(
                            ValidationResult.Error.INVALID_CONCEPT_ID,
                            rule,
                            "Found concept id with illegal identifier <= 0").
                        build());
            }
        }
    }

    private void validateSetOperation(ValidationResult result,
            Map<Long, RefsetRuleDto> ruleIdToRuleMap,
            Map<Long, Long> referencedRefsetRuleIds, RefsetRuleDto rule) {
        if ((rule.getLeft() == null) || (rule.getLeft() == 0)){
            result.addError(
                FieldValidationError.getBuilder(
                        ValidationResult.Error.UNCONNECTED_REFSET_RULE,
                        rule,
                        "Left rule unconnected").
                    build());
        }
        if ((rule.getRight() == null) || (rule.getRight() == 0)){
            result.addError(ValidationResult.Error.UNCONNECTED_REFSET_RULE, rule, "Right rule unconnected");
        }
        if ((rule.getLeft() != null) && !ruleIdToRuleMap.containsKey(rule.getLeft())){
            result.addError(
                FieldValidationError.getBuilder(
                        ValidationResult.Error.REFERENCING_UNDECLARED_RULE,
                        rule,
                        "Referencing undeclared rule: Left rule " + rule.getLeft() + " has not been declared").
                    param(rule.getLeft() == null ? "null" : rule.getLeft().toString()).
                    build());
        }
        if ((rule.getRight() != null) && !ruleIdToRuleMap.containsKey(rule.getRight())){
            result.addError(
                FieldValidationError.getBuilder(
                        ValidationResult.Error.REFERENCING_UNDECLARED_RULE,
                        rule,
                        "Referencing undeclared rule: Right rule " + rule.getRight() + " has not been declared").
                    param(rule.getRight() == null ? "null" : rule.getRight().toString()).
                    build());                            
                    
        }
        if ((rule.getRight() != null) && (rule.getLeft() != null) && rule.getRight() == rule.getLeft()){
            result.addError(
                FieldValidationError.getBuilder(
                        ValidationResult.Error.LEFT_AND_RIGHT_OPERAND_REFERENCE_SAME_RULE,
                        rule,
                        "Left and right operand rule references the same rule " + rule.getLeft()).
                    param(rule.getLeft() == null ? "null" : rule.getLeft().toString()).
                    build());
        }
        if ((rule.getRight() != null) && (rule.getLeft() != null) && rule.getId() == rule.getLeft()){
            result.addError(
                FieldValidationError.getBuilder(
                        ValidationResult.Error.SELF_REFERENCING_RULE,
                        rule,
                        "Left operand references itself").
                    build());
        }
        if ((rule.getRight() != null) && (rule.getLeft() != null) && rule.getId() == rule.getRight()){
            result.addError(
                FieldValidationError.getBuilder(
                        ValidationResult.Error.SELF_REFERENCING_RULE,
                        rule,
                        "Right operand references itself").
                    build());
        }        
        if (referencedRefsetRuleIds.keySet().contains(rule.getLeft())){
            result.addError(
                FieldValidationError.getBuilder(
                        ValidationResult.Error.RULE_REFERENCED_MORE_THAN_ONCE,
                        rule,
                        "Left reference has already been referenced by rule " + rule.getLeft()).
                    param(rule.getLeft().toString()).
                    build());
        }
        if (referencedRefsetRuleIds.keySet().contains(rule.getRight())){
            result.addError(
                FieldValidationError.getBuilder(
                        ValidationResult.Error.RULE_REFERENCED_MORE_THAN_ONCE,
                        rule,
                        "Right reference has already been referenced by rule " + rule.getRight()).
                    param(rule.getRight().toString()).
                    build());
        }                
        referencedRefsetRuleIds.put(rule.getLeft(), rule.getId());
        referencedRefsetRuleIds.put(rule.getRight(), rule.getId());
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
        
        public Builder id(Long id){
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
