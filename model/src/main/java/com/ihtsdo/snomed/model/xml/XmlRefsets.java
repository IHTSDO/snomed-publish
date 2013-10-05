package com.ihtsdo.snomed.model.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="refsets")
public class XmlRefsets {

    @XmlElementWrapper(name = "refsets")
    @XmlElement(name="refset")
    private List<XmlRefset> refsets = new ArrayList<>();
    
    public XmlRefsets(){}
    
    public XmlRefsets(List<XmlRefset> refsets){
        this.refsets = refsets;
    }

    public List<XmlRefset> getRefsets() {
        return refsets;
    }

    public void setRefsets(List<XmlRefset> concepts) {
        this.refsets = concepts;
    }
}
