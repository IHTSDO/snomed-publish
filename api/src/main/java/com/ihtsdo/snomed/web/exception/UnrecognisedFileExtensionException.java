package com.ihtsdo.snomed.web.exception;

public class UnrecognisedFileExtensionException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private String extension;
    
    public UnrecognisedFileExtensionException(String extension) {
        this.extension = extension;
    }

    public UnrecognisedFileExtensionException(String extension, String message) {
        super(message);
        this.extension = extension;
    }

    public UnrecognisedFileExtensionException(String extension, Throwable cause) {
        super(cause);
        this.extension = extension;
    }

    public UnrecognisedFileExtensionException(String extension, String message, Throwable cause) {
        super(message, cause);
        this.extension = extension;
    }

    public UnrecognisedFileExtensionException(String extension, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
