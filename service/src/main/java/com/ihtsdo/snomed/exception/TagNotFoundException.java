package com.ihtsdo.snomed.exception;

public class TagNotFoundException extends Exception {

    private static final long serialVersionUID = 9145348616866771322L;
    private Long id;
    private String tagPublicId;
    private String refsetPublicId;

    public TagNotFoundException(Long id) {
        super();
        this.id = id;
    }
    
    public TagNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }    
    
    public TagNotFoundException(String refsetPublicId, String tagPublicId){
        super();
        this.tagPublicId = tagPublicId;
        this.refsetPublicId = refsetPublicId;
    }
    
    public String getTagPublicId(){
        return tagPublicId;
    }

    public String getRefsetPublicId(){
        return refsetPublicId;
    }

    public Long getId() {
        return id;
    }

    
    
}
