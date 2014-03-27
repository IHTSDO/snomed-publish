package com.ihtsdo.snomed.dto.refset;

import java.util.List;

import com.ihtsdo.snomed.model.xml.RefsetDtoShort;

public class RefsetsDto {

	private List<RefsetDtoShort> refsets;

    public RefsetsDto(){}
    
    public RefsetsDto(List<RefsetDtoShort> refsets){
    	this.refsets = refsets;
    }
    
	public List<RefsetDtoShort> getRefsets() {
		return refsets;
	}

	public void setMembers(List<RefsetDtoShort> refsets) {
		this.refsets = refsets;
	}
	
	

}
