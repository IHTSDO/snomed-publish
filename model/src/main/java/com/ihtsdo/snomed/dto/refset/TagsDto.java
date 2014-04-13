package com.ihtsdo.snomed.dto.refset;

import java.util.List;

public class TagsDto {

	private List<TagDto> tags;
	
	private long totalSize;

    public TagsDto(){}
    
    public TagsDto(List<TagDto> tags, long totalSize){
    	this.tags = tags;
    	this.totalSize = totalSize;
    }
    
	public List<TagDto> getTags() {
		return tags;
	}

	public void setTags(List<TagDto> tags) {
		this.tags = tags;
	}
	
    public long getTotalSize(){
        return totalSize;
    }   	

}
