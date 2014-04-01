package com.ihtsdo.snomed.dto.refset;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;
import com.ihtsdo.snomed.model.refset.Tag;

public class TagDto {

    @NotNull(message="Snapshot object may not be null")
    @Valid
    private SnapshotDtoShort snapshot;
    
    @NotNull(message="Refset ID may not be null")
    private String refsetPublicId;

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
    
    private Date creationTime;
    
    public String toString(){
        return Objects.toStringHelper(this)
                .add("publicId", getPublicId())
                .add("snapshot", getSnapshot())
                .add("refsetPublicId", getRefsetPublicId())
                .add("title", getTitle())
                .add("description", getDescription())
                .add("creationTime", getCreationTime())
                .toString();
    }
    
    public boolean equals(Object o){
        if (o instanceof TagDto){
            TagDto t = (TagDto) o;
            if (Objects.equal(t.getSnapshot(), getSnapshot()) &&
                    Objects.equal(t.getRefsetPublicId(), getRefsetPublicId()) &&
                    Objects.equal(t.getPublicId(), getPublicId())){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        return java.util.Objects.hash(
                getRefsetPublicId(),
                getPublicId());
    }
    
    public static TagDto parse(Tag tag){
        return getBuilder(tag.getTitle(), tag.getDescription(), tag.getPublicId(), 
                tag.getSnapshot() == null ? null : SnapshotDtoShort.parse(tag.getSnapshot()), 
                tag.getRefset() == null ? null : tag.getRefset().getPublicId(), 
                tag.getCreationTime()).build();
    }
    
    public static Builder getBuilder(String title, String description, String publicId, SnapshotDtoShort snapshot, 
            String refsetPublicId, Date creationTime) {
        return new Builder(title, description, publicId, snapshot, refsetPublicId, creationTime);
    }
    
    public static class Builder {
        private TagDto built;

        Builder(String title, String description, String publicId, SnapshotDtoShort snapshot, 
                String refsetPublicId, Date creationTime) {
            built = new TagDto();
            built.setTitle(title);
            built.setDescription(description);
            built.setPublicId(publicId);
            built.setSnapshot(snapshot);
            built.setRefsetPublicId(refsetPublicId);
            built.setCreationTime(creationTime);
        }
        
        public TagDto build(){
            return built;
        }
    }    
    

    
    public SnapshotDtoShort getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(SnapshotDtoShort snapshot) {
        this.snapshot = snapshot;
    }

    public String getRefsetPublicId() {
        return refsetPublicId;
    }
    public void setRefsetPublicId(String refsetPublicId) {
        this.refsetPublicId = refsetPublicId;
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
    public Date getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
