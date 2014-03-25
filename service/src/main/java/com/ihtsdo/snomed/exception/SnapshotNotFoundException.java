package com.ihtsdo.snomed.exception;

public class SnapshotNotFoundException extends Exception {

    private static final long serialVersionUID = 9145348616866771322L;
    private Long id;
    private String snapshotPublicId;
    private String refsetPublicId;

    public SnapshotNotFoundException(Long id) {
        super();
        this.id = id;
    }
    
    public SnapshotNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }    
    
    public SnapshotNotFoundException(String refsetPublicId, String snapshotPublicId){
        super();
        this.snapshotPublicId = snapshotPublicId;
        this.refsetPublicId = refsetPublicId;
    }
    
    public String getSnapshotPublicId(){
        return snapshotPublicId;
    }

    public String getRefsetPublicId(){
        return refsetPublicId;
    }

    public Long getId() {
        return id;
    }

    
    
}
