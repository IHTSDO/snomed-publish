package com.ihtsdo.snomed.dto.refset;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//Yeah, this sucks BIG TIME. Json is not returning wrapping element specified by xmlrootelement.

@XmlRootElement(name="whocares")
public class RefsetDtoWrapper {

    @XmlElement(name="refset")
    private RefsetDto refset;
    
    public RefsetDtoWrapper(){};
    public RefsetDtoWrapper(RefsetDto dto){
        this.refset = dto;
    }
    public RefsetDto getRefset() {
        return refset;
    }
    public void setRefset(RefsetDto refset) {
        this.refset = refset;
    };
    
    

}
