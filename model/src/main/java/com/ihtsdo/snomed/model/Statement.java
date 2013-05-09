package com.ihtsdo.snomed.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

@Entity(name="Statement")
@Table(name="STATEMENT")
public class Statement {
    
    public static final long SERIALISED_ID_NOT_DEFINED = -1l;
    public static final int DEFINING_CHARACTERISTIC_TYPE = 0;
   
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Id private long id;

    @OneToOne private Ontology ontology;
    @OneToOne private Concept subject;
    @OneToOne private Concept predicate;
    @OneToOne private Concept object;
    
    private long serialisedId = SERIALISED_ID_NOT_DEFINED;
    @Column(nullable=true, name="characteristic_type") private int characteristicType;
    @Column(nullable=true) private int refinability;
    @Column(name="relationship_group") private int groupId;

    public Statement(){};
    public Statement(long serialisedId){this.serialisedId = serialisedId;}
    public Statement(long serialisedId, Concept subject, Concept predicate, 
            Concept object, int characteristicType, int group)
    {
        this.groupId = group;
        this.serialisedId = serialisedId;
        this.subject = subject;
        this.object = object;
        this.predicate = predicate;
        this.characteristicType = characteristicType;
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("id", getId())
            .add("internalId", getSerialisedId())
            .add("ontology", getOntology() == null ? null : getOntology().getId())
            .add("subject", getSubject() == null ? null : getSubject().getSerialisedId())
            .add("predicate", getPredicate() == null ? null : getPredicate().getSerialisedId())
            .add("object", getObject() == null ? null : getObject().getSerialisedId())
            .add("characteristic", getCharacteristicType())
            .add("refinability", getRefinability())
            .add("group", getGroupId())
            .toString();
    }
    
    public String shortToString(){
        return "[" + getSerialisedId() + ": " + getSubject().getSerialisedId() + "(" + getPredicate().getSerialisedId() + ")" + getObject().getSerialisedId() + ", T" + getCharacteristicType() + ", G" + getGroupId()+"]";
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
                return (r.getSubject().equals(this.getSubject())
                        && r.getObject().equals(this.getObject())
                        && r.getPredicate().equals(this.getPredicate()));
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
        return getCharacteristicType() == DEFINING_CHARACTERISTIC_TYPE;
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
    }
    public Concept getPredicate() {
        return predicate;
    }
    public void setPredicate(Concept predicate) {
        this.predicate = predicate;
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
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
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
