package com.ihtsdo.snomed.client.manifest.serialiser;

import static org.junit.Assert.assertEquals;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ihtsdo.snomed.client.manifest.model.CrossmapRefsetCollection;
import com.ihtsdo.snomed.client.manifest.model.LanguageRefsetCollection;
import com.ihtsdo.snomed.client.manifest.model.Manifest;
import com.ihtsdo.snomed.client.manifest.model.ManifestFile;
import com.ihtsdo.snomed.client.manifest.model.ManifestFileFactory;
import com.ihtsdo.snomed.client.manifest.model.ManifestFolder;
import com.ihtsdo.snomed.client.manifest.model.Refset;
import com.ihtsdo.snomed.client.manifest.model.RefsetModule;
import com.ihtsdo.snomed.model.Concept;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class XmlSerialiserTest {
    
    @Inject
    private XmlSerialiser serialiser;
    
    @Inject
    ManifestFileFactory manifestFileFactory;
    
    Manifest manifest;
    
    private static final String EXPECTED_SERIALISATION = 
            "<?xml version='1.0'?>\n" +
                    "<sn:manifest xmlns:sn=\"http://ihtsdo.org/snomed/schema/manifest/v1\" sn:name=\"testManifest\">\n" +
                    "   <sn:folder sn:name=\"refset\">\n" +
                    "      <sn:file sn:mimetype=\"application/vnd.ihtsdo.snomed.refset.language+txt\" sn:name=\"der2_cRefset_LanguageDelta-en_INT_20120731.txt\" sn:size=\"0\" sn:type=\"refset\">\n" +
                    "         <sn:module sn:name=\"refsetModule1\" sn:sid=\"10\">\n" +
                    "            <sn:refset sn:name=\"refset11\" sn:sid=\"11\"/>\n" +
                    "            <sn:refset sn:name=\"refset12\" sn:sid=\"12\"/>\n" +
                    "            <sn:refset sn:name=\"refset13\" sn:sid=\"13\"/>\n" +
                    "            <sn:refset sn:name=\"refset14\" sn:sid=\"14\"/>\n" +
                    "         </sn:module>\n" +
                    "      </sn:file>\n" +
                    "      <sn:file sn:mimetype=\"application/vnd.ihtsdo.snomed.refset.crossmap.simple+txt\" sn:name=\"der2_sRefset_SimpleMapDelta_INT_20120731.txt\" sn:size=\"0\" sn:type=\"refset\">\n" +
                    "         <sn:module sn:name=\"refsetModule2\" sn:sid=\"20\">\n" +
                    "            <sn:refset sn:name=\"refset21\" sn:sid=\"21\"/>\n" +
                    "            <sn:refset sn:name=\"refset23\" sn:sid=\"23\"/>\n" +
                    "            <sn:refset sn:name=\"refset22\" sn:sid=\"22\"/>\n" +
                    "            <sn:refset sn:name=\"refset24\" sn:sid=\"24\"/>\n" +
                    "         </sn:module>\n" +
                    "      </sn:file>\n" +
                    "   </sn:folder>\n" +
                    "   <sn:folder sn:name=\"documentation\">\n" +
                    "      <sn:file sn:mimetype=\"application/pdf\" sn:name=\"doc_EditorialGuide_Current-en-US_INT_20120731.pdf\" sn:size=\"0\" sn:type=\"simple\"/>\n" +
                    "      <sn:file sn:mimetype=\"application/pdf\" sn:name=\"doc_IhtsdoGlossary_Current-en-US_INT_20120731.pdf\" sn:size=\"0\" sn:type=\"simple\"/>\n" +
                    "   </sn:folder>\n" +
                    "   <sn:file sn:mimetype=\"application/pdf\" sn:name=\"doc_EditorialGuide_Current-en-US_INT_20120731.pdf\" sn:size=\"0\" sn:type=\"simple\"/>\n" +
                    "</sn:manifest>";

    @Before
    public void init(){
        manifest = new Manifest(new File("testManifest"));

        Concept module1Concept = new Concept(10);
        module1Concept.setFullySpecifiedName("refsetModule1");
        Concept concept11 = new Concept(11);
        concept11.setFullySpecifiedName("refset11");
        Concept concept12 = new Concept(12);
        concept12.setFullySpecifiedName("refset12");
        Concept concept13 = new Concept(13);
        concept13.setFullySpecifiedName("refset13");
        Concept concept14 = new Concept(14);
        concept14.setFullySpecifiedName("refset14");
        
        Concept module2Concept = new Concept(20);
        module2Concept.setFullySpecifiedName("refsetModule2");
        Concept concept21 = new Concept(21);
        concept21.setFullySpecifiedName("refset21");
        Concept concept22 = new Concept(22);
        concept22.setFullySpecifiedName("refset22");
        Concept concept23 = new Concept(23);
        concept23.setFullySpecifiedName("refset23");
        Concept concept24 = new Concept(24);
        concept24.setFullySpecifiedName("refset24");        
        
        RefsetModule refsetModule1 = new RefsetModule(module1Concept);
        refsetModule1.getRefsets().addAll(Arrays.asList(new Refset(concept11), new Refset(concept12), new Refset(concept13), new Refset(concept14)));
        
        RefsetModule refsetModule2 = new RefsetModule(module2Concept);
        refsetModule2.getRefsets().addAll(Arrays.asList(new Refset(concept21), new Refset(concept22), new Refset(concept23), new Refset(concept24)));
        
        ManifestFile langRefsetFile = manifestFileFactory.createManifestFile(new File("der2_cRefset_LanguageDelta-en_INT_20120731.txt"));
        ManifestFile mapRefsetFile = manifestFileFactory.createManifestFile(new File("der2_sRefset_SimpleMapDelta_INT_20120731.txt"));
        ManifestFile readmeFile = manifestFileFactory.createManifestFile(new File("doc_EditorialGuide_Current-en-US_INT_20120731.pdf"));
        ManifestFile docFile1 = manifestFileFactory.createManifestFile(new File("doc_EditorialGuide_Current-en-US_INT_20120731.pdf"));
        ManifestFile docFile2 = manifestFileFactory.createManifestFile(new File("doc_IhtsdoGlossary_Current-en-US_INT_20120731.pdf"));
        
        ManifestFolder docFolder = new ManifestFolder(new File("documentation"));
        ManifestFolder refsetFolder = new ManifestFolder(new File("refset"));
        
        langRefsetFile.setRefsetCollection(new LanguageRefsetCollection(Arrays.asList(refsetModule1)));
        mapRefsetFile.setRefsetCollection(new CrossmapRefsetCollection(Arrays.asList(refsetModule2)));
        refsetFolder.addManifestFile(langRefsetFile);
        refsetFolder.addManifestFile(mapRefsetFile);
        docFolder.addManifestFile(docFile1);
        docFolder.addManifestFile(docFile2);
        manifest.addManifestFolder(refsetFolder);
        manifest.addManifestFolder(docFolder);
        manifest.addManifestFile(readmeFile);
    }
    
    @Test
    public void shouldCreateExpectedSerialisation() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream oStream = new BufferedOutputStream(baos);
        serialiser.serialise(new OutputStreamWriter(oStream, "utf-8"), manifest);
        oStream.flush();
        System.out.println(EXPECTED_SERIALISATION);
        System.out.println(baos.toString());
        
        assertEquals(EXPECTED_SERIALISATION, baos.toString());
    }
    
}
