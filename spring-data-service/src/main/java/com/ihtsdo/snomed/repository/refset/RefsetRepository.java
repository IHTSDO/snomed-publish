package com.ihtsdo.snomed.repository.refset;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Status;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface RefsetRepository extends JpaRepository<Refset, Long> { //, JpaSpecificationExecutor<Refset> {
    //public static final String FIND_REFSET_BY_PUBLIC_ID = 
    //        "SELECT r FROM Refset r WHERE r.deleted=false"; 
    
    //@Query(FIND_REFSET_BY_PUBLIC_ID)
    public Refset findByPublicIdAndStatus(String publicId, Status status);
    
    public List<Refset> findByStatus(Status status, Sort sort);

}
