package com.ihtsdo.snomed.service;

import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;

public interface RefsetRuleService {
    public abstract BaseRefsetRule findById(Long id);
    public abstract BaseRefsetRule delete(Long refsetPlanId) throws RefsetTerminalRuleNotFoundException;
    


}