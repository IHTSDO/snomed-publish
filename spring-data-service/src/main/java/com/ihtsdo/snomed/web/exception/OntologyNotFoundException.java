package com.ihtsdo.snomed.web.exception;

public class OntologyNotFoundException extends Exception{
    private static final long serialVersionUID = 1L;
    private long descriptionId;
    private long ontologyId;
    
    public OntologyNotFoundException(long descriptionId, long ontologyId){
        this.descriptionId = descriptionId;
        this.ontologyId = ontologyId;
    }

    public long getDescriptionId() {
        return descriptionId;
    }

    public long getOntologyId() {
        return ontologyId;
    }
} 