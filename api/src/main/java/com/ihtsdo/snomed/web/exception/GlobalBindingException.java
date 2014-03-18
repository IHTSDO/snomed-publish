package com.ihtsdo.snomed.web.exception;

import java.util.List;

public class GlobalBindingException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private String messageKey;
    private List<String> messageArguments;
    private String defaultMessage;    
    
    public GlobalBindingException(String messageKey, List<String> messageArguments, String defaultMessage) {
        this.messageArguments = messageArguments;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public List<String> getMessageArguments() {
        return messageArguments;
    }
}
