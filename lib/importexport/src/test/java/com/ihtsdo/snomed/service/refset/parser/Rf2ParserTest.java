package com.ihtsdo.snomed.service.refset.parser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.exception.InvalidInputException;
import com.ihtsdo.snomed.service.refset.parser.RefsetParser.Mode;
import com.ihtsdo.snomed.service.refset.parser.RefsetParserFactory.Parser;
import static org.junit.Assert.assertEquals;

public class Rf2ParserTest {

	@Test
	public void shouldParseRefsetFile() throws IOException, InvalidInputException{
		
		Set<MemberDto> expected = new HashSet<>();
		
		expected.addAll(
			Arrays.asList(
					MemberDto.getBuilder(
						"0daa9880-5875-5de0-9650-21fac667afcb",
						new ConceptDto(900000000000207008L),
						new ConceptDto(900000000000509007L),
						new Date(),
						true).build(),
						
					MemberDto.getBuilder(
							"19cd20bd-2841-51b5-a574-05ffb2f3f5a4",
							new ConceptDto(900000000000207008L),
							new ConceptDto(900000000000508004L),
							new Date(),
							true).build(),
							
					MemberDto.getBuilder(
							"2a48ff52-5c41-5d0b-a834-ff3d92c18f4e",
							new ConceptDto(900000000000207008L),
							new ConceptDto(900000000000523009L),
							new Date(),
							true).build(),
							
					MemberDto.getBuilder(
							"2b8c66d0-b13d-54c9-a506-7eb506e012c5",
							new ConceptDto(900000000000207008L),
							new ConceptDto(447563008L),
							new Date(),
							true).build(),
							
					MemberDto.getBuilder(
							"2d37366b-f541-5df6-92a7-ca3add7a79bc",
							new ConceptDto(900000000000207008L),
							new ConceptDto(447563008L),
							new Date(),
							true).build()
					));
		
		Reader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("data/sample.refset.rf2"));
		Set<MemberDto> actual = RefsetParserFactory.getParser(Parser.RF2).parseMode(Mode.STRICT).parse(reader);
		assertEquals(expected, actual);
	}
}
