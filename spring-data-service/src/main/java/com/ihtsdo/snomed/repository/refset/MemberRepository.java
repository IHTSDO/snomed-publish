package com.ihtsdo.snomed.repository.refset;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ihtsdo.snomed.model.refset.Member;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface MemberRepository extends JpaRepository<Member, Long>{
//    public final static String FIND_BY_REFSET_PUBLIC_ID_AND_IS_ACTIVE =
//            "SELECT p FROM Member m, Refset r JOIN r.membersMap p WHERE m MEMBER OF p " + 
//                    "AND r.publicId=:refsetPublicId";
    
    public final static String FIND_BY_REFSET_PUBLIC_ID_AND_IS_ACTIVE_WITH_COMPONENT_TITLE_LIKE =
            "SELECT m FROM Member m, Refset r WHERE m MEMBER OF r.members " + 
                    "AND r.publicId=:refsetPublicId " +
                    "AND r.status=com.ihtsdo.snomed.model.refset.Status.ACTIVE " +
                    "AND m.component.fullySpecifiedName LIKE %:searchTerm%";    
    
    public final static String FIND_BY_REFSET_PUBLIC_AND_SNAPSHOT_PUBLIC_ID_AND_IS_ACTIVE_WITH_COMPONENT_TITLE_LIKE =
            "SELECT m FROM Member m, Refset r, Snapshot s " + 
                    "WHERE m MEMBER OF s.immutableMembers " +
                    "AND s MEMBER OF r.snapshots " +
                    "AND r.publicId=:refsetPublicId " + 
                    "AND s.publicId=:snapshotPublicId " +
                    "AND r.status=com.ihtsdo.snomed.model.refset.Status.ACTIVE " + 
                    "AND s.status=com.ihtsdo.snomed.model.refset.Status.ACTIVE " +
                    "AND m.component.fullySpecifiedName LIKE %:searchTerm%"; 
    
    public static final String FIND_BY_MEMBER_PUBLIC_ID_AND_REFSET_PUBLIC_ID_AND_IS_ACTIVE = 
            "SELECT m FROM Member m, Refset r WHERE m MEMBER OF r.members " + 
                    "AND r.publicId=:refsetPublicId " + 
                    "AND m.publicId=:memberPublicId " +
                    "AND r.status=com.ihtsdo.snomed.model.refset.Status.ACTIVE";                                                                                                                                                                                                                                                                                                                                      
    
    @Query(FIND_BY_REFSET_PUBLIC_ID_AND_IS_ACTIVE_WITH_COMPONENT_TITLE_LIKE)
    public Page<Member> findByRefsetPublicIdAndIsActive(
            @Param("refsetPublicId") String refsetPublicId, Pageable page, @Param("searchTerm") String searchTerm);
    
    @Query(FIND_BY_REFSET_PUBLIC_ID_AND_IS_ACTIVE_WITH_COMPONENT_TITLE_LIKE)
    public List<Member> findByRefsetPublicIdAndIsActive(
            @Param("refsetPublicId") String refsetPublicId, Sort sort, @Param("searchTerm") String searchTerm);    
    
    @Query(FIND_BY_REFSET_PUBLIC_AND_SNAPSHOT_PUBLIC_ID_AND_IS_ACTIVE_WITH_COMPONENT_TITLE_LIKE)
    public Page<Member> findByRefsetPublicIdAndSnapshotPublicIdAndIsActive(
            @Param("refsetPublicId") String refsetPublicId,
            @Param("snapshotPublicId") String snapshotPublicId,
            Pageable page,
            @Param("searchTerm") String searchTerm);
    
    @Query(FIND_BY_REFSET_PUBLIC_AND_SNAPSHOT_PUBLIC_ID_AND_IS_ACTIVE_WITH_COMPONENT_TITLE_LIKE)
    public List<Member> findByRefsetPublicIdAndSnapshotPublicIdAndIsActive(
            @Param("refsetPublicId") String refsetPublicId,
            @Param("snapshotPublicId") String snapshotPublicId,
            Sort sort,
            @Param("searchTerm") String searchTerm);    
    
    @Query(FIND_BY_MEMBER_PUBLIC_ID_AND_REFSET_PUBLIC_ID_AND_IS_ACTIVE)
    public Member findByMemberPublicIdAndRefsetPublicIdAndIsActive(
            @Param("refsetPublicId") String refsetPublicId,
            @Param("memberPublicId") String memberPublicId);
}
