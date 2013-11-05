package com.ihtsdo.snomed.service.manifest.model;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ihtsdo.snomed.service.manifest.model.MimetypeProperties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class MimetypePropertiesTest {

    @Inject
    private MimetypeProperties p;
    
    @Test
    public void shouldDetermineIfRefset(){
        assertTrue(p.isRefsetCollectionFile("der2_cRefset_AssociationReferenceFull_INT_20120731.txt"));
        assertTrue(p.isContentRefsetCollectionFile("der2_cRefset_AssociationReferenceFull_INT_20120731.txt"));
        assertTrue(p.isCrossmapRefsetCollectionFile("der2_iissscRefset_ComplexMapFull_INT_20120731.txt"));
        assertTrue(p.isLanguageRefsetCollectionFile("der2_cRefset_LanguageFull-en_INT_20120731.txt"));
        assertTrue(p.isMetadataRefsetCollectionFile("der2_cciRefset_RefsetDescriptorFull_INT_20120731.txt"));

        assertTrue(p.isRefsetCollectionMimetype("application/vnd.ihtsdo.snomed.rf2.refset.content.associationreference+txt"));
        assertTrue(p.isContentRefsetCollectionMimetype("application/vnd.ihtsdo.snomed.rf2.refset.content.associationreference+txt"));
        assertTrue(p.isCrossmapRefsetCollectionMimetype("application/vnd.ihtsdo.snomed.rf2.refset.crossmap.full+txt"));
        assertTrue(p.isLanguageRefsetCollectionMimetype("application/vnd.ihtsdo.snomed.rf2.refset.language+txt"));
        assertTrue(p.isMetadataRefsetCollectionMimetype("application/vnd.ihtsdo.snomed.rf2.refset.metadata.descriptor+txt"));
    }
    
}
