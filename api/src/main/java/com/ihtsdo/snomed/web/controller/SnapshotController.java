package com.ihtsdo.snomed.web.controller;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.dto.refset.MembersDto;
import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.dto.refset.SnapshotDtoShort;
import com.ihtsdo.snomed.dto.refset.VersionsDto;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.service.refset.MemberService;
import com.ihtsdo.snomed.service.refset.RefsetService;
import com.ihtsdo.snomed.service.refset.SnapshotService;
import com.ihtsdo.snomed.service.refset.RefsetService.SortOrder;
import com.ihtsdo.snomed.service.refset.serialiser.RefsetSerialiserFactory;
import com.ihtsdo.snomed.service.refset.serialiser.RefsetSerialiserFactory.Form;
import com.ihtsdo.snomed.web.dto.RefsetErrorBuilder;
import com.ihtsdo.snomed.web.exception.GlobalBindingException;

@Controller
@RequestMapping("/refsets")
//@Transactional(value = "transactionManager")
public class SnapshotController {
    private static final Logger LOG = LoggerFactory.getLogger(SnapshotController.class);
    
    @Inject
    RefsetService refsetService;
    
    @Inject 
    SnapshotService snapshotService;
    
    @Inject 
    MemberService memberService;    
    
    @Inject
    RefsetErrorBuilder refsetErrorBuilder;
    
    @RequestMapping(value = "{refsetName}/versions", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public VersionsDto getAllSnapshotsForRefset(@PathVariable String refsetName) throws RefsetNotFoundException{
        LOG.debug("Received request for all snapshots for refset {}", refsetName);
        
        //make sure refset exists, or throw exception
        refsetService.findByPublicId(refsetName);
        
        List<SnapshotDtoShort> snapshotDtos = new ArrayList<>();
        for (Snapshot s : snapshotService.findAllSnapshots(refsetName)){
            snapshotDtos.add(SnapshotDtoShort.parse(s));
        }
        return new VersionsDto(snapshotDtos);
    }    
    
    @RequestMapping(value = "{refsetName}/version/{snapshotName}/members", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)    
    public MembersDto  getSnapshotMembers(@PathVariable String refsetName, @PathVariable String snapshotName,
            @RequestParam("sortBy") String sortBy, 
            @RequestParam("sortOrder") SortOrder sortOrder) throws SnapshotNotFoundException  
    {
        LOG.debug("Received request for members of snapshot [{}] for refset [{}]", snapshotName, refsetName);

        //make sure snapshot exists, or throw exception
        snapshotService.findByPublicId(refsetName, snapshotName);
        
        List<Member> members = memberService.findBySnapshotPublicId(refsetName, snapshotName, sortBy, sortOrder);
        
        List<MemberDto> memberDtos = new ArrayList<MemberDto>();
        for (Member m : members){
            memberDtos.add(MemberDto.parse(m));
        }
        return new MembersDto(memberDtos);
    }
    
    @RequestMapping(value = "{refsetName}/version/{snapshotName}", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)    
    public SnapshotDtoShort getSnapshotMember(@PathVariable String refsetName, @PathVariable String snapshotName) throws SnapshotNotFoundException  
    {
        LOG.debug("Received request for snapshot [{}] for refset [{}]", snapshotName, refsetName);
        return SnapshotDtoShort.parse(snapshotService.findByPublicId(refsetName, snapshotName));
    }    
    
    @RequestMapping(value = "{refsetName}/versions", 
            method = RequestMethod.POST, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)     
    public SnapshotDtoShort createSnapshot(@PathVariable String refsetName, @Valid @RequestBody SnapshotDto snapshotDto) 
            throws RefsetNotFoundException, NonUniquePublicIdException
    {
        LOG.debug("Received request to create snapshot of refset [{}]", refsetName);

//        try {
//            snapshotService.findByPublicId(refsetName, snapshotDto.getPublicId());
//            throw new NonUniquePublicIdException("Public Id " + snapshotDto.getPublicId() + " for snapshot already exists");
//        } catch (SnapshotNotFoundException e) {}
                
        return SnapshotDtoShort.parse(snapshotService.createFromRefsetMembers(refsetName, snapshotDto));        
    }   
    
    @RequestMapping(value = "{refsetName}/version/{snapshotName}/{refsetName}.version.{snapshotName}.rf2",
            method = RequestMethod.GET, 
            consumes = {MediaType.ALL_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public void downloadVersionedMembersInRf2(@PathVariable String refsetName, @PathVariable String snapshotName,
            Writer responseWriter, HttpServletResponse response) throws RefsetNotFoundException, GlobalBindingException
    {
        LOG.debug("Controller received request to download version {} of refset {} in RF2 format", snapshotName, refsetName);
        try {
            RefsetSerialiserFactory.getSerialiser(Form.RF2, responseWriter).write(
                    memberService.findBySnapshotPublicId(refsetName, snapshotName, "component.fullySpecifiedName", SortOrder.ASC));
            response.setContentType(RefsetController.RF2_MIME_TYPE);
        } catch (IOException e) {
            LOG.error("Unable to write RF2 file: " + e.getMessage(), e);
            throw new GlobalBindingException(
                    "error.message.unable.to.write.file.io.exception",
                    Arrays.asList(refsetName + "/version/" + snapshotName + "/"+ refsetName + ".version." + snapshotName + ".rf2"),
                    "Unable to write file - general IO exception: " + e.getMessage());
        }
    }
    
    @RequestMapping(value = "{refsetName}/version/{snapshotName}/{refsetName}.version.{snapshotName}.json",
            method = RequestMethod.GET, 
            produces = {MediaType.APPLICATION_JSON_VALUE }, 
            consumes = {MediaType.ALL_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<MemberDto> downloadVersionedMembersInJson(@PathVariable String refsetName,
            @PathVariable String snapshotName) throws RefsetNotFoundException
    {
        LOG.debug("Controller received request to download version {} of refset {} in JSON format", snapshotName, refsetName);
        List<Member> members = memberService.findBySnapshotPublicId(refsetName, snapshotName, "component.fullySpecifiedName", SortOrder.ASC);
        List<MemberDto> memberDtos = new ArrayList<>();
        for (Member m : members){
            memberDtos.add(MemberDto.parse(m));
        }
        return memberDtos;
    }        

    @RequestMapping(value = "{refsetName}/version/{snapshotName}/{refsetName}.version.{snapshotName}.xml",
            method = RequestMethod.GET, 
            produces = {MediaType.APPLICATION_XML_VALUE }, 
            consumes = {MediaType.ALL_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MembersDto downloadVersionedMembersInXml(@PathVariable String refsetName,
            @PathVariable String snapshotName) throws RefsetNotFoundException
    {
        LOG.debug("Controller received request to download version {} of refset {} in XML format", snapshotName, refsetName);
        List<Member> members = memberService.findBySnapshotPublicId(refsetName, snapshotName, "component.fullySpecifiedName", SortOrder.ASC);
        List<MemberDto> memberDtos = new ArrayList<>();
        for (Member m : members){
            memberDtos.add(MemberDto.parse(m));
        }
        return new MembersDto(memberDtos);
    }     
    

//  @RequestMapping(value = "{refsetName}/snapshot/{snapshotName}/members.rf2", 
//          method = RequestMethod.GET, 
//          consumes=MediaType.ALL_VALUE,
//          produces=MediaType.TEXT_PLAIN_VALUE)
//  public void getSnapshotMembersOnlyAsRf2(
//          @PathVariable String refsetName, 
//          HttpServletResponse servletResponse,
//          Writer responseWriter,
//          @PathVariable String snapshotName) throws Exception 
//  {
//      LOG.debug("Received request for members of snapshot [{}] for refset [{}] in rf2 format", snapshotName, refsetName);
//      
//      Refset refset = refsetService.findByPublicId(refsetName);
//      if (refset == null){
//          servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
//          return;
//          //return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
//      }
//      
//      Snapshot snapshot = snapshotService.findByPublicId(refsetName, snapshotName);
//      if (snapshot == null){
//          servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
//          return;
//          //return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
//      }
//      
//      List<Member> members = new ArrayList<>(snapshot.getMembers()); 
//      
//      RefsetSerialiserFactory.getSerialiser(Form.RF2, responseWriter).write(members);
//      
//      servletResponse.setStatus(HttpStatus.OK.value());
//      //servletResponse.setContentType(RF2_MIME_TYPE);
//  }        
    
//  @RequestMapping(value = "{refsetName}/snapshot/{snapshotName}/members.xml", 
//          method = RequestMethod.GET, 
//          consumes=MediaType.ALL_VALUE,
//          produces=MediaType.APPLICATION_XML_VALUE)
//  public ResponseEntity<MembersDto>  getSnapshotMembersOnlyAsXml(
//          @PathVariable String refsetName, 
//          @PathVariable String snapshotName) throws Exception 
//  {
//      LOG.debug("Received request for members of snapshot [{}] for refset [{}] in xml format", snapshotName, refsetName);
//      
//      Refset refset = refsetService.findByPublicId(refsetName);
//      if (refset == null){
//          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//      }
//      
//      Snapshot snapshot = snapshotService.findByPublicId(refsetName, snapshotName);
//      if (snapshot == null){
//          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//      }
//      return new ResponseEntity<>(new MembersDto(new ArrayList<>(SnapshotDto.parse(snapshot).getMemberDtos())), HttpStatus.OK);
//  }    
    

//    @RequestMapping(value = "{refsetName}/snapshots", 
//            method = RequestMethod.POST, 
//            consumes=MediaType.ALL_VALUE,
//            produces=MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<SnapshotResponseDto> importSnapshot(@PathVariable String refsetName,
//            @Valid @ModelAttribute ImportSnapshotDto importSnapshotDto,
//            BindingResult bindingResult)
//    {
//        LOG.debug("Received request to import snapshot of refset [{}]", refsetName);
//
//        int returnCode = RefsetResponseDto.FAIL;
//        SnapshotResponseDto response = new SnapshotResponseDto();
//        
//        Snapshot snapshot = snapshotService.findByPublicId(importSnapshotDto.getPublicId());
//        if (snapshot != null){
//            bindingResult.addError(new FieldError(
//                    bindingResult.getObjectName(), "publicId", importSnapshotDto.getPublicId(),
//                    false, null,null, "xml.response.error.publicid.not.unique"));
//        }
//        
//        Parser parser = getParser(importSnapshotDto);
//        if (parser == null){
//        	bindingResult.addError(new FieldError(
//                    bindingResult.getObjectName(), "fileType", importSnapshotDto.getFileType(),
//                    false, null,new Object[]{importSnapshotDto.getFileType()}, "xml.response.error.filetype.not.recognised"));
//        }
//        
//        if (bindingResult.hasErrors()){
//            return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
//        }
//
//    	MultipartFile file = importSnapshotDto.getFile();
//    	try (Reader reader = new InputStreamReader(new BufferedInputStream(file.getInputStream()))){
//    		SnapshotDto snapshotDto = SnapshotDto.getBuilder(
//    				importSnapshotDto.getTitle(), 
//    				importSnapshotDto.getDescription(),
//    				importSnapshotDto.getPublicId(),
//    				RefsetParserFactory.getParser(parser).parseMode(Mode.FORGIVING).parse(reader)).build();
//    		
//    		SnapshotDto createdDto = refsetService.importSnapshot(refsetName, snapshotDto);
//    		
//    		response.setSnapshot(createdDto);
//            return new ResponseEntity<SnapshotResponseDto>(response, HttpStatus.CREATED);
//    	} catch (InvalidInputException e) {
//			LOG.error("If the parser is running in FORGIVING mode, this should not happen", e);
//			return new ResponseEntity<SnapshotResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
//		} catch (IOException e) {
//			LOG.error("Inable to read file named [" + importSnapshotDto.getFile().getName() + "]");
//			return new ResponseEntity<SnapshotResponseDto>(HttpStatus.BAD_REQUEST);
//		} catch (NonUniquePublicIdException e) {
//			// Allready take care of above, but...
//            bindingResult.addError(new FieldError(
//                    bindingResult.getObjectName(), "publicId", importSnapshotDto.getPublicId(),
//                    false, null,new Object[]{importSnapshotDto.getPublicId()}, "xml.response.error.publicid.not.unique"));
//            return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);	            
//		} catch (RefsetNotFoundException e) {
//			LOG.error("Inable to find refset named [" + refsetName + "]");
//			return new ResponseEntity<SnapshotResponseDto>(HttpStatus.BAD_REQUEST);
//		} catch (ConceptIdNotFoundException e) {
//			LOG.error("Inable to find concept with serialised id [" + e.getId());
//            bindingResult.addError(new FieldError(
//                    bindingResult.getObjectName(), "memberDtos", importSnapshotDto.getMemberDtos(),
//                    false, null,new Object[]{e.getId()}, "xml.response.error.concept.not.found"));
//            return new ResponseEntity<SnapshotResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);	            
//		
//		}
//    }
    
//    private Parser getParser(ImportSnapshotDto importSnapshotDto){
//    	LOG.info("Determining parser for file type [" + importSnapshotDto.getFileType() + "]");
//        Parser parser = null;
//        if (importSnapshotDto.isUseExtension()){
//        	String name = importSnapshotDto.getFile().getOriginalFilename();
//        	String extension = name.substring(name.lastIndexOf('.') + 1, name.length()).toUpperCase();
//        	LOG.info("Determining parser based on file extension [" + extension + "]");
//        	if (extension.equals("JSON")){
//        		parser = Parser.JSON;
//        	}else if (extension.equals("XML")){
//        		parser = Parser.XML;
//        	}else if (extension.equals("TXT")){
//        		parser = Parser.RF2;
//        	}else if (extension.equals("RF2")){
//        		parser = Parser.RF2;
//        	}
//        }else if (importSnapshotDto.isRf2()){
//        	parser = Parser.RF2;
//        }else if (importSnapshotDto.isJson()){
//        	parser = Parser.JSON;
//        }else if (importSnapshotDto.isXml()){
//        	parser = Parser.XML;
//        }
//		return parser;
//    }
    
//  @RequestMapping(value = "{refsetName}/snapshot/{snapshotName}.json", 
//  method = RequestMethod.GET, 
//  consumes=MediaType.ALL_VALUE,
//  produces=MediaType.APPLICATION_JSON_VALUE)
//@ResponseBody
//@ResponseStatus(HttpStatus.OK)
//public SnapshotDto  getSnapshotWithMembersAndRulesAsJson(
//  @PathVariable String refsetName, 
//  @PathVariable String snapshotName) throws SnapshotNotFoundException 
//{
//LOG.debug("Received request for snapshot [{}] for refset [{}] in json format", snapshotName, refsetName);
//return SnapshotDto.parse(snapshotService.findByPublicId(refsetName, snapshotName));
//}

//@ResponseBody
//@ResponseStatus(HttpStatus.OK)
//public SnapshotDto  getSnapshotWithMembersAsXml(
//  @PathVariable String refsetName, 
//  @PathVariable String snapshotName) throws SnapshotNotFoundException 
//{
//LOG.debug("Received request for snapshot [{}] for refset [{}] in xml format", snapshotName, refsetName);
//return SnapshotDto.parse(snapshotService.findByPublicId(refsetName, snapshotName));
//}     
 
    
    /*
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
    
}