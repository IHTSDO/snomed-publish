package com.ihtsdo.snomed.web.controller;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.dto.refset.validation.ValidationResult;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.ConceptsCacheNotBuiltException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Plan;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.model.xml.XmlRefsetConcept;
import com.ihtsdo.snomed.model.xml.XmlRefsetConcepts;
import com.ihtsdo.snomed.model.xml.XmlRefsetShort;
import com.ihtsdo.snomed.service.refset.PlanService;
import com.ihtsdo.snomed.service.refset.RefsetService;
import com.ihtsdo.snomed.service.refset.SnapshotService;
import com.ihtsdo.snomed.web.dto.RefsetPlanResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto.Status;

@Controller
@RequestMapping("/api")
@Transactional(value = "transactionManager")
public class RefsetApiController {
    private static final Logger LOG = LoggerFactory.getLogger(RefsetApiController.class);

    @Inject
    RefsetService refsetService;
    
    @Inject 
    SnapshotService snapshotService;
    
    @Inject
    PlanService planService;
    
    @Inject
    RefsetErrorBuilder error;
    
    @Resource
    private MessageSource messageSource;
    
    @PersistenceContext(unitName="hibernatePersistenceUnit")
    EntityManager em;
    
    @Transactional
    @RequestMapping(value = "refsets", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<XmlRefsetShort> getAllRefsets() throws Exception {
        return getRefsetsDto();
    }    
        
    @Transactional
    @RequestMapping(value = "refsets/{pubId}", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody RefsetDto getRefset(@PathVariable String pubId) throws Exception {
        return getRefsetDto(pubId);
    }    
    
    @Transactional
    @RequestMapping(value = "refsets/{pubId}/concepts.json", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody XmlRefsetConcepts getConcepts(@PathVariable String pubId) throws Exception {
        return new XmlRefsetConcepts(getXmlConceptDtos(pubId));
    }    
    
    @Transactional
    @RequestMapping(value = "refsets/{pubId}/snapshot/{snapshotId}/concepts.json", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody XmlRefsetConcepts getSnapshotConcepts(@PathVariable String pubId, String snapshotId) throws Exception {
        LOG.debug("Received request for concepts of snapshot [{}] for refset [{}]", snapshotId, pubId);
        
        Refset refset = refsetService.findByPublicId(pubId);
        Snapshot snapshot = snapshotService.findByPublicId(snapshotId);
        
        if ((refset == null) || (snapshot == null) || !refset.getSnapshots().contains(snapshot)){
            //error
        }
        
        List<XmlRefsetConcept> xmlConcepts = new ArrayList<>();
        for (Concept c : snapshot.getConcepts()){
            xmlConcepts.add(new XmlRefsetConcept(c));
        }
        LOG.debug("returning xmlconcepts [{}]", xmlConcepts.size());

        
        return null;
    }      

    @Transactional
    @RequestMapping(value = "refsets/{pubId}/plan.json", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody PlanDto getRefsetPlan(@PathVariable String pubId) throws Exception {
        Refset refset = refsetService.findByPublicId(pubId);
        System.out.println("Found refset " + refset);
        return PlanDto.parse(refset.getPlan());
    }    

    @Transactional
    @RequestMapping(value = "refsets/{pubId}", 
            method = RequestMethod.DELETE, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefsetResponseDto> deleteRefset(HttpServletRequest request, @PathVariable String pubId){
        LOG.debug("Received request to delete refset [{}]", pubId);
        RefsetResponseDto response = new RefsetResponseDto();
        response.setPublicId(pubId);
        try {
            Refset deleted = refsetService.delete(pubId);
            response.setRefset(
                RefsetDto.getBuilder(
                    deleted.getId(), 
                    (deleted.getConcept() == null) ? 0 : deleted.getConcept().getSerialisedId(),
                    (deleted.getConcept() == null) ? null : deleted.getConcept().getDisplayName(),
                    deleted.getTitle(), deleted.getDescription(), deleted.getPublicId(), 
                    PlanDto.parse(deleted.getPlan())).build());
            response.setCode(RefsetResponseDto.SUCCESS_DELETED);
            response.setStatus(Status.DELETED);
            return new ResponseEntity<RefsetResponseDto>(response, HttpStatus.OK);
        } catch (RefsetNotFoundException e) {
            response.setCode(RefsetResponseDto.FAIL_REFSET_NOT_FOUND);
            response.setStatus(Status.FAIL);
            response.setGlobalErrors(Arrays.asList(messageSource.getMessage(
                    "global.error.refset.not.found", 
                    Arrays.asList(pubId).toArray(), 
                    LocaleContextHolder.getLocale())));
            return new ResponseEntity<RefsetResponseDto>(response, HttpStatus.PRECONDITION_FAILED);
        }
    }

    @Transactional
    @RequestMapping(value = "refsets", method = RequestMethod.POST, 
    produces = {MediaType.APPLICATION_JSON_VALUE }, 
    consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<RefsetResponseDto> createRefset(@Valid @RequestBody RefsetDto refsetDto, 
            BindingResult bindingResult)
    {
        LOG.debug("Controller received request to create new refset [{}]",
                refsetDto.toString());

        int returnCode = RefsetResponseDto.FAIL;
        RefsetResponseDto response = new RefsetResponseDto();
        
        Refset refset = refsetService.findByPublicId(refsetDto.getPublicId());
        if (refset != null){
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), "publicId", refsetDto.getPublicId(),
                    false, null,null, "xml.response.error.publicid.not.unique"));
        }

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<RefsetResponseDto>(error.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            Refset created = refsetService.create(refsetDto);
            if (created == null){
                return new ResponseEntity<RefsetResponseDto>(error.build(bindingResult, response, RefsetResponseDto.FAIL), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<RefsetResponseDto>(success(response, created, Status.CREATED, RefsetResponseDto.SUCCESS_CREATED), HttpStatus.CREATED);
        } catch (NonUniquePublicIdException e) {
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), "publicId", refsetDto.getPublicId(),
                    false, null,null, "xml.response.error.publicid.not.unique"));
            return new ResponseEntity<RefsetResponseDto>(error.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        } catch (RefsetConceptNotFoundException e) {
            LOG.debug("Create refset failed", e);
            response.setStatus(Status.FAIL);
            response.setCode(RefsetResponseDto.FAIL_VALIDATION);
        } catch (ValidationException e) {
            LOG.debug("Create refset failed", e);
            response.setStatus(Status.FAIL);
            response.setCode(RefsetResponseDto.FAIL_VALIDATION);
        } 
        
        return new ResponseEntity<RefsetResponseDto>(response, HttpStatus.NOT_ACCEPTABLE);
    }
    
    @Transactional
    @RequestMapping(value = "refsets/{pubId}/plan", method = RequestMethod.PUT, 
    produces = {MediaType.APPLICATION_JSON_VALUE}, 
    consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<RefsetPlanResponseDto> updateRefsetPlan(
            @Valid @RequestBody PlanDto planDto,
            BindingResult bindingResult, 
            @PathVariable String pubId)
    {
        LOG.debug("Controller received request to update refset plan {} for refset {}", 
                planDto.toString(), pubId);
        
        RefsetPlanResponseDto response = new RefsetPlanResponseDto();
        response.setRefsetPlan(planDto);
        response.setCode(RefsetResponseDto.FAIL);
        response.setStatus(Status.FAIL);

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<RefsetPlanResponseDto>(
                    error.build(bindingResult, response, RefsetResponseDto.FAIL), 
                    HttpStatus.NOT_ACCEPTABLE);
        }
        
        ValidationResult validationResult = planDto.validate();
        
        if (!validationResult.isSuccess()){
            response.setStatus(Status.FAIL);
            response.setCode(RefsetResponseDto.FAIL_VALIDATION);
            return new ResponseEntity<RefsetPlanResponseDto>(response, HttpStatus.NOT_ACCEPTABLE);            
        }
        
        Refset refset = refsetService.findByPublicId(pubId);        
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
    @RequestMapping(value = "refsets/validate", method = RequestMethod.PUT, 
    produces = {MediaType.APPLICATION_JSON_VALUE}, 
    consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<RefsetPlanResponseDto> validateRefsetPlan(@Valid @RequestBody PlanDto planDto,BindingResult result){
        LOG.debug("Controller received request to validate refset {}", planDto.toString());
        RefsetPlanResponseDto response = new RefsetPlanResponseDto();
        response.setRefsetPlan(planDto);
        response.setCode(RefsetResponseDto.FAIL);
        response.setStatus(Status.FAIL);
        
        if (result.hasErrors()) {
            return new ResponseEntity<RefsetPlanResponseDto>(
                    error.build(result, response, RefsetResponseDto.FAIL), 
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
                error.build(validationResult, response), 
                HttpStatus.NOT_ACCEPTABLE);
    }

    
    
    private RefsetResponseDto success(RefsetResponseDto response, Refset updated, Status status, int returnCode) {
        response.setRefset(RefsetDto.getBuilder(updated.getId(), 
                (updated.getConcept() == null) ? 0 : updated.getConcept().getSerialisedId(),
                (updated.getConcept() == null) ? null : updated.getConcept().getDisplayName(),
                updated.getTitle(), updated.getDescription(), updated.getPublicId(), 
                PlanDto.parse(updated.getPlan())).build());
        response.setStatus(status);
        response.setCode(returnCode);
        return response;
    }

    private List<XmlRefsetConcept> getXmlConceptDtos(String pubId) throws ConceptsCacheNotBuiltException, MalformedURLException {
        Refset refset = refsetService.findByPublicId(pubId);
        System.out.println("Found refset " + refset);
        refset.getPlan().refreshConceptsCache();
        Set<Concept> concepts = refset.getPlan().getConcepts();
        List<XmlRefsetConcept> xmlConcepts = new ArrayList<>();
        for (Concept c : concepts){
            xmlConcepts.add(new XmlRefsetConcept(c));
        }
        System.out.println("returning xmlconcepts [" + xmlConcepts.size() + "]");
        return xmlConcepts;
    }    

    private List<XmlRefsetShort> getRefsetsDto() throws MalformedURLException {
        List<Refset> refsets = refsetService.findAll();
        List<XmlRefsetShort> xmlRefsetShorts = new ArrayList<>();
        for (Refset r : refsets){
            xmlRefsetShorts.add(new XmlRefsetShort(r));
        }
        return xmlRefsetShorts;
    }    
 
    private RefsetDto getRefsetDto(String pubId) {
        Refset refset = refsetService.findByPublicId(pubId);
        System.out.println("Found refset " + refset);
        RefsetDto refsetDto = RefsetDto.getBuilder(refset.getId(), 
                (refset.getConcept() == null) ? 0 : refset.getConcept().getSerialisedId(),
                (refset.getConcept() == null) ? null : refset.getConcept().getDisplayName(),
                refset.getTitle(), refset.getDescription(), refset.getPublicId(), 
                PlanDto.parse(refset.getPlan())).build();
        System.out.println("Returning refsetDto " + refsetDto);

        return refsetDto;
    }    
       
    
}