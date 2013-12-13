package com.ihtsdo.snomed.exception.validation;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.RuleDto;

public class ConceptNotFoundValidationException extends RuleValidationException {
    private static final long serialVersionUID = 1081154695445634831L;
    private ConceptDto conceptDto;

    public ConceptNotFoundValidationException(RuleDto ruleDto, ConceptDto conceptDto) {
        super(ruleDto);
        this.conceptDto = conceptDto;
    }

    public ConceptNotFoundValidationException(RuleDto ruleDto, ConceptDto conceptDto, String message) {
        super(ruleDto, message);
        this.conceptDto = conceptDto;
    }

    public ConceptNotFoundValidationException(RuleDto ruleDto, ConceptDto conceptDto, Throwable cause) {
        super(ruleDto, cause);
        this.conceptDto = conceptDto;
    }

    public ConceptNotFoundValidationException(RuleDto ruleDto, ConceptDto conceptDto, String message, Throwable cause) {
        super(ruleDto, message, cause);
        this.conceptDto = conceptDto;
    }

    public ConceptNotFoundValidationException(RuleDto ruleDto, ConceptDto conceptDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(ruleDto, message, cause, enableSuppression, writableStackTrace);
        this.conceptDto = conceptDto;
    }

    public ConceptDto getConceptDto() {
        return conceptDto;
    }
    
    
    
}
