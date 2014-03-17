package com.ihtsdo.snomed.repository.refset;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ihtsdo.snomed.model.refset.Member;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface MemberRepository extends JpaRepository<Member, Long>{
    public final static String FIND_BY_REFSET_PUBLIC_ID =
            "SELECT m FROM Member m, Refset r WHERE m MEMBER OF r.members " + 
                    "AND r.publicId=:refsetPublicId";
    
    @Query(FIND_BY_REFSET_PUBLIC_ID)
    public List<Member> findByRefsetPublicId(@Param("refsetPublicId") String refsetPublicId);
    
    public Member findByPublicId(String publicId);

}
