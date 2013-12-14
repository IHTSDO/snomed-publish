package com.ihtsdo.snomed.service;

import java.util.List;

import com.ihtsdo.snomed.model.Statement;

public interface StatementService {

    public abstract List<Statement> findAll(int pageIndex);

    public abstract List<Statement> findAll();

    public abstract Statement findById(Long id);

    public abstract Statement findBySerialisedId(Long serialisedId);
}