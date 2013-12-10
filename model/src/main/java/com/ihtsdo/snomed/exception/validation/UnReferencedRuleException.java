package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;


public class UnReferencedRuleException extends RuleValidationException {

    private static final long serialVersionUID = -4576515398257471702L;

    public UnReferencedRuleException(RefsetRuleDto ruleDto) {
        super(ruleDto);
    }

    public UnReferencedRuleException(RefsetRuleDto ruleDto, String message) {
        super(ruleDto, message);
    }

    public UnReferencedRuleException(RefsetRuleDto ruleDto, Throwable cause) {
        super(ruleDto, cause);
    }

    public UnReferencedRuleException(RefsetRuleDto ruleDto, String message, Throwable cause) {
        super(ruleDto, message, cause);
    }

    public UnReferencedRuleException(RefsetRuleDto ruleDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(ruleDto, message, cause, enableSuppression, writableStackTrace);
    }    

}
