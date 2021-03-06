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
import com.ihtsdo.snomed.dto.refset.SnapshotDto;

@XmlRootElement(name="response")
@JsonRootName("response")
public class SnapshotResponseDto {
    
    public static final int SUCCESS                                   = 0;
    public static final int FAIL                                      = -1;
    public static final int SUCCESS_CREATED                           = 10;
    public static final int SUCCESS_DELETED                           = 20;
    public static final int SUCCESS_UPDATED                           = 30;
    public static final int FAIL_UNREFERENCED_RULE                    = -10;
    public static final int FAIL_REFSET_NOT_FOUND                     = -20;
    public static final int FAIL_UNCONNECTED_RULE                     = -30;
    public static final int FAIL_RULE_NOT_FOUND                       = -40;
    public static final int FAIL_PLAN_NOT_FOUND                       = -50;
    public static final int FAIL_PUBLIC_ID_NOT_UNIQUE                 = -60;
    public static final int FAIL_URL_AND_BODY_PUBLIC_ID_NOT_MATCHING  = -70;
    public static final int FAIL_CONCEPT_NOT_FOUND                    = -80;
    public static final int FAIL_VALIDATION                           = -90;    
    
    public enum Status{
        CREATED, DELETED, UPDATED, VALIDATED, FAIL;
    }

    private Status status;
    
    @XmlElementWrapper(name = "fieldErrors")
    @XmlElement(name="refsetErrorBuilder")
    //@JsonSerialize(using = fieldErrorsSerialiser.class, as=String.class)
    private Map<String, List<String>> fieldErrors = new HashMap<>();
    
    @XmlElementWrapper(name = "globalErrors")
    @XmlElement(name="refsetErrorBuilder")
    private List<String> globalErrors = new ArrayList<>();
    
    private SnapshotDto snapshot;
    private String publicId;
    private int code;
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("status", getStatus())
                .add("publicId", getPublicId())
                .add("code", getCode())
                .add("fieldErrors", getFieldErrors())
                .add("globalErrors", getGlobalErrors())
                .add("snapshot", getSnapshot())
                .toString();
    }    
    
    public SnapshotResponseDto addFieldError(String fieldName, String message){
        if (getFieldErrors().get(fieldName) == null){
            getFieldErrors().put(fieldName, new ArrayList<String>());
        }
        this.getFieldErrors().get(fieldName).add(message);
        return this;
    }
    
    public SnapshotResponseDto addGlobalError(String message){
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


    public SnapshotDto getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(SnapshotDto snapshot) {
        this.snapshot = snapshot;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
