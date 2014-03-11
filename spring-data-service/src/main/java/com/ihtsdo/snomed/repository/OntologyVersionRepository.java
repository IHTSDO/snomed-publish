package com.ihtsdo.snomed.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.SnomedOntology;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface OntologyVersionRepository extends JpaRepository<OntologyVersion, Long>{

    public final static String FIND_BY_FLAVOUR_AND_TAGGED_ON =
            "SELECT v FROM OntologyVersion v, OntologyFlavour f, Ontology o " +
            "WHERE v IN f.versions AND f IN o.flavours " + 
            "AND v.taggedOn=:taggedOn AND f.publicId=:flavourPublicId AND o.publicId=" + SnomedOntology.PUBLIC_ID;
    
    @Query(FIND_BY_FLAVOUR_AND_TAGGED_ON)
    public OntologyVersion findByFlavourAndTaggedOn(String flavourPublicId, Date taggedOn);
}
