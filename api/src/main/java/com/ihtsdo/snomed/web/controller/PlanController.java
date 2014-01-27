package com.ihtsdo.snomed.web.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.validation.ValidationResult;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.refset.Plan;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.service.refset.PlanService;
import com.ihtsdo.snomed.service.refset.RefsetService;
import com.ihtsdo.snomed.web.dto.RefsetErrorBuilder;
import com.ihtsdo.snomed.web.dto.RefsetPlanResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto.Status;

@Controller
@RequestMapping("/refsets")
@Transactional(value = "transactionManager")
public class PlanController {
    private static final Logger LOG = LoggerFactory.getLogger(PlanController.class);    

    @Inject
    RefsetService refsetService;
        
    @Inject
    PlanService planService;
    
    @Inject
    RefsetErrorBuilder refsetErrorBuilder;
     
    @Transactional
    @RequestMapping(value = "{refsetName}/plan", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody PlanDto getRefsetPlan(@PathVariable String refsetName) {
        Refset refset = refsetService.findByPublicId(refsetName);
        System.out.println("Found refset " + refset);
        return PlanDto.parse(refset.getPlan());
    }    

    @Transactional
    @RequestMapping(value = "{refsetName}/plan", method = RequestMethod.PUT, 
    produces = {MediaType.APPLICATION_JSON_VALUE}, 
    consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<RefsetPlanResponseDto> updateRefsetPlan(
            @Valid @RequestBody PlanDto planDto,
            BindingResult bindingResult, 
            @PathVariable String refsetName)
    {
        LOG.debug("Controller received request to update refset plan {} for refset {}", 
                planDto.toString(), refsetName);
        
        RefsetPlanResponseDto response = new RefsetPlanResponseDto();
        response.setRefsetPlan(planDto);
        response.setCode(RefsetResponseDto.FAIL);
        response.setStatus(Status.FAIL);

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<RefsetPlanResponseDto>(
                    refsetErrorBuilder.build(bindingResult, response, RefsetResponseDto.FAIL), 
                    HttpStatus.NOT_ACCEPTABLE);
        }
        
        ValidationResult validationResult = planDto.validate();
        
        if (!validationResult.isSuccess()){
            response.setStatus(Status.FAIL);
            response.setCode(RefsetResponseDto.FAIL_VALIDATION);
            return new ResponseEntity<RefsetPlanResponseDto>(response, HttpStatus.NOT_ACCEPTABLE);            
        }
        
        Refset refset = refsetService.findByPublicId(refsetName);        
        if (refset == null){
            return new ResponseEntity<RefsetPlanResponseDto>(HttpStatus.NOT_FOUND);
        }
        
        Plan plan;
        try {
            plan = planService.update(planDto);
            refset.setPlan(plan);
            refsetService.update(refset);
            response.setRefsetPlan(PlanDto.parse(plan));
            response.setCode(RefsetResponseDto.SUCCESS_UPDATED);
            response.setStatus(Status.UPDATED);
            
            return new ResponseEntity<RefsetPlanResponseDto>(response, HttpStatus.OK);              
            
        } catch (RefsetPlanNotFoundException e) {
            return new ResponseEntity<RefsetPlanResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ValidationException e) {
            LOG.debug("Update refset failed", e);
            response.setStatus(Status.FAIL);
            response.setCode(RefsetResponseDto.FAIL_VALIDATION);
        } catch (RefsetTerminalRuleNotFoundException e) {
            LOG.debug("Update refset failed", e);
            response.setStatus(Status.FAIL);
            response.setCode(RefsetResponseDto.FAIL_RULE_NOT_FOUND);            
        }
        
        return new ResponseEntity<RefsetPlanResponseDto>(response, HttpStatus.NOT_ACCEPTABLE);
    }
    
    @Transactional
    @RequestMapping(value = "validate", method = RequestMethod.PUT, 
    produces = {MediaType.APPLICATION_JSON_VALUE}, 
    consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<RefsetPlanResponseDto> validateRefsetPlan(@Valid @RequestBody PlanDto planDto,
            BindingResult result){
        LOG.debug("Controller received request to validate refset {}", planDto.toString());
        RefsetPlanResponseDto response = new RefsetPlanResponseDto();
        response.setRefsetPlan(planDto);
        response.setCode(RefsetResponseDto.FAIL);
        response.setStatus(Status.FAIL);
        
        if (result.hasErrors()) {
            return new ResponseEntity<RefsetPlanResponseDto>(
                    refsetErrorBuilder.build(result, response, RefsetResponseDto.FAIL), 
                    HttpStatus.NOT_ACCEPTABLE);
        }
        
        ValidationResult validationResult = planDto.validate();
        
        //TODO: Add check to see of concepts exists in database
        
        if (validationResult.isSuccess()){
            response.setStatus(Status.VALIDATED);
            response.setCode(RefsetResponseDto.SUCCESS);
            return new ResponseEntity<RefsetPlanResponseDto>(response, HttpStatus.OK);            
        }

        return new ResponseEntity<RefsetPlanResponseDto>(
                refsetErrorBuilder.build(validationResult, response), 
                HttpStatus.NOT_ACCEPTABLE);
    }

}