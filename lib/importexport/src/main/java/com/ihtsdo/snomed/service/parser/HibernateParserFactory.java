package com.ihtsdo.snomed.service.parser;

import com.ihtsdo.snomed.service.InvalidInputException;

public class HibernateParserFactory  {

    public static HibernateParserFactory instance;
    
    public enum Parser{
        RF1, RF2, CANONICAL, CHILD_PARENT;
    }
    
    public static HibernateParser getParser(Parser parser){
        switch (parser){
            case RF1:
                return new Rf1HibernateParser();
            case RF2:
                return new Rf2HibernateParser();
            case CANONICAL:
                return new CanonicalHibernateParser();
            case CHILD_PARENT:
                return new ChildParentHibernateParser();
            default:
                throw new InvalidInputException("Parser for " + parser + " not found");
        }
    }
}
