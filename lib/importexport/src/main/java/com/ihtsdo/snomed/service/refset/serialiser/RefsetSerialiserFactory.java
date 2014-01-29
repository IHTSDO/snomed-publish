package com.ihtsdo.snomed.service.refset.serialiser;

import java.io.IOException;
import java.io.Writer;

import com.ihtsdo.snomed.exception.InvalidInputException;


public class RefsetSerialiserFactory {

    public static RefsetSerialiserFactory instance;
    
    public enum Form{
        RF2, JSON;
    }
    
    public static RefsetSerialiser getSerialiser(Form form, Writer writer) throws IOException{
        switch (form){
            case RF2:
                return new Rf2Serialiser(writer);
            case JSON:
            	return new JsonSerialiser(writer);
            default:
                throw new InvalidInputException("RefsetSerialiser " + form + " not found");
        }
    }
}
