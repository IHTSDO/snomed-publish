package com.ihtsdo.snomed.browse.service;

import com.ihtsdo.snomed.service.ProgrammingException;

public class UnexpectedSparqlResultException extends ProgrammingException {

    private static final long serialVersionUID = 4020539583906058812L;

    public UnexpectedSparqlResultException() {
        // TODO Auto-generated constructor stub
    }

    public UnexpectedSparqlResultException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public UnexpectedSparqlResultException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public UnexpectedSparqlResultException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public UnexpectedSparqlResultException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

}
