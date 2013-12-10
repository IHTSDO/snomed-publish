package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;

public class RefsetRuleNotFoundValidationException extends RuleValidationException {

    private static final long serialVersionUID = 9145348616866771322L;

    public RefsetRuleNotFoundValidationException(RefsetRuleDto ruleDto) {
        super(ruleDto);
    }

    public RefsetRuleNotFoundValidationException(RefsetRuleDto ruleDto, String message) {
        super(ruleDto, message);
    }

    public RefsetRuleNotFoundValidationException(RefsetRuleDto ruleDto, Throwable cause) {
        super(ruleDto, cause);
    }

    public RefsetRuleNotFoundValidationException(RefsetRuleDto ruleDto, String message, Throwable cause) {
        super(ruleDto, message, cause);
    }

    public RefsetRuleNotFoundValidationException(RefsetRuleDto ruleDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(ruleDto, message, cause, enableSuppression, writableStackTrace);
    }
    
}
