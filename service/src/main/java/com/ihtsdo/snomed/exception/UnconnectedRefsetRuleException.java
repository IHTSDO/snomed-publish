package com.ihtsdo.snomed.exception;


public class UnconnectedRefsetRuleException extends Exception {
    private static final long serialVersionUID = -3620245059711682064L;
    
    private Long id;

    public UnconnectedRefsetRuleException(Long id) {
        super();
        this.id = id;
    }

    public UnconnectedRefsetRuleException(Long id, String message) {
        super(message);
        this.id = id;
    }

    public UnconnectedRefsetRuleException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public UnconnectedRefsetRuleException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public UnconnectedRefsetRuleException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }
    
    

}
