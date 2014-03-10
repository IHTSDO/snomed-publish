package com.ihtsdo.snomed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ihtsdo.snomed.model.OntologyVersion;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface OntologyRepository extends JpaRepository<OntologyVersion, Long>{

}
