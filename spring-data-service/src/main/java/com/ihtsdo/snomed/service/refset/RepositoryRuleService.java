package com.ihtsdo.snomed.service.refset;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.model.refset.BaseRule;
import com.ihtsdo.snomed.repository.refset.RuleRepository;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.refset.RuleService;

/**
 * @see http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
 */
@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
//@Transactional (value = "transactionManager", readOnly = true)
public class RepositoryRuleService implements RuleService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryRuleService.class);
    
    @Inject
    protected RuleRepository ruleRepository; 
    
//    @Inject 
//    protected ConceptRepository conceptRepository;
    
    @Inject
    protected ConceptService conceptService;
    
    @Override
    @Transactional(readOnly = true)
    public BaseRule findById(Long id) {
        LOG.debug("Finding refset by id: " + id);
        return ruleRepository.findOne(id);
    }

    @Override
    @Transactional(rollbackFor = RefsetTerminalRuleNotFoundException.class)
    public BaseRule delete(Long refsetRuleId) throws RefsetTerminalRuleNotFoundException {
        LOG.debug("Deleting refset rule with id: " + refsetRuleId);
        BaseRule deleted = ruleRepository.findOne(refsetRuleId);
        if (deleted == null) {
            throw new RefsetTerminalRuleNotFoundException(refsetRuleId, "No refset rule found with id: " + refsetRuleId);
        }
        ruleRepository.delete(deleted);
        return deleted;
    }  
    
}
