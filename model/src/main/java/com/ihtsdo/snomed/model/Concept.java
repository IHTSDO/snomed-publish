
package com.ihtsdo.snomed.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
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
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

/**
 * @author henrikpettersen
 *
 */
@XmlRootElement
@Entity
public class Concept {
    protected static final String ATTRIBUTE = "attribute";
    private static final Logger LOG = LoggerFactory.getLogger( Concept.class );
    public static final long IS_KIND_OF_RELATIONSHIP_TYPE_ID = 116680003l;
    
    @Version
    protected int version = 1;
    
    //TRANSIENT
    @XmlTransient @Transient 
    private Set<Concept> allKindOfPrimitiveCache;
    @XmlTransient @Transient 
    private Set<Concept> allKindOfCache;
    @XmlTransient @Transient
    private Set<Concept> allActiveKindOfCache;
    @XmlTransient @Transient 
    private Map<Integer, Group> groupMap = new HashMap<Integer, Group>();

    
    //SHARED
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private long id;    
    private long serialisedId;
    @XmlTransient
    @OneToOne 
    private Ontology ontology;
    @OneToMany(mappedBy="about")  
    private Set<Description> description;
    
    //RF1
    private String fullySpecifiedName;
    private String ctv3id;
    private String snomedId;
    private String type;
    @Column(columnDefinition = "BIT", length = 1) 
    private boolean primitive = false; 
    private int statusId = -1;

    
    //RF2 
    private long effectiveTime;
    @Column(columnDefinition = "BIT", length = 1) 
    private boolean active;
    @OneToOne 
    private Concept status;
    @OneToOne
    private Concept module;
    
    
    //STATEMENTS
    @XmlTransient 
    @OneToMany(mappedBy="subject") 
    private Set<Statement> subjectOfStatements = new HashSet<Statement>();
    @XmlTransient 
    @OneToMany(mappedBy="object")
    private Set<Statement> objectOfStatements = new HashSet<Statement>();    
    @XmlTransient 
    @OneToMany(mappedBy="predicate")
    private Set<Statement> predicateOfStatements = new HashSet<Statement>();
    
    
    //HIERARCHY
    @XmlTransient
    @ManyToMany
    @JoinTable(name = "KIND_OF", 
        joinColumns = @JoinColumn(name="child_id"),
        inverseJoinColumns = @JoinColumn(name="parent_id"),
        uniqueConstraints=@UniqueConstraint(columnNames={"parent_id", "child_id"}))
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Set<Concept> kindOfs = new HashSet<Concept>();
    @XmlTransient @ManyToMany(mappedBy="kindOfs")
    private Set<Concept> parentOf = new HashSet<Concept>();
    
    
    public Concept(){}
    
    public Concept(long serialisedId){
        this.serialisedId = serialisedId;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("serialisedId", getSerialisedId())
                .add("ontology", getOntology() == null ? null : getOntology().getId())
                .add("description", getDescription())
                .add("statusId(rf1)", getStatusId())
                .add("fullySpecifiedName(rf1)", getFullySpecifiedName())
                .add("ctv3id(rf1)", getCtv3id())
                .add("snomedId(rf1)", getSnomedId())
                .add("primitive(rf1)", isPrimitive())
                .add("effectiveTime(rf2)", getEffectiveTime())
                .add("active(rf2)", isActive())
                .add("status(rf2)", getStatus() == null ? null : getStatus())
                .add("module(rf2)", getModule() == null ? null : getModule())
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
    
    public Set<Concept> getAllKindOfPrimitiveConcepts(boolean useCache){
        if (useCache && (allKindOfPrimitiveCache != null)){
            if (LOG.isDebugEnabled()){
                StringBuffer debugStringBuffer = new StringBuffer("Cache hit for concept " + this.getSerialisedId() + " with values {");
                for (Concept c : allKindOfPrimitiveCache){
                    debugStringBuffer.append(c.getSerialisedId()+ ", ");
                }
                if (!allKindOfPrimitiveCache.isEmpty()){
                    debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
                }
                debugStringBuffer.append("}");
                LOG.debug(debugStringBuffer.toString());
            }
            return allKindOfPrimitiveCache;
        }
        LOG.debug("Populating allKindOfPrimitiveCache for concept " + getSerialisedId());
        allKindOfPrimitiveCache = new HashSet<Concept>();
        
        for (Concept kindOf : getKindOfs()){
            if (kindOf.isPrimitive()){
                allKindOfPrimitiveCache.add(kindOf);
            }
            allKindOfPrimitiveCache.addAll(kindOf.getAllKindOfPrimitiveConcepts(useCache));
        }   
        return allKindOfPrimitiveCache;
    }
    
    public Set<Concept> getAllKindOfConcepts(boolean useCache){
        if (useCache && (allKindOfCache != null)){
            if (LOG.isDebugEnabled()){
                StringBuffer debugStringBuffer = new StringBuffer("Cache hit for concept " + this.getSerialisedId() + " with values {");
                for (Concept c : allKindOfCache){
                    debugStringBuffer.append(c.getSerialisedId()+ ", ");
                }
                if (!allKindOfCache.isEmpty()){
                    debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
                }
                debugStringBuffer.append("}");
                LOG.debug(debugStringBuffer.toString());
            }
            return allKindOfCache;
        }
        LOG.debug("Populating allKindOfCache for concept " + getSerialisedId());
        allKindOfCache = new HashSet<Concept>();
        
        for (Concept kindOf : getKindOfs()){
            allKindOfCache.add(kindOf);
            allKindOfCache.addAll(kindOf.getAllKindOfConcepts(useCache));
        }   
        return allKindOfCache;
    }
    
    public Set<Concept> getAllActiveKindOfConcepts(boolean useCache){
        if (useCache && (allActiveKindOfCache != null)){
            if (LOG.isDebugEnabled()){
                StringBuffer debugStringBuffer = new StringBuffer("Cache hit for concept " + this.getSerialisedId() + " with values {");
                for (Concept c : allActiveKindOfCache){
                    debugStringBuffer.append(c.getSerialisedId()+ ", ");
                }
                if (!allActiveKindOfCache.isEmpty()){
                    debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
                }
                debugStringBuffer.append("}");
                LOG.debug(debugStringBuffer.toString());
            }
            return allActiveKindOfCache;
        }
        LOG.debug("Populating allActiveKindOfCache for concept " + getSerialisedId());
        allActiveKindOfCache = new HashSet<Concept>();

        for (Statement s : getSubjectOfStatements()){
//            if (s.isKindOfStatement()){
//                if (s.isActive() ||
//                        (s.getSubject().isActive() && s.getSubject().getEffectiveTime() == s.getEffectiveTime())){
//                    allActiveKindOfCache.add(s.getObject());
//                    allActiveKindOfCache.addAll(s.getObject().getAllActiveKindOfConcepts(useCache));
//                }
//                
//            }
            if (s.isKindOfStatement() && s.isActive()){
                allActiveKindOfCache.add(s.getObject());
                allActiveKindOfCache.addAll(s.getObject().getAllActiveKindOfConcepts(useCache));
            }
        }
        
        return allActiveKindOfCache;
    }

    public boolean isKindOfPredicate(){
        return getSerialisedId() == IS_KIND_OF_RELATIONSHIP_TYPE_ID;
    }
    
    public boolean isPredicate(){
        return ((getType() != null) && (!getType().isEmpty()) && getType().equals(ATTRIBUTE));
    }
    

    
    /*package level*/ Group getGroup(Statement statement){
        Group group = groupMap.get(statement.getGroupId());
        if (group == null){
            group = new Group();
            groupMap.put(statement.getGroupId(), group);
            for (Statement otherStatement : getSubjectOfStatements()){
                if (!otherStatement.isKindOfStatement() && (otherStatement.getGroupId() == statement.getGroupId())){
                    group.addStatement(otherStatement);
                }
            }
        }
        return group; 
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
    public int getStatusId() {
        return statusId;
    }
    public void setStatusId(int statusId) {
        this.statusId = statusId;
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
        getKindOfs().add(concept);
    }
    public void addParentOf(Concept concept){
        getParentOf().add(concept);
    }
    public Set<Statement> getSubjectOfStatements(){
        return subjectOfStatements;
    }
    public void addSubjectOfStatement(Statement statement){
        getSubjectOfStatements().add(statement);
    }    
    public void addPredicateOfStatement(Statement statement){
        getPredicateOfStatements().add(statement);
    }
    public Set<Statement> getPredicateOfStatements(){
        return predicateOfStatements;
    }
    public void addObjectOfStatement(Statement statement){
        getObjectOfStatements().add(statement);
    }
    public Set<Statement> getObjectOfStatements(){
        return objectOfStatements;
    }
    public Ontology getOntology() {
        return ontology;
    }
    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setSerialisedId(long serialisedId) {
        this.serialisedId = serialisedId;
    }        
    public long getSerialisedId() {
        return serialisedId;
    }
    public long getEffectiveTime() {
        return effectiveTime;
    }
    public void setEffectiveTime(long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public Set<Description> getDescription() {
        return description;
    }
    public void setDescription(Set<Description> description) {
        this.description = description;
    }

    public Concept getStatus() {
        return status;
    }

    public void setStatus(Concept status) {
        this.status = status;
    }

    public void setSubjectOfStatements(Set<Statement> subjectOfStatements) {
        this.subjectOfStatements = subjectOfStatements;
    }

    public void setObjectOfStatements(Set<Statement> objectOfStatements) {
        this.objectOfStatements = objectOfStatements;
    }

    public void setPredicateOfStatements(Set<Statement> predicateOfStatements) {
        this.predicateOfStatements = predicateOfStatements;
    }

    public Concept getModule() {
        return module;
    }

    public void setModule(Concept module) {
        this.module = module;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    
}
