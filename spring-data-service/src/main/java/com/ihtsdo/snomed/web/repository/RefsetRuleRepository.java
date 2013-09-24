package com.ihtsdo.snomed.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ihtsdo.snomed.model.refset.BaseRefsetRule;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface RefsetRuleRepository extends JpaRepository<BaseRefsetRule, Long>{

}
