package com.ihtsdo.snomed.service.refset;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.refset.Plan;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Rule;
import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.model.refset.Status;
import com.ihtsdo.snomed.repository.refset.RefsetRepository;
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
    RefsetRepository refsetRepository;    
    
    @Inject
    protected PlanService planService;
    
    @Inject
    protected RefsetService refsetService;    
    
    @Inject
    protected ConceptService conceptService;   
    
    @PersistenceContext(unitName="hibernatePersistenceUnit") 
    private EntityManager em;    
    
    @PostConstruct
    public void init(){}
    
    @Override
    @Transactional(readOnly=true)
    public Snapshot findByPublicId(String refsetPublicId, String snapshotPublicId) throws SnapshotNotFoundException{
        LOG.debug("Getting snapshot with publicId={} for refset {}", snapshotPublicId, refsetPublicId);
        Snapshot snapshot = snapshotRepository.findOneBySnapshotPublicIdAndRefsetPublicIdAndStatus(
                refsetPublicId, snapshotPublicId, Status.ACTIVE);
        if (snapshot == null){
            throw new SnapshotNotFoundException(refsetPublicId, snapshotPublicId);
        }
        return snapshot;
    } 

// SNAPSHOT MEMBERS ARE IMMUTABLE
//    @Override
//    @Transactional(rollbackFor = {SnapshotNotFoundException.class,
//            ConceptIdNotFoundException.class, RefsetNotFoundException.class})
//    public Snapshot addMembers(Set<MemberDto> members, String snapshotPublicId, String refsetPublicId)
//            throws SnapshotNotFoundException, ConceptIdNotFoundException, RefsetNotFoundException {
//        LOG.debug("Adding {} new members to refset {}", members.size(), snapshotPublicId);
//        
//        Refset refset = refsetService.findByPublicId(refsetPublicId);
//        Snapshot snapshot = findByPublicId(refsetPublicId, snapshotPublicId);
//        snapshot.addMembers(RepositoryRefsetService.fillMembers(members, refset.getModuleConcept(), conceptService));
//        return snapshot;
//    }    

    @Override
    @Transactional(rollbackFor = {SnapshotNotFoundException.class, NonUniquePublicIdException.class})
    public Snapshot update(String refsetPublicId, String snapshotPublicId, SnapshotDto updated) 
            throws NonUniquePublicIdException, SnapshotNotFoundException{
        LOG.debug("Updating snapshot {} for refset {} with: {} ", snapshotPublicId, refsetPublicId, updated);
        
        Snapshot snapshot = findByPublicId(refsetPublicId, snapshotPublicId);
        
        snapshot.update(
                updated.getPublicId(),
                updated.getTitle(),
                updated.getDescription());

        try {
            return snapshotRepository.save(snapshot);
        } catch (DataIntegrityViolationException e) {
            throw new NonUniquePublicIdException("Public id [" + updated.getPublicId() + "] already exist for snapshot for refset [" + refsetPublicId + "]");            
        }
    }   
    
    @Override
    @Transactional(rollbackFor = {
            RefsetNotFoundException.class, 
            NonUniquePublicIdException.class})
    public Snapshot createFromRefsetMembers(String refsetPublicId, SnapshotDto snapshotDto) 
            throws RefsetNotFoundException, NonUniquePublicIdException {
        
        Refset refset = refsetService.findByPublicId(refsetPublicId);
        
        Snapshot snapshot = snapshotRepository.
                findOneBySnapshotPublicIdAndRefsetPublicIdAndAnyStatus(refsetPublicId, snapshotDto.getPublicId());

        if (snapshot != null){
            throw new NonUniquePublicIdException("Snapshot with public id {} allready exists");
        }
        
        Plan plan = refset.getPlan();
        em.detach(plan);
        
        snapshot = Snapshot.getBuilder(
                snapshotDto.getPublicId(), 
                snapshotDto.getTitle(), 
                snapshotDto.getDescription(), 
                refset.getMembers(),
                plan.getTerminal() != null ? plan.getTerminal().clone() : null).build();
        
        refset.addSnapshot(snapshot);
        refset.setPendingChanges(false);
        return snapshot;
    }    

    @Override
    @Transactional(rollbackFor={RefsetConceptNotFoundException.class,
            NonUniquePublicIdException.class, ValidationException.class})
    public Snapshot createFromDeclaredMembers(String refsetPublicId, SnapshotDto created) throws NonUniquePublicIdException, ValidationException, ConceptIdNotFoundException, RefsetNotFoundException{
        LOG.debug("Creating new snapshot [{}] for refset {}", created.toString(), refsetPublicId);
        
        Refset refset = refsetService.findByPublicId(refsetPublicId);
        
        PlanDto planDto = new PlanDto();
        planDto.setRefsetRules(created.getRefsetRules());
        planDto.setTerminal(created.getTerminal());
        
        Rule rule = planService.createRules(planDto);
        
        Snapshot snapshot = Snapshot.getBuilder(
                created.getPublicId(), 
                created.getTitle(), 
                created.getDescription(),
                RepositoryRefsetService.fillMembers(created.getMemberDtos(), refset.getModuleConcept(), conceptService),
                rule
                ).build();
        try {
            return snapshotRepository.save(snapshot);
        } catch (DataIntegrityViolationException e) {
            throw new NonUniquePublicIdException(e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public Snapshot delete(String refsetPublicId, String snapshotPublicId) throws SnapshotNotFoundException {
        LOG.debug("Inactivating (deleting) snapshot with public id {} for refset {} " + snapshotPublicId, refsetPublicId);
        
        Snapshot inactivated = snapshotRepository.findOneBySnapshotPublicIdAndRefsetPublicIdAndStatus(
                refsetPublicId, snapshotPublicId, Status.ACTIVE);
        if (inactivated == null) {
            throw new SnapshotNotFoundException(refsetPublicId, snapshotPublicId);
        }
        inactivated.setStatus(Status.INACTIVE);
        return inactivated;
    }    

    @Override
    @Transactional
    public Snapshot resurect(String refsetPublicId, String snapshotPublicId) throws SnapshotNotFoundException {
        LOG.debug("Resurecting snapshot with public id {} for refset {}", snapshotPublicId, refsetPublicId);
        Snapshot resurected = snapshotRepository.findOneBySnapshotPublicIdAndRefsetPublicIdAndStatus(
                refsetPublicId, snapshotPublicId, Status.INACTIVE);
        if (resurected == null) {
            throw new SnapshotNotFoundException(refsetPublicId, snapshotPublicId);
        }
        resurected.setStatus(Status.ACTIVE);
        return resurected;
    }

    @Override
    @Transactional(readOnly=true)
    public List<Snapshot> findAllSnapshots(String refsetPublicId) {
        return snapshotRepository.findAllByRefsetPublicIdAndStatus(refsetPublicId, Status.ACTIVE);
    }
}
