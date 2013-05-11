package com.ihtsdo.snomed.service.serialiser;

import java.io.IOException;
import java.io.Writer;

import com.ihtsdo.snomed.service.InvalidInputException;

public class SerialiserFactory  {

    public static SerialiserFactory instance;
    
    public enum Form{
        CANONICAL, CHILD_PARENT;
    }
    
    public static BaseOntologySerialiser getSerialiser(Form form, Writer writer) throws IOException{
        switch (form){
            case CANONICAL:
                return new CanonicalSerialiser(writer);
            case CHILD_PARENT:
                return new ChildParentSerialiser(writer);
            default:
                throw new InvalidInputException("BaseOntologySerialiser " + form + " not found");
        }
    }
}
