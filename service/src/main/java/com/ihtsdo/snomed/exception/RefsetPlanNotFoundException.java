package com.ihtsdo.snomed.exception;

public class RefsetPlanNotFoundException extends Exception {

    private static final long serialVersionUID = -6850463390093856124L;
    
    Long id;

    public RefsetPlanNotFoundException(Long id) {
        super();
        this.id = id;
    }

    public RefsetPlanNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }

    public RefsetPlanNotFoundException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public RefsetPlanNotFoundException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public RefsetPlanNotFoundException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }
    
    

}
