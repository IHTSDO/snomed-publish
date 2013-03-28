package com.ihtsdo.snomed.canonical.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.primitives.Longs;

@Entity
public class Relationship {
	
	@Id private long id;
	@OneToOne private Concept concept1;
	private long relationshipType;
	@OneToOne private Concept concept2;
	private int characteristicType;
	private boolean refinability;
	private int relationShipGroup;
	
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    @Override
    public int hashCode(){
    	return Longs.hashCode(id);
    }
    
    @Override
    public boolean equals(Object o){
    	if (o instanceof Relationship){
    		Relationship r = (Relationship) o;
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
	public Concept getConcept1() {
		return concept1;
	}
	public void setConcept1(Concept concept1) {
		this.concept1 = concept1;
	}
	public long getRelationshipType() {
		return relationshipType;
	}
	public void setRelationshipType(long relationshipType) {
		this.relationshipType = relationshipType;
	}
	public Concept getConcept2() {
		return concept2;
	}
	public void setConcept2(Concept concept2) {
		this.concept2 = concept2;
	}
	public int getCharacteristicType() {
		return characteristicType;
	}
	public void setCharacteristicType(int characteristicType) {
		this.characteristicType = characteristicType;
	}
	public boolean isRefinability() {
		return refinability;
	}
	public void setRefinability(boolean refinability) {
		this.refinability = refinability;
	}
	public int getRelationShipGroup() {
		return relationShipGroup;
	}
	public void setRelationShipGroup(int relationShipGroup) {
		this.relationShipGroup = relationShipGroup;
	}
	
	
	
}
