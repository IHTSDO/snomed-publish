package com.ihtsdo.snomed.web.exception;

import java.util.List;

public class FieldBindingException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private String field;
    private String messageKey;
    private List<String> messageArguments;
    private String defaultMessage;
    
    public FieldBindingException(String field, String messageKey, List<String> messageArguments, String defaultMessage) {
        this.field = field;
        this.messageArguments = messageArguments;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
    }

    public String getField() {
        return field;
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
