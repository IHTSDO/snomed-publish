package com.ihtsdo.snomed.dto.refset;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Snapshot;

@XmlRootElement(name="snapshot")
@JsonRootName("snapshot")
public class SnapshotDto {
    
    private Long id;
    
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
    
    @XmlElementWrapper(name = "conceptDtos")
    @XmlElement(name="concept")
    @JsonProperty("conceptDtos")
    private Set<ConceptDto> conceptDtos = new HashSet<>();
    
    public SnapshotDto(){}

    public static SnapshotDto parse(Snapshot snapshot){
        return fillConcepts(snapshot, parseSansConcepts(snapshot));
    }    
    
    public static SnapshotDto parseSansConcepts(Snapshot snapshot){
        return SnapshotDto.getBuilder(snapshot.getId(), 
                snapshot.getTitle(),
                snapshot.getDescription(),
                snapshot.getPublicId(),
                null).build();
    }
    
    private static SnapshotDto fillConcepts(Snapshot snap, SnapshotDto snapDto){
        Set<ConceptDto> conceptDtos = new HashSet<ConceptDto>();
        for (Concept c : snap.getConcepts()){
            conceptDtos.add(ConceptDto.parse(c));
        }
        snapDto.setConceptDtos(conceptDtos);
        return snapDto;
    }
    
    public SnapshotDto(Long id, String publicId, String title, String description, Set<ConceptDto> concepts){
        this.id = id;
        this.publicId = publicId;
        this.title = title;
        this.description = description;
        this.conceptDtos = concepts;
    }
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("title", getTitle())
                .add("description", getDescription())
                .add("publicId", getPublicId())
                .add("conceptDtos", getConceptDtos() == null ? 0 : getConceptDtos().size())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof SnapshotDto){
            SnapshotDto dto = (SnapshotDto) o;
            if (Objects.equal(dto.getId(), getId()) &&
                    (Objects.equal(dto.getTitle(), getTitle())) &&
                    (Objects.equal(dto.getDescription(), getDescription())) &&
                    (Objects.equal(dto.getConceptDtos(), getConceptDtos())) &&
                    (Objects.equal(dto.getPublicId(), getPublicId()))){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        if (getId() != null){
            return Longs.hashCode(getId());
        }else if (getPublicId() != null){
            return getPublicId().hashCode();
        }else{
            return -1; //delegate to equals method
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

    
    
    public Set<ConceptDto> getConceptDtos() {
        return conceptDtos;
    }

    public void setConceptDtos(Set<ConceptDto> conceptDtos) {
        this.conceptDtos = conceptDtos;
    }

    public static Builder getBuilder(Long id, String title, String description, String publicId, Set<ConceptDto> concepts) {
        return new Builder(id, title, description, publicId, concepts);
    }
    
    public static class Builder {
        private SnapshotDto built;

        Builder(Long id, String title, String description, String publicId, Set<ConceptDto> concepts) {
            built = new SnapshotDto();
            built.setDescription(description);
            built.setId(id);
            built.setPublicId(publicId);
            built.setTitle(title);
            built.setConceptDtos(concepts);
        }
        
        public SnapshotDto build(){
            return built;
        }
    }
}
