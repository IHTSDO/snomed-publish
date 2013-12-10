package com.ihtsdo.snomed.web.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.RefsetRuleService;
import com.ihtsdo.snomed.web.repository.RefsetRuleRepository;

/**
 * @see http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
 */
@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
//@Transactional (value = "transactionManager", readOnly = true)
public class RepositoryRefsetRuleService implements RefsetRuleService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryRefsetRuleService.class);
    
    @Inject
    protected RefsetRuleRepository refsetRuleRepository; 
    
//    @Inject 
//    protected ConceptRepository conceptRepository;
    
    @Inject
    protected ConceptService conceptService;
    
    @Override
    @Transactional(readOnly = true)
    public BaseRefsetRule findById(Long id) {
        LOG.debug("Finding refset by id: " + id);
        return refsetRuleRepository.findOne(id);
    }

    @Override
    @Transactional(rollbackFor = RefsetTerminalRuleNotFoundException.class)
    public BaseRefsetRule delete(Long refsetRuleId) throws RefsetTerminalRuleNotFoundException {
        LOG.debug("Deleting refset rule with id: " + refsetRuleId);
        BaseRefsetRule deleted = refsetRuleRepository.findOne(refsetRuleId);
        if (deleted == null) {
            throw new RefsetTerminalRuleNotFoundException(refsetRuleId, "No refset rule found with id: " + refsetRuleId);
        }
        refsetRuleRepository.delete(deleted);
        return deleted;
    }  
    
}
