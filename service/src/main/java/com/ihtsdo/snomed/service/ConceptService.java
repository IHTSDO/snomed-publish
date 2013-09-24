package com.ihtsdo.snomed.service;

import java.util.List;

import com.ihtsdo.snomed.model.Concept;

/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
public interface ConceptService {

    public abstract List<Concept> findAll(int pageIndex);

    public abstract List<Concept> findAll();

    public abstract Concept findById(Long id);

    public abstract Concept findBySerialisedId(Long serialisedId);
}