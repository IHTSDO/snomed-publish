package com.ihtsdo.snomed.service;

import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.UnconnectedRefsetRuleException;
import com.ihtsdo.snomed.model.refset.RefsetPlan;

/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
public interface RefsetPlanService {

    public abstract RefsetPlan findById(Long id);

    public abstract RefsetPlan update(RefsetPlanDto updated) throws RefsetPlanNotFoundException, UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException, ConceptNotFoundException;

    public abstract RefsetPlan create(RefsetPlanDto created) throws UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException, ConceptNotFoundException;

    public abstract RefsetPlan delete(Long refsetId) throws RefsetPlanNotFoundException;
}