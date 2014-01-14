package com.ihtsdo.snomed.model.refset;

import java.util.Set;

import com.ihtsdo.snomed.model.Concept;

//@MappedSuperclass
public interface Rule extends Visitable{
    public Set<Concept> generateConcepts();
    public Long getId();
    public Rule append(String inputName, Rule rule);
    public Rule prepend(String inputName, Rule rule);
    public Rule clone();
}
