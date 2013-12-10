package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;


public class MoreThanOneCandidateForTerminalException extends PlanValidationException {

    private static final long serialVersionUID = 4350521038721708405L;


    public MoreThanOneCandidateForTerminalException(RefsetPlanDto planDto) {
        super(planDto);
    }

    public MoreThanOneCandidateForTerminalException(RefsetPlanDto planDto, String message) {
        super(planDto, message);
    }

    public MoreThanOneCandidateForTerminalException(RefsetPlanDto planDto, Throwable cause) {
        super(planDto, cause);
    }

    public MoreThanOneCandidateForTerminalException(RefsetPlanDto planDto, String message, Throwable cause) {
        super(planDto, message, cause);
    }

    public MoreThanOneCandidateForTerminalException(RefsetPlanDto planDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(planDto, message, cause, enableSuppression, writableStackTrace);
    }

}
