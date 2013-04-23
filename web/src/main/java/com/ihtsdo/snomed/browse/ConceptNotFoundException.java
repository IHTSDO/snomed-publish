package com.ihtsdo.snomed.browse;

class ConceptNotFoundException extends Exception{
    private static final long serialVersionUID = 1L;
    private long conceptId;
    private String ontologyName;
    
    public ConceptNotFoundException(long conceptId, String ontologyName){
        this.conceptId = conceptId;
        this.ontologyName = ontologyName;
    }

    public long getConceptId() {
        return conceptId;
    }

    public String getOntologyName() {
        return ontologyName;
    }
}