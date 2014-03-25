package com.ihtsdo.snomed.repository.refset;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ihtsdo.snomed.model.refset.Member;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface MemberRepository extends JpaRepository<Member, Long>{
//    public final static String FIND_BY_REFSET_PUBLIC_ID_AND_IS_ACTIVE =
//            "SELECT p FROM Member m, Refset r JOIN r.membersMap p WHERE m MEMBER OF p " + 
//                    "AND r.publicId=:refsetPublicId";

    public final static String FIND_BY_REFSET_PUBLIC_ID_AND_IS_ACTIVE =
            "SELECT m FROM Member m, Refset r WHERE m MEMBER OF r.members " + 
                    "AND r.publicId=:refsetPublicId " +
                    "AND r.status=com.ihtsdo.snomed.model.refset.Status.ACTIVE";
    
    public final static String FIND_BY_REFSET_PUBLIC_AND_SNAPSHOT_PUBLIC_ID_AND_IS_ACTIVE =
            "SELECT m FROM Member m, Refset r, Snapshot s " + 
                    "WHERE m MEMBER OF s.immutableMembers " +
                    "AND s MEMBER OF r.snapshots " +
                    "AND r.publicId=:refsetPublicId " + 
                    "AND s.publicId=:snapshotPublicId " +
                    "AND r.status=com.ihtsdo.snomed.model.refset.Status.ACTIVE " + 
                    "AND s.status=com.ihtsdo.snomed.model.refset.Status.ACTIVE";    
    
    public static final String FIND_BY_MEMBER_PUBLIC_ID_AND_REFSET_PUBLIC_ID_AND_IS_ACTIVE = 
            "SELECT m FROM Member m, Refset r WHERE m MEMBER OF r.members " + 
                    "AND r.publicId=:refsetPublicId " + 
                    "AND m.publicId=:memberPublicId " +
                    "AND r.status=com.ihtsdo.snomed.model.refset.Status.ACTIVE";                                                                                                                                                                                                                                                                                                                                      
    
    @Query(FIND_BY_REFSET_PUBLIC_ID_AND_IS_ACTIVE)
    public List<Member> findByRefsetPublicIdAndIsActive(@Param("refsetPublicId") String refsetPublicId);
    
    @Query(FIND_BY_MEMBER_PUBLIC_ID_AND_REFSET_PUBLIC_ID_AND_IS_ACTIVE)
    public Member findByMemberPublicIdAndRefsetPublicIdAndIsActive(
            @Param("refsetPublicId") String refsetPublicId,
            @Param("memberPublicId") String memberPublicId);
    
    @Query(FIND_BY_REFSET_PUBLIC_AND_SNAPSHOT_PUBLIC_ID_AND_IS_ACTIVE)
    public List<Member> findByRefsetPublicIdAndSnapshotPublicIdAndIsActive(
            @Param("refsetPublicId") String refsetPublicId,
            @Param("snapshotPublicId") String snapshotPublicId);
    
}
