package com.ihtsdo.snomed.dto.refset.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.ihtsdo.snomed.dto.refset.RuleDto;

@XmlRootElement(name="validation-result")
public class ValidationResult{
    
    public enum Error{
        NULL_OR_ZERO_REFSET_RULE_ID,
        UNCONNECTED_REFSET_RULE,
        REFERENCING_UNDECLARED_RULE,
        SELF_REFERENCING_RULE,
        LEFT_AND_RIGHT_OPERAND_REFERENCE_SAME_RULE,
        UNRECOGNISED_REFSET_RULE_TYPE,
        EMPTY_CONCEPT_LIST,
        INVALID_CONCEPT_ID,
        RULE_REFERENCED_MORE_THAN_ONCE,
        DECLARED_RULE_NEVER_REFERENCED,
        MORE_THAN_ONE_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE,
        NO_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE
    }
    
    private static final Map<ValidationResult.Error, String> errorToMessageKeyMap = new HashMap<>();
    
    static
    {   
        errorToMessageKeyMap.put(Error.NULL_OR_ZERO_REFSET_RULE_ID, "validation.error.null.or.zero.refset.rule.id");
        errorToMessageKeyMap.put(Error.UNCONNECTED_REFSET_RULE, "validation.error.unconnected.refset.rule");
        errorToMessageKeyMap.put(Error.REFERENCING_UNDECLARED_RULE, "validation.error.referencing.undeclared.rule");
        errorToMessageKeyMap.put(Error.SELF_REFERENCING_RULE, "validation.error.self.refrencing.rule");
        errorToMessageKeyMap.put(Error.LEFT_AND_RIGHT_OPERAND_REFERENCE_SAME_RULE, "validation.error.left.and.right.operand.reference.same.rule");
        errorToMessageKeyMap.put(Error.UNRECOGNISED_REFSET_RULE_TYPE, "validation.error.unrecognised.refset.rule.type");
        errorToMessageKeyMap.put(Error.EMPTY_CONCEPT_LIST, "validation.error.empty.concept.list");
        errorToMessageKeyMap.put(Error.INVALID_CONCEPT_ID, "validation.error.missing.concept.id");        
        errorToMessageKeyMap.put(Error.RULE_REFERENCED_MORE_THAN_ONCE, "validation.error.rule.referenced.more.than.once");
        errorToMessageKeyMap.put(Error.DECLARED_RULE_NEVER_REFERENCED, "validation.error.declared.rule.never.referenced");
        errorToMessageKeyMap.put(Error.MORE_THAN_ONE_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE, "validation.error.more.than.one.unreferenced.rule");
        errorToMessageKeyMap.put(Error.NO_UNREFERENCED_RULE_FOR_TERMINAL_CANDIDATE, "validation.error.no.unreferenced.rule.for.terminal.candidate");
    }
    
    public static String messageKeyFor(final ValidationResult.Error error){
        return errorToMessageKeyMap.get(error);
    }

    private boolean success = true;
    private RuleDto terminal;
    
    private List<GlobalValidationError> globalErrors = new ArrayList<>();
    private List<FieldValidationError> fieldErrors = new ArrayList<>();;
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("success", isSuccess())
                .add("globalErrors", getGlobalErrors())
                .add("fieldErrors", getFieldErrors())
                .toString();            
    }

    @Override
    public int hashCode(){
        return java.util.Objects.hash(
                isSuccess(), 
                getGlobalErrors(), 
                getFieldErrors());
    }        
    
    @Override
    public boolean equals (Object o){
        if (o instanceof ValidationResult){
            ValidationResult v = (ValidationResult) o;
            if (java.util.Objects.equals(v.getGlobalErrors(), this.getGlobalErrors()) && 
                java.util.Objects.equals(v.getFieldErrors(), this.getFieldErrors()) &&
                v.isSuccess() == this.isSuccess()){
                return true;
            }
        }
        return false;
    }
    
    public ValidationResult addError(ValidationResult.Error error, RuleDto rule, String defaultMessage){
        setSuccess(false);
        fieldErrors.add(new FieldValidationError(rule, error, defaultMessage));
        return this;
    }
    
    public ValidationResult addError(ValidationResult.Error error, String defaultMessage){
        setSuccess(false);
        globalErrors.add(new GlobalValidationError(error, defaultMessage));
        return this;
    }
    
    public ValidationResult addError(GlobalValidationError error){
        setSuccess(false);
        globalErrors.add(error);
        return this;
    }

    public ValidationResult addError(FieldValidationError error){
        setSuccess(false);
        fieldErrors.add(error);
        return this;
    }    
    
    protected void setSuccess(boolean success) {
        this.success = success;
        
    }
    
    public ValidationResult withParams(Collection<String> params){
        params.addAll(params);
        return this;
    }    
    
    public boolean isSuccess() {
        return success;
    }
   
    public RuleDto getTerminal() {
        return terminal;
    }

    public void setTerminal(RuleDto terminal) {
        this.terminal = terminal;
    }

    public List<GlobalValidationError> getGlobalErrors() {
        return globalErrors;
    }

    public List<FieldValidationError> getFieldErrors() {
        return fieldErrors;
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }
    
    public static class Builder {
        private ValidationResult built;

        Builder() {
            built = new ValidationResult();
        }

        public Builder terminal(RuleDto terminal){
            built.setTerminal(terminal);
            return this;
        }
        
        public ValidationResult build(){
            return built;
        }
    }    
}