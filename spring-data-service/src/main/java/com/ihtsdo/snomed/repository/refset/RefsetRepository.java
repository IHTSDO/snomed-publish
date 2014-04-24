package com.ihtsdo.snomed.repository.refset;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Status;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface RefsetRepository extends JpaRepository<Refset, Long> { //, JpaSpecificationExecutor<Refset> {
    //public static final String FIND_REFSET_BY_PUBLIC_ID = 
    //        "SELECT r FROM Refset r WHERE r.deleted=false";
    
    public final static String FIND_ALL_ACTIVE_WITH_TITLE_LIKE =
            "SELECT r from Refset r " +
            "WHERE r.status=com.ihtsdo.snomed.model.refset.Status.ACTIVE " +
            "AND r.title LIKE %:searchTerm%";
    
    public static final String MEMBER_SIZE = "SELECT NEW java.lang.Integer(SIZE(r.members)) FROM Refset r " + 
            "WHERE r.publicId=:refsetPublicId";
    
    @Query(FIND_ALL_ACTIVE_WITH_TITLE_LIKE)
    public Page<Refset> findAllActiveWithTitleLike(Pageable page);
    
    @Query(FIND_ALL_ACTIVE_WITH_TITLE_LIKE)
    public Page<Refset> findAllActiveRefsetsWithTitleLike(@Param("searchTerm") String searchTerm, Pageable page);    
    
    public Refset findByPublicIdAndStatus(String publicId, Status status);
    
    public List<Refset> findByStatus(Status status, Sort sort);
    
    @Query(MEMBER_SIZE)
    public Integer memberSize(@Param("refsetPublicId") String refsetPublicId);

}
