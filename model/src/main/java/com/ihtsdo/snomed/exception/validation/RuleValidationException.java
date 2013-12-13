package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RuleDto;


public class RuleValidationException extends ValidationException {
    private static final long serialVersionUID = -3620245059711682064L;
    
    protected RuleDto ruleDto;

    public RuleValidationException(RuleDto ruleDto) {
        super();
        this.ruleDto = ruleDto;
    }

    public RuleValidationException(RuleDto ruleDto, String message) {
        super(message);
        this.ruleDto = ruleDto;
    }

    public RuleValidationException(RuleDto ruleDto, Throwable cause) {
        super(cause);
        this.ruleDto = ruleDto;
    }

    public RuleValidationException(RuleDto ruleDto, String message, Throwable cause) {
        super(message, cause);
        this.ruleDto = ruleDto;
    }

    public RuleValidationException(RuleDto ruleDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.ruleDto = ruleDto;
    }
    
    public RuleDto getRuleDto() {
        return ruleDto;
    }    
}
