package com.ihtsdo.snomed.web.exception;

public class DescriptionNotFoundException extends Exception{
    private static final long serialVersionUID = 1L;
    private long descriptionId;
    private long ontologyId;
    
    public DescriptionNotFoundException(long descriptionId, long ontologyId){
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