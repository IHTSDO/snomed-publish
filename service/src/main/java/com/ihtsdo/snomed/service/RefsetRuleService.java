package com.ihtsdo.snomed.service;

import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotPersistedException;
import com.ihtsdo.snomed.model.refset.RefsetRule;

/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
public interface RefsetRuleService {
    public abstract RefsetRule findById(Long id);

    public abstract RefsetRule update(RefsetRuleDto updated) throws RefsetRuleNotFoundException, RefsetRuleNotPersistedException;

    public abstract RefsetRule create(RefsetRuleDto created) throws RefsetRuleNotPersistedException;

    public abstract RefsetRule delete(Long refsetId) throws RefsetRuleNotFoundException;

}