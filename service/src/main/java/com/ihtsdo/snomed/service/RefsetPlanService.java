package com.ihtsdo.snomed.service;

import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.refset.RefsetPlan;

public interface RefsetPlanService {

    public abstract RefsetPlan findById(Long id);

    public abstract RefsetPlan update(RefsetPlanDto updated) throws  ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException;

    public abstract RefsetPlan create(RefsetPlanDto created) throws ValidationException;

    public abstract RefsetPlan delete(Long refsetId) throws RefsetPlanNotFoundException;

}