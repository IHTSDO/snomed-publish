package com.ihtsdo.snomed.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ihtsdo.snomed.test.RefsetTestUtil;

/**
 * @author Henrik Pettersen @ http://sparklingideas.co.uk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-applicationContext.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RefsetDtoTest {

    @Inject Validator validator;
    
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
    public void testTitleIsNull(){
        successDto.setTitle(null);
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testTitleIsTooLong(){
        successDto.setTitle("title11231231414234rfsjkdnglkjdsnfgkdnbsldknfskajnglsjngaoetimovinsgoinrs;iovnrvrvrv");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testTitleIsTooShort(){
        successDto.setTitle("t");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testMissingConcept(){
        successDto.setConcept(null);
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }    
    
    @Test
    public void testDescriptionIsNull(){
        successDto.setDescription(null);
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }    

    @Test
    public void testDescriptionIsTooShort(){
        successDto.setDescription("t");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdIsNull(){
        successDto.setPublicId(null);
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdIsTooShort(){
        successDto.setPublicId("t");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdIsTooLong(){
        successDto.setPublicId("tshdddsbciasbdclsdnclkjsnadkcnsadchbsvkjhsbfhaesofnsaldvnklsakjhvbakshjvbnawoeivnsdvsdcsadcsadcs");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdContainsIllegalCharacters(){
        successDto.setPublicId("tshddd*!@$%asbd");
        Set<ConstraintViolation<RefsetDto>> violations = validator.validate(successDto);
        assertEquals(1, violations.size());
    }
        
}
