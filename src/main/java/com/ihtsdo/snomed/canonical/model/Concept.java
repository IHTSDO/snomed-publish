package com.ihtsdo.snomed.canonical.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Concept {
	@Id private long id;
	private int status;
	private String fullySpecifiedName;
	private String ctv3id;
	private String snomedId;
	private boolean isPrimitive;
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
		return isPrimitive;
	}
	public void setPrimitive(boolean isPrimitive) {
		this.isPrimitive = isPrimitive;
	}
	
	
}
