package com.ihtsdo.snomed.model.refset;

import java.beans.Transient;
import java.sql.Date;
import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.exception.ConceptsCacheNotBuiltException;
import com.ihtsdo.snomed.model.Concept;

@Entity
@org.hibernate.annotations.Table(appliesTo = "RefsetPlan",
        indexes={})
@Table(name = "RefsetPlan"//, 
//uniqueConstraints = @UniqueConstraint(columnNames = {"publicId"})
)
public class RefsetPlan {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        joinColumns = @JoinColumn(name="refset_id"),
        inverseJoinColumns = @JoinColumn(name="concept_id"),
        uniqueConstraints=@UniqueConstraint(columnNames={"refset_id", "concept_id"}))
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Set<Concept> concepts;
    
    @OneToOne(targetEntity=BaseRefsetRule.class, cascade=CascadeType.ALL)
    private RefsetRule terminal;
    
    @NotNull
    private Date creationTime;
    
    @NotNull
    private Date modificationTime;
    
    @Version
    private long version = 0;
    
    public void refreshConceptsCache(){
        concepts = terminal.generateConcepts();
    }
    
    @Transient
    public final Set<Concept> getConcepts() throws ConceptsCacheNotBuiltException{
        if (concepts == null){
            throw new ConceptsCacheNotBuiltException("Build the cache before calling getConcepts");
        }
        return concepts;
    }
    
    @Override
    public int hashCode(){
        return Longs.hashCode(getId());
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof RefsetPlan){
            RefsetPlan r = (RefsetPlan) o;
            if ((r.getTerminal()).equals(this.getTerminal())){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString(){
        try {
            return Objects.toStringHelper(this)
                    .add("id", getId())
                    .add("terminal", getTerminal())
                    .add("concepts", hasConcepts() ? getConcepts().size() : "0")
                    .toString();
        } catch (ConceptsCacheNotBuiltException e) {
            //will never happen, because of the hasConcepts guard above ;-)
            throw new RuntimeException(e);
        }
    }    
    
    public boolean hasConcepts(){
        return concepts != null;
    }

    public RefsetRule getTerminal() {
        return terminal;
    }

    public void setTerminal(RefsetRule terminal) {
        this.terminal = terminal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    
    public static Builder getBuilder(RefsetRule terminal) {
        return new Builder(terminal);
    }
    
    public static class Builder {
        private RefsetPlan built;

        Builder(RefsetRule terminal) {
            built = new RefsetPlan();
            built.terminal = terminal;
        }

        public RefsetPlan build() {
            return built;
        }
    }
    
}
