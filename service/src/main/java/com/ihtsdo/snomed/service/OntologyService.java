package com.ihtsdo.snomed.service;

import java.util.List;

import com.ihtsdo.snomed.model.OntologyVersion;

public interface OntologyService {
    public abstract List<OntologyVersion> findAll();
}