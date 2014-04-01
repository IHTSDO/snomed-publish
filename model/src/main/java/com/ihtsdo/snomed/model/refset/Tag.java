package com.ihtsdo.snomed.model.refset;

import java.sql.Date;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Index;

import com.google.common.base.Objects;

@Entity
@org.hibernate.annotations.Table(appliesTo = "Tag",
        indexes={@Index(name="tagPublicIdAndRefsetIdIndex", columnNames={"publicId","refset_id"})})
@Table(name = "Tag", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"publicId","refset_id"}))
public class Tag {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Refset refset;

    @NotNull
    @OneToOne(fetch=FetchType.EAGER)
    private Snapshot snapshot;
    
    @NotNull
    @Size(min=2, max=20, message="Public ID must be between 2 and 20 characters")
    @Pattern(regexp="[a-zA-Z0-9_]+", message="Public ID may contain characters, numbers, and underscores only")
    //For some reason, declaring uniqueness on columns like this, does not seem t work,
    //at least not for hibernate implementation of JPA 2. Moved to @Table level instead.
    //@Column(name="publicId", unique = true, nullable=false, length=30)
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
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("publicId", getPublicId())
                .add("title", getTitle())
                .add("description", getDescription())
                .add("snapshot", getSnapshot())
                .add("refset", getRefset())
                .toString();
    }
    
    @Override
    public int hashCode(){
        return java.util.Objects.hash(
                getRefset(),
                getPublicId());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Tag){
            Tag t = (Tag) o;
            if (Objects.equal(t.getRefset(), getRefset()) &&
                Objects.equal(t.getPublicId(), getPublicId())){
                return true;
            }
        }
        return false;
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
    
    public static Builder getBuilder(String publicId, String title, String description, Refset refset, Snapshot snapshot) {
        return new Builder(publicId, title, description, refset, snapshot);
    }
    

    public static class Builder {
        private Tag built;

        Builder(String publicId, String title, String description, Refset refset, Snapshot snapshot) {
            built = new Tag();
            built.setTitle(title);
            built.setDescription(description);
            built.setPublicId(publicId);
            built.setRefset(refset);
            built.setSnapshot(snapshot);
        }

        public Tag build() {
            return built;
        }
    }    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Refset getRefset() {
        return refset;
    }

    public void setRefset(Refset refset) {
        this.refset = refset;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
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
    
    
    

}
