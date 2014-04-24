package com.ihtsdo.snomed.dto.refset;

import java.util.List;

import com.ihtsdo.snomed.model.xml.RefsetDtoShort;

public class RefsetsDto {
	private List<RefsetDtoShort> refsets;
    private long totalSize;
	
    public RefsetsDto(){}
    
    public RefsetsDto(List<RefsetDtoShort> refsets, long totalSize){
    	this.refsets = refsets;
    	this.totalSize = totalSize;
    }
    
	public List<RefsetDtoShort> getRefsets() {
		return refsets;
	}

	public void setMembers(List<RefsetDtoShort> refsets) {
		this.refsets = refsets;
	}

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
