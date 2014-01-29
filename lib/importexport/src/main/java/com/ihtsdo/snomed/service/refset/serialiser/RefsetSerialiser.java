package com.ihtsdo.snomed.service.refset.serialiser;

import java.io.IOException;
import java.util.List;

import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.model.refset.Member;

public interface RefsetSerialiser {
	
    public abstract RefsetSerialiser header() throws IOException;
    
    public abstract RefsetSerialiser footer() throws IOException;
    
    public void write(List<Member> members) throws IOException;

	void writeDto(List<MemberDto> members) throws IOException;
}
