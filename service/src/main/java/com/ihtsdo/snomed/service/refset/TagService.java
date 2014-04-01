package com.ihtsdo.snomed.service.refset;

import java.util.List;

import com.ihtsdo.snomed.dto.refset.TagDto;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.exception.TagNotFoundException;
import com.ihtsdo.snomed.model.refset.Tag;
import com.ihtsdo.snomed.service.refset.RefsetService.SortOrder;

public interface TagService {

    public abstract List<Tag> findAllTags(String refsetPublicId, String sortBy, SortOrder sortOrder) throws RefsetNotFoundException;
    
    public abstract Tag findByPublicId(String refsetPublicId, String tagPublicId) throws TagNotFoundException;
    
    public abstract Tag create(String refsetPublicId, TagDto tagDto) throws RefsetNotFoundException, SnapshotNotFoundException, NonUniquePublicIdException;
        
    public abstract Tag delete(String refsetPublicId, String tagPublicId) throws TagNotFoundException;

}