package com.ihtsdo.snomed.web.service;

import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotPersistedException;
import com.ihtsdo.snomed.exception.UnrecognisedRefsetException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.model.refset.RefsetRule;
import com.ihtsdo.snomed.model.refset.rule.BaseSetOperationRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.service.RefsetRuleService;
import com.ihtsdo.snomed.web.repository.ConceptRepository;
import com.ihtsdo.snomed.web.repository.RefsetRuleRepository;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
//@Transactional (value = "transactionManager", readOnly = true)
/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class RepositoryRefsetRuleService implements RefsetRuleService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryRefsetRuleService.class);

    protected static final int NUMBER_OF_REFSETS_PER_PAGE = 5;

    @Inject
    RefsetRuleRepository refsetRuleRepository;
    
    @Inject
    ConceptRepository conceptRepository;    
    
    @PostConstruct
    public void init(){}
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findById(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public RefsetRule findById(Long id) {
        LOG.debug("Finding refset by id: " + id);
        return refsetRuleRepository.findOne(id);
    }
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#update(com.ihtsdo.snomed.web.dto.RefsetDto)
     */
    @Override
    @Transactional(rollbackFor = RefsetNotFoundException.class)
    public RefsetRule update(RefsetRuleDto updated) throws RefsetRuleNotFoundException, RefsetRuleNotPersistedException {
        LOG.debug("Updating refset rule with information: " + updated);
        if (!RefsetRuleDto.isPersited(updated.getId())){
            throw new RefsetRuleNotPersistedException("RefsetDto must have all its rules persisted, and the dto.id updated with the peristence id, before calling this method");
        }
        BaseRefsetRule rule = refsetRuleRepository.findOne(updated.getId());
        if (rule == null) {
            LOG.debug("No refset found with id: " + updated.getId());
            throw new RefsetRuleNotFoundException();
        }
        populateRule(updated, rule);
        refsetRuleRepository.save(rule);
        return rule;
    }    

    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#create(com.ihtsdo.snomed.web.dto.RefsetDto)
     */
    @Override
    @Transactional
    public RefsetRule create(RefsetRuleDto created) throws RefsetRuleNotPersistedException{
        if (!RefsetRuleDto.isPersited(created.getId())){
            throw new RefsetRuleNotPersistedException("RefsetDto must have all its rules persisted, and the dto.id updated with the peristence id, before calling this method");
        }
        LOG.debug("Creating new refset rule [{}]", created.toString());
        try {
            BaseRefsetRule newRule = (BaseRefsetRule)RefsetRuleDto.TYPE_CLASS_MAP.get(created.getType()).newInstance();
            populateRule(created, newRule);
            refsetRuleRepository.save(newRule);
            return newRule;
        } catch (InstantiationException e1) {
            throw new UnrecognisedRefsetException("I've not been able to handle RefsetRule of class " + RefsetRuleDto.TYPE_CLASS_MAP.get(created.getType()));
        } catch (IllegalAccessException e1) {
            throw new UnrecognisedRefsetException("I've not been able to handle RefsetRule of class " + RefsetRuleDto.TYPE_CLASS_MAP.get(created.getType()));
        }
    }

    private void populateRule(RefsetRuleDto created, RefsetRule newRule)
            throws RefsetRuleNotPersistedException 
    {
        if (newRule instanceof ListConceptsRefsetRule){
            ListConceptsRefsetRule lcRule = (ListConceptsRefsetRule)newRule;
            lcRule.setConcepts(new HashSet<Concept>());
            for (ConceptDto c : created.getConcepts()){
                lcRule.addConcept(conceptRepository.findBySerialisedId(c.getId()));
            }
        }else if (newRule instanceof BaseSetOperationRefsetRule){
            BaseSetOperationRefsetRule setRule = (BaseSetOperationRefsetRule)newRule;
            if (!RefsetRuleDto.isPersited(created.getLeft())){
                throw new RefsetRuleNotPersistedException("RefsetDto must have all its rules persisted, and the dto.id updated with the peristence id, before calling this method");
            }
            if (!RefsetRuleDto.isPersited(created.getRight())){
                throw new RefsetRuleNotPersistedException("RefsetDto must have all its rules persisted, and the dto.id updated with the peristence id, before calling this method");
            }
            setRule.setRightRule(this.findById(created.getRight()));
            setRule.setLeftRule(this.findById(created.getLeft()));
        }
    }
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#delete(java.lang.Long)
     */
    @Override
    @Transactional(rollbackFor = RefsetNotFoundException.class)
    public RefsetRule delete(Long refsetRuleId) throws RefsetRuleNotFoundException {
        LOG.debug("Deleting refset with id: " + refsetRuleId);
        BaseRefsetRule deleted = refsetRuleRepository.findOne(refsetRuleId);
        if (deleted == null) {
            LOG.debug("No refset found with id: " + refsetRuleId);
            throw new RefsetRuleNotFoundException();
        }
        refsetRuleRepository.delete(deleted);
        return deleted;
    }  
}
