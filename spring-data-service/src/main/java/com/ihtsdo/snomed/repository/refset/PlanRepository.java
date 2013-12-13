package com.ihtsdo.snomed.repository.refset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ihtsdo.snomed.model.refset.Plan;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface PlanRepository extends JpaRepository<Plan, Long>,
// QueryDslPredicateExecutor<BaseRule>,
        PagingAndSortingRepository<Plan, Long> {

}
