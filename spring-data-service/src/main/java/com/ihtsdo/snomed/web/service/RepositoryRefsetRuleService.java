package com.ihtsdo.snomed.web.service;

import java.util.HashSet;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto.RuleType;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.UnrecognisedRefsetRuleException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
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
    @Transactional(rollbackFor = {RefsetRuleNotFoundException.class})
    public BaseRefsetRule update(RefsetRuleDto updated) throws RefsetRuleNotFoundException, ConceptNotFoundException {
        LOG.debug("Updating refset rule with information: " + updated);
        BaseRefsetRule rule = refsetRuleRepository.findOne(updated.getId());
        if (rule == null) {
            throw new RefsetRuleNotFoundException(updated.getId(), "No refset found with id: " + updated.getId());
        }
        if (rule instanceof ListConceptsRefsetRule){
            updateConcepts(updated, rule);
        }
        BaseRefsetRule saved = refsetRuleRepository.save(rule);
        LOG.debug("Updated refset rule with information: " + saved);
        return rule;
    }    

    @Override
    @Transactional
    public BaseRefsetRule create(RefsetRuleDto created) throws ConceptNotFoundException {
        LOG.debug("Creating new refset rule [{}]", created.toString());
        BaseRefsetRule newRule = createRule(created.getType());
        if (newRule instanceof ListConceptsRefsetRule){
            updateConcepts(created, newRule);
        }
        BaseRefsetRule persisted = refsetRuleRepository.save(newRule);
        LOG.debug("Created new refset rule [{}]", persisted.toString());
        return persisted;
    }

    @Override
    @Transactional(rollbackFor = RefsetRuleNotFoundException.class)
    public BaseRefsetRule delete(Long refsetRuleId) throws RefsetRuleNotFoundException {
        LOG.debug("Deleting refset with id: " + refsetRuleId);
        BaseRefsetRule deleted = refsetRuleRepository.findOne(refsetRuleId);
        if (deleted == null) {
            throw new RefsetRuleNotFoundException(refsetRuleId, "No refset found with id: " + refsetRuleId);
        }
        refsetRuleRepository.delete(deleted);
        return deleted;
    }  
    
    private void updateConcepts(RefsetRuleDto created, BaseRefsetRule newRule) throws ConceptNotFoundException {
        ListConceptsRefsetRule lcRule = (ListConceptsRefsetRule)newRule;
        lcRule.setConcepts(new HashSet<Concept>());
        for (ConceptDto conceptDto : created.getConcepts()){
            Concept c = conceptService.findBySerialisedId(conceptDto.getId());
            if (c == null){
                throw new ConceptNotFoundException(conceptDto.getId(), "Did not find concept with serialisedId " + conceptDto.getId());
            }
            lcRule.addConcept(c);
        }
    }    

    private static BaseRefsetRule createRule(RuleType type) throws UnrecognisedRefsetRuleException {
        try {
            return (BaseRefsetRule)RefsetRuleDto.TYPE_CLASS_MAP.get(type).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UnrecognisedRefsetRuleException("I've not been able to handle RefsetRule of type " + type + " and class " + RefsetRuleDto.TYPE_CLASS_MAP.get(type));
        }
    }
  
}
