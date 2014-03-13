package com.ihtsdo.snomed.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Index;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

@Entity(name="Statement")
@org.hibernate.annotations.Table(appliesTo = "Statement",
indexes={@Index(name="statementSerialisedIdIndex", columnNames={"serialisedId"}),
         @Index(name="statementSerialisedIdAndOntologyIndex", columnNames={"serialisedId", "ontologyVersion_id"})})
public class Statement {
    
    public static final long SERIALISED_ID_NOT_DEFINED = -1l;
    public static final int DEFINING_CHARACTERISTIC_TYPE = 0;
    
    //SHARED  
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Id 
    private long id;
    @OneToOne(fetch=FetchType.LAZY)
    private OntologyVersion ontologyVersion;
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
    private Concept subject;
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
    private Concept predicate;
    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST) 
    private Concept object; 
    
    @Index(name="statementSerialisedIdIndex")
    private long serialisedId = SERIALISED_ID_NOT_DEFINED;
    @Column(name="groupId") 
    private int groupId;
    
    //RF1 
    private int characteristicTypeIdentifier;
    private int refinability;
    
    //RF2
    @OneToOne
    private Concept characteristicType;
    @Column(columnDefinition = "BIT", length = 1)
    private boolean active;
//    @OneToOne(fetch=FetchType.EAGER)
    @OneToOne(fetch=FetchType.LAZY)
    private Concept module;
    //@OneToOne(fetch=FetchType.EAGER)
    @OneToOne(fetch=FetchType.LAZY)
    private Concept modifier;
    private int effectiveTime;
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("id", getId())
            .add("serialisedId", getSerialisedId())
            .add("ontologyVersion", getOntologyVersion() == null ? null : getOntologyVersion().getId())
            .add("subject", getSubject() == null ? null : getSubject().getSerialisedId())
            .add("predicate", getPredicate() == null ? null : getPredicate().getSerialisedId())
            .add("object", getObject() == null ? null : getObject().getSerialisedId())
            .add("groupId", getGroupId())
            .add("characteristicTypeIdentifier(rf1)", getCharacteristicTypeIdentifier())
            .add("refinability(rf1)", getRefinability())
            .add("characteristicType(rf2)", getCharacteristicType() == null ? null : getCharacteristicType().getSerialisedId())
            .add("active(rf2)", isActive())
            .add("module(rf2)", getModule() == null ? null : getModifier().getSerialisedId())
            .add("modifier(rf2)", getModifier() == null ? null : getModifier().getSerialisedId())
            .toString();
    }
     
    public Statement(){};
    public Statement(long serialisedId){this.serialisedId = serialisedId;}
    public Statement(long serialisedId, Concept subject, Concept predicate, 
            Concept object, int characteristicTypeId, int group)
    {
        this.groupId = group;
        this.serialisedId = serialisedId;
        this.subject = subject;
        this.object = object;
        this.predicate = predicate;
        this.characteristicTypeIdentifier = characteristicTypeId;
        subject.addSubjectOfStatement(this);
        object.addObjectOfStatement(this);
        predicate.addPredicateOfStatement(this);
    }
    
    public Statement(long serialisedId, Concept subject,
            Concept predicate, Concept object) 
    {
        this.serialisedId = serialisedId;
        this.subject = subject;
        this.object = object;
        this.predicate = predicate;    
        subject.addSubjectOfStatement(this);
        object.addObjectOfStatement(this);
        predicate.addPredicateOfStatement(this);
    }

    public String shortToString(){
        return "[" + getSerialisedId() + ": " + getSubject().getSerialisedId() + "(" + getPredicate().getSerialisedId() + ")" + getObject().getSerialisedId() + ", T" + getCharacteristicTypeIdentifier() + ", G" + getGroupId()+"]";
    }
    
    public Date getParsedEffectiveTime() throws ParseException{
        return new SimpleDateFormat("yyyymmdd").parse(Long.toString(effectiveTime));
    }
    
    @Override
    public int hashCode(){
        if (this.getSerialisedId() == SERIALISED_ID_NOT_DEFINED){
            return Longs.hashCode((this.getSubject() == null) ? -1 : this.getSubject().getSerialisedId());
        }
        return Longs.hashCode(getSerialisedId());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Statement){
            Statement r = (Statement) o;
            
            if ((r.getSerialisedId() == SERIALISED_ID_NOT_DEFINED) || (this.getSerialisedId() == SERIALISED_ID_NOT_DEFINED)){
                return (Objects.equal(r.getSubject(), this.getSubject())
                        && Objects.equal(r.getObject(), this.getObject())
                        && Objects.equal(r.getPredicate(), this.getPredicate()));
            }
            
            if (r.getSerialisedId() == this.getSerialisedId()){
                return true;
            }
        }
        return false;
    }

    public Group getGroup(){
        return getSubject().getGroup(this);
    }
    
    public boolean isKindOfStatement(){
        return (getPredicate().isKindOfPredicate());
    }
    
    public boolean isDefiningCharacteristic(){
        return getCharacteristicTypeIdentifier() == DEFINING_CHARACTERISTIC_TYPE;
    }
        
    
    public boolean isMemberOfGroup(){
        return (getGroupId() != 0);
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
    public Concept getSubject() {
        return subject;
    }
    public void setSubject(Concept subject) {
        this.subject = subject;
        subject.addSubjectOfStatement(this);
    }
    public Concept getPredicate() {
        return predicate;
    }
    public void setPredicate(Concept predicate) {
        this.predicate = predicate;
        predicate.addPredicateOfStatement(this);
    }
    public Concept getObject() {
        return object;
    }
    public void setObject(Concept object) {
        this.object = object;
        object.addObjectOfStatement(this);
    }
    public int getCharacteristicTypeIdentifier() {
        return characteristicTypeIdentifier;
    }
    public void setCharacteristicTypeIdentifier(int characteristicTypeIdentifier) {
        this.characteristicTypeIdentifier = characteristicTypeIdentifier;
    }
    public int getRefinability() {
        return refinability;
    }
    public void setRefinability(int refinability) {
        this.refinability = refinability;
    }
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    public OntologyVersion getOntologyVersion() {
        return ontologyVersion;
    }
    public void setOntologyVersion(OntologyVersion ontologyVersion) {
        this.ontologyVersion = ontologyVersion;
    }
    public long getSerialisedId() {
        return serialisedId;
    }
    public void setSerialisedId(long serialisedId) {
        this.serialisedId = serialisedId;
    }
    public Concept getCharacteristicType() {
        return characteristicType;
    }
    public void setCharacteristicType(Concept characteristicType) {
        this.characteristicType = characteristicType;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public Concept getModule() {
        return module;
    }
    public void setModule(Concept module) {
        this.module = module;
    }
    public Concept getModifier() {
        return modifier;
    }
    public void setModifier(Concept modifier) {
        this.modifier = modifier;
    }
    public int getEffectiveTime() {
        return effectiveTime;
    }
    public void setEffectiveTime(int effectiveTime) {
        this.effectiveTime = effectiveTime;
    }
    
}
