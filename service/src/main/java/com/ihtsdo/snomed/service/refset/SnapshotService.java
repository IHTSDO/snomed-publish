package com.ihtsdo.snomed.service.refset;

import java.util.List;

import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.refset.Snapshot;

public interface SnapshotService {

    public abstract List<Snapshot> findAllSnapshots(String refsetPublicId);
    
    public abstract Snapshot findByPublicId(String refsetPublicId, String snapshotPublicId) throws SnapshotNotFoundException;

    public abstract Snapshot update(String refsetPublicId, String snapshotPublicId, SnapshotDto updated) throws  NonUniquePublicIdException, SnapshotNotFoundException;

    public abstract Snapshot createFromDeclaredMembers(String refsetPublicId, SnapshotDto created) throws NonUniquePublicIdException, ValidationException, ConceptIdNotFoundException, RefsetNotFoundException;
    
    public abstract Snapshot createFromRefsetMembers(String refsetPublicId, SnapshotDto snapshotDto) throws RefsetNotFoundException, NonUniquePublicIdException;
    
    public abstract Snapshot delete(String refsetPublicId, String snapshotPublicId) throws SnapshotNotFoundException;

    public abstract Snapshot resurect(String refsetPublicId, String snapshotPublicId) throws SnapshotNotFoundException;

}