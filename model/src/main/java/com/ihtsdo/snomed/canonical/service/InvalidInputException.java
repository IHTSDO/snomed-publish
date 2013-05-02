package com.ihtsdo.snomed.canonical.service;

public class InvalidInputException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 6577629608629244977L;

    public InvalidInputException() {
        // TODO Auto-generated constructor stub
    }

    public InvalidInputException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public InvalidInputException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

}
