
package com.ihtsdo.snomed.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.Null;

import org.apache.commons.lang.WordUtils;
import org.hibernate.annotations.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

/**
 * @author henrikpettersen
 *
 */

//uniqueConstraints={
//@UniqueConstraint(name="uniqueSerialisedId", columnNames={
//  "serialisedId", "ontologyVersion_id"
//}),


@Entity
@org.hibernate.annotations.Table(appliesTo = "Concept",
        indexes={@Index(name="conceptSerialisedIdIndex", columnNames={"serialisedId"}),
                 @Index(name="conceptSerialisedIdAndOntologyVersionIndex", columnNames={"serialisedId", "ontologyVersion_id"})})
public class Concept {
    protected static final String ATTRIBUTE = "attribute";
    private static final Logger LOG = LoggerFactory.getLogger( Concept.class );
    public static final long IS_KIND_OF_RELATIONSHIP_TYPE_ID = 116680003l;
    
    @Version
    protected int version = 1;
    
    @Transient 
    private Set<Concept> allKindOfPrimitiveCache;
    @Transient 
    private Set<Concept> allKindOfCache;
    @Transient
    private Set<Concept> allActiveKindOfCache;
    @Transient 
    private Map<Integer, Group> groupMap = new HashMap<Integer, Group>();
    @Transient
    private Description preferredTerm;
    @Transient
    private Set<Description> synonyms;
    @Transient 
    private Set<Description> unspecifiedDescriptions;
    @Transient
    private Set<Description> fullySpecifiedNameDescriptions;

    
    //SHARED
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    private long id;
    
    //@Index(name="conceptSerialisedIdIndex")
    private long serialisedId;
    @OneToOne(fetch=FetchType.LAZY)
    private OntologyVersion ontologyVersion;
    @OneToMany(mappedBy="about", fetch=FetchType.LAZY)  
    private Set<Description> description;
    private String fullySpecifiedName;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy="masterConcept", fetch=FetchType.LAZY)
    private Set<Concept> history = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name="MasterConcept_id", nullable=true)
    private Concept masterConcept = null;
    
    //RF1
    private String ctv3id;
    private String snomedId;
    //private String type;
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
    @OneToMany(mappedBy="subject", fetch=FetchType.LAZY) 
    private Set<Statement> subjectOfStatements = new HashSet<Statement>(); 
    @OneToMany(mappedBy="object", fetch=FetchType.LAZY)
    private Set<Statement> objectOfStatements = new HashSet<Statement>();    
    @OneToMany(mappedBy="predicate", fetch=FetchType.LAZY)
    private Set<Statement> predicateOfStatements = new HashSet<Statement>();
    
    
    //HIERARCHY
    @ManyToMany
    @JoinTable(
        joinColumns = @JoinColumn(name="child_id"),
        inverseJoinColumns = @JoinColumn(name="parent_id"),
        uniqueConstraints=@UniqueConstraint(columnNames={"parent_id", "child_id"}))
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Set<Concept> kindOfs = new HashSet<Concept>();
    @ManyToMany(mappedBy="kindOfs")
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
                .add("ontologyVersion", getOntologyVersion() == null ? null : getOntologyVersion().getId())
                .add("descriptions", getDescription() == null ? 0 : getDescription().size())
                .add("statusId(rf1)", getStatusId())
                .add("fullySpecifiedName(rf1)", getFullySpecifiedName())
                .add("ctv3id(rf1)", getCtv3id())
                .add("snomedId(rf1)", getSnomedId())
                .add("primitive(rf1)", isPrimitive())
                .add("effectiveTime(rf2)", getEffectiveTime())
                .add("active(rf2)", isActive())
                .add("status(rf2)", getStatus() == null ? null : getStatus().getSerialisedId())
                .add("module(rf2)", getModule() == null ? null : getModule().getSerialisedId())
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
            if ((c.getSerialisedId() == this.getSerialisedId())){
                return true;
            }
        }
        return false;
    }
    
    
    public String getDisplayName(){
        return getFullySpecifiedName();
    }
    
    @Transient
    private String shortDisplayNameCache;
    
    public String getShortDisplayName(){
        if (shortDisplayNameCache == null){
            shortDisplayNameCache = getDisplayName().trim();
            if (shortDisplayNameCache.contains("core metadata concept")){
                shortDisplayNameCache = shortDisplayNameCache.substring(0, shortDisplayNameCache.lastIndexOf('('));
            }
            if (shortDisplayNameCache.contains(" concept definition status")){
                shortDisplayNameCache = shortDisplayNameCache.substring(0, shortDisplayNameCache.indexOf(" concept definition status"));
            }
            shortDisplayNameCache = shortDisplayNameCache.trim();
            if (shortDisplayNameCache.endsWith(" module")){
                shortDisplayNameCache = shortDisplayNameCache.substring(0, shortDisplayNameCache.indexOf(" module"));
            }
            if (shortDisplayNameCache.startsWith("SNOMED CT ")){
                shortDisplayNameCache = shortDisplayNameCache.substring(10, shortDisplayNameCache.length());
                shortDisplayNameCache = WordUtils.capitalize(shortDisplayNameCache);
            }
            
        }
        return shortDisplayNameCache;
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
        if (!getPredicateOfStatements().isEmpty()){
            return true;
        }
        return false;
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
    
    public Date getParsedEffectiveTime() throws ParseException{
        return new SimpleDateFormat("yyyymmdd").parse(Long.toString(effectiveTime));
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
    public OntologyVersion getOntologyVersion() {
        return ontologyVersion;
    }
    public void setOntologyVersion(OntologyVersion ontologyVersion) {
        this.ontologyVersion = ontologyVersion;
    }
//    public String getType() {
//        return type;
//    }
//    public void setType(String type) {
//        this.type = type;
//    }
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

    public Set<Concept> getHistory() {
        return history;
    }

    public void setHistory(Set<Concept> history) {
        this.history = history;
    }

    public Concept getMasterConcept() {
        return masterConcept;
    }

    public void setMasterConcept(Concept masterConcept) {
        this.masterConcept = masterConcept;
    }

    
    
}
