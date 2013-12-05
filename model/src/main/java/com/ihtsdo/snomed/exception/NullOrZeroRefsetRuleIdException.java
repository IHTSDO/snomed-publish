package com.ihtsdo.snomed.exception;


public class NullOrZeroRefsetRuleIdException extends Exception {
    private static final long serialVersionUID = -3620245059711682064L;
    
    private Long id;

    public NullOrZeroRefsetRuleIdException(Long id) {
        super();
        this.id = id;
    }

    public NullOrZeroRefsetRuleIdException(Long id, String message) {
        super(message);
        this.id = id;
    }

    public NullOrZeroRefsetRuleIdException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public NullOrZeroRefsetRuleIdException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public NullOrZeroRefsetRuleIdException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }
    
    

}
