package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RuleDto;


public class UnconnectedRefsetRuleException extends RuleValidationException {
    private static final long serialVersionUID = -3620245059711682064L;

    public UnconnectedRefsetRuleException(RuleDto ruleDto) {
        super(ruleDto);
    }

    public UnconnectedRefsetRuleException(RuleDto ruleDto, String message) {
        super(ruleDto, message);
    }

    public UnconnectedRefsetRuleException(RuleDto ruleDto, Throwable cause) {
        super(ruleDto, cause);
    }

    public UnconnectedRefsetRuleException(RuleDto ruleDto, String message, Throwable cause) {
        super(ruleDto, message, cause);
    }

    public UnconnectedRefsetRuleException(RuleDto ruleDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(ruleDto, message, cause, enableSuppression, writableStackTrace);
    }

}
