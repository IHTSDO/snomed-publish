package com.ihtsdo.snomed.service.refset;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.dto.refset.TagDto;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.exception.TagNotFoundException;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.model.refset.Tag;
import com.ihtsdo.snomed.repository.refset.RefsetRepository;
import com.ihtsdo.snomed.repository.refset.SnapshotRepository;
import com.ihtsdo.snomed.repository.refset.TagRepository;
import com.ihtsdo.snomed.service.refset.RefsetService.SortOrder;

@Service
@Transactional(value = "transactionManager")
public class RepositoryTagService implements TagService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryTagService.class);
    
    @Inject
    protected TagRepository tagRepository;
    
    
    @Inject
    protected SnapshotRepository snapshotRepository;

    @Inject
    RefsetRepository refsetRepository; 
    
    @Inject
    protected RefsetService refsetService;    

    @Inject
    protected SnapshotService snapshotService;    
    
    
    @Override
    @Transactional(readOnly=true)
    public com.ihtsdo.snomed.service.Page<Tag> findAllTags(String refsetPublicId, String sortBy, SortOrder sortOrder, String searchTerm, int page, int pageSize) throws RefsetNotFoundException {
        LOG.debug("Getting all tags for refset with publicId={} sorted by {} {}, page {} of {}, with search term '{}'", refsetPublicId, sortBy, sortOrder, page, pageSize, searchTerm);
        Page<Tag> pageResult = tagRepository.findAllByRefsetPublicId(refsetPublicId, searchTerm, 
                new PageRequest(page, pageSize, new Sort(RepositoryRefsetService.sortDirection(sortOrder), sortBy)));
        return new com.ihtsdo.snomed.service.Page<Tag>(pageResult.getContent(), pageResult.getTotalElements());
    }

    @Override
    @Transactional(readOnly=true)
    public Tag findByPublicId(String refsetPublicId, String tagPublicId) throws TagNotFoundException {
        LOG.debug("Getting tag with publicid {} for refset {}", tagPublicId, refsetPublicId);
        Tag found = tagRepository.findOneByTagPublicIdAndRefsetPublicId(refsetPublicId, tagPublicId);
        if (found == null){
            throw new TagNotFoundException(refsetPublicId, tagPublicId);
        }
        return found;
    }

    @Override
    @Transactional
    public Tag create(String refsetPublicId, TagDto tagDto) throws RefsetNotFoundException, SnapshotNotFoundException, NonUniquePublicIdException {
        LOG.debug("Creating tag for refset {}: {}", refsetPublicId, tagDto);
        Refset refset = refsetService.findByPublicId(refsetPublicId);
        Snapshot snapshot = snapshotService.findByPublicId(refsetPublicId, tagDto.getSnapshot().getPublicId());
        
        try {
            findByPublicId(refsetPublicId, tagDto.getPublicId());
            throw new NonUniquePublicIdException("Tag publicId " + tagDto.getPublicId() + " allready exists");
        } catch (TagNotFoundException e) {}

        return tagRepository.save(Tag.getBuilder(
                tagDto.getPublicId(), 
                tagDto.getTitle(), 
                tagDto.getDescription(), 
                refset, 
                snapshot).build());
    }

    @Override
    @Transactional
    public Tag delete(String refsetPublicId, String tagPublicId) throws TagNotFoundException{
        LOG.debug("Deleting tag with publicid {} for refset {}", tagPublicId, refsetPublicId);
        Tag found = tagRepository.findOneByTagPublicIdAndRefsetPublicId(refsetPublicId, tagPublicId);
        if (found == null){
            throw new TagNotFoundException(refsetPublicId, tagPublicId);
        }
        tagRepository.delete(found);
        return found;
    }
}
