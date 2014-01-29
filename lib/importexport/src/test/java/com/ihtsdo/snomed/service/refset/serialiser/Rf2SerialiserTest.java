package com.ihtsdo.snomed.service.refset.serialiser;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.exception.InvalidInputException;
import com.ihtsdo.snomed.service.refset.serialiser.RefsetSerialiserFactory.Form;

public class Rf2SerialiserTest {

	private static final char TAB = '\t';
	private static final String LINE_ENDING = "\r\n";
	
    private static final String EXPECTED_RESULT =
    		"id" + TAB + "effectiveTime" + TAB + "active" + TAB + "moduleId" + TAB + "refsetId" + TAB + "referencedComponentId" + LINE_ENDING +
    		"0daa9880-5875-5de0-9650-21fac667afcb" + TAB + "20020131" + TAB + "1" + TAB + "900000000000207008" + TAB + "900000000000456007" + TAB + "900000000000509007" + LINE_ENDING +
    		"19cd20bd-2841-51b5-a574-05ffb2f3f5a4" + TAB + "20020131" + TAB + "1" + TAB + "900000000000207008" + TAB + "900000000000456007" + TAB + "900000000000508004" + LINE_ENDING +
    		"2a48ff52-5c41-5d0b-a834-ff3d92c18f4e" + TAB + "20020131" + TAB + "1" + TAB + "900000000000207008" + TAB + "900000000000456007" + TAB + "900000000000523009" + LINE_ENDING +
    		"2b8c66d0-b13d-54c9-a506-7eb506e012c5" + TAB + "20020131" + TAB + "1" + TAB + "900000000000207008" + TAB + "900000000000456007" + TAB + "447563008" + LINE_ENDING +
    		"2d37366b-f541-5df6-92a7-ca3add7a79bc" + TAB + "20020131" + TAB + "1" + TAB + "900000000000207008" + TAB + "900000000000456007" + TAB + "447563008" + LINE_ENDING;
	
	@Test
	public void shouldParseRefsetFile() throws IOException, InvalidInputException, ParseException{
		
		//You've got to be kidding me...
//		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//		
//		String expected = new String(
//				Files.readAllBytes(
//						FileSystems.getDefault().getPath(
//								path, 
//								"data/sample.refset.rf2")));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse("20020131");
		
		List<MemberDto> input = new ArrayList<>();
		input.addAll(
			Arrays.asList(
					MemberDto.getBuilder(
							"0daa9880-5875-5de0-9650-21fac667afcb",
							new ConceptDto(900000000000207008L),
							new ConceptDto(900000000000509007L),
							date,
							true).build(),
							
					MemberDto.getBuilder(
							"19cd20bd-2841-51b5-a574-05ffb2f3f5a4",
							new ConceptDto(900000000000207008L),
							new ConceptDto(900000000000508004L),
							date,
							true).build(),
							
					MemberDto.getBuilder(
							"2a48ff52-5c41-5d0b-a834-ff3d92c18f4e",
							new ConceptDto(900000000000207008L),
							new ConceptDto(900000000000523009L),
							date,
							true).build(),
							
					MemberDto.getBuilder(
							"2b8c66d0-b13d-54c9-a506-7eb506e012c5",
							new ConceptDto(900000000000207008L),
							new ConceptDto(447563008L),
							date,
							true).build(),
							
					MemberDto.getBuilder(
							"2d37366b-f541-5df6-92a7-ca3add7a79bc",
							new ConceptDto(900000000000207008L),
							new ConceptDto(447563008L),
							date,
							true).build()
					));
		
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, "utf-8"))){
            RefsetSerialiserFactory.getSerialiser(Form.RF2, pw).writeDto(input);
        }
        baos.flush();
        assertEquals(EXPECTED_RESULT, baos.toString());
	}

}
