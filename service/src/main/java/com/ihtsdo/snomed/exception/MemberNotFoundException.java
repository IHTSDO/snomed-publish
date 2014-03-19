package com.ihtsdo.snomed.exception;

public class MemberNotFoundException extends Exception {

    private static final long serialVersionUID = 9145348616866771322L;
    private Long id;
    private String publicId;
    private String refsetName;

    public MemberNotFoundException(Long id) {
        super();
        this.id = id;
    }
    
    public MemberNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }    
    
    public MemberNotFoundException(String publicId, String refsetName){
        super();
        this.publicId = publicId;
        this.refsetName = refsetName;
    }
    
    public String getPublicId(){
        return publicId;
    }

    public String getRefsetName(){
        return refsetName;
    }

    public Long getId() {
        return id;
    }

    
    
    
}
