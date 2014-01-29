package com.ihtsdo.snomed.service.refset.parser;


public abstract class BaseRefsetParser implements RefsetParser {

    protected Mode parseMode = Mode.FORGIVING;
    
    public RefsetParser parseMode(Mode parseMode){
        this.parseMode = parseMode;
        return this;
    }

	protected boolean stringToBoolean(String string) throws IllegalArgumentException {
	    if (string.trim().equals("0")){
	        return false;
	    }
	    else if (string.trim().equals("1")){
	        return true;
	    }
	    else{
	        throw new IllegalArgumentException("Unable to convert value [" + string + "] to boolean value");
	    }
	}
}
