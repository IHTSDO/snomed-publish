package com.ihtsdo.snomed.service.refset.serialiser;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.model.refset.Member;

public class Rf2Serialiser extends BaseSerialiser{

	private static final long DEFAULT_REFSET_ID = 900000000000456007L;
	private static final long DEFAULT_MODULE_ID = 900000000000207008L;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public Rf2Serialiser(Writer writer) throws IOException{
		super(writer);
	}
	
    @Override
    public Rf2Serialiser header() throws IOException {
        writer.write(
        		"id" + DELIMITER + 
        		"effectiveTime" + DELIMITER + 
        		"active" + DELIMITER + 
        		"moduleId" + DELIMITER + 
        		"refsetId" + DELIMITER +
        		"referencedComponentId" + LINE_ENDING);
        return this;
    }

    @Override
    public Rf2Serialiser footer() throws IOException {
        return this;
    }

    @Override
    public void writeDto(List<MemberDto> members) throws IOException {
    	for (MemberDto m : members){
    		writer.write(
    				Objects.toString(m.getSerialisedId()) + DELIMITER +
    				sdf.format(m.getEffective()) + DELIMITER +
    				(m.isActive() ? '1' : '0') + DELIMITER +
    				(m.getModule() == null ? DEFAULT_MODULE_ID : Objects.toString(m.getModule().getId())) + DELIMITER +
    				DEFAULT_REFSET_ID + DELIMITER +
    				m.getComponent().getId() + LINE_ENDING);
    	}
    }
    
    @Override
    public void write(List<Member> members) throws IOException {
    	for (Member m : members){
    		writer.write(
    				Objects.toString(m.getSerialisedId()) + DELIMITER +
    				sdf.format(m.getEffective()) + DELIMITER +
    				(m.isActive() ? '1' : '0') + DELIMITER +
    				(m.getModule() == null ? DEFAULT_MODULE_ID : Objects.toString(m.getModule().getSerialisedId())) + DELIMITER +
    				DEFAULT_REFSET_ID + DELIMITER +
    				m.getComponent().getSerialisedId() + LINE_ENDING);
    	}
    }    
}
