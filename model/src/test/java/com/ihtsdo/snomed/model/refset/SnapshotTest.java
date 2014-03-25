package com.ihtsdo.snomed.model.refset;

import java.sql.Date;
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
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.OntologyFlavour;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.SnomedFlavours;
import com.ihtsdo.snomed.model.SnomedOntology;
import com.ihtsdo.snomed.model.refset.Refset.Source;
import com.ihtsdo.snomed.model.refset.Refset.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:spring-data.xml"})
public class SnapshotTest {

    Snapshot s1;
    @Inject Validator validator;
    
    @PersistenceContext(unitName="testdb")
    EntityManager em;
    
    static Date DEFAULT_TAGGED_ON;
    
    static{
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();
        DEFAULT_TAGGED_ON = new Date(utilDate.getTime());        
    }       
    
    @Before
    public void setUp() {
        Concept c = new Concept(1234);
        Member m = Member.getBuilder(null, c).build();
        em.persist(c);
        em.persist(m);

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
        
        Refset r = Refset.getBuilder(
                Refset.Source.LIST, 
                Refset.Type.CONCEPT, 
                ontologyVersion, 
                refsetConcept, 
                moduleConcept, 
                "title1", 
                "description1", 
                "pubid_1", 
                new Plan()).build();
        
        em.persist(r);

        s1 = Snapshot.getBuilder("pubid_1", "title1", "description1", new HashSet<Member>(Arrays.asList(m)), null).build();
        
        
        s1.setRefset(r);
        r.addSnapshot(s1);
        
        em.persist(s1);
        
        em.flush();
        em.clear();
    }
    
    public static Ontology createOntology() {
        OntologyVersion ov = new OntologyVersion();
        ov.setTaggedOn(DEFAULT_TAGGED_ON);
        OntologyFlavour of = new OntologyFlavour();
        of.setPublicId(SnomedFlavours.INTERNATIONAL.getPublicIdString());
        of.setLabel(SnomedFlavours.INTERNATIONAL.getLabel());
        Ontology o = new Ontology();
        o.setLabel(SnomedOntology.LABEL);
        o.setPublicId(SnomedOntology.PUBLIC_ID);
        o.addFlavour(of);
        of.addVersion(ov);
        return o;
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
    
//    @Test
//    public void shouldNotBeEqual(){
//        Snapshot s = em.createQuery("SELECT s FROM Snapshot s WHERE id=:id", Snapshot.class)
//                .setParameter("id", s1.getId())
//                .getSingleResult();
//        s.setId(45l);
//        assertNotEquals(s1, s);
//    }    
    
    @Test
    public void shouldHaveSameHashcode(){
        Snapshot s = em.createQuery("SELECT s FROM Snapshot s WHERE id=:id", Snapshot.class)
                .setParameter("id", s1.getId())
                .getSingleResult();
        
        assertEquals(s.hashCode(), s1.hashCode());
    }    
    
//    @Test
//    public void shouldNotHaveSameHashcode(){
//        Snapshot s = em.createQuery("SELECT s FROM Snapshot s WHERE id=:id", Snapshot.class)
//                .setParameter("id", s1.getId())
//                .getSingleResult();
//        s.setId(45L);
//        assertNotEquals(s.hashCode(), s1.hashCode());
//    }       
    
}
