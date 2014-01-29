package com.ihtsdo.snomed.dto.refset;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="export")
public class MembersDto {

    @XmlElementWrapper(name = "members")
    @XmlElement(name="member")
	private List<MemberDto> members;

    public MembersDto(){}
    
    public MembersDto(List<MemberDto> members){
    	this.members = members;
    }
    
	public List<MemberDto> getMembers() {
		return members;
	}

	public void setMembers(List<MemberDto> members) {
		this.members = members;
	}
	
	

}
