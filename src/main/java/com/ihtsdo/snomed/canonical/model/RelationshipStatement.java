package com.ihtsdo.snomed.canonical.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.primitives.Longs;

@Entity(name="RelationshipStatement")
@Table(name="RELATIONSHIP_STATEMENT")
public class RelationshipStatement {
    
    public static final long IS_KIND_OF_RELATIONSHIP_TYPE_ID = 116680003;
    public static final int DEFINING_CHARACTERISTIC_TYPE = 0;
    public static final int NOT_DEFINING_CHARACTERISTIC_TYPE = 1;
    
    public RelationshipStatement(){};
    public RelationshipStatement(long id){this.id = id;}
    public RelationshipStatement(long id, Concept subject, long relationshipType, Concept object, int characteristicType){
        this.id = id;
        this.subject = subject;
        this.object = object;
        this.relationshipType = relationshipType;
        this.characteristicType = characteristicType;
        subject.addSubjectOfRelationshipStatement(this);
    }
    
    @OneToOne
    private Ontology ontology;
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne (cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Concept subject;

    @Column(name="relationship_type")
    private long relationshipType;
    @OneToOne private Concept object;
    
    @Column(name="characteristic_type")
    private int characteristicType;
    
    @Column(nullable=true)
    private int refinability;
    
    @Column(name="relationship_group")
    private int relationShipGroup;

    public boolean isKindOfRelationship(){
        return (getRelationshipType() == IS_KIND_OF_RELATIONSHIP_TYPE_ID);
    }
    
    public boolean isDefiningCharacteristic(){
        return getCharacteristicType() == DEFINING_CHARACTERISTIC_TYPE;
    }
    
    @Override
    public String toString() {
        return "(" + getId() + ": " + getSubject() + " --" + getRelationshipType() + "-> " + getObject() + ", group: " + getRelationShipGroup() + ")";
        //return ToStringBuilder.reflectionToString(this);
    }
    
    public String shortToString(){
        return "[" + getId() + ": " + getSubject() + "(" + getRelationshipType() + ")" + getObject() + ", type:" + getCharacteristicType() + "]";
    }

    @Override
    public int hashCode(){
        return Longs.hashCode(getId());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof RelationshipStatement){
            RelationshipStatement r = (RelationshipStatement) o;
            if (r.getId() == this.getId()){
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

    public int getRelationShipGroup() {
        return relationShipGroup;
    }
    public void setRelationShipGroup(int relationShipGroup) {
        this.relationShipGroup = relationShipGroup;
    }



}
