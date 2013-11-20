package com.ihtsdo.snomed.web.controller;

import java.util.Locale;

import javax.annotation.Resource;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.ihtsdo.snomed.web.dto.RefsetResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto.Status;

@Named
public class RefsetErrorBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RefsetErrorBuilder.class);

    @Resource
    private MessageSource messageSource;    
    
    public RefsetResponseDto build(BindingResult result, RefsetResponseDto response, int returnCode) {
        LOG.error("Found errors: ");
        for (ObjectError error : result.getAllErrors()){
            LOG.error("Error: {}", error.getObjectName() + " - " + error.getDefaultMessage());
        }
        
        LOG.error("Found errors: ");
        for (ObjectError error : result.getAllErrors()){
            LOG.error("Error: {}", error.getObjectName() + " - " + error.getDefaultMessage());
        }
        
        for (FieldError fError : result.getFieldErrors()){
            response.addFieldError(fError.getField(), resolveLocalizedErrorMessage(fError));
        }
        for (ObjectError gError : result.getGlobalErrors()){
            response.addGlobalError(resolveLocalizedErrorMessage(gError));
        }
        response.setCode(returnCode);
        response.setStatus(Status.FAIL);
        return response;
    }

    private String resolveLocalizedErrorMessage(ObjectError error) {
        Locale currentLocale =  LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(error.getDefaultMessage(), null, currentLocale);
        } catch (NoSuchMessageException e) {
            return error.getDefaultMessage();
        }
    }    
    
}
