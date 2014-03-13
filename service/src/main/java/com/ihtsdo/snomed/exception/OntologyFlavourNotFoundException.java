package com.ihtsdo.snomed.exception;


public class OntologyFlavourNotFoundException extends Exception{

    private static final long serialVersionUID = -8503497279533972065L;
        
    private String flavourPublicIdString; 

    public OntologyFlavourNotFoundException(String flavourPublicIdString) {
        this.flavourPublicIdString = flavourPublicIdString;
    }

    public OntologyFlavourNotFoundException(String flavourPublicIdString, String message) {
        super(message);
        this.flavourPublicIdString = flavourPublicIdString;
    }

    public OntologyFlavourNotFoundException(String flavourPublicIdString, Throwable cause) {
        super(cause);
        this.flavourPublicIdString = flavourPublicIdString;
    }

    public OntologyFlavourNotFoundException(String flavourPublicIdString, String message, Throwable cause) {
        super(message, cause);
        this.flavourPublicIdString = flavourPublicIdString;
    }

    public OntologyFlavourNotFoundException(String flavourPublicIdString, String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.flavourPublicIdString = flavourPublicIdString;
    }

    public String getFlavourPublicIdString() {
        return flavourPublicIdString;
    }    
}
