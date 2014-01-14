package com.ihtsdo.snomed.service.refset;

import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.refset.Plan;
import com.ihtsdo.snomed.model.refset.Rule;

public interface PlanService {

    public abstract Plan findById(Long id);

    public abstract Plan update(PlanDto updated) throws  ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException;

    public abstract Plan create(PlanDto created) throws ValidationException;

    public abstract Plan delete(Long refsetId) throws RefsetPlanNotFoundException;

    public abstract Rule createRules(PlanDto planDto) throws ValidationException;

}