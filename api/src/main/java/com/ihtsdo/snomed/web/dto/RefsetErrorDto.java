package com.ihtsdo.snomed.web.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;

@XmlRootElement(name="errors")
@JsonRootName("errors")
public class RefsetErrorDto {

    public static final String GLOBAL = "global";
    
    private String message;

    public RefsetErrorDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("message", getMessage())
                .toString();
    }        
    
}
