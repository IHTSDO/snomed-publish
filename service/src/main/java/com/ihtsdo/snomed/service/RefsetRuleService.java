package com.ihtsdo.snomed.service;

import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;

public interface RefsetRuleService {
    public abstract BaseRefsetRule findById(Long id);

    public abstract BaseRefsetRule create(RefsetRuleDto created) throws ConceptNotFoundException;
    
    public abstract BaseRefsetRule update(RefsetRuleDto updated) throws RefsetRuleNotFoundException, ConceptNotFoundException;

    public abstract BaseRefsetRule delete(Long refsetId) throws RefsetRuleNotFoundException;

}