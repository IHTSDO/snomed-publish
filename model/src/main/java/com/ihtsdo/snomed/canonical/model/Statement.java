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
    @Column(name="characteristic_type") private int characteristicType;
    @Column(nullable=true) private int refinability;
    @Column(name="relationship_group") private int group;

    public Statement(){};
    public Statement(long serialisedId){this.serialisedId = serialisedId;}
    public Statement(long serialisedId, Concept subject, Concept predicate, 
            Concept object, int characteristicType, int group)
    {
        this.group = group;
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

    
    public boolean isKindOfRelationship(){
        return (getPredicate().isKindOfPredicate());
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
            .add("predicate", getPredicate())
            .add("object", getObject() == null ? null : getObject().getSerialisedId())
            .add("characteristic", getCharacteristicType())
            .add("refinability", getRefinability())
            .add("group", getGroup())
            .toString();
    }
    
    public String shortToString(){
        return "[" + getSerialisedId() + ": " + getSubject().getSerialisedId() + "(" + getPredicate().getSerialisedId() + ")" + getObject().getSerialisedId() + ", T" + getCharacteristicType() + ", G" + getGroup()+"]";
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
    
    public boolean isMemberOfGroup(){
        return (group != 0);
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
