package com.ihtsdo.snomed.dto.refset.validation;

import com.google.common.base.Objects;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.dto.refset.validation.GlobalValidationError.Builder;

public class FieldValidationError extends GlobalValidationError{
    public RefsetRuleDto rule;
    
    public FieldValidationError(RefsetRuleDto rule, ValidationResult.Error error, String defaultMessage){
        super(error, defaultMessage);
        this.rule = rule;
    }
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("error", getError())
                .add("defaultMessage", getDefaultMessage())
                .add("rule id", getRule().getId())
                .add("params", this.getParams())
                .toString();
    }
    
    @Override
    public boolean equals (Object o){
        if (o instanceof GlobalValidationError){
            FieldValidationError f = (FieldValidationError) o;
            if (java.util.Objects.equals(f.getDefaultMessage(), this.getDefaultMessage()) && 
                java.util.Objects.equals(f.getError(), this.getError()) &&
                java.util.Objects.equals(f.getParams(), this.getParams()) &&
                java.util.Objects.equals(f.getRule(), this.getRule())){
                return true;
            }
        }
        return false;
    }            
    
    @Override
    public int hashCode(){
        return java.util.Objects.hash(getDefaultMessage(), getError(), getRule());
    }   
    
    public FieldValidationError withParam(String param){
        getParams().add(param);
        return this;
    }    

    public RefsetRuleDto getRule() {
        return rule;
    }

    public void setRule(RefsetRuleDto rule) {
        this.rule = rule;
    }
    
    public static Builder getBuilder(ValidationResult.Error error, RefsetRuleDto rule, String defaultMessage) {
        return new Builder(error, rule, defaultMessage);
    }    

    public static class Builder{
        private FieldValidationError built;

        Builder(ValidationResult.Error error, RefsetRuleDto rule, String defaultMessage) {
            built = new FieldValidationError(rule, error, defaultMessage);
        }
        
        public Builder param(String param){
            built.withParam(param);
            return this;
        }

        public FieldValidationError build() {
            return built;
        }
    }        
}