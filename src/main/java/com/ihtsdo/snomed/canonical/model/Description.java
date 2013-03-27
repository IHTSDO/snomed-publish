package com.ihtsdo.snomed.canonical.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Description {
	@Id private long id;
	private int status;
	@OneToOne (optional=false)
	private Concept concept;
	private String term;
	private int initialCapitalStatus;
	private int type;
	private String langaugeCode;
}
