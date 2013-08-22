package com.ihtsdo.snomed.browse.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ihtsdo.snomed.model.Refset;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface RefsetRepository extends JpaRepository<Refset, Long>{

    public Refset findByPublicId(String publicId);

}
