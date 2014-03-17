package com.ihtsdo.snomed.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;

@JsonRootName("response")
public class ErrorDto {
    
    private Map<String, List<String>> fieldErrors = new HashMap<>();    
    private List<String> globalErrors = new ArrayList<>();
        
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("fieldErrors", getFieldErrors())
                .add("globalErrors", getGlobalErrors())
                .toString();
    }    
    
    public ErrorDto addFieldError(String fieldName, String message){
        if (getFieldErrors().get(fieldName) == null){
            getFieldErrors().put(fieldName, new ArrayList<String>());
        }
        this.getFieldErrors().get(fieldName).add(message);
        return this;
    }
    
    public ErrorDto addGlobalError(String message){
        this.getGlobalErrors().add(message);
        return this;
    }

    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, List<String>> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public List<String> getGlobalErrors() {
        return globalErrors;
    }

    public void setGlobalErrors(List<String> globalErrors) {
        this.globalErrors = globalErrors;
    }
}
