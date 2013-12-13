package com.ihtsdo.snomed.service.refset;

import java.util.List;

import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.model.refset.Snapshot;

public interface SnapshotService {

    public abstract List<Snapshot> findAll(int pageIndex);

    public abstract List<Snapshot> findAll();

    public abstract Snapshot findById(Long id);

    public abstract Snapshot findByPublicId(String publicId);

    public abstract Snapshot update(SnapshotDto updated) throws  NonUniquePublicIdException, SnapshotNotFoundException, RefsetConceptNotFoundException;

    public abstract Snapshot create(SnapshotDto created) throws RefsetConceptNotFoundException, NonUniquePublicIdException;
    
    public abstract Snapshot delete(Long snapshotId) throws SnapshotNotFoundException;

    public abstract Snapshot delete(String publicId)
            throws SnapshotNotFoundException;

}