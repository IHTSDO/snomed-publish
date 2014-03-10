package com.ihtsdo.snomed.service.refset.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.exception.InvalidInputException;

public class JsonParser extends BaseRefsetParser {
	//private static final Logger LOG = LoggerFactory.getLogger( RefsetParser.class );
	private final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public Set<MemberDto> parse(Reader reader) throws IOException, InvalidInputException {
		List<MemberDto> members = mapper.readValue(reader, new TypeReference<List<MemberDto>>() { } ); 

		//reset any internal ids, just in case
		for (MemberDto m : members){
			m.setId(0L);
		}
		
		return new HashSet<>(members);
	}	
}
