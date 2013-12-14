package com.ihtsdo.snomed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ihtsdo.snomed.model.Statement;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface StatementRepository extends JpaRepository<Statement, Long>{

    public Statement findBySerialisedId(Long serialisedId);

}
