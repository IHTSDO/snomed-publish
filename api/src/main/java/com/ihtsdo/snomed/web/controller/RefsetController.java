package com.ihtsdo.snomed.web.controller;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.dto.refset.validation.ValidationResult;
import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
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
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiser;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory;
import com.ihtsdo.snomed.service.serialiser.SnomedSerialiserFactory.Form;
import com.ihtsdo.snomed.web.dto.RefsetErrorBuilder;
import com.ihtsdo.snomed.web.dto.RefsetPlanResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto.Status;
import com.ihtsdo.snomed.web.dto.SnapshotResponseDto;

@Controller
@RequestMapping("/refsets")
@Transactional(value = "transactionManager")
public class RefsetController {
    private static final Logger LOG = LoggerFactory.getLogger(RefsetController.class);
    
    public static final String RF2_MIME_TYPE = "application/vnd.ihtsdo.snomed.rf2.terminology.concept+txt";    

    @Inject
    RefsetService refsetService;
    
    @Inject 
    SnapshotService snapshotService;
    
    @Inject
    PlanService planService;
    
    @Inject
    RefsetErrorBuilder refsetErrorBuilder;
    
    @Resource
    private MessageSource messageSource;
    
    
    @Transactional
    @RequestMapping(value = "", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<XmlRefsetShort> getAllRefsets(){
        return getRefsetsDto();
    }    
        
    @Transactional
    @RequestMapping(value = "{refsetName}", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody RefsetDto getRefset(@PathVariable String refsetName){
        return getRefsetDto(refsetName);
    }    
    
    @Transactional
    @RequestMapping(value = "{refsetName}/concepts.json", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody XmlRefsetConcepts getConcepts(@PathVariable String refsetName){
        return new XmlRefsetConcepts(getXmlConceptDtos(refsetName));
    }
    
    @Transactional
    @RequestMapping(value = "{refsetName}/snapshots", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SnapshotDto>> getAllSnapshots(@PathVariable String refsetName){
        LOG.debug("Received request for all snapshots for refset [{}]", refsetName);
        
        Refset refset = refsetService.findByPublicId(refsetName);

        if (refset == null){
            return new ResponseEntity<List<SnapshotDto>>(HttpStatus.NOT_FOUND);
        }
        
        List<SnapshotDto> snapshots = new ArrayList<SnapshotDto>(refset.getSnapshots().size());
        for (Snapshot snapshot : refset.getSnapshots()){
            snapshots.add(SnapshotDto.parseSansConcepts(snapshot));
        }
        
        return new ResponseEntity<List<SnapshotDto>>(snapshots, HttpStatus.OK);
    }    
    
    
    @Transactional
    @RequestMapping(value = "{refsetName}/snapshot/{snapshotName}.json", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SnapshotDto>  getSnapshotWithConceptsAsJson(
            @PathVariable String refsetName, 
            @PathVariable String snapshotName) throws Exception 
    {
        LOG.debug("Received request for concepts of snapshot [{}] for refset [{}] in json format", snapshotName, refsetName);
        
        Refset refset = refsetService.findByPublicId(refsetName);
        if (refset == null){
            return new ResponseEntity<SnapshotDto>(HttpStatus.NOT_FOUND);
        }
        
        Snapshot snapshot = refset.getSnapshot(snapshotName);
        if (snapshot == null){
            return new ResponseEntity<SnapshotDto>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<SnapshotDto>(SnapshotDto.parse(snapshot), HttpStatus.OK);
    }
    
    @Transactional
    @RequestMapping(value = "{refsetName}/snapshot/{snapshotName}.xml", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<SnapshotDto>  getSnapshotWithConceptsAsXml(
            @PathVariable String refsetName, 
            @PathVariable String snapshotName) throws Exception 
    {
        LOG.debug("Received request for concepts of snapshot [{}] for refset [{}] in xml format", snapshotName, refsetName);
        
        Refset refset = refsetService.findByPublicId(refsetName);
        if (refset == null){
            return new ResponseEntity<SnapshotDto>(HttpStatus.NOT_FOUND);
        }
        
        Snapshot snapshot = refset.getSnapshot(snapshotName);
        if (snapshot == null){
            return new ResponseEntity<SnapshotDto>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<SnapshotDto>(SnapshotDto.parse(snapshot), HttpStatus.OK);
    }    
    
    @Transactional
    @RequestMapping(value = "{refsetName}/snapshot/{snapshotName}.txt", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String>  getSnapshotWithConceptsAsList(
            @PathVariable String refsetName, 
            HttpServletResponse servletResponse,
            @PathVariable String snapshotName) throws Exception 
    {
        LOG.debug("Received request for concepts of snapshot [{}] for refset [{}] in list format", snapshotName, refsetName);
        
        Refset refset = refsetService.findByPublicId(refsetName);
        if (refset == null){
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        Snapshot snapshot = refset.getSnapshot(snapshotName);
        if (snapshot == null){
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        List<Long> concepts = new ArrayList<Long>(snapshot.getConcepts().size());
        
        for (Concept c : snapshot.getConcepts()){
            concepts.add(c.getSerialisedId());
        }
        String conceptsString = concepts.toString();
        conceptsString = conceptsString.substring(1);
        conceptsString = conceptsString.substring(0, conceptsString.length() - 1);
        return new ResponseEntity<String>(conceptsString, HttpStatus.OK);
    }        
    
    @Transactional
    @RequestMapping(value = "{refsetName}/snapshot/{snapshotName}.rf2", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.TEXT_PLAIN_VALUE)
    public void getSnapshotWithConceptsAsRf2(
            @PathVariable String refsetName, 
            HttpServletResponse servletResponse,
            Writer responseWriter,
            @PathVariable String snapshotName) throws Exception 
    {
        LOG.debug("Received request for concepts of snapshot [{}] for refset [{}] in list format", snapshotName, refsetName);
        
        Refset refset = refsetService.findByPublicId(refsetName);
        if (refset == null){
            servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return;
            //return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        Snapshot snapshot = refset.getSnapshot(snapshotName);
        if (snapshot == null){
            servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return;
            //return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        
        SnomedSerialiser serialiser = SnomedSerialiserFactory.getSerialiser(Form.RF2, responseWriter);
        for (Concept c : snapshot.getConcepts()){
            serialiser.write(c);
        }
        servletResponse.setStatus(HttpStatus.OK.value());
        //servletResponse.setContentType(RF2_MIME_TYPE);
    }    
    
    @Transactional
    @RequestMapping(value = "{refsetName}/snapit", 
            method = RequestMethod.POST, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SnapshotResponseDto> snapit(@PathVariable String refsetName,
            @Valid @RequestBody SnapshotDto snapshotDto,
            BindingResult bindingResult)
    {
        LOG.debug("Received request to create snapshot of refset [{}]", refsetName);

        int returnCode = RefsetResponseDto.FAIL;
        SnapshotResponseDto response = new SnapshotResponseDto();
            
        Snapshot snapshot = snapshotService.findByPublicId(snapshotDto.getPublicId());
        if (snapshot != null){
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), "publicId", snapshotDto.getPublicId(),
                    false, null,null, "xml.response.error.publicid.not.unique"));
        }
        
        if (bindingResult.hasErrors()){
            return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        }
        
        try {
            SnapshotDto resultDto = refsetService.takeSnapshot(refsetName, snapshotDto);
            response.setSnapshot(resultDto);
            return new ResponseEntity<SnapshotResponseDto>(response, HttpStatus.CREATED);
        }
        catch (RefsetNotFoundException e) {
            return new ResponseEntity<SnapshotResponseDto>(HttpStatus.NOT_FOUND);
        }
        catch (NonUniquePublicIdException e) {
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), "publicId", snapshotDto.getPublicId(),
                    false, null,null, "xml.response.error.publicid.not.unique"));            
            return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        }
    }    

    @Transactional
    @RequestMapping(value = "{refsetName}/snapshots", 
            method = RequestMethod.POST, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SnapshotResponseDto> importSnapshot(@PathVariable String refsetName,
            @Valid @RequestBody SnapshotDto snapshotDto,
            BindingResult bindingResult)
    {
        LOG.debug("Received request to create snapshot of refset [{}]", refsetName);

        int returnCode = RefsetResponseDto.FAIL;
        SnapshotResponseDto response = new SnapshotResponseDto();
            
        if (bindingResult.hasErrors()){
            return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        }
        
        try {
            SnapshotDto resultDto = refsetService.importSnapshot(refsetName, snapshotDto);
            response.setSnapshot(resultDto);
            return new ResponseEntity<SnapshotResponseDto>(response, HttpStatus.CREATED);
        }
        catch (RefsetNotFoundException e) {
            return new ResponseEntity<SnapshotResponseDto>(HttpStatus.NOT_FOUND);
        }
        catch (NonUniquePublicIdException e) {
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), "publicId", snapshotDto.getPublicId(),
                    false, null,null, "xml.response.error.publicid.not.unique"));            
            returnCode = RefsetResponseDto.FAIL_PUBLIC_ID_NOT_UNIQUE;
        }
        catch (ConceptIdNotFoundException e) {
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), "concepts", e.getId(),
                    false, null,null, "xml.response.error.concept.not.found"));
            returnCode = RefsetResponseDto.FAIL_CONCEPT_NOT_FOUND;
        }
        return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);        
    }        
    
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
    @RequestMapping(value = "{refsetName}", 
            method = RequestMethod.DELETE, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefsetResponseDto> deleteRefset(HttpServletRequest request, @PathVariable String refsetName){
        LOG.debug("Received request to delete refset [{}]", refsetName);
        RefsetResponseDto response = new RefsetResponseDto();
        response.setPublicId(refsetName);
        try {
            Refset deleted = refsetService.delete(refsetName);
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
                    Arrays.asList(refsetName).toArray(), 
                    LocaleContextHolder.getLocale())));
            return new ResponseEntity<RefsetResponseDto>(response, HttpStatus.PRECONDITION_FAILED);
        }
    }

    @Transactional
    @RequestMapping(value = "", method = RequestMethod.POST, 
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
            return new ResponseEntity<RefsetResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            Refset created = refsetService.create(refsetDto);
            if (created == null){
                return new ResponseEntity<RefsetResponseDto>(refsetErrorBuilder.build(bindingResult, response, RefsetResponseDto.FAIL), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<RefsetResponseDto>(success(response, created, Status.CREATED, RefsetResponseDto.SUCCESS_CREATED), HttpStatus.CREATED);
        } catch (NonUniquePublicIdException e) {
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), "publicId", refsetDto.getPublicId(),
                    false, null,null, "xml.response.error.publicid.not.unique"));
            return new ResponseEntity<RefsetResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
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

    private List<XmlRefsetConcept> getXmlConceptDtos(String pubId){
        Refset refset = refsetService.findByPublicId(pubId);
        System.out.println("Found refset " + refset);
        Set<Concept> concepts = refset.getPlan().refreshAndGetConcepts();
        List<XmlRefsetConcept> xmlConcepts = new ArrayList<>();
        for (Concept c : concepts){
            xmlConcepts.add(new XmlRefsetConcept(c));
        }
        System.out.println("returning xmlconcepts [" + xmlConcepts.size() + "]");
        return xmlConcepts;
    }    

    private List<XmlRefsetShort> getRefsetsDto(){
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