package com.ihtsdo.snomed.service.manifest.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.exception.ProgrammingException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.manifest.model.BaseRefsetCollection;
import com.ihtsdo.snomed.service.manifest.model.MimetypeProperties;
import com.ihtsdo.snomed.service.manifest.model.RefsetCollectionFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@Transactional
public class RefsetCollectionParserTest {

    @Inject private RefsetCollectionParser refsetCollectionParser;
    @Inject private RefsetCollectionFactory refsetCollectionFactory;
    @Inject private MimetypeProperties mimetypeProperties;
    @PersistenceContext private EntityManager em;
    
    private Ontology ontology;
    private File file;
    
    @Before
    public void init() throws URISyntaxException{
        ontology = new Ontology();
        
        file = new File(this.getClass().getClassLoader().getResource("SnomedCT_Release_INT_20120731/refset/der2_cRefset_LanguageSnapshot-en_INT_20120731.txt").toURI());
        if (!file.exists()){
            throw new ProgrammingException("Unable to find file " + file.toString());
        }

        Concept moduleConcept = new Concept(900000000000207008l);
        Concept refsetId1 = new Concept(900000000000509007l);
        Concept refsetId2 = new Concept(900000000000508004l);
        Concept refsetId3 = new Concept(447563008l);
        
        moduleConcept.setPrimitive(true);
        refsetId1.setPrimitive(true);
        refsetId2.setPrimitive(true);
        refsetId3.setPrimitive(true);
        
        moduleConcept.setFullySpecifiedName("United States of America English language reference set (foundation metadata concept)");
        refsetId1.setFullySpecifiedName("United states of america english language reference set (Foundation metadata concept)");
        refsetId2.setFullySpecifiedName("Great britain english language reference set (Foundation metadata concept)");
        refsetId3.setFullySpecifiedName("Icd-9-cm equivalence complex map reference set (Foundation metadata concept)");

        moduleConcept.setOntology(ontology);
        refsetId1.setOntology(ontology);
        refsetId2.setOntology(ontology);
        refsetId3.setOntology(ontology);
                
        ontology.addConcept(refsetId1);
        ontology.addConcept(refsetId2);
        ontology.addConcept(refsetId3);
        ontology.addConcept(moduleConcept);
        
        em.persist(ontology);
        em.persist(refsetId1);
        em.persist(refsetId2);
        em.persist(refsetId3);
        em.persist(moduleConcept);
    }
      
    @Test
    public void shouldParseRefsetFile() throws FileNotFoundException{
        
        BaseRefsetCollection refsetCollection = refsetCollectionFactory
                .getRefsetCollection(mimetypeProperties.getMimetype(file.getName()));
        
        refsetCollectionParser.parse(new FileInputStream(file), ontology, refsetCollection, em);
        
        assertEquals(1, refsetCollection.getModules().size());
        assertEquals(2, refsetCollection.getModule(900000000000207008l).getRefsets().size());
        assertEquals("United states of america english language reference set (Foundation metadata concept)", refsetCollection.getModule(900000000000207008l).getRefset(900000000000509007l).getName());
        assertEquals(900000000000509007l, refsetCollection.getModule(900000000000207008l).getRefset(900000000000509007l).getSid());
        assertEquals("Great britain english language reference set (Foundation metadata concept)", refsetCollection.getModule(900000000000207008l).getRefset(900000000000508004l).getName());
        assertEquals(900000000000508004l, refsetCollection.getModule(900000000000207008l).getRefset(900000000000508004l).getSid());
    }
}
