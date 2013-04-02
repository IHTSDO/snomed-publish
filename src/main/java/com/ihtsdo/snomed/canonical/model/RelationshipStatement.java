package com.ihtsdo.snomed.canonical.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.primitives.Longs;

@Entity(name="RelationshipStatement")
@Table(name="RELATIONSHIP_STATEMENT")
public class RelationshipStatement {
    
    public static final long IS_KIND_OF_RELATIONSHIP_TYPE_ID = 116680003;
    protected static final int DEFINING_CHARACTERISTIC_RELATIONSHIP_GROUP = 0;
    
    public RelationshipStatement(){};
    public RelationshipStatement(long id){this.id = id;}
    
    @Id private long id;

    @OneToOne (cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Concept subject;

    @Column(name="relationship_type")
    private long relationshipType;
    @OneToOne private Concept object;
    
    @Column(name="characteristic_type")
    private int characteristicType;
    
    private int refinability;
    
    @Column(name="relationship_group")
    private int relationShipGroup;

    public boolean isKindOfRelationship(){
        return (relationshipType == IS_KIND_OF_RELATIONSHIP_TYPE_ID);
    }
    
    public boolean isDefiningCharacteristic(){
        return relationShipGroup == DEFINING_CHARACTERISTIC_RELATIONSHIP_GROUP;
    }
    
    @Override
    public String toString() {
        return "{id: " + getId() + ", " + getSubject() + "-" + getRelationshipType() + "-" + getObject() + ", group: " + getRelationShipGroup() + "}";
        //return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode(){
        return Longs.hashCode(id);
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof RelationshipStatement){
            RelationshipStatement r = (RelationshipStatement) o;
            if (r.id == this.id){
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
