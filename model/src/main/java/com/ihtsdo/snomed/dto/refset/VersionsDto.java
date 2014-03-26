package com.ihtsdo.snomed.dto.refset;

import java.util.List;

public class VersionsDto {

	private List<SnapshotDtoShort> versions;

    public VersionsDto(){}
    
    public VersionsDto(List<SnapshotDtoShort> versions){
    	this.versions = versions;
    }
    
	public List<SnapshotDtoShort> getVersions() {
		return versions;
	}

	public void setVersions(List<SnapshotDtoShort> versions) {
		this.versions = versions;
	}
	
	

}
