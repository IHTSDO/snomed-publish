package com.ihtsdo.snomed.dto.refset;

import java.util.List;

public class MembersDto {

	private List<MemberDto> members;
    
	private long totalSize;

    public MembersDto(){}
    
    public MembersDto(List<MemberDto> members){
        this.members = members;
    }
    
    public MembersDto(List<MemberDto> members, long totalSize){
    	this.members = members;
    	this.totalSize = totalSize;
    }
    
	public List<MemberDto> getMembers() {
		return members;
	}
	
	public long getTotalSize(){
	    return totalSize;
	}

	public void setMembers(List<MemberDto> members) {
		this.members = members;
	}
	
	

}
