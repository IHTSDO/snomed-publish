package com.ihtsdo.snomed.canonical.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
public class Ontology {
	@Id private long id;
	private String name;
	private String description;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Relationship> triples;
	
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Relationship> getTriples() {
		return triples;
	}

	public void setTriples(List<Relationship> triples) {
		this.triples = triples;
	}
    
    /*
     * Generated Getters and Setters
     */

}
