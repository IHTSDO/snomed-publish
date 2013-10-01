package com.ihtsdo.snomed.web.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.UnconnectedRefsetRuleException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.service.RefsetPlanService;
import com.ihtsdo.snomed.service.RefsetService;
import com.ihtsdo.snomed.service.UnReferencedReferenceRuleException;
import com.ihtsdo.snomed.web.repository.ConceptRepository;
import com.ihtsdo.snomed.web.repository.RefsetRepository;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
//@Transactional (value = "transactionManager", readOnly = true)
/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class RepositoryRefsetService implements RefsetService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryRefsetService.class);

    protected static final int NUMBER_OF_REFSETS_PER_PAGE = 5;

    @Inject
    RefsetRepository refsetRepository;
    
    @Inject
    RefsetPlanService planService;
    
    @Inject
    ConceptRepository conceptRepository;    
    
    @PostConstruct
    public void init(){}
    
    @Override
    @Transactional(readOnly = true)
    public List<Refset> findAll(int pageIndex){
        LOG.debug("Retrieving all refsets");
//        System.out.println("\n\n\n\n\n\nFUCK!!!!!!!\n\n\n\n\n\n\n");
//        Pageable pageSpecification = new PageRequest(pageIndex, NUMBER_OF_REFSETS_PER_PAGE, sortByAscendingTitle());
//        Page requestedPage = refsetRepository.findAll(constructPageSpecification(pageIndex));
//        return requestedPage.getContent();
        return null;
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
    public Refset update(RefsetDto updated) throws RefsetNotFoundException, NonUniquePublicIdException, ConceptNotFoundException, UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException, RefsetPlanNotFoundException {
        LOG.debug("Updating refset with information: " + updated);
        Refset refset = refsetRepository.findOne(updated.getId());
        if (refset == null) {
            throw new RefsetNotFoundException("No refset found with id: " + updated.getId());
        }
        Concept concept = conceptRepository.findBySerialisedId(updated.getConcept());
        if (concept == null){
            throw new ConceptNotFoundException("No concept found with id: " + updated.getConcept());
        }
        
        RefsetPlan plan = planService.findById(updated.getPlan().getId());
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
    @Transactional
    public Refset create(RefsetDto created) throws NonUniquePublicIdException, ConceptNotFoundException, 
            UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, 
            RefsetRuleNotFoundException, RefsetPlanNotFoundException
    {
        LOG.debug("Creating new refset [{}]", created.toString());
        
        Concept concept = conceptRepository.findBySerialisedId(created.getConcept());
        if (concept == null){
            throw new ConceptNotFoundException("No concept found with id: " + created.getConcept());
        }        

        
        RefsetPlan plan = planService.findById(created.getPlan().getId());
        if (plan == null){
            plan = planService.create(created.getPlan());
        }else{
            plan = planService.update(created.getPlan());
        }

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
            LOG.debug("No refset found with id: " + refsetId);
            throw new RefsetNotFoundException();
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
            LOG.debug("No refset found with id: " + publicId);
            throw new RefsetNotFoundException();
        }
        refsetRepository.delete(deleted);
        return deleted;
    }     

    private Sort sortByAscendingTitle() {
        return new Sort(Sort.Direction.ASC, "title");
    }

//    private Pageable constructPageSpecification(int pageIndex) {
//        Pageable pageSpecification = new PageRequest(pageIndex, NUMBER_OF_REFSETS_PER_PAGE, sortByAscendingTitle());
//        return pageSpecification;
//    }    

    /*
     * @Query("SELECT p FROM Person p WHERE LOWER(p.lastName) = LOWER(:lastName)")
    public List<Person> find(@Param("lastName") String lastName);
     */
    
}
