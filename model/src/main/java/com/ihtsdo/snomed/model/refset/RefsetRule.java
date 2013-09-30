package com.ihtsdo.snomed.model.refset;

import java.util.Set;

import com.ihtsdo.snomed.model.Concept;

//@MappedSuperclass
public interface RefsetRule extends Visitable{
    public Set<Concept> generateConcepts();
    public Long getId();
    public RefsetRule append(String inputName, RefsetRule rule);
    public RefsetRule prepend(String inputName, RefsetRule rule);
}
