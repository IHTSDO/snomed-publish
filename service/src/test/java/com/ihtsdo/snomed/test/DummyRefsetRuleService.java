package com.ihtsdo.snomed.test;

import javax.inject.Named;

import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.service.RefsetRuleService;

@Named
public class DummyRefsetRuleService implements RefsetRuleService {

    public DummyRefsetRuleService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public BaseRefsetRule findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BaseRefsetRule delete(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    
}
