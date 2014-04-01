package com.ihtsdo.snomed.model.refset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.OntologyVersion;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:spring-data.xml"})
public class RefsetTest {

    Refset r1;
    @Inject Validator validator;
    
    @PersistenceContext(unitName="testdb")
    EntityManager em;
    
    
    @Before
    public void setUp() {
        Concept refsetConcept = new Concept(1234l);
        Concept moduleConcept = new Concept(2345l);
        OntologyVersion ontologyVersion = new OntologyVersion();
        
        refsetConcept.setOntologyVersion(ontologyVersion);
        moduleConcept.setOntologyVersion(ontologyVersion);
        
        ontologyVersion.addConcept(refsetConcept);
        ontologyVersion.addConcept(moduleConcept);
        ontologyVersion.setTaggedOn(new Date(java.util.Calendar.getInstance().getTime().getTime()));
        
        em.persist(ontologyVersion);
        em.persist(refsetConcept);
        em.persist(moduleConcept);
        
        r1 = Refset.getBuilder(
                Refset.Source.LIST, 
                Refset.Type.CONCEPT, 
                ontologyVersion, 
                refsetConcept, 
                moduleConcept, 
                "title1", 
                "description1", 
                "pubid_1", 
                new Plan()).build();
        
        em.persist(r1);
        em.flush();
        em.clear();
    }
    
    @Test
    public void shouldValidateRefset(){
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testRefsetConceptIsNull(){
        r1.setRefsetConcept(null);
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }    
    
    @Test
    public void testTitleIsNull(){
        r1.setTitle(null);
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testTitleIsTooLong(){
        r1.setTitle("title11231231414234rfsjkdnglkjdsnfgkdnbsldknfskajnglsjngaoetimovinsgoinrs;iovnrvrvrv");
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testTitleIsTooShort(){
        r1.setTitle("t");
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testDescriptionIsNull(){
        r1.setDescription(null);
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }    

    @Test
    public void testDescriptionIsTooShort(){
        r1.setDescription("t");
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdIsNull(){
        r1.setPublicId(null);
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdIsTooShort(){
        r1.setPublicId("t");
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdIsTooLong(){
        r1.setPublicId("tshdddsbciasbdclsdnclkjsnadkcnsadchbsvkjhsbfhaesofnsaldvnklsakjhvbakshjvbnawoeivnsdvsdcsadcsadcs");
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdContainsIllegalCharacters(){
        r1.setPublicId("tshddd*!@$%asbd");
        Set<ConstraintViolation<Refset>> violations = validator.validate(r1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void shouldStoreAllData(){
        Refset r = em.createQuery("SELECT r FROM Refset r WHERE id=:id", Refset.class)
                .setParameter("id", r1.getId())
                .getSingleResult();
        
        assertEquals(r.getId(), r1.getId());
        assertEquals(r.getTitle(), r1.getTitle());
        assertEquals(r.getDescription(), r1.getDescription());
        assertEquals(r.getPublicId(), r1.getPublicId());
    }
    
    @Test
    public void shouldBeEqual(){
        Refset r = em.createQuery("SELECT r FROM Refset r WHERE id=:id", Refset.class)
                .setParameter("id", r1.getId())
                .getSingleResult();
        
        assertEquals(r1, r);
    }
    
//    @Test
//    public void shouldNotBeEqual(){
//        Refset r = em.createQuery("SELECT r FROM Refset r WHERE id=:id", Refset.class)
//                .setParameter("id", r1.getId())
//                .getSingleResult();
//        r.setId(45l);
//        assertNotEquals(r1, r);
//    }    
    
//    @Test
//    public void shouldHaveSameHashcode(){
//        Refset r = em.createQuery("SELECT r FROM Refset r WHERE id=:id", Refset.class)
//                .setParameter("id", r1.getId())
//                .getSingleResult();
//        
//        assertEquals(r.hashCode(), r1.hashCode());
//    }    
//    
//    @Test
//    public void shouldNotHaveSameHashcode(){
//        Refset r = em.createQuery("SELECT r FROM Refset r WHERE id=:id", Refset.class)
//                .setParameter("id", r1.getId())
//                .getSingleResult();
//        r.setId(45l);
//        assertNotEquals(r.hashCode(), r1.hashCode());
//    }       
    
}
