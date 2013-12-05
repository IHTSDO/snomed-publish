package com.ihtsdo.snomed.web.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.ProgrammingException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.UnReferencedReferenceRuleException;
import com.ihtsdo.snomed.exception.UnconnectedRefsetRuleException;
import com.ihtsdo.snomed.exception.UnrecognisedRefsetRuleTYpeException;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.model.refset.RefsetRule;
import com.ihtsdo.snomed.model.refset.rule.BaseSetOperationRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.service.RefsetPlanService;
import com.ihtsdo.snomed.service.RefsetRuleService;
import com.ihtsdo.snomed.web.repository.RefsetPlanRepository;

@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
//@Transactional (value = "transactionManager", readOnly = true)
public class RepositoryRefsetPlanService implements RefsetPlanService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryRefsetPlanService.class);
    
    @Inject
    protected RefsetPlanRepository refsetPlanRepository; 
    
    @Inject
    protected RefsetRuleService refsetRuleService;
    
    @Override
    @Transactional(readOnly = true)
    public RefsetPlan findById(Long id) {
        LOG.debug("Finding refset plan by id: " + id);
        return refsetPlanRepository.findOne(id);
    }
    
    @Override
    @Transactional(rollbackFor = {RefsetPlanNotFoundException.class})
    public RefsetPlan update(RefsetPlanDto updated) throws RefsetPlanNotFoundException, UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException, ConceptNotFoundException{
        LOG.debug("Updating refset plan with information: " + updated);
        RefsetPlan plan = refsetPlanRepository.findOne(updated.getId());
        if (plan == null) {
            throw new RefsetPlanNotFoundException(updated.getId(), "No refset plan found with id: " + updated.getId());
        }
        plan.setTerminal(createRules(updated));
        RefsetPlan saved = refsetPlanRepository.save(plan);
        LOG.debug("Updated refset plan with information: " + saved);
        return saved;
    }    

    @Override
    @Transactional
    public RefsetPlan create(RefsetPlanDto created) throws UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException, ConceptNotFoundException {
        LOG.debug("Creating new refset plan [{}]", created.toString());

        RefsetPlan plan = new RefsetPlan();
        plan.setTerminal(createRules(created));

        RefsetPlan persisted = refsetPlanRepository.save(plan);
        LOG.debug("Created new refset plan [{}]", persisted.toString());
        return persisted;
    }

    @Override
    @Transactional(rollbackFor = RefsetRuleNotFoundException.class)
    public RefsetPlan delete(Long refsetPlanId) throws RefsetPlanNotFoundException {
        LOG.debug("Deleting refset plan with id: " + refsetPlanId);
        RefsetPlan deleted = refsetPlanRepository.findOne(refsetPlanId);
        if (deleted == null) {
            throw new RefsetPlanNotFoundException(refsetPlanId, "No refset plan found with id: " + refsetPlanId);
        }
        refsetPlanRepository.delete(deleted);
        return deleted;
    }  
    
    private RefsetRule createRules(RefsetPlanDto planDto) throws UnconnectedRefsetRuleException, UnReferencedReferenceRuleException, RefsetRuleNotFoundException, ConceptNotFoundException{
        if ((planDto.getRefsetRules() == null) || planDto.getRefsetRules().isEmpty()){
            return null;
        }
        LOG.debug("Creating rules for refset plan {}", planDto);
        Map<Long, RefsetRule> dtoIdToRuleMap = new HashMap<>();
        Map<Long, RefsetRuleDto> dtoIdToDtoMap = new HashMap<>();
        Set<RefsetRule> allRules  = new HashSet<>();
        for (RefsetRuleDto ruleDto : planDto.getRefsetRules()){
            LOG.debug("Processing rule {}", ruleDto);
            RefsetRule rule;
            if (RefsetRuleDto.isPersisted(ruleDto.getId())){
                rule = refsetRuleService.update(ruleDto);
            }
            else{
                rule = refsetRuleService.create(ruleDto);
            }
            
            if (rule == null){
                throw new RefsetRuleNotFoundException(ruleDto.getId(), "Unable to find rule for ruleDto " + ruleDto.toString());
            }
            
            dtoIdToRuleMap.put(ruleDto.getId(), rule);
            dtoIdToDtoMap.put(ruleDto.getId(), ruleDto);
            allRules.add(rule);
        }
        LOG.debug("First pass complete");
        LOG.debug("dtoIdToRuleMap is [{}]", dtoIdToRuleMap);
        LOG.debug("dtoIdToDtoMap is [{}]", dtoIdToDtoMap);
        LOG.debug("allRules is [{}]", allRules);
        
        Iterator<Long> keyIt = dtoIdToRuleMap.keySet().iterator();
        Set<RefsetRule> referencedRules = new HashSet<>();
        while(keyIt.hasNext()){
            Long dtoId = keyIt.next();
            RefsetRule rule = dtoIdToRuleMap.get(dtoId);
            RefsetRuleDto ruleDto = dtoIdToDtoMap.get(dtoId);
            
            if (ruleDto == null){
                throw new ProgrammingException("Rule DTO was null");
            }            
            
            LOG.debug("Processing RuleDto with id {}", dtoId);
            LOG.debug("Rule is [{}]", rule);
            LOG.debug("ruleDto is [{}]", ruleDto);
            if (rule instanceof BaseSetOperationRefsetRule){
                LOG.debug("Found BaseSetOperationRule");
                BaseSetOperationRefsetRule setOpRule = (BaseSetOperationRefsetRule) rule;
                setOpRule.setLeftRule(dtoIdToRuleMap.get(ruleDto.getLeft()));
                setOpRule.setRightRule(dtoIdToRuleMap.get(ruleDto.getRight()));
                if ((setOpRule.getLeft() == null)||(setOpRule.getRight() == null)){
                    throw new UnconnectedRefsetRuleException(ruleDto.getId(), "Rule DTO [" + ruleDto.toString() + "] has unconnected inputs");
                }
                referencedRules.add(setOpRule.getLeft());
                referencedRules.add(setOpRule.getRight());
                LOG.debug("Updated BaseSetOperationRule {}", setOpRule);
            }else if (rule instanceof ListConceptsRefsetRule){
//                ListConceptsRefsetRule lcRule = (ListConceptsRefsetRule)rule;
//                lcRule.setConcepts(new HashSet<Concept>());
//                for (ConceptDto c : ruleDto.getConcepts()){
//                    lcRule.addConcept(conceptService.findById(c.getId()));
//                }
            }else{
                throw new UnrecognisedRefsetRuleTYpeException("Unable to handle rule of type " + ruleDto.getType());
            }
        }
        LOG.debug("Done Processing");
        LOG.debug("referencedRules is {}", referencedRules);
        Set<RefsetRule> unreferencedRules = Sets.difference(allRules, referencedRules);
        LOG.debug("unreferencedRule(s) is {}", unreferencedRules);
        if (unreferencedRules.size() != 1){
            throw new UnReferencedReferenceRuleException("Found more than one rule that has not been connected by another rule. Only the terminal rule can remain unreferenced");
        }
        return unreferencedRules.iterator().next();
        
    }    
}