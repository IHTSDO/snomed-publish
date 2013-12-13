package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.PlanDto;


public class PlanValidationException extends ValidationException {
    private static final long serialVersionUID = -3620245059711682064L;
    
    protected PlanDto planDto;

    public PlanValidationException(PlanDto planDto) {
        super();
        this.planDto = planDto;
    }

    public PlanValidationException(PlanDto planDto, String message) {
        super(message);
        this.planDto = planDto;
    }

    public PlanValidationException(PlanDto planDto, Throwable cause) {
        super(cause);
        this.planDto = planDto;
    }

    public PlanValidationException(PlanDto planDto, String message, Throwable cause) {
        super(message, cause);
        this.planDto = planDto;
    }

    public PlanValidationException(PlanDto planDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.planDto = planDto;
    }
    
    public PlanDto getplanDto() {
        return planDto;
    }    
}
