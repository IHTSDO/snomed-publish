package com.ihtsdo.snomed.service.refset;

import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.model.refset.BaseRule;

public interface RuleService {
    public abstract BaseRule findById(Long id);
    public abstract BaseRule delete(Long refsetPlanId) throws RefsetTerminalRuleNotFoundException;
    


}