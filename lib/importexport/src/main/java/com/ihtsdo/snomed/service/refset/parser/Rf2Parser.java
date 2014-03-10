package com.ihtsdo.snomed.service.refset.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.exception.InvalidInputException;

public class Rf2Parser extends BaseRefsetParser {
	private static final Logger LOG = LoggerFactory.getLogger( RefsetParser.class );
	
	@Override
	public Set<MemberDto> parse(Reader reader) throws IOException, InvalidInputException {
		Set<MemberDto> members = new HashSet<>();
		try (@SuppressWarnings("resource") BufferedReader br = new BufferedReader(reader)){
	        int currentLine = 1;
	        String line = null;
	        line = br.readLine();
	        //skip the headers
	        line = br.readLine();
	        while (line != null) {
	            currentLine++;
	            if (line.isEmpty()){
	                line = br.readLine();
	                continue;
	            }
	            Iterable<String> split = Splitter.on('\t').split(line);
	            Iterator<String> splitIt = split.iterator();
	            try {
	                String serialisedId = splitIt.next();
	                String effectiveString = splitIt.next();
	                boolean active = stringToBoolean(splitIt.next()); 
	                Long moduleId = new Long(splitIt.next());
	                @SuppressWarnings("unused")
                    Long refsetId = new Long(splitIt.next()); //not used!
	                Long componentId = new Long(splitIt.next());
	                
	                members.add(
	                		MemberDto.getBuilder(
	                				serialisedId, 
	                				new ConceptDto(moduleId), 
	                				new ConceptDto(componentId), 
	                				parseRf2Date(effectiveString), 
	                				active)
	                		.build());
	                //ignore the rest
	            } catch (NumberFormatException e) {
	                LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
	                if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
	            } catch (IllegalArgumentException e){
	                LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
	                if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
	            } catch (ParseException e) {
	                LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
	                if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
				}
	            line = br.readLine();
	        }
		}
		return members;
	}	
	
	protected Date parseRf2Date(String effectiveString) throws ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.parse(effectiveString);
	}

}
