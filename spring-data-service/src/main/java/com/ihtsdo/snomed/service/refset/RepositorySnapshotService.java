package com.ihtsdo.snomed.service.refset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.model.refset.Rule;
import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.repository.refset.SnapshotRepository;
import com.ihtsdo.snomed.service.ConceptService;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/

@Service
public class RepositorySnapshotService implements SnapshotService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositorySnapshotService.class);

    protected static final int NUMBER_OF_SNAPSHOTS_PER_PAGE = 5;

    @Inject
    protected SnapshotRepository snapshotRepository;
    
    @Inject
    protected PlanService planService;
    
    @Inject
    protected ConceptService conceptService;   
    
    @PostConstruct
    public void init(){}
    
    @Override
    @Transactional(readOnly = true)
    public List<Snapshot> findAll(int pageIndex){
        LOG.debug("Retrieving all snapshots");
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Snapshot> findAll(){
        LOG.debug("Retrieving all snapshots");
        return snapshotRepository.findAll(sortByAscendingTitle());
    }    
    
    @Override
    @Transactional(readOnly = true)
    public Snapshot findById(Long id) {
        LOG.debug("Finding snapshot by id: " + id);
        return snapshotRepository.findOne(id);
    }
    
    @Override
    @Transactional
    public Snapshot findByPublicId(String publicId){
        LOG.debug("Getting snapshot with publicId=" + publicId);
        return snapshotRepository.findByPublicId(publicId);
    }    

    @Override
    @Transactional(rollbackFor = {SnapshotNotFoundException.class})
    public Snapshot update(SnapshotDto updated) throws NonUniquePublicIdException, SnapshotNotFoundException, RefsetConceptNotFoundException{
        LOG.debug("Updating snapshot with information: " + updated);
        
        Snapshot snapshot = snapshotRepository.findOne(updated.getId());
        if (snapshot == null) {
            throw new SnapshotNotFoundException("No snapshot found with id: " + updated.getId());
        }
        
        snapshot.update(
                updated.getPublicId(),
                updated.getTitle(),
                updated.getDescription(),
                fillMembers(updated.getMemberDtos()));

        try {
            return snapshotRepository.save(snapshot);
        } catch (DataIntegrityViolationException e) {
            throw new NonUniquePublicIdException(e.getMessage(), e);
        }
    }   

    @Override
    @Transactional(rollbackFor={RefsetConceptNotFoundException.class})
    public Snapshot create(SnapshotDto created) throws RefsetConceptNotFoundException, NonUniquePublicIdException, ValidationException{
        LOG.debug("Creating new snapshot [{}]", created.toString());
        
        PlanDto planDto = new PlanDto();
        planDto.setRefsetRules(created.getRefsetRules());
        planDto.setTerminal(created.getTerminal());
        
        Rule rule = planService.createRules(planDto);
        
        Snapshot snapshot = Snapshot.getBuilder(
                created.getPublicId(), 
                created.getTitle(), 
                created.getDescription(),
                fillMembers(created.getMemberDtos()),
                rule
                ).build();
        try {
            return snapshotRepository.save(snapshot);
        } catch (DataIntegrityViolationException e) {
            throw new NonUniquePublicIdException(e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = SnapshotNotFoundException.class)
    public Snapshot delete(Long snapshotId) throws SnapshotNotFoundException {
        LOG.debug("Deleting snapshot with id: " + snapshotId);
        Snapshot deleted = snapshotRepository.findOne(snapshotId);
        if (deleted == null) {
            throw new SnapshotNotFoundException(snapshotId, "No snapshot found with id: " + snapshotId);
        }
        snapshotRepository.delete(deleted);
        return deleted;
    }  

    @Override
    @Transactional(rollbackFor = SnapshotNotFoundException.class)
    public Snapshot delete(String publicId) throws SnapshotNotFoundException {
        LOG.debug("Deleting snapshot with public id: " + publicId);
        Snapshot deleted = snapshotRepository.findByPublicId(publicId);
        if (deleted == null) {
            throw new SnapshotNotFoundException(publicId, "No snapshot found with public id: " + publicId);
        }
        snapshotRepository.delete(deleted);
        return deleted;
    }    
    
    private Set<Member> fillMembers(Set<MemberDto> memberDtos) throws RefsetConceptNotFoundException {
        Set<Member> members = new HashSet<>();
        if ((memberDtos == null) || (memberDtos.isEmpty())){
            return members;
        }
        for (MemberDto memberDto : memberDtos){
            Concept c = conceptService.findBySerialisedId(memberDto.getComponent().getId());
            if (c == null){
                throw new RefsetConceptNotFoundException(memberDto.getComponent(), "Did not find component concept with serialisedId " + memberDto.getComponent().getId());
            }
            members.add(Member.getBuilder(null, c).build());
        }
        return members;
    }        

    private Sort sortByAscendingTitle() {
        return new Sort(Sort.Direction.ASC, "title");
    }
}
