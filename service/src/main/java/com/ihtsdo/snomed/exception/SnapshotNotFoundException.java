package com.ihtsdo.snomed.exception;

public class SnapshotNotFoundException extends Exception {

    private static final long serialVersionUID = 9145348616866771322L;
    private Long id;
    private String publicId;

    public SnapshotNotFoundException(Long id) {
        super();
        this.id = id;
    }
    
    public SnapshotNotFoundException(String publicId){
        super();
        this.publicId = publicId;
    }

    public SnapshotNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }
    
    public SnapshotNotFoundException(String publicId, String message) {
        super(message);
        this.publicId = publicId;
    }
    
    public String getPublicId(){
        return publicId;
    }


    public SnapshotNotFoundException(Throwable cause) {
        super(cause);
    }

    public SnapshotNotFoundException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public SnapshotNotFoundException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }

    
    
}
