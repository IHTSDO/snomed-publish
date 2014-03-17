package com.ihtsdo.snomed.model;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;

@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"flavour_id", "taggedOn"})
})
@Entity
public class OntologyVersion {
    
    public enum Source{
        RF1, RF2, CANONICAL, CHILD_PARENT, UNKNOWN;
    }    
    
    public enum Status{
        DEVELOPMENT, RELEASE;
    }
    
    @Transient
    @XmlTransient 
    private Concept isKindOfPredicate;
    
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private OntologyFlavour flavour;
    
    @NotNull
    private Date taggedOn;

    private Status status = Status.RELEASE;

    @Enumerated
    private Source source = Source.UNKNOWN;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="ontologyVersion", cascade=CascadeType.ALL)
    private Set<Statement> statements = new HashSet<>();

    @OneToMany(fetch=FetchType.LAZY, mappedBy="ontologyVersion", cascade=CascadeType.ALL)
    private Set<Concept> concepts = new HashSet<>();

    @OneToMany(fetch=FetchType.LAZY, mappedBy="ontologyVersion", cascade=CascadeType.ALL)
    private Set<Description> descriptions = new HashSet<>();
    
    @NotNull private Date creationTime;
    @NotNull private Date modificationTime;
    @Version private long version = 0;

    public OntologyVersion(){}
    
    public OntologyVersion(Long id){
        this.id = id;
    }
    
    @Override
    public int hashCode(){
        return java.util.Objects.hash(
                //getId(),
                getTaggedOn(),
                getStatus(),
                getSource());
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", getId()).
                add("source", getSource()).
                add("taggedOn", getTaggedOn()).
                add("status", getStatus()).
                //add("statements", getStatements() == null ? 0 : getStatements().size()).
                //add("concepts", getConcepts() == null ? 0 : getConcepts().size()).
                //add("descriptions", getDescriptions() == null ? 0 : getDescriptions().size()).
                add("isA", (isKindOfPredicate == null) ? "not set" : "set").toString();
    }

    @Override
    public boolean equals(Object ov){
        if (ov instanceof OntologyVersion){
            OntologyVersion version = (OntologyVersion) ov;
            if (
                //java.util.Objects.equals(version.getId(), getId()) &&
                java.util.Objects.equals(version.getSource(), getSource()) &&
                java.util.Objects.equals(version.getStatus(), getStatus()) &&
                //java.util.Objects.equals(version.getStatements(), getStatements()) &&
                //java.util.Objects.equals(version.getConcepts(), getConcepts()) &&
                //Hibernate stores java.util.date as Timestamp, so the straight equals dont. work.
                java.util.Objects.equals(version.getTaggedOn().toString(), getTaggedOn().toString()) )//&&
                //java.util.Objects.equals(version.getDescriptions(), getDescriptions()))
               
            {
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

    public Concept getIsKindOfPredicate(){
        if (isKindOfPredicate == null){
            for (Concept c : getConcepts()){
                if (c.isKindOfPredicate()){
                    isKindOfPredicate = c;
                    return isKindOfPredicate;
                }
            }
            throw new IllegalStateException("IsA Concept not found in ontology");
        }
        else{
        	return isKindOfPredicate;
        }
    }

    public void setIsKindOfPredicate(Concept isKindOfPredicate) {
        this.isKindOfPredicate = isKindOfPredicate;
    } 
    
    public boolean isRf1(){
        return getSource().equals(Source.RF1);
    }
    
    public boolean isRf2(){
        return getSource().equals(Source.RF2);
    }        

    public void addConcept(Concept c){
        getConcepts().add(c);
        c.setOntologyVersion(this);
    }
    
    public void addDescription(Description d){
        getDescriptions().add(d);
        d.setOntologyVersion(this);
    }
    
    public void addStatement(Statement r){
        getStatements().add(r);
        r.setOntologyVersion(this);
    }
        
    
    /*
     * Generated Getters and Setters
     */
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Set<Statement> getStatements() {
        return statements;
    }
    public void setStatements(Set<Statement> statements) {
        this.statements = statements;
    }
    public Set<Concept> getConcepts() {
        return concepts;
    }
    public void setConcepts(Set<Concept> concepts) {
        this.concepts = concepts;
    }
    public Set<Description> getDescriptions() {
        return descriptions;
    }
    public void setDescriptions(Set<Description> descriptions) {
        this.descriptions = descriptions;
    }
    public Source getSource() {
        return source;
    }
    public void setSource(Source source) {
        this.source = source;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
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
    public Date getTaggedOn() {
        return taggedOn;
    }
    public void setTaggedOn(Date taggedOn) {
        this.taggedOn = taggedOn;
    }

    public OntologyFlavour getFlavour() {
        return flavour;
    }

    public void setFlavour(OntologyFlavour flavour) {
        this.flavour = flavour;
    }
    
    
}