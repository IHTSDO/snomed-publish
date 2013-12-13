package com.ihtsdo.snomed.test;

import javax.inject.Named;

import com.ihtsdo.snomed.model.refset.BaseRule;
import com.ihtsdo.snomed.service.refset.RuleService;

@Named
public class DummyRefsetRuleService implements RuleService {

    public DummyRefsetRuleService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public BaseRule findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BaseRule delete(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    
}
