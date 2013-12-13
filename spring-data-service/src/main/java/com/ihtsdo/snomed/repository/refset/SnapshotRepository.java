package com.ihtsdo.snomed.repository.refset;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ihtsdo.snomed.model.refset.Snapshot;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
public interface SnapshotRepository extends JpaRepository<Snapshot, Long>{

    public Snapshot findByPublicId(String publicId);


}
