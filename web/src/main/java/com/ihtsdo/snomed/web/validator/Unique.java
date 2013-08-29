package com.ihtsdo.snomed.web.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=UniqueValidator.class)
public @interface Unique {
    String message() default "validation.entity.field.not.unique";
    Class<? extends Payload>[] payload() default {};
    Class<?>[] groups() default {};
    
    /**
     * The mapped hibernate/jpa entity class
     */
    Class<?> entity();
 
    /**
     * The property of the entity we want to validate for uniqueness. Default name is "id"
     */
    String property();// default "id";
}