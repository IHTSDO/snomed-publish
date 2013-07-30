package com.ihtsdo.snomed.browse.model;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Statement;

public class SparqlResults {
    private Set<String> variables = new HashSet<>();
    private Set<Map<String, Binding>> results = new HashSet<Map<String, Binding>>();
    
    public Set<String> getVariables() {
        return variables;
    }
    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }
    public Set<Map<String, Binding>> getResults() {
        return results;
    }
    public void setResults(Set<Map<String, Binding>> results) {
        this.results = results;
    }
    
    @Override
    public String toString(){
            return Objects.toStringHelper(this)
                    .add("variables", variables)
                    .add("results", results)
                    .toString();
    }
    
    public static class Binding{
        public URL source;
        public Object sourceBackedObject;
        
        public boolean isConcept(){
            return sourceBackedObject instanceof Concept;
        }
        public boolean isStatement(){
            return sourceBackedObject instanceof Statement;
        }
        public boolean isDescription(){
            return sourceBackedObject instanceof Description;
        }
        public boolean isDatatype(){
            if ((sourceBackedObject instanceof String) && 
                (!((String)sourceBackedObject).startsWith("http"))){
                return true;
            }
            return false;
        }
        
        
        public Binding(){}
        
        public Binding(URL source, Object sourceBackedObject){
            this.source = source;
            this.sourceBackedObject = sourceBackedObject;
        }
        
        @Override
        public String toString(){
            return Objects.toStringHelper(this)
                    .add("source", source)
                    .add("sourceBackedObject", sourceBackedObject)
                    .toString();
        }
        public URL getSource() {
            return source;
        }
        public void setSource(URL source) {
            this.source = source;
        }
        public Object getSourceBackedObject() {
            return sourceBackedObject;
        }
        public void setSourceBackedObject(Object sourceBackedObject) {
            this.sourceBackedObject = sourceBackedObject;
        }
        
        
    }
}
