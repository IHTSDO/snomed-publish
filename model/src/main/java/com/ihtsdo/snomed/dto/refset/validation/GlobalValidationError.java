package com.ihtsdo.snomed.dto.refset.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

public class GlobalValidationError{
    protected String defaultMessage;
    protected ValidationResult.Error error;
    protected List<String> params = new ArrayList<>();
    
    public GlobalValidationError(ValidationResult.Error error, String defaultMessage){
        this.defaultMessage = defaultMessage;
        this.error = error;
    }
    
    public String key(){
        return ValidationResult.messageKeyFor(getError());
    }
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("error", getError())
                .add("defaultMessage", getDefaultMessage())
                .add("parameters", getParams())
                .toString();
    }
    
    @Override
    public int hashCode(){
        return java.util.Objects.hash(
                getDefaultMessage(), 
                getError(),
                getParams());
    }
    
    @Override
    public boolean equals (Object o){
        if (o instanceof GlobalValidationError){
            GlobalValidationError g = (GlobalValidationError) o;
            if (java.util.Objects.equals(g.getDefaultMessage(), this.getDefaultMessage()) &&
                java.util.Objects.equals(g.getParams(), this.getParams()) &&
                java.util.Objects.equals(g.getError(), this.getError())){
                return true;
            }
        }
        return false;
    }  
    
    public GlobalValidationError withParam(String param){
        getParams().add(param);
        return this;
    }
     
    protected List<String> getParams() {
        return params;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
    public ValidationResult.Error getError() {
        return error;
    }
    
    public static Builder getBuilder(ValidationResult.Error error, String defaultMessage) {
        return new Builder(error, defaultMessage);
    }    

    public static class Builder{
        private GlobalValidationError built;

        Builder(ValidationResult.Error error, String defaultMessage) {
            built = new GlobalValidationError(error, defaultMessage);
        }
        
        public Builder param(String param){
            built.withParam(param);
            return this;
        }

        public GlobalValidationError build() {
            return built;
        }
    }    
    
}