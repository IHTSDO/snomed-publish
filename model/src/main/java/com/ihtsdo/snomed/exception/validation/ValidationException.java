package com.ihtsdo.snomed.exception.validation;

public class ValidationException extends Exception {
    private static final long serialVersionUID = -2905944342726908601L;

    public ValidationException() {}

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
