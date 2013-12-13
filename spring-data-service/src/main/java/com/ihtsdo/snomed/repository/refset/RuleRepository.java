package com.ihtsdo.snomed.repository.refset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ihtsdo.snomed.model.refset.BaseRule;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface RuleRepository extends 
    JpaRepository<BaseRule, Long>,  
    //QueryDslPredicateExecutor<BaseRule>,
    PagingAndSortingRepository<BaseRule, Long>{

}
