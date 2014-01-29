package com.ihtsdo.snomed.service.refset.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.exception.InvalidInputException;

public interface RefsetParser {

	public enum Mode{
        STRICT, FORGIVING
    }

	public Set<MemberDto> parse(Reader reader) throws IOException, InvalidInputException;
	
	public RefsetParser parseMode(Mode parseMode);

}
