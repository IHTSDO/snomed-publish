package com.ihtsdo.snomed.canonical.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Ontology {
	@Id private long id;
	private String name;
	private String description;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<Relationship> triples;
}
