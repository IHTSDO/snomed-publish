package com.ihtsdo.snomed.exception;

public class ConceptNotFoundException extends Exception {
    private static final long serialVersionUID = 1081154695445634831L;

    private Long id;
    
    public ConceptNotFoundException(Long id) {
        super();
        this.id = id;
    }

    public ConceptNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }

    public ConceptNotFoundException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public ConceptNotFoundException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ConceptNotFoundException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }
    
    

}
