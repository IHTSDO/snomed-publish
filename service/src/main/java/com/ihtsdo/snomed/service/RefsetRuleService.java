package com.ihtsdo.snomed.service;

import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;

/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
public interface RefsetRuleService {
    public abstract BaseRefsetRule findById(Long id);

    public abstract BaseRefsetRule create(RefsetRuleDto created);
    
    public abstract BaseRefsetRule update(RefsetRuleDto updated) throws RefsetRuleNotFoundException;

    public abstract BaseRefsetRule delete(Long refsetId) throws RefsetRuleNotFoundException;

}