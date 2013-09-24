package com.ihtsdo.snomed.test;

import java.util.List;

import javax.inject.Named;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.service.ConceptService;

@Named
public class DummyConceptService implements ConceptService {

    public DummyConceptService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<Concept> findAll(int pageIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Concept> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Concept findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Concept findBySerialisedId(Long serialisedId) {
        // TODO Auto-generated method stub
        return null;
    }

}
