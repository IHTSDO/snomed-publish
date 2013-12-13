package com.ihtsdo.snomed.web.service;

public class SimpleConceptNotFoundException extends Exception {

    private static final long serialVersionUID = 5144653550549364131L;
    
    private Long conceptId;
    private Long ontologyId;
    
    public SimpleConceptNotFoundException(Long conceptId, Long ontologyId) {
        this.conceptId = conceptId;
        this.ontologyId = ontologyId;
    }

    
    
    public Long getConceptId() {
        return conceptId;
    }



    public Long getOntologyId() {
        return ontologyId;
    }



    public SimpleConceptNotFoundException() {
        // TODO Auto-generated constructor stub
    }

    public SimpleConceptNotFoundException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public SimpleConceptNotFoundException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public SimpleConceptNotFoundException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public SimpleConceptNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }


}
