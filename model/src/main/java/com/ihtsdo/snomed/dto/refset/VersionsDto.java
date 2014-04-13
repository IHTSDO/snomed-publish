package com.ihtsdo.snomed.dto.refset;

import java.util.List;

public class VersionsDto {

	private List<SnapshotDtoShort> versions;
	
	private long totalSize;

    public VersionsDto(){}
    
    public VersionsDto(List<SnapshotDtoShort> versions){
    	this.versions = versions;
    }
    
    public VersionsDto(List<SnapshotDtoShort> versions, long totalSize){
        this.versions = versions;
        this.totalSize = totalSize;
    }
    
    
	public List<SnapshotDtoShort> getVersions() {
		return versions;
	}

	public void setVersions(List<SnapshotDtoShort> versions) {
		this.versions = versions;
	}
	
    public long getTotalSize(){
        return totalSize;
    }	
	

}
