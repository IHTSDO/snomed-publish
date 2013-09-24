package com.ihtsdo.snomed.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.test.RefsetTestUtil;

/**
 * @author Henrik Pettersen @ http://sparklingideas.co.uk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
        "classpath:test-applicationContext.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RefsetPlanDtoTest {

    @Inject Validator validator;
    
    @Resource(name = "messageSource")
    private MessageSource messageSource;
    
    RefsetDto successDto;
    
    @Before
    public void setUp() {    
        successDto = RefsetTestUtil.createDto(null, 1234l, "pub11", "title1", "description1");
    }
    
    @Test
    public void shouldPassValidation(){
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testConceptIsNull(){
        successDto.setConcept(null);
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }

    @Test
    public void testTitleIsNull(){
        successDto.setTitle(null);
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }
    
    @Test
    public void testTitleIsTooLong(){
        successDto.setTitle("title11231231414234rfsjkdnglkjdsnfgkdnbsldknfskajnglsjngaoetimovinsgoinrs;iovnrvrvrv");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }
    
    @Test
    public void testTitleIsTooShort(){
        successDto.setTitle("t");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }
    
    @Test
    public void testMissingConcept(){
        successDto.setConcept(null);
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }    
    
    @Test
    public void testDescriptionIsNull(){
        successDto.setDescription(null);
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }    

    @Test
    public void testDescriptionIsTooShort(){
        successDto.setDescription("t");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }
    
    @Test
    public void testPublicIdIsNull(){
        successDto.setPublicId(null);
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }
    
    @Test
    public void testPublicIdIsTooShort(){
        successDto.setPublicId("t");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }
    
    @Test
    public void testPublicIdIsTooLong(){
        successDto.setPublicId("tshdddsbciasbdclsdnclkjsnadkcnsadchbsvkjhsbfhaesofnsaldvnklsakjhvbakshjvbnawoeivnsdvsdcsadcsadcs");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }
    
    @Test
    public void testPublicIdContainsIllegalCharacters(){
        successDto.setPublicId("tshddd*!@$%asbd");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
        assertMessage(violations);
    }
    private void assertMessage(Set<ConstraintViolation<RefsetDto>> violations) {
        String message = messageSource.getMessage(violations.iterator().next().getMessage(), null, "default", Locale.UK);
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }
        
            
}
