package com.ihtsdo.snomed.model.refset;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.OntologyVersion;

@Entity
@org.hibernate.annotations.Table(
        appliesTo = "Refset",
        indexes={@Index(name="refsetPublicIdIndex", columnNames={"publicId"})})
@Table(
        name = "Refset", 
        uniqueConstraints = @UniqueConstraint(columnNames = {"publicId"}))
public class Refset {
    
    public enum Source {
        LIST, RULES;
    }
    
    public enum Type {
        CONCEPT, DESCRIPTION, STATEMENT;
    }    
    
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private Source source;
    
    @NotNull
    private Type type;
    
    @NotNull
    @OneToOne
    private OntologyVersion ontologyVersion;
    
    @NotNull
    @OneToOne
    private Concept refsetConcept;    
    
    @NotNull
    @OneToOne
    private Concept moduleConcept;    
        
    @NotNull
    @OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Plan plan;
    
    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="refset_id", referencedColumnName="id", nullable=true)
    private Map<String, Snapshot> snapshotsMap;
    
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
    
    public Refset() {}
    
    public Refset update(Source source, Type type, OntologyVersion ontologyVersion,
            Concept refsetConcept, Concept moduleConcept, String title, String description, 
            String publicId, Plan plan)
    {
        this.setSource(source);
        this.setType(type);
        this.setOntologyVersion(ontologyVersion);
        this.setRefsetConcept(refsetConcept);
        this.setModuleConcept(moduleConcept);
        this.setTitle(title);
        this.setDescription(description);
        this.setPublicId(publicId);
        this.setPlan(plan);
        
        return this;
    }
    
    public Snapshot getSnapshot(String snapshotPublicId){
        return getSnapshotsMap().get(snapshotPublicId);
    }
    
    public Collection<Snapshot> getSnapshots(){
        return getSnapshotsMap().values();
    }
    
    public void addSnapshot(Snapshot snapshot){
        getSnapshotsMap().put(snapshot.getPublicId(), snapshot);
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
        return com.google.common.base.Objects.toStringHelper(this)
                .add("id", getId())
                .add("source", getSource())
                .add("type", getType())
                .add("ontologyVersion", ontologyVersion)
                .add("refsetConcept", getRefsetConcept())
                .add("moduleConcept", getModuleConcept())
                .add("title", getTitle())
                .add("description", getDescription())
                .add("publicId", getPublicId())
                .add("plan", getPlan())
                .toString();
    }
    
    @Override
    public int hashCode(){
        return java.util.Objects.hash(
                getId(),
                getSource(),
                getType(),
                getOntologyVersion(),
                getRefsetConcept(),
                getModuleConcept(),
                getTitle(),
                getDescription(),
                getPublicId(),
                getPlan());
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
    
    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
    
    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Concept getRefsetConcept() {
        return refsetConcept;
    }

    public void setRefsetConcept(Concept refsetConcept) {
        this.refsetConcept = refsetConcept;
    }

    public Concept getModuleConcept() {
        return moduleConcept;
    }

    public void setModuleConcept(Concept moduleConcept) {
        this.moduleConcept = moduleConcept;
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
    
    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
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

     Map<String, Snapshot> getSnapshotsMap() {
        return snapshotsMap;
    }

     void setSnapshotsMap(Map<String, Snapshot> snapshotsMap) {
        this.snapshotsMap = snapshotsMap;
    }
     
     public OntologyVersion getOntologyVersion() {
        return ontologyVersion;
    }

    public void setOntologyVersion(OntologyVersion ontologyVersion) {
        this.ontologyVersion = ontologyVersion;
    }

    public static Builder getBuilder(Long id, Source source, Type type, OntologyVersion snomedRelease,
             Concept refsetConcept, Concept moduleConcept, String title, String description, 
             String publicId, Plan plan) {
         return new Builder(id, source, type, snomedRelease, refsetConcept, moduleConcept, 
                 title, description, publicId, plan);
     }
     
     public static Builder getBuilder(Source source, Type type, OntologyVersion snomedRelease,
             Concept refsetConcept, Concept moduleConcept, String title, String description, 
             String publicId, Plan plan) {
         return new Builder(null, source, type, snomedRelease, refsetConcept, moduleConcept, 
                 title, description, publicId, plan);
     }     
     
     
     public static class Builder {
         private Refset built;

         Builder(Long id, Source source, Type type, OntologyVersion ontologyVersion,
                 Concept refsetConcept, Concept moduleConcept, String title, String description, 
                 String publicId, Plan plan) {
             built = new Refset();
             built.setId(id);
             built.setSource(source);
             built.setType(type);
             built.setOntologyVersion(ontologyVersion);
             built.setRefsetConcept(refsetConcept);
             built.setModuleConcept(moduleConcept);
             built.setTitle(title);
             built.setDescription(description);
             built.setPublicId(publicId);
             built.setPlan(plan);
         }
         
         public Refset build(){
             return built;
         }
     }
     
}
