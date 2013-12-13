package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RuleDto;

public class UnrecognisedRefsetRuleTypeException extends RuleValidationException {

    private static final long serialVersionUID = 4350521038721708405L;

    public UnrecognisedRefsetRuleTypeException(RuleDto ruleDto) {
        super(ruleDto);
    }

    public UnrecognisedRefsetRuleTypeException(RuleDto ruleDto, String message) {
        super(ruleDto, message);
    }

    public UnrecognisedRefsetRuleTypeException(RuleDto ruleDto, Throwable cause) {
        super(ruleDto, cause);
    }

    public UnrecognisedRefsetRuleTypeException(RuleDto ruleDto, String message, Throwable cause) {
        super(ruleDto, message, cause);
    }

    public UnrecognisedRefsetRuleTypeException(RuleDto ruleDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(ruleDto, message, cause, enableSuppression, writableStackTrace);
    }
}
