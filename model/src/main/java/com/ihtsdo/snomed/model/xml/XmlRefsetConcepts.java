package com.ihtsdo.snomed.model.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="refset")
public class XmlRefsetConcepts {

    @XmlElementWrapper(name = "concepts")
    @XmlElement(name="concept")
    private List<XmlRefsetConcept> concepts = new ArrayList<>();
    
    public XmlRefsetConcepts(){}
    
    public XmlRefsetConcepts(List<XmlRefsetConcept> concepts){
        this.concepts = concepts;
    }

    public List<XmlRefsetConcept> getConcepts() {
        return concepts;
    }

    public void setConcepts(List<XmlRefsetConcept> concepts) {
        this.concepts = concepts;
    }
    
    
}
