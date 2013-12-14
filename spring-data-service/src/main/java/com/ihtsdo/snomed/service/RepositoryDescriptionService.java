package com.ihtsdo.snomed.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.repository.DescriptionRepository;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
//@Transactional (value = "transactionManager", readOnly = true)
@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class RepositoryDescriptionService implements com.ihtsdo.snomed.service.DescriptionService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryDescriptionService.class);

    protected static final int NUMBER_OF_DESCRIPTIONS_PER_PAGE = 5;

    @Inject
    DescriptionRepository descriptionRepository;
    
    @PostConstruct
    public void init(){}
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findAll(int)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Description> findAll(int pageIndex){
        LOG.debug("TBD: Retrieving all refsets");
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findAll()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Description> findAll(){
        LOG.debug("Retrieving all descriptions");
        return descriptionRepository.findAll(sortByAscendingTitle());
    }    
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findById(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public Description findById(Long id) {
        LOG.debug("Finding description by id: " + id);
        return descriptionRepository.findOne(id);
    }
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findByPublicId(java.lang.String)
     */
    @Override
    @Transactional
    public Description findBySerialisedId(Long serialisedId){
        LOG.debug("Getting description with publicId=" + serialisedId);
        return descriptionRepository.findBySerialisedId(serialisedId);
    }
    
    private Sort sortByAscendingTitle() {
        return new Sort(Sort.Direction.ASC, "title");
    }
    
}
