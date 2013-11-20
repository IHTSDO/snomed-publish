package com.ihtsdo.snomed.web.config;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.ihtsdo.snomed.web.controller.RefsetErrorBuilder;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @Resource
    private MessageSource messageSource;
    
    @Inject
    private RefsetErrorBuilder error;
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public RefsetResponseDto processValidationError(MethodArgumentNotValidException ex) {
        return error.build(ex.getBindingResult(), new RefsetResponseDto(), RefsetResponseDto.FAIL_VALIDATION);
    }    
    
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<String> handleServletRequestBindingException(ServletRequestBindingException ex)   {
        return new ResponseEntity<String>(ex.getMessage(),HttpStatus.PRECONDITION_REQUIRED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex)   {
        if (ex.getCause() instanceof JsonParseException){
            return new ResponseEntity<String>(((JsonParseException)ex.getCause()).getOriginalMessage(),HttpStatus.NOT_ACCEPTABLE);
        }else if (ex.getCause() instanceof UnrecognizedPropertyException){
            return new ResponseEntity<String>(((UnrecognizedPropertyException)ex.getCause()).getOriginalMessage(),HttpStatus.NOT_ACCEPTABLE);
        }else{
            return new ResponseEntity<String>(ex.getMessage(),HttpStatus.NOT_ACCEPTABLE);
        }
    }
    
}
