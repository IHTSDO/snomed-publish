package com.ihtsdo.snomed.browse.validator;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.browse.repository.PersistenceConstants;
import com.ihtsdo.snomed.model.Refset;

public class UniqueValidator implements ConstraintValidator<Unique, Serializable>{
    private static final Logger LOG = LoggerFactory.getLogger( ConstraintValidator.class );

    @PersistenceContext(unitName=PersistenceConstants.ENTITYMANAGER_UNIT_NAME)
    EntityManager em;
    
    private Class<?> entityClass;
    private String uniqueField;
    
    public void initialize(Unique unique) {
        entityClass = unique.entity();
        uniqueField = unique.property();
    }

    public boolean isValid(Serializable property, ConstraintValidatorContext cvContext) {
        LOG.debug("Attempting to validate uniqueness field [{}] on class [{}] with value [{}]", uniqueField, entityClass.getName(), property);
        if (em.createQuery("SELECT x FROM " + entityClass.getSimpleName() + " x WHERE " + uniqueField + "=:value", entityClass)
            .setParameter("value", property)
            .getResultList()
            .isEmpty())
        {
            LOG.debug("Validation successfull");
            return true;
        }
        LOG.debug("Validation failed, value not unique");
        return false;
    }
}
