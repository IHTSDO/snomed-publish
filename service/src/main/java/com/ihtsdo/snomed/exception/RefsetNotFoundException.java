package com.ihtsdo.snomed.exception;

public class RefsetNotFoundException extends Exception {

    private static final long serialVersionUID = 9145348616866771322L;
    private Long id;
    private String publicId;

    public RefsetNotFoundException(Long id) {
        super();
        this.id = id;
    }
    
    public RefsetNotFoundException(String publicId){
        super();
        this.publicId = publicId;
    }

    public RefsetNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }
    
    public RefsetNotFoundException(String publicId, String message) {
        super(message);
        this.publicId = publicId;
    }
    
    public String getPublicId(){
        return publicId;
    }


    public RefsetNotFoundException(Throwable cause) {
        super(cause);
    }

    public RefsetNotFoundException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public RefsetNotFoundException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }

    
    
}
