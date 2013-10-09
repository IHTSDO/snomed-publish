package com.ihtsdo.snomed.exception;

public class RefsetRuleNotFoundException extends Exception {

    private static final long serialVersionUID = 9145348616866771322L;

    private Long id;
    
    public RefsetRuleNotFoundException(Long id) {
        super();
        this.id = id;
    }

    public RefsetRuleNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }

    public RefsetRuleNotFoundException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public RefsetRuleNotFoundException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public RefsetRuleNotFoundException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }
}
