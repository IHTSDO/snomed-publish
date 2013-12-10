package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;


public class RuleValidationException extends ValidationException {
    private static final long serialVersionUID = -3620245059711682064L;
    
    protected RefsetRuleDto ruleDto;

    public RuleValidationException(RefsetRuleDto ruleDto) {
        super();
        this.ruleDto = ruleDto;
    }

    public RuleValidationException(RefsetRuleDto ruleDto, String message) {
        super(message);
        this.ruleDto = ruleDto;
    }

    public RuleValidationException(RefsetRuleDto ruleDto, Throwable cause) {
        super(cause);
        this.ruleDto = ruleDto;
    }

    public RuleValidationException(RefsetRuleDto ruleDto, String message, Throwable cause) {
        super(message, cause);
        this.ruleDto = ruleDto;
    }

    public RuleValidationException(RefsetRuleDto ruleDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.ruleDto = ruleDto;
    }
    
    public RefsetRuleDto getRuleDto() {
        return ruleDto;
    }    
}
