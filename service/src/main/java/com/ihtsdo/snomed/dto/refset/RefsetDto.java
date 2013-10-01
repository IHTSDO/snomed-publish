package com.ihtsdo.snomed.dto.refset;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

public class RefsetDto {
    
    private Long id;
    
    @NotNull(message="validation.refset.concept.not.null")
    private Long concept;

    @NotNull(message="validation.refset.publicid.not.null")
    @Size(min=2, max=20, message="validation.refset.publicid.size")
    @Pattern(regexp="[a-zA-Z0-9_]+", message="validation.refset.publicid.charactermix")
    private String publicId;
    
    @NotNull(message="validation.refset.title.not.null")
    @Size(min=4, max=50, message="validation.refset.title.size")
    private String title;
    
    @NotNull(message="validation.refset.description.not.null")
    @Size(min=4, message="validation.refset.description.size")
    private String description;
    
    @NotNull(message="validation.refset.plan.not.null")
    private RefsetPlanDto plan = new RefsetPlanDto();
    
    public RefsetDto(){}
    
    public RefsetDto(Long id, Long concept, String publicId, String title, String description){
        this.concept = concept;
        this.id = id;
        this.publicId = publicId;
        this.title = title;
        this.description = description;
    }
    

    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("title", getTitle())
                .add("concept", getConcept())
                .add("description", getDescription())
                .add("publicId", getPublicId())
                .add("plan", getPlan())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof RefsetDto){
            RefsetDto dto = (RefsetDto) o;
            if (Objects.equal(dto.getId(), getId()) &&
                    (Objects.equal(dto.getTitle(), getTitle())) &&
                    (Objects.equal(dto.getDescription(), getDescription())) &&
                    (Objects.equal(dto.getConcept(), getConcept())) &&
                    (Objects.equal(dto.getPublicId(), getPublicId()))){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        if (id != null){
            return Longs.hashCode(id);
        }else if (publicId != null){
            return publicId.hashCode();
        }else{
            return 1; //delegate to equals method
        }
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConcept() {
        return concept;
    }

    public void setConcept(Long concept) {
        this.concept = concept;
    }

    public RefsetPlanDto getPlan() {
        return plan;
    }

    public void setPlan(RefsetPlanDto plan) {
        this.plan = plan;
    }
    
    public static Builder getBuilder(Long id, Long conceptId, String title, String description, String publicId, RefsetPlanDto plan) {
        return new Builder(id, conceptId, title, description, publicId, plan);
    }
    
    public static class Builder {
        private RefsetDto built;

        Builder(Long id, Long conceptId, String title, String description, String publicId, RefsetPlanDto plan) {
            built = new RefsetDto();
            built.setConcept(conceptId);
            built.setDescription(description);
            built.setId(id);
            built.setPlan(plan);
            built.setPublicId(publicId);
            built.setTitle(title);
        }
        
        public RefsetDto build(){
            return built;
        }
    }

}
