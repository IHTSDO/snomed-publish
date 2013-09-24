package com.ihtsdo.snomed.test;

import javax.inject.Named;

import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotPersistedException;
import com.ihtsdo.snomed.model.refset.RefsetRule;
import com.ihtsdo.snomed.service.RefsetRuleService;

@Named
public class DummyRefsetRuleService implements RefsetRuleService {

    public DummyRefsetRuleService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public RefsetRule findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RefsetRule update(RefsetRuleDto updated)
            throws RefsetRuleNotFoundException, RefsetRuleNotPersistedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RefsetRule create(RefsetRuleDto created)
            throws RefsetRuleNotPersistedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RefsetRule delete(Long refsetId) throws RefsetRuleNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}
