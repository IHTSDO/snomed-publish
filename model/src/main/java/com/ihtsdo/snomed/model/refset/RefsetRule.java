package com.ihtsdo.snomed.model.refset;

import java.util.Set;

import javax.persistence.MappedSuperclass;

import com.ihtsdo.snomed.model.Concept;

@MappedSuperclass
public interface RefsetRule extends Visitable{
    public Set<Concept> generateConcepts();
    public RefsetRule append(String inputName, RefsetRule rule);
    public RefsetRule prepend(String inputName, RefsetRule rule);
}
