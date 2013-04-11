package com.ihtsdo.snomed.canonical.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

@Entity(name="RelationshipStatement")
@Table(name="RELATIONSHIP_STATEMENT")
public class RelationshipStatement {
    
    public static final long SERIALISED_ID_NOT_DEFINED = -1l;
    public static final long IS_KIND_OF_RELATIONSHIP_TYPE_ID = 116680003;
    public static final int DEFINING_CHARACTERISTIC_TYPE = 0;
   
    @OneToOne
    private Ontology ontology;
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id private long id;
    
    private long serialisedId = SERIALISED_ID_NOT_DEFINED;

    @OneToOne// (cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Concept subject;

    @Column(name="relationship_type")
    private long relationshipType;
    @OneToOne private Concept object;
    
    @Column(name="characteristic_type")
    private int characteristicType;
    
    @Column(nullable=true)
    private int refinability;
    
    @Column(name="relationship_group")
    private int group;

    public RelationshipStatement(){};
    public RelationshipStatement(long serialisedId){this.serialisedId = serialisedId;}
    public RelationshipStatement(long serialisedId, Concept subject, long relationshipType, 
            Concept object, int characteristicType, int group)
    {
        this.group = group;
        this.serialisedId = serialisedId;
        this.subject = subject;
        this.object = object;
        this.relationshipType = relationshipType;
        this.characteristicType = characteristicType;
        subject.addSubjectOfRelationshipStatement(this);
    }
    
    public RelationshipStatement(long serialisedId, Concept subject,
            long relationshipType, Concept object) 
    {
        this.serialisedId = serialisedId;
        this.subject = subject;
        this.object = object;
        this.relationshipType = relationshipType;    
        subject.addSubjectOfRelationshipStatement(this);
    }

    
    public boolean isKindOfRelationship(){
        return (getRelationshipType() == IS_KIND_OF_RELATIONSHIP_TYPE_ID);
    }
    
    public boolean isDefiningCharacteristic(){
        return getCharacteristicType() == DEFINING_CHARACTERISTIC_TYPE;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("id", getId())
            .add("internalId", getSerialisedId())
            .add("ontology", getOntology() == null ? null : getOntology().getId())
            .add("subject", getSubject() == null ? null : getSubject().getSerialisedId())
            .add("predicate", getRelationshipType())
            .add("object", getObject() == null ? null : getObject().getSerialisedId())
            .add("characteristic", getCharacteristicType())
            .add("refinability", getRefinability())
            .add("group", getGroup())
            .toString();
    }
    
    public String shortToString(){
        return "[" + getSerialisedId() + ": " + getSubject() + "(" + getRelationshipType() + ")" + getObject() + " type:" + getCharacteristicType() + "]";
    }

    @Override
    public int hashCode(){
        if (this.getSerialisedId() == SERIALISED_ID_NOT_DEFINED){
            return Longs.hashCode(this.getSubject().getSerialisedId());
        }
        return Longs.hashCode(getSerialisedId());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof RelationshipStatement){
            RelationshipStatement r = (RelationshipStatement) o;
            
            if ((r.getSerialisedId() == SERIALISED_ID_NOT_DEFINED) || (this.getSerialisedId() == SERIALISED_ID_NOT_DEFINED)){
                return (r.getSubject().equals(this.getSubject())
                        && r.getObject().equals(this.getObject())
                        && r.getRelationshipType() == (this.getRelationshipType()));
            }
            
            if (r.getSerialisedId() == this.getSerialisedId()){
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
    public Concept getSubject() {
        return subject;
    }
    public void setSubject(Concept subject) {
        this.subject = subject;
    }
    public long getRelationshipType() {
        return relationshipType;
    }
    public void setRelationshipType(long relationshipType) {
        this.relationshipType = relationshipType;
    }
    public Concept getObject() {
        return object;
    }
    public void setObject(Concept object) {
        this.object = object;
    }
    public int getCharacteristicType() {
        return characteristicType;
    }
    public void setCharacteristicType(int characteristicType) {
        this.characteristicType = characteristicType;
    }
    public int getRefinability() {
        return refinability;
    }
    public void setRefinability(int refinability) {
        this.refinability = refinability;
    }
    public int getGroup() {
        return group;
    }
    public void setGroup(int group) {
        this.group = group;
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
