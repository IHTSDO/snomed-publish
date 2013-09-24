package com.ihtsdo.snomed.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.UnrecognisedRefsetException;
import com.ihtsdo.snomed.model.refset.BaseRefsetRule;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.model.refset.RefsetRule;
import com.ihtsdo.snomed.model.refset.rule.BaseSetOperationRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;

@Named
public class RefsetPlanBuilder {
    
    private static final Logger LOG = LoggerFactory.getLogger(RefsetPlanBuilder.class);

    
    @Inject
    private ConceptService conceptService;
    
    

    public RefsetPlan build(RefsetPlanDto planDto){
        Map<Long, RefsetRule> idRuleMap = createRules(planDto); 
        RefsetPlan plan = buildLinks(idRuleMap);
        plan.setId(planDto.getId());
        if (LOG.isDebugEnabled()) LOG.debug("Built plan: {}", plan.toString());
        return plan;
    }

    private Map<Long, RefsetRule> createRules(RefsetPlanDto planDto) {
        Map<Long, RefsetRule> idRuleMap = new HashMap<>();
        for (RefsetRuleDto ruleDto : planDto.getRefsetRules()){
            try {
                BaseRefsetRule rule = (BaseRefsetRule)RefsetRuleDto.TYPE_CLASS_MAP.get(ruleDto.getType()).newInstance();
                rule.setId(ruleDto.getId());
                idRuleMap.put(rule.getId(), rule);
                if (rule instanceof ListConceptsRefsetRule){
                    ListConceptsRefsetRule lcRule = (ListConceptsRefsetRule)rule;
                    for (ConceptDto c : ruleDto.getConcepts()){
                        lcRule.addConcept(conceptService.findBySerialisedId(c.getId()));
                    }
                }else if (rule instanceof BaseSetOperationRefsetRule){
                    //nothing for now
                }
                else{
                    throw new UnrecognisedRefsetException("I've not been able to handle RefsetRule of class " + rule.getClass());                    
                }
            } catch (Exception e) {
                throw new UnrecognisedRefsetException("I've not been able to handle RefsetRule of class " + RefsetRuleDto.TYPE_CLASS_MAP.get(ruleDto.getType()));
            }
        }
        return idRuleMap;
    }
    
    private RefsetPlan buildLinks(Map<Long, RefsetRule> idRuleMap){
        RefsetPlan plan = new RefsetPlan();
        
        
        
        return plan;
    }
}
