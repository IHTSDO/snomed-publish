package com.ihtsdo.snomed.web.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.ProgrammingException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ConceptNotFoundValidationException;
import com.ihtsdo.snomed.exception.validation.MoreThanOneCandidateForTerminalException;
import com.ihtsdo.snomed.exception.validation.NoTerminalCandidateException;
import com.ihtsdo.snomed.exception.validation.RuleValidationException;
import com.ihtsdo.snomed.exception.validation.UnrecognisedRefsetRuleTypeException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.model.refset.RefsetRule;
import com.ihtsdo.snomed.model.refset.rule.BaseSetOperationRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.RefsetPlanService;
import com.ihtsdo.snomed.service.RefsetRuleService;
import com.ihtsdo.snomed.web.repository.RefsetPlanRepository;
import com.ihtsdo.snomed.web.repository.RefsetRuleRepository;

@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
//@Transactional (value = "transactionManager", readOnly = true)
public class RepositoryRefsetPlanService implements RefsetPlanService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryRefsetPlanService.class);
    
    @Inject
    protected RefsetPlanRepository refsetPlanRepository; 
    
    @Inject
    protected RefsetRuleService refsetRuleService;
    
    @Inject
    protected RefsetRuleRepository refsetRuleRepository;    

    
    @Inject
    protected ConceptService conceptService;
    
    @Override
    @Transactional(readOnly = true)
    public RefsetPlan findById(Long id) {
        LOG.debug("Finding refset plan by id: " + id);
        return refsetPlanRepository.findOne(id);
    }
    
    @Override
    @Transactional(rollbackFor = {RefsetPlanNotFoundException.class, ValidationException.class, RefsetTerminalRuleNotFoundException.class})
    public RefsetPlan update(RefsetPlanDto updated) throws ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException{
        LOG.debug("Updating refset plan with information: " + updated);
        RefsetPlan plan = refsetPlanRepository.findOne(updated.getId());
        if (plan == null) {
            throw new RefsetPlanNotFoundException(updated.getId(), "No refset plan found with id: " + updated.getId());
        }
        if (plan.getTerminal() != null){
            refsetRuleService.delete(plan.getTerminal().getId());
        }
        plan.setTerminal(createRules(updated));
        RefsetPlan saved = refsetPlanRepository.save(plan);
        LOG.debug("Updated refset plan with information: " + saved);
        return saved;
    }    

    @Override
    @Transactional(rollbackFor = ValidationException.class)
    public RefsetPlan create(RefsetPlanDto created) throws ValidationException{
        LOG.debug("Creating new refset plan [{}]", created.toString());

        RefsetPlan plan = new RefsetPlan();
        plan.setTerminal(createRules(created));

        RefsetPlan persisted = refsetPlanRepository.save(plan);
        LOG.debug("Created new refset plan [{}]", persisted.toString());
        return persisted;
    }

    @Override
    @Transactional(rollbackFor = RefsetPlanNotFoundException.class)
    public RefsetPlan delete(Long refsetPlanId) throws RefsetPlanNotFoundException {
        LOG.debug("Deleting refset plan with id: " + refsetPlanId);
        RefsetPlan deleted = refsetPlanRepository.findOne(refsetPlanId);
        if (deleted == null) {
            throw new RefsetPlanNotFoundException(refsetPlanId, "No refset plan found with id: " + refsetPlanId);
        }
        refsetPlanRepository.delete(deleted);
        return deleted;
    }  
    
    private RefsetRule createRules(RefsetPlanDto planDto) throws ValidationException
            
    {
        if ((planDto.getRefsetRules() == null) || planDto.getRefsetRules().isEmpty()){
            return null;
        }
        LOG.debug("Creating rules for refset plan {}", planDto);
        
        Map<Long, Long> statedIdToPersistedId = new HashMap<Long, Long>();
        Map<Long, RefsetRule> statedIdToRuleInstance = new HashMap<Long, RefsetRule>();
        Map<Long, RefsetRule> persistedIdToRuleInstance = new HashMap<Long, RefsetRule>();
        Set<Long> referencedPersistedIds = new HashSet<Long>();
        Set<Long> allPersistedIds = new HashSet<Long>();

        for (RefsetRuleDto ruleDto : planDto.getRefsetRules()){
            LOG.debug("Indexing and instatiating rule {}", ruleDto);
            Long statedId = ruleDto.getId();
            
            if (statedId == null){
                throw new RuleValidationException(ruleDto, "You need to validate the plan DTO before persisting");
            }
            
            RefsetRule created;
            try {
                created = refsetRuleRepository.save(BaseRefsetRule.getRuleInstanceFor(ruleDto));
                statedIdToPersistedId.put(statedId, created.getId());
                statedIdToRuleInstance.put(statedId, created);
                persistedIdToRuleInstance.put(created.getId(), created);
                allPersistedIds.add(created.getId());

            } catch (UnrecognisedRefsetRuleTypeException e) {
                throw new RuleValidationException(ruleDto, "You need to validate the plan DTO before persisting", e);
            }
            
        }
        
        LOG.debug("First pass of rules complete. Now we populate individual rule data");
        
        for (RefsetRuleDto ruleDto : planDto.getRefsetRules()){
            LOG.debug("2: Populating rule {}", ruleDto);
            
            RefsetRule rule = statedIdToRuleInstance.get(ruleDto.getId());
            
            if (ruleDto.isSetOperation()){
                BaseSetOperationRefsetRule soRule = (BaseSetOperationRefsetRule)rule;
                soRule.setLeftRule(statedIdToRuleInstance.get(ruleDto.getLeft()));
                soRule.setRightRule(statedIdToRuleInstance.get(ruleDto.getRight()));
                referencedPersistedIds.add(statedIdToPersistedId.get(ruleDto.getLeft()));
                referencedPersistedIds.add(statedIdToPersistedId.get(ruleDto.getRight()));
            } else if (ruleDto.isListOperation()){
                updateConcepts(ruleDto, rule);
            } else{
                throw new ProgrammingException("Failed to handle rule type " + ruleDto.getType() + " for rule DTO " + ruleDto.getId());
            }
        }
        
        SetView<Long> unReferencedPersistedIds = Sets.difference(allPersistedIds, referencedPersistedIds);
        if (unReferencedPersistedIds.size() > 1){
            throw new MoreThanOneCandidateForTerminalException(planDto, "Found more than one unreferenced rule. There can only be one unreferenced rule, as the terminal candidate." + 
                              " Unreferenced rules are: " + unReferencedPersistedIds.toString());
        }
        else if (unReferencedPersistedIds.size() < 1){
            throw new NoTerminalCandidateException(planDto, "Unabled to find an unreferenced rule to act as terminal candidate");            
        }
        
        return persistedIdToRuleInstance.get(unReferencedPersistedIds.iterator().next());                
    }

    
    
    private void updateConcepts(RefsetRuleDto ruleDto, RefsetRule rule) throws ConceptNotFoundValidationException {
        for (ConceptDto conceptDto : ruleDto.getConcepts()){
            Concept c = conceptService.findBySerialisedId(conceptDto.getId());
            if (c == null){
                throw new ConceptNotFoundValidationException(ruleDto, conceptDto, "Did not find concept with serialisedId " + conceptDto.getId() + " for rule DTO " + rule.getId());
            }
            ((ListConceptsRefsetRule)rule).addConcept(c);
        }
    }     
        
        
//        
//        Map<Long, RefsetRule> dtoIdToRuleMap = new HashMap<>();
//        Map<Long, RefsetRuleDto> dtoIdToDtoMap = new HashMap<>();
//        Set<RefsetRule> allRules  = new HashSet<>();
//        for (RefsetRuleDto planDto : planDto.getRefsetRules()){
//            LOG.debug("Processing rule {}", planDto);
//            RefsetRule rule;
//            if (RefsetRuleDto.isPersisted(planDto.getId())){
//                rule = refsetRuleService.update(planDto);
//            }
//            else{
//                rule = refsetRuleService.create(planDto);
//            }
//            
//            if (rule == null){
//                throw new RefsetRuleNotFoundValidationException(planDto.getId(), "Unable to find rule for planDto " + planDto.toString());
//            }
//            
//            dtoIdToRuleMap.put(planDto.getId(), rule);
//            dtoIdToDtoMap.put(planDto.getId(), planDto);
//            allRules.add(rule);
//        }
//        LOG.debug("First pass complete");
//        LOG.debug("dtoIdToRuleMap is [{}]", dtoIdToRuleMap);
//        LOG.debug("dtoIdToDtoMap is [{}]", dtoIdToDtoMap);
//        LOG.debug("allRules is [{}]", allRules);
//        
//        Iterator<Long> keyIt = dtoIdToRuleMap.keySet().iterator();
//        Set<RefsetRule> referencedRules = new HashSet<>();
//        while(keyIt.hasNext()){
//            Long dtoId = keyIt.next();
//            RefsetRule rule = dtoIdToRuleMap.get(dtoId);
//            RefsetRuleDto planDto = dtoIdToDtoMap.get(dtoId);
//            
//            if (planDto == null){
//                throw new ProgrammingException("Rule DTO was null");
//            }            
//            
//            LOG.debug("Processing RuleDto with id {}", dtoId);
//            LOG.debug("Rule is [{}]", rule);
//            LOG.debug("planDto is [{}]", planDto);
//            if (rule instanceof BaseSetOperationRefsetRule){
//                LOG.debug("Found BaseSetOperationRule");
//                BaseSetOperationRefsetRule setOpRule = (BaseSetOperationRefsetRule) rule;
//                setOpRule.setLeftRule(dtoIdToRuleMap.get(planDto.getLeft()));
//                setOpRule.setRightRule(dtoIdToRuleMap.get(planDto.getRight()));
//                if ((setOpRule.getLeft() == null)||(setOpRule.getRight() == null)){
//                    throw new UnconnectedRefsetRuleException(planDto.getId(), "Rule DTO [" + planDto.toString() + "] has unconnected inputs");
//                }
//                referencedRules.add(setOpRule.getLeft());
//                referencedRules.add(setOpRule.getRight());
//                LOG.debug("Updated BaseSetOperationRule {}", setOpRule);
//            }else if (rule instanceof ListConceptsRefsetRule){
////                ListConceptsRefsetRule lcRule = (ListConceptsRefsetRule)rule;
////                lcRule.setConcepts(new HashSet<Concept>());
////                for (ConceptDto c : planDto.getConcepts()){
////                    lcRule.addConcept(conceptService.findById(c.getId()));
////                }
//            }else{
//                throw new UnrecognisedRefsetRuleTypeException("Unable to handle rule of type " + planDto.getType());
//            }
//        }
//        LOG.debug("Done Processing");
//        LOG.debug("referencedRules is {}", referencedRules);
//        Set<RefsetRule> unreferencedRules = Sets.difference(allRules, referencedRules);
//        LOG.debug("unreferencedRule(s) is {}", unreferencedRules);
//        if (unreferencedRules.size() != 1){
//            throw new UnReferencedRuleException("Found more than one rule that has not been connected by another rule. Only the terminal rule can remain unreferenced");
//        }
//        return unreferencedRules.iterator().next();
//        
//    } 

}