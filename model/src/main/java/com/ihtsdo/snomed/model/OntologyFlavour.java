package com.ihtsdo.snomed.model;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Table( uniqueConstraints = @UniqueConstraint(columnNames = {"ontology_id", "publicId"}))
@Entity
public class OntologyFlavour {

    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private String publicId;
    
    @NotNull
    private String label;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private Ontology ontology;
    
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="flavour")
    private Set<OntologyVersion> versions = new HashSet<>();
    
    @NotNull
    private Date creationTime;
    
    @NotNull
    private Date modificationTime;
    
    @Version private long version = 0;

    @Override
    public String toString(){
        return com.google.common.base.Objects.toStringHelper(this)
                .add("id", getId())
                .add("publicId", getPublicId())
                .add("label", getLabel())
                .add("versions", getVersions())
                .toString();
    }
    
    @Override
    public int hashCode(){
        return java.util.Objects.hash(
                //getId(),
                getPublicId(),
                getLabel(),
                getVersions());
    }
    
    @Override
    public boolean equals(Object of){
        if (of instanceof OntologyFlavour){
            OntologyFlavour flavour = (OntologyFlavour) of;
            if (
                //Objects.equals(flavour.getId(), getId()) &&
                Objects.equals(flavour.getPublicId(), getPublicId()) &&
                Objects.equals(flavour.getLabel(), getLabel()) &&
                Objects.equals(flavour.getVersions(), getVersions())){
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
    
    public OntologyFlavour addVersion(OntologyVersion ov){
        getVersions().add(ov);
        ov.setFlavour(this);
        return this;
    }
    
    public Set<OntologyVersion> getVersions() {
        return versions;
    }

    public void setVersions(Set<OntologyVersion> versions) {
        this.versions = versions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
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

    public Ontology getOntology() {
        return ontology;
    }

    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }
    
    
}
