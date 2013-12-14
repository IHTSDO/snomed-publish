package com.ihtsdo.snomed.service;

import java.util.List;

import com.ihtsdo.snomed.model.Description;

public interface DescriptionService {

    public abstract List<Description> findAll(int pageIndex);

    public abstract List<Description> findAll();

    public abstract Description findById(Long id);

    public abstract Description findBySerialisedId(Long serialisedId);
}