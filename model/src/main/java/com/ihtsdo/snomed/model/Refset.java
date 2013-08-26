package com.ihtsdo.snomed.model;

import java.sql.Date;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Index;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
@Entity
@org.hibernate.annotations.Table(appliesTo = "Refset",
        indexes={@Index(name="refsetPublicIdIndex", columnNames={"publicId"})})
public class Refset {
    
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @NotNull
    @Size(min=2, max=50, message="Public ID must be between 2 and 50 characters")
    @Pattern(regexp="[a-zA-Z0-9_]+", message="Public ID may contain characters, numbers, and underscores only")
    @Column(unique = true)
    private String publicId;
    
    @NotNull
    @Size(min=4, max=50, message="Title must be between 4 and 50 characters")
    private String title;
    
    @NotNull
    @Size(min=4, message="Description must be longer than 4 characters")
    private String description;
    
    @NotNull
    private Date creationTime;
    
    @NotNull
    private Date modificationTime;
    
    @Version
    private long version = 0;
    
    public Refset() {}
    
    public Refset update(String publicId, String title, String description){
        this.setPublicId(publicId);
        this.setTitle(title);
        this.setDescription(description);
        return this;
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Refset){
            Refset r = (Refset) o;
            if (r.getId() == getId()){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("title", getTitle())
                .add("description", getDescription())
                .add("publicId", getPublicId())
                .toString();
    }
    
    @Override
    public int hashCode(){
        return Longs.hashCode(id);
    }

    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date(Calendar.getInstance().getTime().getTime());
    }
    
    @PrePersist
    public void prePersist() {
        Date now = new Date(Calendar.getInstance().getTime().getTime());
        creationTime = now;
        modificationTime = now;
    }    
    
    public static Builder getBuilder(String publicId, String title, String description) {
        return new Builder(publicId, title, description);
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public static class Builder {
        Refset built;

        Builder(String publicId, String title, String description) {
            built = new Refset();
            built.publicId = publicId;
            built.title = title;
            built.description = description;
        }

        public Refset build() {
            return built;
        }
    }

}
