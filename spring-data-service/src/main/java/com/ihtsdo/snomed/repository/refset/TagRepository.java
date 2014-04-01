package com.ihtsdo.snomed.repository.refset;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.model.refset.Tag;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface TagRepository extends JpaRepository<Tag, Long>{

    public final static String FIND_ALL_BY_REFSET_PUBLIC_ID =
            "SELECT t FROM Tag t, Refset r WHERE t MEMBER OF r.tags " + 
                    "AND r.publicId = :refsetPublicId";
    
    public static final String FIND_ONE_BY_TAG_PUBLIC_ID_AND_REFSET_PUBLIC_ID = 
            "SELECT t FROM Tag t, Refset r WHERE t MEMBER OF r.tags " + 
                    "AND r.publicId = :refsetPublicId " + 
                    "AND t.publicId = :tagPublicId";
    
    @Query(FIND_ALL_BY_REFSET_PUBLIC_ID)
    public List<Tag> findAllByRefsetPublicId(
            @Param("refsetPublicId") String refsetPublicId,
            Sort sort);
    
    @Query(FIND_ONE_BY_TAG_PUBLIC_ID_AND_REFSET_PUBLIC_ID)
    public Tag findOneByTagPublicIdAndRefsetPublicId(
            @Param("refsetPublicId") String refsetPublicId,
            @Param("tagPublicId") String tagPublicId);
}
