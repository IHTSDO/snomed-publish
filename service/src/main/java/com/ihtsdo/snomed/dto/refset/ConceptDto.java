package com.ihtsdo.snomed.dto.refset;

public class ConceptDto {

    public ConceptDto(){}
    
    public ConceptDto(long id){
        this.id = id;
    }
    
    private Long id;
    private String displayName;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    
}
