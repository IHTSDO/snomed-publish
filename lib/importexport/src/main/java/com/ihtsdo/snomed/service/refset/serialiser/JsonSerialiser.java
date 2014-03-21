package com.ihtsdo.snomed.service.refset.serialiser;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.model.refset.Member;

public class JsonSerialiser extends BaseSerialiser{

	private final ObjectMapper mapper = new ObjectMapper();

	public JsonSerialiser(Writer writer) throws IOException{
		super(writer);
	}
	
    @Override
    public JsonSerialiser header() throws IOException {
        return this;
    }

    @Override
    public JsonSerialiser footer() throws IOException {
        return this;
    }

    @Override
    public void writeDto(List<MemberDto> members) throws IOException {
        throw new UnsupportedOperationException();
    	//mapper.writeValue(writer, new TypeReference<List<MemberDto>>() { } );
    }
    
    @Override
    public void write(List<Member> members) throws IOException {
        throw new UnsupportedOperationException();
//    	List<MemberDto> memberDtos = new ArrayList<>(members.size());
//    	for (Member member : members){
//    		memberDtos.add(MemberDto.parse(member));
//    	}
//    	writeDto(memberDtos);
    }
}
