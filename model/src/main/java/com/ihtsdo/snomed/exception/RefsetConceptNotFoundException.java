package com.ihtsdo.snomed.exception;

import com.ihtsdo.snomed.dto.refset.ConceptDto;


public class RefsetConceptNotFoundException extends Exception {
    private static final long serialVersionUID = 1081154695445634831L;
    
    private ConceptDto conceptDto;
    
    public RefsetConceptNotFoundException() {
        super();
    }

    public RefsetConceptNotFoundException(ConceptDto conceptDto, String message) {
        super(message);
        this.conceptDto = conceptDto;
    }

    public RefsetConceptNotFoundException(ConceptDto conceptDto, Throwable cause) {
        super(cause);
        this.conceptDto = conceptDto;
    }

    public RefsetConceptNotFoundException(ConceptDto conceptDto, String message, Throwable cause) {
        super(message, cause);
        this.conceptDto = conceptDto;
    }

    public RefsetConceptNotFoundException(ConceptDto conceptDto, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.conceptDto = conceptDto;
    }
    

    public ConceptDto getConceptDto() {
        return conceptDto;
    }
        
}
