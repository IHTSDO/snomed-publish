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
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.MemberNotFoundException;
import com.ihtsdo.snomed.exception.ProgrammingError;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.exception.TagNotFoundException;
import com.ihtsdo.snomed.web.dto.ErrorDto;
import com.ihtsdo.snomed.web.exception.FieldBindingException;
import com.ihtsdo.snomed.web.exception.GlobalBindingException;

@ControllerAdvice
public class RefsetControllerExceptionHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(RefsetControllerExceptionHandler.class);
    
    @Resource
    private MessageSource messageSource;
    
    //SPRING BIND EXCEPTION
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    @ResponseBody
    public ErrorDto handleBindException(BindException ex)   {
        LOG.debug("In Validation Bind Exception Handler");
        ErrorDto response = new ErrorDto();
        BindingResult result = ex.getBindingResult();
        for (ObjectError error : result.getAllErrors()){
            LOG.error("Error: {}", error.getObjectName() + " - " + error.getDefaultMessage());
        }
        
        for (FieldError fError : result.getFieldErrors()){
            response.addFieldError(fError.getField(), resolveLocalizedErrorMessage(fError));
        }
        for (ObjectError gError : result.getGlobalErrors()){
            response.addGlobalError(resolveLocalizedErrorMessage(gError));
        }
        return response;
    }    
    
    // FIELD BINDING EXCEPTION
    @ExceptionHandler(FieldBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleFieldBindingException(FieldBindingException e){
        LOG.debug("In Field Binding Exception Handler");
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
    public ErrorDto handleGlobalBindingException(GlobalBindingException e){
        LOG.debug("In Global Binding Exception Handler");
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
        LOG.debug("In Refset Not Found Exception Handler");
        LOG.error("Unable to find refset with name {}", e.getPublicId(), e);
        return new ErrorDto().addGlobalError(
                resolveLocalizedErrorMessage(
                        "error.message.refset.publicid.not.found", 
                        Arrays.asList(e.getPublicId()),
                        "Unable to find refset with name " + e.getId()));
    }

    // MEMBER NOT FOUND EXCEPTION
    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleMemberNotFoundException(MemberNotFoundException e){
        LOG.debug("In Member Not Found Exception Handler");
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
    
    // SNAPSHOT NOT FOUND EXCEPTION
    @ExceptionHandler(SnapshotNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleSnapshotNotFoundException(SnapshotNotFoundException e){
        LOG.debug("In Snapshot Not Found Exception Handler");
        if (e.getId() != null){
            LOG.error("Unable to find snapshot with internal id {}", e.getId(), e);
            return new ErrorDto().addGlobalError(
                    resolveLocalizedErrorMessage(
                            "error.message.internal.error", 
                            Arrays.asList(e.getMessage()),
                            "Internal Server Error: " + e.getMessage()));            
        }else{
            LOG.error("Unable to find snapshot {} for refset {}", e.getSnapshotPublicId(), e.getRefsetPublicId(), e);
            return new ErrorDto().addGlobalError(
                    resolveLocalizedErrorMessage(
                            "error.message.snapshot.not.found", 
                            Arrays.asList(e.getSnapshotPublicId(), e.getRefsetPublicId()),
                            "Unable to find snapshot with id " + e.getSnapshotPublicId() + " for refset with name " + e.getRefsetPublicId()));            
        }
    }    
    
    // TAG NOT FOUND EXCEPTION
    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleTagNotFoundException(TagNotFoundException e){
        LOG.debug("In TagNotFoundExceptionHandler");
        if (e.getId() != null){
            LOG.error("Unable to find tag with internal id {}", e.getId(), e);
            return new ErrorDto().addGlobalError(
                    resolveLocalizedErrorMessage(
                            "error.message.internal.error", 
                            Arrays.asList(e.getMessage()),
                            "Internal Server Error: " + e.getMessage()));            
        }else{
            LOG.error("Unable to find tag {} for refset {}", e.getTagPublicId(), e.getRefsetPublicId(), e);
            return new ErrorDto().addGlobalError(
                    resolveLocalizedErrorMessage(
                            "error.message.member.publicid.not.found", 
                            Arrays.asList(e.getTagPublicId(), e.getRefsetPublicId()),
                            "Unable to find member with id " + e.getTagPublicId() + " for refset with name " + e.getRefsetPublicId()));            
        }
    }        
    
    
    // CONCEPT NOT FOUND EXCEPTION
    @ExceptionHandler(ConceptIdNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto handleConceptIdNotFoundException(ConceptIdNotFoundException e){
        LOG.debug("In Concept Not Found Exception Handler");
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
        LOG.debug("In Programming Error Exception Handler");
        LOG.error(e.getMessage(), e);
        return new ErrorDto().addGlobalError(
                resolveLocalizedErrorMessage(
                        "error.message.internal.error", 
                        Arrays.asList(e.getMessage()),
                        "Internal Server Error: " + e.getMessage()));
    }        
    
    
    //TODO: Refactor
//    @ExceptionHandler(ServletRequestBindingException.class)
//    public ResponseEntity<String> handleServletRequestBindingException(ServletRequestBindingException ex)   {
//        LOG.debug("In Servlet Request Binding Exception Handler");
//        return new ResponseEntity<String>(ex.getMessage(),HttpStatus.PRECONDITION_REQUIRED);
//    }

    //TODO: Refactor    
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex)   {
//        LOG.debug("In Http Message Not Readable Exception Handler");
//        if (ex.getCause() instanceof JsonParseException){
//            return new ResponseEntity<String>(((JsonParseException)ex.getCause()).getOriginalMessage(),HttpStatus.NOT_ACCEPTABLE);
//        }else if (ex.getCause() instanceof UnrecognizedPropertyException){
//            return new ResponseEntity<String>(((UnrecognizedPropertyException)ex.getCause()).getOriginalMessage(),HttpStatus.NOT_ACCEPTABLE);
//        }else if (ex.getCause() instanceof JsonMappingException){
//            return new ResponseEntity<String>(((JsonMappingException)ex.getCause()).getOriginalMessage(),HttpStatus.NOT_ACCEPTABLE);            
//        }else{
//            LOG.error("Not handling cause exception, please add handler to this class", ex.getCause());
//            return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_ACCEPTABLE);
//        }
//    }
    
    private String resolveLocalizedErrorMessage(ObjectError error) {
        return resolveLocalizedErrorMessage(error.getDefaultMessage(), null, error.getDefaultMessage());
    }    

    private String resolveLocalizedErrorMessage(String key, List<String> args, String defaultMessage) {
        Locale currentLocale =  LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(key, args.toArray(), currentLocale);
        } catch (NoSuchMessageException e) {
            return defaultMessage;
        }
    }
}
