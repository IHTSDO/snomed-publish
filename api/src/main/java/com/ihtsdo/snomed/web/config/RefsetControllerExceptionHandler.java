package com.ihtsdo.snomed.web.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.MemberNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.ProgrammingError;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.web.dto.ErrorDto;
import com.ihtsdo.snomed.web.exception.FieldBindingException;
import com.ihtsdo.snomed.web.exception.GlobalBindingException;

@ControllerAdvice
public class RefsetControllerExceptionHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(RefsetControllerExceptionHandler.class);
    
    @Resource
    private MessageSource messageSource;
    
    // FIELD BINDING EXCEPTION
    @ExceptionHandler(FieldBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleFieldBindingException(FieldBindingException e){
        return new ErrorDto().addFieldError(
                e.getField(),
                resolveLocalizedErrorMessage(
                        e.getMessageKey(), 
                        e.getMessageArguments(),
                        e.getDefaultMessage()));
    }
    
    // GLOBAL BINDING EXCEPTION
    @ExceptionHandler(GlobalBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleUnrecognisedFileExtensionException(GlobalBindingException e){
        return new ErrorDto().addGlobalError(
                resolveLocalizedErrorMessage(
                        e.getMessageKey(), 
                        e.getMessageArguments(),
                        e.getDefaultMessage()));
    }    

    // REFSET NOT FOUND EXCEPTION
    @ExceptionHandler(RefsetNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleRefsetNotFoundException(RefsetNotFoundException e){
        LOG.error("Unable to find refset with name {}", e.getId(), e);
        return new ErrorDto().addGlobalError(
                resolveLocalizedErrorMessage(
                        "error.message.refset.publicid.not.found", 
                        Arrays.asList(Long.toString(e.getId())),
                        "Unable to find refset with name " + e.getId()));
    }

    // MEMBER NOT FOUND EXCEPTION
    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleMemberNotFoundException(MemberNotFoundException e){
        if (e.getId() != null){
            LOG.error("Unable to find refset with internal id {}", e.getId(), e);
            return new ErrorDto().addGlobalError(
                    resolveLocalizedErrorMessage(
                            "error.message.internal.error", 
                            Arrays.asList(e.getMessage()),
                            "Internal Server Error: " + e.getMessage()));            
        }else{
            LOG.error("Unable to find member {} for refset {}", e.getPublicId(), e.getRefsetName(), e);
            return new ErrorDto().addGlobalError(
                    resolveLocalizedErrorMessage(
                            "error.message.member.publicid.not.found", 
                            Arrays.asList(e.getPublicId(), e.getRefsetName()),
                            "Unable to find member with id " + e.getPublicId() + " for refset with name " + e.getRefsetName()));            
        }
    }    
    
    // NON UNIQUE PUBLIC ID EXCEPTION
    @ExceptionHandler(NonUniquePublicIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleNonUniquePublicIdException(NonUniquePublicIdException e){
        LOG.error("Non Unique Public ID: {}", e.getMessage(), e);
        return new ErrorDto().addGlobalError(
                resolveLocalizedErrorMessage(
                        "error.message.internal.error", 
                        Arrays.asList(e.getMessage()),
                        "Internal Server Error: " + e.getMessage()));
    }        
    
    // CONCEPT NOT FOUND EXCEPTION
    @ExceptionHandler(ConceptIdNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleRefsetNotFoundException(ConceptIdNotFoundException e){
        LOG.error("Unable to find concept with id {}", e.getId(), e);
        return new ErrorDto().addGlobalError(
                resolveLocalizedErrorMessage(
                        "error.message.global.concept.not.found.exception", 
                        Arrays.asList(Long.toString(e.getId())),
                        "Unable to find concept with id " + e.getId()));
    }
    
    // PROGRAMMING ERROR
    @ExceptionHandler(ProgrammingError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleProgrammingError(ProgrammingError e){
        LOG.error(e.getMessage(), e);
        return new ErrorDto().addGlobalError(
                resolveLocalizedErrorMessage(
                        "error.message.internal.error", 
                        Arrays.asList(e.getMessage()),
                        "Internal Server Error: " + e.getMessage()));
    }        
    
    
    //TODO: Refactor
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<String> handleServletRequestBindingException(ServletRequestBindingException ex)   {
        return new ResponseEntity<String>(ex.getMessage(),HttpStatus.PRECONDITION_REQUIRED);
    }

    //TODO: Refactor    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex)   {
        if (ex.getCause() instanceof JsonParseException){
            return new ResponseEntity<String>(((JsonParseException)ex.getCause()).getOriginalMessage(),HttpStatus.NOT_ACCEPTABLE);
        }else if (ex.getCause() instanceof UnrecognizedPropertyException){
            return new ResponseEntity<String>(((UnrecognizedPropertyException)ex.getCause()).getOriginalMessage(),HttpStatus.NOT_ACCEPTABLE);
        }else if (ex.getCause() instanceof JsonMappingException){
            return new ResponseEntity<String>(((JsonMappingException)ex.getCause()).getOriginalMessage(),HttpStatus.NOT_ACCEPTABLE);            
        }else{
            LOG.error("Not handling cause exception, please add handler to this class", ex.getCause());
            return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private String resolveLocalizedErrorMessage(String key, List<String> args, String defaultMessage) {
        Locale currentLocale =  LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(key, null, currentLocale);
        } catch (NoSuchMessageException e) {
            return defaultMessage;
        }
    }
}
