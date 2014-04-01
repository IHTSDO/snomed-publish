package com.ihtsdo.snomed.dto.refset;

import java.util.ArrayList;
import java.util.List;

import com.ihtsdo.snomed.model.refset.Tag;

public class TagsDto {

	private List<TagDto> tags;

    public TagsDto(){}
    
    public TagsDto(List<TagDto> tags){
    	this.tags = tags;
    }
    
    public static TagsDto parse(List<Tag> tags){
        List<TagDto> tagDtos = new ArrayList<>();
        for (Tag t : tags){
            tagDtos.add(TagDto.parse(t));
        }
        return new TagsDto(tagDtos);        
    }
    
    
	public List<TagDto> getTags() {
		return tags;
	}

	public void setTags(List<TagDto> tags) {
		this.tags = tags;
	}
	
	

}
