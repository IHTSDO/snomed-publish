package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RuleDto;

public class RefsetRuleNotFoundValidationException extends RuleValidationException {

    private static final long serialVersionUID = 9145348616866771322L;

    public RefsetRuleNotFoundValidationException(RuleDto ruleDto) {
        super(ruleDto);
    }

    public RefsetRuleNotFoundValidationException(RuleDto ruleDto, String message) {
        super(ruleDto, message);
    }

    public RefsetRuleNotFoundValidationException(RuleDto ruleDto, Throwable cause) {
        super(ruleDto, cause);
    }

    public RefsetRuleNotFoundValidationException(RuleDto ruleDto, String message, Throwable cause) {
        super(ruleDto, message, cause);
    }

    public RefsetRuleNotFoundValidationException(RuleDto ruleDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(ruleDto, message, cause, enableSuppression, writableStackTrace);
    }
    
}
