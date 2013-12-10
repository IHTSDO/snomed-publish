package com.ihtsdo.snomed.exception;

public class ConceptsCacheNotBuiltException extends Exception {

    private static final long serialVersionUID = 7956436192594842741L;

    public ConceptsCacheNotBuiltException() {
        super();
    }

    public ConceptsCacheNotBuiltException(String message) {
        super(message);
    }

    public ConceptsCacheNotBuiltException(Throwable cause) {
        super(cause);
    }

    public ConceptsCacheNotBuiltException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConceptsCacheNotBuiltException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
