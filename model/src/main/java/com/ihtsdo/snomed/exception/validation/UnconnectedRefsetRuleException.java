package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;


public class UnconnectedRefsetRuleException extends RuleValidationException {
    private static final long serialVersionUID = -3620245059711682064L;

    public UnconnectedRefsetRuleException(RefsetRuleDto ruleDto) {
        super(ruleDto);
    }

    public UnconnectedRefsetRuleException(RefsetRuleDto ruleDto, String message) {
        super(ruleDto, message);
    }

    public UnconnectedRefsetRuleException(RefsetRuleDto ruleDto, Throwable cause) {
        super(ruleDto, cause);
    }

    public UnconnectedRefsetRuleException(RefsetRuleDto ruleDto, String message, Throwable cause) {
        super(ruleDto, message, cause);
    }

    public UnconnectedRefsetRuleException(RefsetRuleDto ruleDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(ruleDto, message, cause, enableSuppression, writableStackTrace);
    }

}
