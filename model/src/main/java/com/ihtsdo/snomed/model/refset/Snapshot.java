package com.ihtsdo.snomed.model.refset;

import java.sql.Date;
import java.util.Calendar;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Concept;

@Entity
public class Snapshot {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        joinColumns = @JoinColumn(name="snapshot_id"),
        inverseJoinColumns = @JoinColumn(name="concept_id"),
        uniqueConstraints = @UniqueConstraint(columnNames={"snapshot_id", "concept_id"}))
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Set<Concept> concepts;
    
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
    
    public void update(String publicId, String title, String description, Set<Concept> concepts){
        setPublicId(publicId);
        setTitle(title);
        setDescription(description);
        setConcepts(concepts);
    }
    
    public Snapshot() {}
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Snapshot){
            Snapshot r = (Snapshot) o;
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
                .toString();
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
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    
    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public void setConcepts(Set<Concept> concepts){
        this.concepts = concepts;
    }
    
    public Set<Concept> getConcepts(){
        return concepts;
    }

    public static Builder getBuilder(String publicId, String title, String description, Set<Concept> concepts) {
        return new Builder(publicId, title, description, concepts);
    }
    

    public static class Builder {
        private Snapshot built;

        Builder(String publicId, String title, String description, Set<Concept> concepts) {
            built = new Snapshot();
            built.title = title;
            built.publicId = publicId;
            built.description = description;
            built.concepts = concepts;
        }

        public Snapshot build() {
            return built;
        }
    }

}
