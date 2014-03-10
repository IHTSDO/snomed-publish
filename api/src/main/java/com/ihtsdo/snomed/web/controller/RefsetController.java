package com.ihtsdo.snomed.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
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

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.dto.refset.SnomedReleaseDto;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.OntologyNotFoundException;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.xml.XmlRefsetConcept;
import com.ihtsdo.snomed.model.xml.XmlRefsetConcepts;
import com.ihtsdo.snomed.model.xml.XmlRefsetShort;
import com.ihtsdo.snomed.service.refset.RefsetService;
import com.ihtsdo.snomed.web.dto.RefsetErrorBuilder;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto.Status;

@Controller
@RequestMapping("/refsets")
@Transactional(value = "transactionManager")
public class RefsetController {
    private static final Logger LOG = LoggerFactory.getLogger(RefsetController.class);
    
    public static final String RF2_MIME_TYPE = "application/vnd.ihtsdo.snomed.rf2.terminology.concept+txt";    

    @Inject
    RefsetService refsetService;
        
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
                        deleted.getSource(), 
                        deleted.getType(), 
                        SnomedReleaseDto.getBuilder(deleted.getSnomedRelease().getId(), new Date()).build(),
                        ConceptDto.parse(deleted.getRefsetConcept()),
                        ConceptDto.parse(deleted.getModuleConcept()),
                        deleted.getTitle(),
                        deleted.getDescription(), 
                        deleted.getPublicId(), 
                        PlanDto.parse(deleted.getPlan())).build()
                    );

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
        } catch (OntologyNotFoundException e) {
            LOG.debug("Create refset failed", e);
            response.setStatus(Status.FAIL);
            response.setCode(RefsetResponseDto.FAIL_VALIDATION);
        } 
        
        return new ResponseEntity<RefsetResponseDto>(response, HttpStatus.NOT_ACCEPTABLE);
    }
      
    
    private RefsetResponseDto success(RefsetResponseDto response, Refset updated, Status status, int returnCode) {
        response.setRefset(
                RefsetDto.getBuilder(
                        updated.getId(),
                        updated.getSource(), 
                        updated.getType(), 
                        SnomedReleaseDto.getBuilder(updated.getSnomedRelease().getId(), new Date()).build(),
                        ConceptDto.parse(updated.getRefsetConcept()),
                        ConceptDto.parse(updated.getModuleConcept()),
                        updated.getTitle(),
                        updated.getDescription(), 
                        updated.getPublicId(), 
                        PlanDto.parse(updated.getPlan())).build()
                    );
        
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

        return RefsetDto.getBuilder(
                refset.getId(),
                refset.getSource(), 
                refset.getType(), 
                SnomedReleaseDto.getBuilder(refset.getSnomedRelease().getId(), new Date()).build(),
                ConceptDto.parse(refset.getRefsetConcept()),
                ConceptDto.parse(refset.getModuleConcept()),
                refset.getTitle(),
                refset.getDescription(), 
                refset.getPublicId(), 
                PlanDto.parse(refset.getPlan())).build();
    }
}