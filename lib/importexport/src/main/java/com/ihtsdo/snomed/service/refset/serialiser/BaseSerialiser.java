package com.ihtsdo.snomed.service.refset.serialiser;

import java.io.IOException;
import java.io.Writer;

public abstract class BaseSerialiser implements RefsetSerialiser{

    protected static final char DELIMITER = '\t';
    protected static final String LINE_ENDING = "\r\n";
		
	protected Writer writer;
	
	public BaseSerialiser(Writer writer) throws IOException{
		this.writer = writer;
        header();
	}

}
