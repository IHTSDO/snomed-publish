package com.ihtsdo.snomed.service;

import com.ihtsdo.snomed.service.InvalidInputException;

public class SerialiserFactory  {

    public static SerialiserFactory instance;
    
    private static CanonicalSerialiser canonical = new CanonicalSerialiser();
    private static ChildParentSerialiser childParent = new ChildParentSerialiser();
    
    public enum Form{
        CANONICAL, CHILD_PARENT;
    }
    
    public static Serialiser getSerialiser(Form form){
        switch (form){
            case CANONICAL:
                return canonical;
            case CHILD_PARENT:
                return childParent;
            default:
                throw new InvalidInputException("Serialiser " + form + " not found");
        }
    }
}
