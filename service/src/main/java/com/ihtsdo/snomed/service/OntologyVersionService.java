package com.ihtsdo.snomed.service;

import java.sql.Date;
import java.util.List;

import com.ihtsdo.snomed.model.OntologyVersion;

public interface OntologyVersionService {
    public abstract List<OntologyVersion> findAll();
    public abstract OntologyVersion findByFlavourAndTaggedOn(String flavourPublicId, Date taggedOn);
    
}