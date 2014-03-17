package com.ihtsdo.snomed.service.refset.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.dto.refset.MembersDto;
import com.ihtsdo.snomed.exception.InvalidInputException;

public class XmlParser extends BaseRefsetParser {
	//private static final Logger LOG = LoggerFactory.getLogger( RefsetParser.class );
	
	@Override
	public Set<MemberDto> parse(Reader reader) throws IOException, InvalidInputException {
		try {
			return new HashSet<>(((MembersDto) JAXBContext.newInstance(MembersDto.class).createUnmarshaller().unmarshal(reader)).getMembers());
		} catch (JAXBException e) {
			throw new InvalidInputException("Unable to parse xml using domain model jaxb mappings", e);
		}
	}
}
