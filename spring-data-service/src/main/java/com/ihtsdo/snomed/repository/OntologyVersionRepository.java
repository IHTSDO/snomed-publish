package com.ihtsdo.snomed.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ihtsdo.snomed.model.OntologyVersion;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface OntologyVersionRepository extends JpaRepository<OntologyVersion, Long>{

    public final static String FIND_BY_FLAVOUR_AND_TAGGED_ON =
            "SELECT v FROM OntologyVersion v, OntologyFlavour f, Ontology o " +
            "WHERE v MEMBER OF f.versions AND f MEMBER OF o.flavours " + 
            "AND v.taggedOn=:taggedOn AND f.publicId=:flavourPublicId AND o.publicId=:ontologyPublicId";
    
    @Query(FIND_BY_FLAVOUR_AND_TAGGED_ON)
    public OntologyVersion findByFlavourAndTaggedOn(
            @Param("flavourPublicId") String flavourPublicId, 
            @Param("taggedOn") Date taggedOn,
            @Param("ontologyPublicId") String ontologyPublicId);
}
