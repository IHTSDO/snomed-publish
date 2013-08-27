package com.ihtsdo.snomed.browse.exception;

import com.ihtsdo.snomed.service.InvalidInputException;

public class NonUniquePublicIdException extends InvalidInputException {

    public NonUniquePublicIdException() {
        // TODO Auto-generated constructor stub
    }

    public NonUniquePublicIdException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public NonUniquePublicIdException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public NonUniquePublicIdException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public NonUniquePublicIdException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

}
