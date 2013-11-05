package com.ihtsdo.snomed.service.manifest;


import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.ihtsdo.snomed.service.manifest.model.Manifest;

public class JaxbBindingTest {
    @Test
    public void testJaxbBinding() throws JAXBException{
        JAXBContext jaxbContext = JAXBContext.newInstance(Manifest.class);
        assertTrue((jaxbContext instanceof org.eclipse.persistence.jaxb.JAXBContext));
    }
}