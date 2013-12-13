package com.ihtsdo.snomed.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.repository.ConceptRepository;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
//@Transactional (value = "transactionManager", readOnly = true)
@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class RepositoryConceptService implements com.ihtsdo.snomed.service.ConceptService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryConceptService.class);

    protected static final int NUMBER_OF_REFSETS_PER_PAGE = 5;

    @Inject
    ConceptRepository conceptRepository;
    
    @PostConstruct
    public void init(){}
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findAll(int)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Concept> findAll(int pageIndex){
        LOG.debug("TBD: Retrieving all refsets");
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findAll()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Concept> findAll(){
        LOG.debug("Retrieving all concepts");
        return conceptRepository.findAll(sortByAscendingTitle());
    }    
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findById(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public Concept findById(Long id) {
        LOG.debug("Finding concept by id: " + id);
        return conceptRepository.findOne(id);
    }
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findByPublicId(java.lang.String)
     */
    @Override
    @Transactional
    public Concept findBySerialisedId(Long serialisedId){
        LOG.debug("Getting concept with publicId=" + serialisedId);
        return conceptRepository.findBySerialisedId(serialisedId);
    }
    
    private Sort sortByAscendingTitle() {
        return new Sort(Sort.Direction.ASC, "title");
    }
    
}
