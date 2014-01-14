package com.ihtsdo.snomed.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto.Status;

@XmlRootElement(name="response")
@JsonRootName("response")
public class RefsetPlanResponseDto {

    @XmlElementWrapper(name = "fieldErrors")
    @XmlElement(name="refsetErrorBuilder")
    //@JsonSerialize(using = fieldErrorsSerialiser.class, as=String.class)
    private Map<String, List<String>> fieldErrors = new HashMap<>();
    
    @XmlElementWrapper(name = "globalErrors")
    @XmlElement(name="refsetErrorBuilder")
    private List<String> globalErrors = new ArrayList<>();
    
    private PlanDto refsetPlan;
    private int code;
    private Status status;
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("code", getCode())
                .add("fieldErrors", getFieldErrors())
                .add("globalErrors", getGlobalErrors())
                .toString();
    }    
    
    public RefsetPlanResponseDto addFieldError(String fieldName, String message){
        if (getFieldErrors().get(fieldName) == null){
            getFieldErrors().put(fieldName, new ArrayList<String>());
        }
        this.getFieldErrors().get(fieldName).add(message);
        return this;
    }
    
    public RefsetPlanResponseDto addGlobalError(String message){
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

  
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    
    
    
    public PlanDto getRefsetPlan() {
        return refsetPlan;
    }

    public void setRefsetPlan(PlanDto refsetPlan) {
        this.refsetPlan = refsetPlan;
    }



    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }



//    private class fieldErrorsSerialiser extends JsonSerializer<Map<String, List<String>>>{
//
//        @Override
//        public void serialize(Map<String, List<String>> errors,JsonGenerator arg1, SerializerProvider arg2)
//                throws IOException, JsonProcessingException 
//        {
//
//            
//        }
//        
//    }
    

}
