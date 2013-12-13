package com.ihtsdo.snomed.exception;


public class StatementNotFoundException extends Exception{
    private static final long serialVersionUID = 1L;
    private long statementId;
    private long ontologyId;
    
    public StatementNotFoundException(long statementId, long ontologyId){
        this.statementId = statementId;
        this.ontologyId = ontologyId;
    }

    public long getDescriptionId() {
        return statementId;
    }

    public long getOntologyId() {
        return ontologyId;
    }
}     
