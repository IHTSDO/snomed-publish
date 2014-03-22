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
    private List<RefsetDtoShort> refsets = new ArrayList<>();
    
    public XmlRefsets(){}
    
    public XmlRefsets(List<RefsetDtoShort> refsets){
        this.refsets = refsets;
    }

    public List<RefsetDtoShort> getRefsets() {
        return refsets;
    }

    public void setRefsets(List<RefsetDtoShort> concepts) {
        this.refsets = concepts;
    }
}
