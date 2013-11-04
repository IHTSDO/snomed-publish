package com.ihtsdo.snomed.service;

import java.io.IOException;
import java.text.ParseException;

import javax.persistence.EntityManager;

import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiser;

public interface DiffAlgorithm {

    public void diff(Ontology base, Ontology compare, SnomedSerialiser extrasSerialiser, 
            SnomedSerialiser missingSerialiser, EntityManager em) throws IOException, ParseException;
    
}
