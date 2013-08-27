package com.ihtsdo.snomed.browse.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.browse.repository.PersistenceConstants;
import com.ihtsdo.snomed.browse.testing.RefsetTestUtil;
import com.ihtsdo.snomed.model.Refset;

/**
 * @author Henrik Pettersen @ http://sparklingideas.co.uk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
        "classpath:applicationContext.xml", 
        "classpath:spring-data.xml",
        "classpath:spring-mvc.xml",
        "classpath:test-applicationContext.xml",
        "classpath:test-spring-data.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class RefsetDtoTest {

//    @PersistenceContext(unitName=PersistenceConstants.ENTITYMANAGER_UNIT_NAME)
//    EntityManager em;
//    
    @Inject Validator validator;
    
    RefsetDto successDto;
    
//    @PostConstruct
//    @Transactional
//    public void initDb(){
//        em.persist(Refset.getBuilder("pub1", "title1", "description1").build());
//        em.flush();
//        em.clear();        
//    }
    
    @Before
    public void setUp() {
//        em.persist(Refset.getBuilder("pub1", "title1", "description1").build());
//        em.flush();
//        em.clear();        
        successDto = RefsetTestUtil.createDto(null, "pub11", "title1", "description1");
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
