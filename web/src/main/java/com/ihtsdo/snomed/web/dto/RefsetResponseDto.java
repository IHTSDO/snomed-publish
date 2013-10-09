package com.ihtsdo.snomed.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.ihtsdo.snomed.dto.refset.RefsetDto;

@XmlRootElement(name="response")
public class RefsetResponseDto {
    
    /*
     *         } catch (NonUniquePublicIdException e) {
            //defensive coding
            result.addError(createPublicIdNotUniqueFieldError(refsetDto, result));
            return withErrors(result, response);

        } catch (UnReferencedReferenceRuleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RefsetNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConceptNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnconnectedRefsetRuleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RefsetRuleNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RefsetPlanNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     */
    
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
    
    public enum Status{
        CREATED, DELETED, UPDATED, FAIL;
    }

    private boolean success = false;
    private Status status;
    
    @XmlElementWrapper(name = "fieldErrors")
    @XmlElement(name="error")
    private Map<String, RefsetErrorDto> fieldErrors = new HashMap<>();
    
    @XmlElementWrapper(name = "globalErrors")
    @XmlElement(name="error")
    private List<RefsetErrorDto> globalErrors = new ArrayList<>();
    
    private RefsetDto refset;
    private String publicId;
    private int code;
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("success", isSuccess())
                .add("status", getStatus())
                .add("publicId", getPublicId())
                .add("code", getCode())
                .add("fieldErrors", getFieldErrors())
                .add("globalErrors", getGlobalErrors())
                .add("refset", getRefset())
                .toString();
    }    
    
    public RefsetResponseDto addFieldError(String field, RefsetErrorDto error){
        this.getFieldErrors().put(field, error);
        return this;
    }
    
    public RefsetResponseDto addGlobalError(RefsetErrorDto error){
        this.getGlobalErrors().add(error);
        return this;
    }
    
    public Map<String, RefsetErrorDto> getFieldErrors() {
        return fieldErrors;
    }
    public void setFieldErrors(Map<String, RefsetErrorDto> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
    public List<RefsetErrorDto> getGlobalErrors() {
        return globalErrors;
    }
    public void setGlobalErrors(List<RefsetErrorDto> objectErrors) {
        this.globalErrors = objectErrors;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public RefsetDto getRefset() {
        return refset;
    }
    public void setRefset(RefsetDto refset) {
        this.refset = refset;
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
    
    

}
