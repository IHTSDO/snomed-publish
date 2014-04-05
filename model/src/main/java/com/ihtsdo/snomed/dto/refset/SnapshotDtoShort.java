package com.ihtsdo.snomed.dto.refset;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.ihtsdo.snomed.model.refset.Snapshot;

@JsonRootName("snapshot")
public class SnapshotDtoShort {
    
    //@NotNull(message="Public ID can not be empty")
    //@Size(min=2, max=20, message="Public ID must be between 2 and 50 characters")
    //@Pattern(regexp="[a-zA-Z0-9_]+", message="Public ID may contain characters, numbers, and underscores only")
    protected String publicId;
    
    @NotNull(message="validation.title.not.empty")
    @Size(min=4, max=50, message="validation.title.wrong.size")
    protected String title;
    
    @NotNull(message="Description can not be empty")
    @Size(min=4, message="Description must be longer than 4 characters")
    protected String description;
    
    protected Date createdOn;
    
    protected int size;
    
    public SnapshotDtoShort(){}

    public static SnapshotDtoShort parse(Snapshot snapshot){        
        return SnapshotDtoShort.getBuilder( 
                snapshot.getTitle(),
                snapshot.getDescription(),
                snapshot.getPublicId(),
                snapshot.getCreationTime(),
                snapshot.getSize()).build();        
    }  
        
    public SnapshotDtoShort(String publicId, String title, String description){
        this.publicId = publicId;
        this.title = title;
        this.description = description;
    }
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("title", getTitle())
                .add("description", getDescription())
                .add("publicId", getPublicId())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof SnapshotDtoShort){
            SnapshotDtoShort dto = (SnapshotDtoShort) o;
            if ((Objects.equal(dto.getTitle(), getTitle())) &&
                (Objects.equal(dto.getDescription(), getDescription())) &&
                (Objects.equal(dto.getPublicId(), getPublicId()))){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        return java.util.Objects.hash(
                getTitle(),
                getDescription(),
                getPublicId());
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
    
	public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
    
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static Builder getBuilder(String title, String description, String publicId, Date createdOn, int size) {
        return new Builder(title, description, publicId, createdOn, size);
    }
    
    public static class Builder {
        private SnapshotDtoShort built;

        Builder(String title, String description, String publicId, Date createdOn, int size) {
            built = new SnapshotDtoShort();
            built.setDescription(description);
            built.setPublicId(publicId);
            built.setTitle(title);
            built.setCreatedOn(createdOn);
            built.setSize(size);
        }
        
        public SnapshotDtoShort build(){
            return built;
        }
    }
}
