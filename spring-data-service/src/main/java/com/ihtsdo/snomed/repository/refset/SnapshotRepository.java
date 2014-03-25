package com.ihtsdo.snomed.repository.refset;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.model.refset.Status;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface SnapshotRepository extends JpaRepository<Snapshot, Long>{

    public final static String FIND_ALL_BY_REFSET_PUBLIC_ID_AND_STATUS =
            "SELECT s FROM Snapshot s, Refset r WHERE s MEMBER OF r.snapshots " + 
                    "AND r.publicId = :refsetPublicId " + 
                    "AND s.status = :status";
    
    public static final String FIND_ONE_BY_SNAPSHOT_PUBLIC_ID_AND_REFSET_PUBLIC_ID_AND_STATUS = 
            "SELECT s FROM Snapshot s, Refset r WHERE s MEMBER OF r.snapshots " + 
                    "AND r.publicId = :refsetPublicId " + 
                    "AND s.publicId = :snapshotPublicId " +
                    "AND s.status = :status";                                                                                                                                                                                                                                                                                                                                      

    public static final String FIND_ONE_BY_SNAPSHOT_PUBLIC_ID_AND_REFSET_PUBLIC_ID_AND_ANY_STATUS = 
            "SELECT s FROM Snapshot s, Refset r WHERE s MEMBER OF r.snapshots " + 
                    "AND r.publicId = :refsetPublicId " + 
                    "AND s.publicId = :snapshotPublicId";                                                                                                                                                                                                                                                                                        
    
    
    @Query(FIND_ALL_BY_REFSET_PUBLIC_ID_AND_STATUS)
    public List<Snapshot> findAllByRefsetPublicIdAndStatus(
            @Param("refsetPublicId") String refsetPublicId,
            @Param("status") Status status);
    
    @Query(FIND_ONE_BY_SNAPSHOT_PUBLIC_ID_AND_REFSET_PUBLIC_ID_AND_STATUS)
    public Snapshot findOneBySnapshotPublicIdAndRefsetPublicIdAndStatus(
            @Param("refsetPublicId") String refsetPublicId,
            @Param("snapshotPublicId") String snapshotPublicId,
            @Param("status") Status status);
    
    @Query(FIND_ONE_BY_SNAPSHOT_PUBLIC_ID_AND_REFSET_PUBLIC_ID_AND_ANY_STATUS)
    public Snapshot findOneBySnapshotPublicIdAndRefsetPublicIdAndAnyStatus(
            @Param("refsetPublicId") String refsetPublicId,
            @Param("snapshotPublicId") String snapshotPublicId);    
}
