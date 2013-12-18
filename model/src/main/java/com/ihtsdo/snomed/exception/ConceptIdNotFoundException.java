package com.ihtsdo.snomed.exception;

public class ConceptIdNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private long id;
    
    public ConceptIdNotFoundException(long id) {
        this.id = id;
    }

    public ConceptIdNotFoundException(long id, String message) {
        super(message);
        this.id = id;
    }

    public ConceptIdNotFoundException(long id, Throwable cause) {
        super(cause);
        this.id = id;
    }

    public ConceptIdNotFoundException(long id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }

    public long getId(){return id;}
    
}
