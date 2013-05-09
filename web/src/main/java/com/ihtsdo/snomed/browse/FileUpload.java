package com.ihtsdo.snomed.browse;

import org.springframework.web.multipart.MultipartFile;

public class FileUpload{
 
    MultipartFile concepts;
    MultipartFile relationships;
    String name;
    
    public MultipartFile getConcepts() {
        return concepts;
    }
    public void setConcepts(MultipartFile concepts) {
        this.concepts = concepts;
    }
    public MultipartFile getRelationships() {
        return relationships;
    }
    public void setRelationships(MultipartFile relationships) {
        this.relationships = relationships;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    
}
