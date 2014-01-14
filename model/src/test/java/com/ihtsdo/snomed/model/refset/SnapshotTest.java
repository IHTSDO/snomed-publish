package com.ihtsdo.snomed.model.refset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
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

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:spring-data.xml"})
public class SnapshotTest {

    Snapshot s1;
    @Inject Validator validator;
    
    @PersistenceContext(unitName="testdb")
    EntityManager em;
    
    @Before
    public void setUp() {
        Concept c = new Concept(1234);
        em.persist(c);
        //ConceptDto cDto = ConceptDto.getBuilder().id(c.getId()).build();
        s1 = Snapshot.getBuilder("pubid_1", "title1", "description1", new HashSet<Concept>(Arrays.asList(c)), null).build();
        em.persist(s1);
        em.flush();
        em.clear();
    }
    
    @Test
    public void shouldValidateRefset(){
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    public void testTitleIsNull(){
        s1.setTitle(null);
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testTitleIsTooLong(){
        s1.setTitle("title11231231414234rfsjkdnglkjdsnfgkdnbsldknfskajnglsjngaoetimovinsgoinrs;iovnrvrvrv");
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testTitleIsTooShort(){
        s1.setTitle("t");
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testDescriptionIsNull(){
        s1.setDescription(null);
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertEquals(1, violations.size());
    }    

    @Test
    public void testDescriptionIsTooShort(){
        s1.setDescription("t");
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdIsNull(){
        s1.setPublicId(null);
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdIsTooShort(){
        s1.setPublicId("t");
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdIsTooLong(){
        s1.setPublicId("tshdddsbciasbdclsdnclkjsnadkcnsadchbsvkjhsbfhaesofnsaldvnklsakjhvbakshjvbnawoeivnsdvsdcsadcsadcs");
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void testPublicIdContainsIllegalCharacters(){
        s1.setPublicId("tshddd*!@$%asbd");
        Set<ConstraintViolation<Snapshot>> violations = validator.validate(s1);
        assertEquals(1, violations.size());
    }
    
    @Test
    public void shouldStoreAllData(){
        Snapshot s = em.createQuery("SELECT s FROM Snapshot s WHERE id=:id", Snapshot.class)
                .setParameter("id", s1.getId())
                .getSingleResult();
        
        assertEquals(s.getId(), s1.getId());
        assertEquals(s.getTitle(), s1.getTitle());
        assertEquals(s.getDescription(), s1.getDescription());
        assertEquals(s.getPublicId(), s1.getPublicId());
    }
    
    @Test
    public void shouldBeEqual(){
        Snapshot s = em.createQuery("SELECT s FROM Snapshot s WHERE id=:id", Snapshot.class)
                .setParameter("id", s1.getId())
                .getSingleResult();
        
        assertEquals(s1, s);
    }
    
    @Test
    public void shouldNotBeEqual(){
        Snapshot s = em.createQuery("SELECT s FROM Snapshot s WHERE id=:id", Snapshot.class)
                .setParameter("id", s1.getId())
                .getSingleResult();
        s.setId(45l);
        assertNotEquals(s1, s);
    }    
    
    @Test
    public void shouldHaveSameHashcode(){
        Snapshot s = em.createQuery("SELECT s FROM Snapshot s WHERE id=:id", Snapshot.class)
                .setParameter("id", s1.getId())
                .getSingleResult();
        
        assertEquals(s.hashCode(), s1.hashCode());
    }    
    
    @Test
    public void shouldNotHaveSameHashcode(){
        Snapshot s = em.createQuery("SELECT s FROM Snapshot s WHERE id=:id", Snapshot.class)
                .setParameter("id", s1.getId())
                .getSingleResult();
        s.setId(45L);
        assertNotEquals(s.hashCode(), s1.hashCode());
    }       
    
}
