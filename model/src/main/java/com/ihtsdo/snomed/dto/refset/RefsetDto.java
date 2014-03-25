package com.ihtsdo.snomed.dto.refset;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Refset.Source;
import com.ihtsdo.snomed.model.refset.Refset.Type;

@XmlRootElement(name="refset")
@JsonRootName("refset")
public class RefsetDto {
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    
    @NotNull(message="You must select a type")
    private Type type;
    
    @NotNull(message="You must select a source")
    private Source source;
    
    @NotNull
    private boolean pendingChanges;
        
    @Valid
    @NotNull(message="You must select a refset concept")
    private ConceptDto refsetConcept;
    
    @NotNull(message="You must select a module")
    private ConceptDto moduleConcept;
    
    @NotNull(message="You must select a default Snomed extension")
    private String snomedExtension;
    
    @NotNull(message="You must select a default Snomed release date")
    private String snomedReleaseDate;
    
    @NotNull(message="Internet Address can not be empty")
    @Size(min=2, max=20, message="Internet Address must be between 2 and 50 characters")
    @Pattern(regexp="[a-zA-Z0-9_]+", message="Internet Address may contain characters, numbers, and underscores only")
    private String publicId;
    
    @NotNull(message="validation.title.not.empty")
    @Size(min=4, max=50, message="validation.title.wrong.size")
    private String title;
    
    @NotNull(message="Description can not be empty")
    @Size(min=4, message="Description must be longer than 4 characters")
    private String description;
    
    @NotNull(message="A plan must be associated with a refset")
    private PlanDto plan = new PlanDto();
        
    @Override
    public String toString(){
        return com.google.common.base.Objects.toStringHelper(this)
                .add("source", getSource())
                .add("type", getType())
                .add("refsetConcept", getRefsetConcept())
                .add("moduleConcept", getModuleConcept())
                .add("snomedExtension", getSnomedExtension())
                .add("snomedReleaseDate", getSnomedReleaseDate())
                .add("title", getTitle())
                .add("description", getDescription())
                .add("publicId", getPublicId())
                .add("plan", getPlan())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof RefsetDto){
            RefsetDto dto = (RefsetDto) o;
            if (
        		Objects.equals(dto.getSource(), getSource()) &&
        		Objects.equals(dto.getType(), getType()) &&
        		Objects.equals(dto.getRefsetConcept(), getRefsetConcept()) &&
                Objects.equals(dto.getModuleConcept(), getModuleConcept()) &&
                Objects.equals(dto.getSnomedExtension(), getSnomedExtension()) &&
                Objects.equals(dto.getSnomedReleaseDate(), getSnomedReleaseDate()) &&
                Objects.equals(dto.getTitle(), getTitle()) &&
                Objects.equals(dto.getDescription(), getDescription()) &&
                Objects.equals(dto.getPublicId(), getPublicId()) &&
                Objects.equals(dto.getPlan(), getPlan())){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(
        		getSource(),
        		getType(),
        		getSnomedExtension(),
        		getSnomedReleaseDate(),
        		getRefsetConcept(),
        		getModuleConcept(),
        		getTitle(),
        		getDescription(),
        		getPublicId(),
        		getPlan());
    }
    
    @JsonIgnore
    public Date getSnomedReleaseDateAsDate() throws ParseException{
        return new java.sql.Date(dateFormat.parse(getSnomedReleaseDate()).getTime());
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

    public PlanDto getPlan() {
        return plan;
    }

    public void setPlan(PlanDto plan) {
        this.plan = plan;
    }
    
    public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public ConceptDto getRefsetConcept() {
		return refsetConcept;
	}

	public void setRefsetConcept(ConceptDto refsetConcept) {
		this.refsetConcept = refsetConcept;
	}

	public ConceptDto getModuleConcept() {
		return moduleConcept;
	}

	public void setModuleConcept(ConceptDto moduleConcept) {
		this.moduleConcept = moduleConcept;
	}

    public String getSnomedExtension() {
        return snomedExtension;
    }

    public void setSnomedExtension(String snomedExtension) {
        this.snomedExtension = snomedExtension;
    }

    public String getSnomedReleaseDate() {
        return snomedReleaseDate;
    }

    public void setSnomedReleaseDate(String snomedReleaseDate) {
        this.snomedReleaseDate = snomedReleaseDate;
    }
    
    public boolean isPendingChanges() {
        return pendingChanges;
    }

    public void setPendingChanges(boolean pendingChanges) {
        this.pendingChanges = pendingChanges;
    }

    public static RefsetDto parse(Refset refset){
        return  getBuilder(
                refset.getSource(), 
                refset.getType(), 
                refset.isPendingChanges(),
                refset.getOntologyVersion().getFlavour().getPublicId(),
                refset.getOntologyVersion().getTaggedOn(),
                ConceptDto.parse(refset.getRefsetConcept()),
                ConceptDto.parse(refset.getModuleConcept()),
                refset.getTitle(),
                refset.getDescription(), 
                refset.getPublicId(), 
                PlanDto.parse(refset.getPlan())).build();
    }

    public static Builder getBuilder(Source source, Type type, boolean pendingChanges, String snomedExtension,
            Date releaseDate, ConceptDto refsetConcept, ConceptDto moduleConcept, String title, String description, 
			String publicId, PlanDto plan) {
        return new Builder(source, type, pendingChanges, snomedExtension, releaseDate, refsetConcept, moduleConcept, 
        		title, description, publicId, plan);
    }
    
    public static class Builder {
        private RefsetDto built;
        
        public Builder(Source source, Type type, boolean pendingChanges,
                String snomedExtension, Date snomedReleaseDate,
                ConceptDto refsetConcept, ConceptDto moduleConcept,
                String title, String description, String publicId, PlanDto plan) 
        {
            built = new RefsetDto();
            built.setSource(source);
            built.setType(type);
            built.setPendingChanges(pendingChanges);
            built.setRefsetConcept(refsetConcept);
            built.setModuleConcept(moduleConcept);
            built.setSnomedExtension(snomedExtension);
            built.setSnomedReleaseDate(dateFormat.format(snomedReleaseDate));
            built.setTitle(title);
            built.setDescription(description);
            built.setPublicId(publicId);
            built.setPlan(plan);
        }

        public RefsetDto build(){
            return built;
        }
    }
}
