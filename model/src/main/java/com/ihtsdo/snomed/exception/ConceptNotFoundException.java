package com.ihtsdo.snomed.exception;


public class ConceptNotFoundException extends Exception{
    private static final long serialVersionUID = 1L;
    private long conceptId;
    private long ontologyId;
    
    public ConceptNotFoundException(long conceptId, long ontologyId){
        this.conceptId = conceptId;
        this.ontologyId = ontologyId;
    }

    public long getConceptId() {
        return conceptId;
    }

    public long getOntologyId() {
        return ontologyId;
    }
}