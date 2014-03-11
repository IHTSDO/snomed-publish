package com.ihtsdo.snomed.exception;

public class InvalidSnomedDateFormatException extends Exception {

    private static final long serialVersionUID = -8503497279533972065L;
    
    private String dateString;

    public InvalidSnomedDateFormatException(String dateString) {
        this.dateString = dateString;
    }

    public InvalidSnomedDateFormatException(String dateString, String message) {
        super(message);
        this.dateString = dateString;
    }

    public InvalidSnomedDateFormatException(String dateString, Throwable cause) {
        super(cause);
        this.dateString = dateString;
    }

    public InvalidSnomedDateFormatException(String dateString, String message, Throwable cause) {
        super(message, cause);
        this.dateString = dateString;
    }

    public InvalidSnomedDateFormatException(String dateString, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.dateString = dateString;
    }
    
    public String getDateString(){
        return dateString;
    }

}
