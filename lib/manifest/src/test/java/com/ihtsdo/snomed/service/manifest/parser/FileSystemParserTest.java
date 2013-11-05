package com.ihtsdo.snomed.service.manifest.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.exception.ProgrammingException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.manifest.model.CrossmapRefsetCollection;
import com.ihtsdo.snomed.service.manifest.model.LanguageRefsetCollection;
import com.ihtsdo.snomed.service.manifest.model.Manifest;
import com.ihtsdo.snomed.service.manifest.parser.FileSystemParser;
import com.ihtsdo.snomed.service.manifest.parser.RefsetCollectionParser;
import com.ihtsdo.snomed.service.manifest.parser.RefsetCollectionParser.Mode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@Transactional
public class FileSystemParserTest {

    private static final String DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT = "der2_iissscRefset_ComplexMapSnapshot_INT_20120731.txt";

    private static final String DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT = "der2_cRefset_LanguageSnapshot-en_INT_20120731.txt";

    @Inject
    FileSystemParser parser;
    
    @PersistenceContext EntityManager em;
    
    File root;
    Manifest manifest;
    Ontology ontology;
    
    @BeforeTransaction
    public void initTransaction(){
        
    }    

    @Before
    public void init() throws URISyntaxException{
        ontology = new Ontology();
        
        root = new File(this.getClass().getClassLoader().getResource("SnomedCT_Release_INT_20120731").toURI());
        if (!root.exists()){
            throw new ProgrammingException("Unable to find file " + root.toString());
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
    
    
//    @Inject
//    private XmlSerialiser serialiser;    
    
    @Test
    public void shouldCreateAllFilesAndFolders(){
        manifest = parser.parse(root, ontology, em);
        
        assertEquals(2, manifest.getManifestFolders().size());
        assertEquals(0, manifest.getManifestFiles().size());
        assertNotNull(manifest.getManifestFolder("folder"));
        
        assertNotNull(manifest.getManifestFolder("refset"));
        assertNotNull(manifest.getManifestFolder("refset").getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT));
        assertNotNull(manifest.getManifestFolder("refset").getManifestFile(DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT));
        assertEquals(2, manifest.getManifestFolder("refset").getManifestFiles().size());
        assertEquals(0, manifest.getManifestFolder("refset").getManifestFolders().size());
        
        assertNotNull(manifest.getManifestFolder("folder").getManifestFile("file"));
        assertNotNull(manifest.getManifestFolder("folder").getManifestFolder("folder"));
        assertNotNull(manifest.getManifestFolder("folder").getManifestFolder("folder").getManifestFile("file"));
        
        //serialiser.serialise(System.out, manifest);
    }
    
    @Test
    public void shouldCreateRefsetManifestFilesForRefsets(){
        parser.setParsemode(Mode.STRICT);
        manifest = parser.parse(root, ontology, em);
        assertEquals("application/vnd.ihtsdo.snomed.refset.language+txt", 
                manifest.getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .getMimetype());
        
        assertEquals("application/vnd.ihtsdo.snomed.refset.crossmap.full+txt", 
                manifest.getManifestFolder("refset")
                .getManifestFile(DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT)
                .getMimetype());        
        
        assertTrue(manifest.getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .isRefsetCollection());
        
        assertTrue(manifest.getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .getRefsetCollection() instanceof LanguageRefsetCollection);
        
        assertTrue(manifest.getManifestFolder("refset")
                .getManifestFile(DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT)
                .isRefsetCollection());
        
        assertTrue(manifest.getManifestFolder("refset")
                .getManifestFile(DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT)
                .getRefsetCollection() instanceof CrossmapRefsetCollection);        
    }
    
    @Test
    public void shouldSetModules(){
        parser.setParsemode(RefsetCollectionParser.Mode.STRICT);
        manifest = parser.parse(root, ontology, em);
        
        assertEquals(900000000000207008l, manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getSid());
        
        assertEquals("United States of America English language reference set (foundation metadata concept)", manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getName());  
        
        assertEquals(900000000000207008l, manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getSid());
        
        assertEquals("United States of America English language reference set (foundation metadata concept)", manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getName());         
    }
    
    @Test
    public void shouldSetRefsets(){
        parser.setParsemode(RefsetCollectionParser.Mode.STRICT);
        manifest = parser.parse(root, ontology, em);
        
        assertEquals(2, 
                manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getRefsets()
                .size());
        
        assertEquals(900000000000509007l, 
                manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getRefset(900000000000509007l)
                .getSid());
        
        assertEquals("United states of america english language reference set (Foundation metadata concept)", 
                manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getRefset(900000000000509007l)
                .getName());   
        
        assertEquals(900000000000508004l, 
                manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getRefset(900000000000508004l)
                .getSid());
        
        assertEquals("Great britain english language reference set (Foundation metadata concept)", 
                manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_C_REFSET_LANGUAGE_SNAPSHOT_EN_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getRefset(900000000000508004l)
                .getName());   

        
        assertEquals(447563008l, 
                manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getRefset(447563008l)
                .getSid());
        
        assertEquals("Icd-9-cm equivalence complex map reference set (Foundation metadata concept)", 
                manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getRefset(447563008l)
                .getName());   
        
        assertEquals(1, 
                manifest
                .getManifestFolder("refset")
                .getManifestFile(DER2_IISSSC_REFSET_COMPLEX_MAP_SNAPSHOT_INT_20120731_TXT)
                .getRefsetCollection()
                .getModule(900000000000207008l)
                .getRefsets().size());           
    }    
    
}
