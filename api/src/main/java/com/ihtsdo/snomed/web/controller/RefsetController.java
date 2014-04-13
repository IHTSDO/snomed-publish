package com.ihtsdo.snomed.web.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.LocaleResolver;

import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.dto.refset.MembersDto;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.dto.refset.RefsetsDto;
import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.InvalidInputException;
import com.ihtsdo.snomed.exception.InvalidSnomedDateFormatException;
import com.ihtsdo.snomed.exception.MemberNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.OntologyFlavourNotFoundException;
import com.ihtsdo.snomed.exception.OntologyNotFoundException;
import com.ihtsdo.snomed.exception.OntologyVersionNotFoundException;
import com.ihtsdo.snomed.exception.ProgrammingError;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.xml.RefsetDtoShort;
import com.ihtsdo.snomed.model.xml.XmlRefsetConcept;
import com.ihtsdo.snomed.model.xml.XmlRefsetConcepts;
import com.ihtsdo.snomed.service.Page;
import com.ihtsdo.snomed.service.refset.MemberService;
import com.ihtsdo.snomed.service.refset.RefsetService;
import com.ihtsdo.snomed.service.refset.RefsetService.SortOrder;
import com.ihtsdo.snomed.service.refset.parser.RefsetParser.Mode;
import com.ihtsdo.snomed.service.refset.parser.RefsetParserFactory;
import com.ihtsdo.snomed.service.refset.parser.RefsetParserFactory.Parser;
import com.ihtsdo.snomed.service.refset.serialiser.RefsetSerialiserFactory;
import com.ihtsdo.snomed.service.refset.serialiser.RefsetSerialiserFactory.Form;
import com.ihtsdo.snomed.web.dto.ImportFileDto;
import com.ihtsdo.snomed.web.dto.RefsetErrorBuilder;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto.Status;
import com.ihtsdo.snomed.web.exception.FieldBindingException;
import com.ihtsdo.snomed.web.exception.GlobalBindingException;
import com.ihtsdo.snomed.web.exception.UnrecognisedFileExtensionException;

@Controller
@RequestMapping("/refsets")
public class RefsetController {
    private static final Logger LOG = LoggerFactory.getLogger(RefsetController.class);
    
    public static final String RF2_MIME_TYPE = "application/vnd.ihtsdo.snomed.rf2.terminology.concept+txt";    

    @Inject
    RefsetService refsetService;
    
    @Inject
    MemberService memberService;    
        
    @Inject
    RefsetErrorBuilder refsetErrorBuilder;
    
    @Inject
    LocaleResolver localeResolver;
    
    @Resource
    private MessageSource messageSource;
    

    
    @RequestMapping(value = "", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RefsetsDto getAllRefsets(@RequestParam("sortBy") String sortBy,
            @RequestParam("sortOrder") SortOrder sortOrder){
        List<Refset> refsets = refsetService.findAll(sortBy, sortOrder);
        List<RefsetDtoShort> refsetDtos = new ArrayList<>();
        for (Refset r : refsets){
            refsetDtos.add(RefsetDtoShort.parse(r));
        }
        return new RefsetsDto(refsetDtos);

    }    
        
    @RequestMapping(value = "{refsetName}", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RefsetDto getRefset(@PathVariable String refsetName) throws RefsetNotFoundException{
        return getRefsetDto(refsetName);
    }
    
    @RequestMapping(value = "{refsetName}/members",
            params = "type=list",
            method = RequestMethod.POST, 
            produces = {MediaType.APPLICATION_JSON_VALUE }, 
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMembersByList(@Valid @RequestBody Set<MemberDto> members, @PathVariable String refsetName) 
                    throws RefsetNotFoundException, ConceptIdNotFoundException
    {
        LOG.debug("Controller received request to add new members from a list to refset [{}]", refsetName);
        refsetService.addMembers(members, refsetName);
    }
    
    @RequestMapping(value = "{refsetName}/members/{memberId}",
            method = RequestMethod.DELETE, 
            produces = {MediaType.APPLICATION_JSON_VALUE }, 
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MemberDto deleteMembership(@PathVariable String refsetName, @PathVariable String memberId) 
                    throws RefsetNotFoundException, ConceptIdNotFoundException, MemberNotFoundException, NonUniquePublicIdException
    {
        LOG.debug("Controller received request to delete member {} from refset {}", memberId, refsetName);
        return MemberDto.parse(refsetService.deleteMembership(refsetName, memberId));
    }
    
    @RequestMapping(value = "{refsetName}",
            method = RequestMethod.DELETE, 
            produces = {MediaType.APPLICATION_JSON_VALUE }, 
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RefsetDto deleteRefset(@PathVariable String refsetName) throws RefsetNotFoundException
    {
        LOG.debug("Controller received request to delete refset {}", refsetName);
        return RefsetDto.parse(refsetService.delete(refsetName));
    }
    
    @RequestMapping(value = "{refsetName}",
            params = {"action=resurect"},
            method = RequestMethod.PUT, 
            produces = {MediaType.APPLICATION_JSON_VALUE }, 
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RefsetDto resurectRefset(@PathVariable String refsetName) throws RefsetNotFoundException
    {
        LOG.debug("Controller received request to resurect deleted refset {}", refsetName);
        return RefsetDto.parse(refsetService.resurect(refsetName));
    }         
    
    @RequestMapping(value = "{refsetName}/members/{refsetName}.unversioned.rf2",
            method = RequestMethod.GET, 
            consumes = {MediaType.ALL_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public void downloadUnversionedMembersInRf2(@PathVariable String refsetName,
            Writer responseWriter, HttpServletResponse response) throws RefsetNotFoundException, GlobalBindingException
    {
        LOG.debug("Controller received request to download unversioned members of refset {} in RF2 format", refsetName);
        try {
            RefsetSerialiserFactory.getSerialiser(Form.RF2, responseWriter).write(
                    memberService.findByRefsetPublicId(refsetName, "component.fullySpecifiedName", SortOrder.ASC, ""));
            response.setContentType(RF2_MIME_TYPE);
        } catch (IOException e) {
            LOG.error("Unable to write RF2 file: " + e.getMessage(), e);
            throw new GlobalBindingException(
                    "error.message.unable.to.write.file.io.exception",
                    Arrays.asList(refsetName + "/members/" + refsetName + ".unversioned.rf2"),
                    "Unable to write file - general IO exception: " + e.getMessage());
        }
    }
    
    @RequestMapping(value = "{refsetName}/members/{refsetName}.unversioned.json",
            method = RequestMethod.GET, 
            produces = {MediaType.APPLICATION_JSON_VALUE }, 
            consumes = {MediaType.ALL_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<MemberDto> downloadUnversionedMembersInJson(@PathVariable String refsetName) throws RefsetNotFoundException
    {
        LOG.debug("Controller received request to download unversioned members of refset {} in JSON format", refsetName);
        List<Member> members = memberService.findByRefsetPublicId(refsetName, "component.fullySpecifiedName", SortOrder.ASC, "");
        List<MemberDto> memberDtos = new ArrayList<>();
        for (Member m : members){
            memberDtos.add(MemberDto.parse(m));
        }
        return memberDtos;
    }        

    @RequestMapping(value = "{refsetName}/members/{refsetName}.unversioned.xml",
            method = RequestMethod.GET, 
            produces = {MediaType.APPLICATION_XML_VALUE }, 
            consumes = {MediaType.ALL_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MembersDto downloadUnversionedMembersInXml(@PathVariable String refsetName) throws RefsetNotFoundException
    {
        LOG.debug("Controller received request to download unversioned members of refset {} in XML format", refsetName);
        List<Member> members = memberService.findByRefsetPublicId(refsetName, "component.fullySpecifiedName", SortOrder.ASC, "");
        List<MemberDto> memberDtos = new ArrayList<>();
        for (Member m : members){
            memberDtos.add(MemberDto.parse(m));
        }
        return new MembersDto(memberDtos);
    }     
    
    @RequestMapping(value = "{refsetName}/members",
            params = "type=file",
            method = RequestMethod.POST, 
            produces = {MediaType.APPLICATION_JSON_VALUE }, 
            consumes = {MediaType.ALL_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMembersByFile(@Valid @ModelAttribute ImportFileDto importDto, @PathVariable String refsetName) 
                    throws RefsetNotFoundException, ConceptIdNotFoundException, ProgrammingError, 
                    FieldBindingException, GlobalBindingException
    {
        LOG.debug("Controller received request to add new members to refset [{}] from a file: {}", refsetName, importDto);
        
        if ((importDto.getFile().getOriginalFilename() == null) || (importDto.getFile().getOriginalFilename().isEmpty())){
            throw new FieldBindingException("file", "error.message.no.file.sent", new ArrayList<String>(), "No file was sent");            
        }
        
        try (Reader reader = new InputStreamReader(new BufferedInputStream(importDto.getFile().getInputStream()))){
            Set <MemberDto> membersToImport = RefsetParserFactory.getParser(getParser(importDto)).parseMode(Mode.FORGIVING).parse(reader);
            refsetService.addMembers(membersToImport, refsetName);
            LOG.debug("Imported {} members", membersToImport.size());
        } catch (IOException e) {
            throw new GlobalBindingException(
                    "error.message.unable.to.read.file.io.exception",
                    Arrays.asList(importDto.getFile().getOriginalFilename()),
                    "Unable to read file - general IO exception: " + e.getMessage());
        } catch (InvalidInputException e) {
            throw new ProgrammingError(e);
        } catch (UnrecognisedFileExtensionException e) {
            throw new FieldBindingException(
                    "fileType", 
                    "error.message.file.extension.not.recognised",
                    Arrays.asList(e.getExtension()), 
                    "File extension " + e.getExtension() + "not recognised");
        }
    }   
    
    private Parser getParser(ImportFileDto importFileDto) throws UnrecognisedFileExtensionException{
        LOG.info("Determining parser for file type [" + importFileDto.getFileType() + "]");
        Parser parser = null;
        if (importFileDto.isUseExtension()){
            String name = importFileDto.getFile().getOriginalFilename();
            String extension = name.substring(name.lastIndexOf('.') + 1, name.length()).toUpperCase();
            LOG.info("Determining parser based on file extension [" + extension + "]");
            if (extension.equals("JSON")){
                parser = Parser.JSON;
            }else if (extension.equals("XML")){
                parser = Parser.XML;
            }else if (extension.equals("TXT")){
                parser = Parser.RF2;
            }else if (extension.equals("RF2")){
                parser = Parser.RF2;
            }else{
                throw new UnrecognisedFileExtensionException(extension);
            }
        }else if (importFileDto.isRf2()){
            parser = Parser.RF2;
        }else if (importFileDto.isJson()){
            parser = Parser.JSON;
        }else if (importFileDto.isXml()){
            parser = Parser.XML;
        }else{
            throw new ProgrammingError("You forgot to handle file type enumeration of " + importFileDto.getFileType());
        }
        return parser;
    }    
    
    
    @RequestMapping(value = "{refsetName}/members", 
            method = RequestMethod.GET, 
            produces = {MediaType.APPLICATION_JSON_VALUE }, 
            consumes = {MediaType.ALL_VALUE})
    @ResponseBody   
    @ResponseStatus(HttpStatus.OK)
    public MembersDto getMembers(
            @PathVariable String refsetName, 
            @RequestParam("sortBy") String sortBy, 
            @RequestParam("sortOrder") SortOrder sortOrder,
            @RequestParam(value="filter", defaultValue="", required=false) String filter,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize) throws RefsetNotFoundException, ConceptIdNotFoundException
    {
        LOG.debug("Controller received request to retrieve members for refset [{}]", refsetName);
        Page<Member> membersPage = memberService.findByRefsetPublicId(refsetName, sortBy, sortOrder, filter, pageIndex, pageSize);
        
        List<MemberDto> memberDtos = new ArrayList<MemberDto>();
        for (Member m : membersPage.getContent()){
            memberDtos.add(MemberDto.parse(m));
        }
        return new MembersDto(memberDtos, membersPage.getTotalElements());
        
    }
    
    @RequestMapping(value = "{refsetName}/concepts.json", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody XmlRefsetConcepts getConcepts(@PathVariable String refsetName) throws RefsetNotFoundException{
        return new XmlRefsetConcepts(getXmlConceptDtos(refsetName));
    }
    
//    @RequestMapping(value = "{refsetName}", 
//            method = RequestMethod.DELETE, 
//            consumes=MediaType.ALL_VALUE,
//            produces=MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<RefsetResponseDto> deleteRefset(HttpServletRequest request, @PathVariable String refsetName){
//        LOG.debug("Received request to delete refset [{}]", refsetName);
//        RefsetResponseDto response = new RefsetResponseDto();
//        response.setPublicId(refsetName);
//        try {
//            Refset deleted = refsetService.delete(refsetName);
//            
//            response.setRefset(
//                RefsetDto.getBuilder(
//                        deleted.getSource(), 
//                        deleted.getType(), 
//                        deleted.getOntologyVersion().getFlavour().getPublicId(),
//                        deleted.getOntologyVersion().getTaggedOn(),
//                        ConceptDto.parse(deleted.getRefsetConcept()),
//                        ConceptDto.parse(deleted.getModuleConcept()),
//                        deleted.getTitle(),
//                        deleted.getDescription(), 
//                        deleted.getPublicId(), 
//                        PlanDto.parse(deleted.getPlan())).build()
//                    );
//
//            response.setCode(RefsetResponseDto.SUCCESS_DELETED);
//            response.setStatus(Status.DELETED);
//            return new ResponseEntity<RefsetResponseDto>(response, HttpStatus.OK);
//        } catch (RefsetNotFoundException e) {
//            response.setCode(RefsetResponseDto.FAIL_REFSET_NOT_FOUND);
//            response.setStatus(Status.FAIL);
//            response.setGlobalErrors(Arrays.asList(messageSource.getMessage(
//                    "global.error.refset.not.found", 
//                    Arrays.asList(refsetName).toArray(), 
//                    LocaleContextHolder.getLocale())));
//            return new ResponseEntity<RefsetResponseDto>(response, HttpStatus.PRECONDITION_FAILED);
//        }
//    }

    @RequestMapping(value = "", method = RequestMethod.POST, 
    produces = {MediaType.APPLICATION_JSON_VALUE }, 
    consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<RefsetResponseDto> createRefset(@Valid @RequestBody RefsetDto refsetDto, 
            BindingResult bindingResult, HttpServletRequest request) throws RefsetNotFoundException
    {
        LOG.debug("Controller received request to create new refset [{}]",
                refsetDto.toString());

        int returnCode = RefsetResponseDto.FAIL;
        RefsetResponseDto response = new RefsetResponseDto();
        
        Refset refset = null;
        try {
            refset = refsetService.findByPublicId(refsetDto.getPublicId());
        } catch (RefsetNotFoundException e1) {
        }
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
                    false, null, null, "xml.response.error.publicid.not.unique"));
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
        } catch (InvalidSnomedDateFormatException e) {
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), "snomedReleaseDate", refsetDto.getSnomedReleaseDate(),
                    false, new String[] {e.getDateString()},null, "xml.response.error.invalid.date.format"));
            return new ResponseEntity<RefsetResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        } catch (OntologyVersionNotFoundException e) {
            String releaseDateString = DateFormat.getDateInstance(DateFormat.LONG, request.getLocale()).format(e.getReleaseDate());            
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), 
                    "snomedExtension", refsetDto.getSnomedReleaseDate(),
                    false, null, null,
                    messageSource.getMessage(
                            "xml.response.error.snomed.version.not.found", 
                            new String[] {e.getFlavour().getLabel(), releaseDateString},
                            Locale.UK)));
            return new ResponseEntity<RefsetResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        } catch (OntologyFlavourNotFoundException e) {
            bindingResult.addError(new FieldError(
                    bindingResult.getObjectName(), 
                    "snomedExtension", refsetDto.getSnomedReleaseDate(),
                    false, null, null, 
                    messageSource.getMessage(
                            "xml.response.error.snomed.flavour.not.found", 
                            new String[] {e.getFlavourPublicIdString()},
                            Locale.UK)));
            return new ResponseEntity<RefsetResponseDto>(refsetErrorBuilder.build(bindingResult, response, returnCode), HttpStatus.NOT_ACCEPTABLE);
        }
        
        return new ResponseEntity<RefsetResponseDto>(response, HttpStatus.NOT_ACCEPTABLE);
    }
      
    
    private RefsetResponseDto success(RefsetResponseDto response, Refset updated, Status status, int returnCode) {
        response.setRefset(RefsetDto.parse(updated));        
        response.setStatus(status);
        response.setCode(returnCode);
        return response;
    }

    private List<XmlRefsetConcept> getXmlConceptDtos(String pubId) throws RefsetNotFoundException{
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
 
    private RefsetDto getRefsetDto(String pubId) throws RefsetNotFoundException {
        Refset found = refsetService.findByPublicId(pubId);
        return RefsetDto.parse(found);
    }
}