package com.ihtsdo.snomed.service;

import java.io.IOException;

import javax.persistence.EntityManager;

import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.serialiser.OntologySerialiser;

public interface DiffAlgorithm {

    public void diff(Ontology base, Ontology compare, OntologySerialiser extrasSerialiser, 
            OntologySerialiser missingSerialiser, EntityManager em) throws IOException;
    
}
