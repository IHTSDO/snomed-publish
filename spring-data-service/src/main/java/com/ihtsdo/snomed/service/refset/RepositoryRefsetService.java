package com.ihtsdo.snomed.service.refset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.model.refset.Plan;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.repository.ConceptRepository;
import com.ihtsdo.snomed.repository.refset.RefsetRepository;
import com.ihtsdo.snomed.repository.refset.SnapshotRepository;
import com.ihtsdo.snomed.service.ConceptService;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
//@Transactional (value = "transactionManager", readOnly = true)
@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class RepositoryRefsetService implements RefsetService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryRefsetService.class);

    protected static final int NUMBER_OF_REFSETS_PER_PAGE = 5;

    @Inject
    RefsetRepository refsetRepository;
    
    @Inject
    PlanService planService;
    
    @Inject
    ConceptRepository conceptRepository; 
    
    @Inject
    SnapshotService snapshotService;
    
    @Inject
    protected ConceptService conceptService;   
    
    
    @Inject
    protected SnapshotRepository snapshotRepository;   
    
    @PersistenceContext(unitName="hibernatePersistenceUnit") 
    private EntityManager em;
    
    @PostConstruct
    public void init(){}
    
    @Override
    @Transactional(readOnly = true)
    public List<Refset> findAll(int pageIndex){
        LOG.debug("Retrieving all refsets");
//        System.out.println("\n\n\n\n\n\nFUCK!!!!!!!\n\n\n\n\n\n\n");
//        Pageable pageSpecification = new PageRequest(pageIndex, NUMBER_OF_SNAPSHOTS_PER_PAGE, sortByAscendingTitle());
//        Page requestedPage = snapshotRepository.findAll(constructPageSpecification(pageIndex));
//        return requestedPage.getContent();
        throw new UnsupportedOperationException();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Refset> findAll(){
        LOG.debug("Retrieving all refsets");
        return refsetRepository.findAll(sortByAscendingTitle());
    }    
    
    @Override
    @Transactional(readOnly = true)
    public Refset findById(Long id) {
        LOG.debug("Finding refset by id: " + id);
        return refsetRepository.findOne(id);
    }
    
    @Override
    @Transactional
    public Refset findByPublicId(String publicId){
        LOG.debug("Getting refset with publicId=" + publicId);
        return refsetRepository.findByPublicId(publicId);
    }    

    @Override
    @Transactional(rollbackFor = RefsetNotFoundException.class)    
    public Refset update(Refset refset){
        return refsetRepository.save(refset);
    }
    @Override
    @Transactional(rollbackFor = {RefsetNotFoundException.class, RefsetConceptNotFoundException.class, ValidationException.class, RefsetPlanNotFoundException.class, RefsetTerminalRuleNotFoundException.class})
    public Refset update(RefsetDto updated) throws RefsetNotFoundException, RefsetConceptNotFoundException, ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException, NonUniquePublicIdException{
        LOG.debug("Updating refset with information: " + updated);
        Refset refset = refsetRepository.findOne(updated.getId());
        if (refset == null) {
            throw new RefsetNotFoundException("No refset found with id: " + updated.getId());
        }
        Concept concept = conceptRepository.findBySerialisedId(updated.getConcept());
        if (concept == null){
            throw new RefsetConceptNotFoundException(new ConceptDto(updated.getConcept()), "No concept found with id: " + updated.getConcept());
        }
        
        Plan plan = planService.findById(updated.getPlan().getId());
        if (plan == null){
            planService.create(updated.getPlan());
        }else{
            planService.update(updated.getPlan());
        }
        
        refset.update(concept, updated.getPublicId(), updated.getTitle(), updated.getDescription(), plan);
        try {
            return refsetRepository.save(refset);
        } catch (DataIntegrityViolationException e) {
            throw new NonUniquePublicIdException(e.getMessage(), e);
        }
    }   

    @Override
    @Transactional(rollbackFor={RefsetConceptNotFoundException.class, ValidationException.class})
    public Refset create(RefsetDto created) throws RefsetConceptNotFoundException, 
        ValidationException, NonUniquePublicIdException
    {
        LOG.debug("Creating new refset [{}]", created.toString());
        
        Concept concept = conceptRepository.findBySerialisedId(created.getConcept());
        if (concept == null){
            throw new RefsetConceptNotFoundException(new ConceptDto(created.getConcept()), "No concept found with id: " + created.getConcept());
        }        

        if (created.getPlan() == null){
            created.setPlan(new PlanDto());
        }

        Plan plan = planService.create(created.getPlan());

        Refset refset = Refset.getBuilder(concept, created.getPublicId(), created.getTitle(), created.getDescription(),
                plan).build();        
        
        try {
            return refsetRepository.save(refset);
        } catch (DataIntegrityViolationException e) {
            throw new NonUniquePublicIdException(e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = RefsetNotFoundException.class)
    public Refset delete(Long refsetId) throws RefsetNotFoundException {
        LOG.debug("Deleting refset with id: " + refsetId);
        Refset deleted = refsetRepository.findOne(refsetId);
        if (deleted == null) {
            throw new RefsetNotFoundException(refsetId, "No refset found with id: " + refsetId);
        }
        refsetRepository.delete(deleted);
        return deleted;
    }  

    @Override
    @Transactional(rollbackFor = RefsetNotFoundException.class)
    public Refset delete(String publicId) throws RefsetNotFoundException {
        LOG.debug("Deleting refset with public id: " + publicId);
        Refset deleted = refsetRepository.findByPublicId(publicId);
        if (deleted == null) {
            throw new RefsetNotFoundException(publicId, "No refset found with public id: " + publicId);
        }
        refsetRepository.delete(deleted);
        return deleted;
    }    
    
    @Override
    @Transactional(rollbackFor = RefsetNotFoundException.class)
    public SnapshotDto takeSnapshot(String refsetPublicId, SnapshotDto snapshotDto) throws RefsetNotFoundException, NonUniquePublicIdException {
        Refset refset = findByPublicId(refsetPublicId);
        if (refset == null){
            throw new RefsetNotFoundException(refsetPublicId);
        }
        
        Snapshot snapshot = refset.getSnapshot(snapshotDto.getPublicId());
        
        if (snapshot != null){
            throw new NonUniquePublicIdException("Snapshot with public id {} allready exists");
        }
        
        em.detach(refset.getPlan());
        
        snapshot = Snapshot.getBuilder(
                snapshotDto.getPublicId(), 
                snapshotDto.getTitle(), 
                snapshotDto.getDescription(), 
                Member.createFromConcepts(refset.getPlan().refreshAndGetConcepts()),
                refset.getPlan().getTerminal() != null ? refset.getPlan().getTerminal().clone() : null).build();
        
        refset.addSnapshot(snapshot);
        refset = refsetRepository.save(refset);
        return SnapshotDto.parse(snapshot);
    }
    
    @Override
    @Transactional(rollbackFor = RefsetNotFoundException.class)
    public SnapshotDto importSnapshot(String refsetPublicId, SnapshotDto snapshotDto) throws RefsetNotFoundException, NonUniquePublicIdException, ConceptIdNotFoundException {
        Refset refset = findByPublicId(refsetPublicId);
        if (refset == null){
            throw new RefsetNotFoundException(refsetPublicId);
        }
        
        Snapshot snapshot = refset.getSnapshot(snapshotDto.getPublicId());
        
        if (snapshot != null){
            throw new NonUniquePublicIdException("Snapshot with public id {} allready exists");
        }
        
        
        
        snapshot = Snapshot.getBuilder(
                snapshotDto.getPublicId(), 
                snapshotDto.getTitle(), 
                snapshotDto.getDescription(), 
                fillMembers(snapshotDto.getMemberDtos()),
                refset.getPlan().getTerminal() != null ? refset.getPlan().getTerminal().clone() : null).build();
        
        refset.addSnapshot(snapshot);
        refset = refsetRepository.save(refset);
        return SnapshotDto.parse(snapshot);
    }    

    private Sort sortByAscendingTitle() {
        return new Sort(Sort.Direction.ASC, "title");
    }
    
    private Set<Member> fillMembers(Set<MemberDto> memberDtos) throws ConceptIdNotFoundException  {
        Set<Member> members = new HashSet<Member>();
        if ((memberDtos == null) || (memberDtos.isEmpty())){
            return members;
        }
        for (MemberDto memberDto : memberDtos){
            Concept component = conceptService.findBySerialisedId(memberDto.getComponent().getId());
            if (component == null){
                throw new ConceptIdNotFoundException(memberDto.getComponent().getId(), 
                		"Did not find component concept with serialisedId " + memberDto.getComponent().getId());
            }
            Concept module = null;
            if (memberDto.getModule() != null){
                module = conceptService.findBySerialisedId(memberDto.getModule().getId());
                if (module == null){
                    throw new ConceptIdNotFoundException(memberDto.getComponent().getId(), 
                    		"Did not find module concept with serialisedId " + memberDto.getComponent().getId());
                }
            }
            members.add(Member.getBuilder(module, component).build());
        }
        return members;
    }      



//    private Pageable constructPageSpecification(int pageIndex) {
//        Pageable pageSpecification = new PageRequest(pageIndex, NUMBER_OF_SNAPSHOTS_PER_PAGE, sortByAscendingTitle());
//        return pageSpecification;
//    }    

    /*
     * @Query("SELECT p FROM Person p WHERE LOWER(p.lastName) = LOWER(:lastName)")
    public List<Person> find(@Param("lastName") String lastName);
     */
    
}
