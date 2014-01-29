package com.ihtsdo.snomed.service.refset.parser;

import com.ihtsdo.snomed.exception.InvalidInputException;

public class RefsetParserFactory {

    public static RefsetParserFactory instance;
    
    public enum Parser{
        RF2, JSON, XML;
    }
    
    public static RefsetParser getParser(Parser parser){
        switch (parser){
            case RF2:
                return new Rf2Parser();
            case JSON:
                return new JsonParser();
            case XML:
            	return new XmlParser();
            default:
                throw new InvalidInputException("Parser for " + parser + " not found");
        }
    }

}
