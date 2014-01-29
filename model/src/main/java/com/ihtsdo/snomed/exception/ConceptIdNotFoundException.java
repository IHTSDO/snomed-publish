package com.ihtsdo.snomed.exception;

public class ConceptIdNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private Long id;
    
    public ConceptIdNotFoundException(Long id) {
        this.id = id;
    }
    
    public ConceptIdNotFoundException(String id) {
    	if (id == null){
    		return;
    	}
        this.id = new Long(id);
    }    

    public ConceptIdNotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }
    
    public ConceptIdNotFoundException(String id, String message) {
        super(message);
        if (id == null) return;
        this.id = new Long(id);
    }    

    public ConceptIdNotFoundException(Long id, Throwable cause) {
        super(cause);
        this.id = id;
    }

    public ConceptIdNotFoundException(Long id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }

    

	public long getId(){return id;}
    
}
