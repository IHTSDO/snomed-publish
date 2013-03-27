package com.ihtsdo.snomed.canonical.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Relationship {
	
	@Id 
	private long id;
	
	@OneToOne(optional=false) 
//    @JoinColumn(name="conceptId1", 
//    	unique=true, 
//    	nullable=false, 
//    	updatable=false)
	private Concept concept1;
	
	private long relationshipType;
	
	@OneToOne(optional=false)
	private Concept concept2;
	
	private int characteristicType;
	private boolean refinability;
	private int relationShipGroup;
	
}
