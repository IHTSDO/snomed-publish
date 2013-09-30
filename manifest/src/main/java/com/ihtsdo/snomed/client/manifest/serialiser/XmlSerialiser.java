package com.ihtsdo.snomed.client.manifest.serialiser;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.ihtsdo.snomed.client.manifest.model.Manifest;
import com.ihtsdo.snomed.exception.ProgrammingException;

@Named
public class XmlSerialiser {

    public void serialise(OutputStreamWriter oStream, Manifest manifest) throws IOException{
        try {
            oStream.write("<?xml version='1.0'?>\n");
            JAXBContext jaxbContext = JAXBContext.newInstance(Manifest.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(manifest, oStream);
        } catch (JAXBException e) {
            throw new ProgrammingException("Unable to serialise [" + manifest.toString() + "]", e);
        } 
    }
}
