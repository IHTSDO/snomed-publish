
package com.ihtsdo.snomed.canonical.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

@Entity
public class Concept {
    private static final Logger LOG = LoggerFactory.getLogger( Concept.class );
        
    @Transient private Set<Concept> cache;
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    private long serialisedId;
    private int status;
    private String fullySpecifiedName;
    private String ctv3id;
    private String snomedId;
    private boolean primitive;
    
    @OneToOne
    private Ontology ontology;

    @OneToMany(mappedBy="subject")//, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Set<RelationshipStatement> subjectOfRelationShipStatements = new HashSet<RelationshipStatement>();

    @ManyToMany//(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinTable(name = "KIND_OF", 
        joinColumns = @JoinColumn(name="child_id"),
        inverseJoinColumns = @JoinColumn(name="parent_id"),
        uniqueConstraints=@UniqueConstraint(columnNames={"parent_id", "child_id"}))
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Set<Concept> kindOfs = new HashSet<Concept>();

    @ManyToMany(mappedBy="kindOfs")//, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Set<Concept> parentOf = new HashSet<Concept>();
    
    public Concept(){}
    public Concept(long serialisedId){this.serialisedId = serialisedId;}
    
    public Set<Concept> getAllKindOfPrimitiveConcepts(boolean useCache){
        if (useCache && (cache != null)){
            if (LOG.isDebugEnabled()){
                StringBuffer debugStringBuffer = new StringBuffer("Cache hit for concept " + this.getSerialisedId() + " with values {");
                for (Concept c : cache){
                    debugStringBuffer.append(c.getSerialisedId()+ ", ");
                }
                if (!cache.isEmpty()){
                    debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
                }
                debugStringBuffer.append("}");
                LOG.debug(debugStringBuffer.toString());
            }
            return cache;
        }
        LOG.debug("Populating cache for concept " + getSerialisedId());
        cache = new HashSet<Concept>();
        
        for (Concept kindOf : kindOfs){
            if (kindOf.isPrimitive()){
                cache.add(kindOf);
            }
            cache.addAll(kindOf.getAllKindOfPrimitiveConcepts(useCache));
        }   
        return cache;
    }
    
    public boolean isLeaf(){
        return getParentOf().isEmpty();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("internalId", getSerialisedId())
                .add("ontology", getOntology() == null ? null : getOntology().getId())
                .add("status", getStatus())
                .add("fullySpecifiedName", getFullySpecifiedName())
                .add("ctv3id", getCtv3id())
                .add("snomedId", getSnomedId())
                .add("primitive", isPrimitive())
                .toString();
    }

    @Override
    public int hashCode(){
        return Longs.hashCode(getSerialisedId());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Concept){
            Concept c = (Concept) o;
            if (c.getSerialisedId() == this.getSerialisedId()){
                return true;
            }
        }
        return false;
    }

    /*
     * Generated Getters and Setters
     */

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getFullySpecifiedName() {
        return fullySpecifiedName;
    }
    public void setFullySpecifiedName(String fullySpecifiedName) {
        this.fullySpecifiedName = fullySpecifiedName;
    }
    public String getCtv3id() {
        return ctv3id;
    }
    public void setCtv3id(String ctv3id) {
        this.ctv3id = ctv3id;
    }
    public String getSnomedId() {
        return snomedId;
    }
    public void setSnomedId(String snomedId) {
        this.snomedId = snomedId;
    }
    public boolean isPrimitive() {
        return primitive;
    }
    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public Set<Concept> getKindOfs() {
        return kindOfs;
    }
    
    public void setKindOfs(Set<Concept> kindOfs) {
        this.kindOfs = kindOfs;
    }

    public Set<Concept> getParentOf() {
        return parentOf;
    }

    public void setParentOf(Set<Concept> parentOf) {
        this.parentOf = parentOf;
    }

    public void addKindOf(Concept concept){
        this.kindOfs.add(concept);
    }

    public void addParentOf(Concept concept){
        this.parentOf.add(concept);
    }
    
    public void addSubjectOfRelationshipStatement(RelationshipStatement relationshipStatement){
        subjectOfRelationShipStatements.add(relationshipStatement);
    }

    public Set<RelationshipStatement> getSubjectOfRelationShipStatements(){
        return subjectOfRelationShipStatements;
    }

    public void addSubjectOfRelationShipStatements(RelationshipStatement statement){
        this.subjectOfRelationShipStatements.add(statement);
    }
    public Ontology getOntology() {
        return ontology;
    }
    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }
    public long getSerialisedId() {
        return serialisedId;
    }
    public void setSerialisedId(long serialisedId) {
        this.serialisedId = serialisedId;
    }
    
}
