package com.ihtsdo.snomed.service;

import java.io.IOException;
import java.io.Writer;

public class SerialiserFactory  {

    public static SerialiserFactory instance;
    
    public enum Form{
        CANONICAL, CHILD_PARENT;
    }
    
    public static OntologySerialiser getSerialiser(Form form, Writer writer) throws IOException{
        switch (form){
            case CANONICAL:
                return new CanonicalSerialiser(writer);
            case CHILD_PARENT:
                return new ChildParentSerialiser(writer);
            default:
                throw new InvalidInputException("OntologySerialiser " + form + " not found");
        }
    }
}
