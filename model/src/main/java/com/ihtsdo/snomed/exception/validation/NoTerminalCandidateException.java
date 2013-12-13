package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.PlanDto;

public class NoTerminalCandidateException extends PlanValidationException {

    private static final long serialVersionUID = 4350521038721708405L;


    public NoTerminalCandidateException(PlanDto planDto) {
        super(planDto);
    }

    public NoTerminalCandidateException(PlanDto planDto, String message) {
        super(planDto, message);
    }

    public NoTerminalCandidateException(PlanDto planDto, Throwable cause) {
        super(planDto, cause);
    }

    public NoTerminalCandidateException(PlanDto planDto, String message, Throwable cause) {
        super(planDto, message, cause);
    }

    public NoTerminalCandidateException(PlanDto planDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(planDto, message, cause, enableSuppression, writableStackTrace);
    }

}
