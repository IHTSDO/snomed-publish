package com.ihtsdo.snomed.test;

import java.sql.Date;

import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.OntologyFlavour;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.SnomedFlavours;
import com.ihtsdo.snomed.model.SnomedOntology;


public class RefsetTestUtil {
    
    static Date DEFAULT_TAGGED_ON;
    
    static{
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();
        DEFAULT_TAGGED_ON = new Date(utilDate.getTime());        
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
    

//    public static RefsetDto createDto(Long id, Long concept, String publicId, String title, String description) {
//        RefsetDto dto = new RefsetDto();
//        
//        dto.setConcept(concept);
//        dto.setId(id);
//        dto.setPublicId(publicId);
//        dto.setTitle(title);
//        dto.setDescription(description);
//        dto.setPlan(new PlanDto());
//
//        return dto;
//    }

//    public static Refset createModelObject(Long id, String publicId, String title, String description) {
//        Refset model = Refset.getBuilder(publicId, title, description).build();
//        model.setId(id);
//        return model;
//    }
}

