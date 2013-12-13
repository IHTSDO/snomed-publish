package com.ihtsdo.snomed.dto.refset;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

@XmlRootElement(name="refset")
@JsonRootName("refset")
public class RefsetDto {
    
    private Long id;
    
    @NotNull(message="You must select a concept")
    private Long concept;
    
    private String conceptDisplayName;

    @NotNull(message="Public ID can not be empty")
    @Size(min=2, max=20, message="Public ID must be between 2 and 50 characters")
    @Pattern(regexp="[a-zA-Z0-9_]+", message="Public ID may contain characters, numbers, and underscores only")
    private String publicId;
    
    @NotNull(message="validation.title.not.empty")
    @Size(min=4, max=50, message="validation.title.wrong.size")
    private String title;
    
    @NotNull(message="Description can not be empty")
    @Size(min=4, message="Description must be longer than 4 characters")
    private String description;
    
    @NotNull(message="A plan must be associated with a refset")
    private PlanDto plan = new PlanDto();
    
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

    public PlanDto getPlan() {
        return plan;
    }

    public void setPlan(PlanDto plan) {
        this.plan = plan;
    }
    
    
    
    public String getConceptDisplayName() {
        return conceptDisplayName;
    }

    public void setConceptDisplayName(String conceptDisplayName) {
        this.conceptDisplayName = conceptDisplayName;
    }

    public static Builder getBuilder(Long id, Long conceptId, String conceptDisplayName, String title, String description, String publicId, PlanDto plan) {
        return new Builder(id, conceptId, conceptDisplayName, title, description, publicId, plan);
    }
    
    public static class Builder {
        private RefsetDto built;

        Builder(Long id, Long conceptId, String conceptDisplayName, String title, String description, String publicId, PlanDto plan) {
            built = new RefsetDto();
            built.setConcept(conceptId);
            built.setDescription(description);
            built.setId(id);
            built.setPlan(plan);
            built.setPublicId(publicId);
            built.setTitle(title);
            built.setConceptDisplayName(conceptDisplayName);
        }
        
        public RefsetDto build(){
            return built;
        }
    }

}
