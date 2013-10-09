package com.ihtsdo.snomed.web.dto;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

@XmlRootElement(name="error")
public class RefsetErrorDto {
    private long code;
    private String field;
    private String displayMessage;
    
    @Transient
    public boolean isFieldError(){
        return field != null;
    }
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("code", getCode())
                .add("field", getField())
                .add("displayMessage", getDisplayMessage())
                .toString();
    }        
    
    public long getCode() {
        return code;
    }
    public void setCode(long code) {
        this.code = code;
    }
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getDisplayMessage() {
        return displayMessage;
    }
    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }    
    

}
