package com.ihtsdo.snomed.web.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.InvalidInputException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.service.refset.RefsetService;
import com.ihtsdo.snomed.service.refset.SnapshotService;
import com.ihtsdo.snomed.service.refset.parser.RefsetParser.Mode;
import com.ihtsdo.snomed.service.refset.parser.RefsetParserFactory;
import com.ihtsdo.snomed.service.refset.parser.RefsetParserFactory.Parser;
import com.ihtsdo.snomed.service.refset.serialiser.RefsetSerialiserFactory;
import com.ihtsdo.snomed.service.refset.serialiser.RefsetSerialiserFactory.Form;
import com.ihtsdo.snomed.web.dto.ImportSnapshotDto;
import com.ihtsdo.snomed.web.dto.RefsetErrorBuilder;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto;
import com.ihtsdo.snomed.web.dto.SnapshotResponseDto;

@Controller
@RequestMapping("/refsets")
@Transactional(value = "transactionManager")
public class SnapshotController {
    private static final Logger LOG = LoggerFactory.getLogger(SnapshotController.class);
    
    @Inject
    RefsetService refsetService;
    
    @Inject 
    SnapshotService snapshotService;
    
    @Inject
    RefsetErrorBuilder refsetErrorBuilder;
    
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
            snapshots.add(SnapshotDto.parseSansMembers(snapshot));
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

    /*
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
        
        List<Long> concepts = new ArrayList<Long>(snapshot.getMembers().size());
        
        for (Concept c : snapshot.getConcepts()){
            concepts.add(c.getSerialisedId());
        }
        String conceptsString = concepts.toString();
        conceptsString = conceptsString.substring(1);
        conceptsString = conceptsString.substring(0, conceptsString.length() - 1);
        return new ResponseEntity<String>(conceptsString, HttpStatus.OK);
    } 
    */       
    
    
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
        LOG.debug("Received request for concepts of snapshot [{}] for refset [{}] in rf2 format", snapshotName, refsetName);
        
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
        
        List<Member> members = new ArrayList<>(snapshot.getMembers()); 
        
        RefsetSerialiserFactory.getSerialiser(Form.RF2, responseWriter).write(members);
        
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
            @Valid @ModelAttribute ImportSnapshotDto importSnapshotDto,
            BindingResult bindingResult)
    {
        LOG.debug("Received request to import snapshot of refset [{}]", refsetName);

        int returnCode = RefsetResponseDto.FAIL;
        SnapshotResponseDto response = new SnapshotResponseDto();
        
        Snapshot snapshot = snapshotService.findByPublicId(importSnapshotDto.getPublicId());
        if (snapshot != null){
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), "publicId", importSnapshotDto.getPublicId(),
                    false, null,null, "xml.response.error.publicid.not.unique"));
        }
        
        if (bindingResult.hasErrors()){
            return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        }
        
        if (importSnapshotDto.isRf2()){
        	MultipartFile file = importSnapshotDto.getFile();
        	try (Reader reader = new InputStreamReader(new BufferedInputStream(file.getInputStream()))){
        		SnapshotDto snapshotDto = SnapshotDto.getBuilder(
        				0L, 
        				importSnapshotDto.getTitle(), 
        				importSnapshotDto.getDescription(),
        				importSnapshotDto.getPublicId(),
        				RefsetParserFactory.getParser(Parser.RF2).parseMode(Mode.FORGIVING).parse(reader)).build();
        		
        		SnapshotDto createdDto = refsetService.importSnapshot(refsetName, snapshotDto);
        		
        		response.setSnapshot(createdDto);
                return new ResponseEntity<SnapshotResponseDto>(response, HttpStatus.CREATED);
        	} catch (InvalidInputException e) {
				LOG.error("If the parser is running in FORGIVING mode, this should not happen");
				return new ResponseEntity<SnapshotResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (IOException e) {
				LOG.error("Inable to read file named [" + importSnapshotDto.getFile().getName() + "]");
				return new ResponseEntity<SnapshotResponseDto>(HttpStatus.BAD_REQUEST);
			} catch (NonUniquePublicIdException e) {
				// Allready take care of above, but...
	            bindingResult.addError(new FieldError(
	                    bindingResult.getObjectName(), "publicId", importSnapshotDto.getPublicId(),
	                    false, null,new Object[]{importSnapshotDto.getPublicId()}, "xml.response.error.publicid.not.unique"));
	            return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);	            
			} catch (RefsetNotFoundException e) {
				LOG.error("Inable to find refset named [" + refsetName + "]");
				return new ResponseEntity<SnapshotResponseDto>(HttpStatus.BAD_REQUEST);
			} catch (ConceptIdNotFoundException e) {
				LOG.error("Inable to find concept with serialised id [" + e.getId());
	            bindingResult.addError(new FieldError(
	                    bindingResult.getObjectName(), "memberDtos", importSnapshotDto.getMemberDtos(),
	                    false, null,new Object[]{e.getId()}, "xml.response.error.concept.not.found"));
	            return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);	            
			
			}        	
        }

        //handle json + handle xml
        //handle list        
        
        LOG.error("We have not handled filetype [" + importSnapshotDto.getFileType() + "]");
        return new ResponseEntity<SnapshotResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}