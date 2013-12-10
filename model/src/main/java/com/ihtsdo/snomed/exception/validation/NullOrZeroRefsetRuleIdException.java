package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;


public class NullOrZeroRefsetRuleIdException extends RuleValidationException {
    private static final long serialVersionUID = -3620245059711682064L;

    public NullOrZeroRefsetRuleIdException(RefsetRuleDto ruleDto) {
        super(ruleDto);
    }

    public NullOrZeroRefsetRuleIdException(RefsetRuleDto ruleDto, String message) {
        super(ruleDto, message);
    }

    public NullOrZeroRefsetRuleIdException(RefsetRuleDto ruleDto, Throwable cause) {
        super(ruleDto, cause);
    }

    public NullOrZeroRefsetRuleIdException(RefsetRuleDto ruleDto, String message, Throwable cause) {
        super(ruleDto, message, cause);
    }

    public NullOrZeroRefsetRuleIdException(RefsetRuleDto ruleDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(ruleDto, message, cause, enableSuppression, writableStackTrace);
    }
    

}
