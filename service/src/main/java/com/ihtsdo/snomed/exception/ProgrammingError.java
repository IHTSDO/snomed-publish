package com.ihtsdo.snomed.exception;

public class ProgrammingError extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -6866591597008068731L;

    public ProgrammingError() {
        // TODO Auto-generated constructor stub
    }

    public ProgrammingError(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingError(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingError(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ProgrammingError(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

}
