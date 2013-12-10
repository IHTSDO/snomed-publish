package com.ihtsdo.snomed.service.manifest.model;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ihtsdo.snomed.service.InvalidInputException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class RefsetCollectionFactoryTest {

    @Inject
    private RefsetCollectionFactory factory;
    
    @Test
    public void shouldGetCorrectRefsetCollectionFromMimetype(){
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf2.refset.language+txt") 
                instanceof LanguageRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf2.refset.content.attributevalue+txt") 
                instanceof ContentRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf2.refset.content.associationreference+txt") 
                instanceof ContentRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf2.refset.content.simple+txt") 
                instanceof ContentRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf2.refset.crossmap.full+txt") 
                instanceof CrossmapRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf2.refset.crossmap.simple+txt") 
                instanceof CrossmapRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf2.refset.metadata.descriptor+txt") 
                instanceof MetadataRefsetCollection);
        
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf1.refset.language+txt") 
                instanceof LanguageRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf1.refset.content.attributevalue+txt") 
                instanceof ContentRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf1.refset.content.associationreference+txt") 
                instanceof ContentRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf1.refset.content.simple+txt") 
                instanceof ContentRefsetCollection);        
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf1.refset.crossmap.full+txt") 
                instanceof CrossmapRefsetCollection);
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf1.refset.crossmap.simple+txt") 
                instanceof CrossmapRefsetCollection);        
        assertTrue(factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf1.refset.metadata.descriptor+txt") 
                instanceof MetadataRefsetCollection);
        
    }
    
    @Test(expected=InvalidInputException.class)
    public void shouldThrowExceptionForNonRefsetMimetypes(){
        factory.getRefsetCollection("application/vnd.ihtsdo.snomed.rf2.terminology.triple+txt");
    }

}
