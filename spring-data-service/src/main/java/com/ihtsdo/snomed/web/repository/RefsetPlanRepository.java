package com.ihtsdo.snomed.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ihtsdo.snomed.model.refset.RefsetPlan;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface RefsetPlanRepository extends 
    JpaRepository<RefsetPlan, Long>,  
    //QueryDslPredicateExecutor<BaseRefsetRule>,
    PagingAndSortingRepository<RefsetPlan, Long>{

}
