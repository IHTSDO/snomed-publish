package com.ihtsdo.snomed.model;

import java.sql.Date;

public abstract class CreateOntologyUtil {

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

}
