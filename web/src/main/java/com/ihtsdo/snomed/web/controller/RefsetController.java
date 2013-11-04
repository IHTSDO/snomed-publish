package com.ihtsdo.snomed.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.AutoPopulatingList;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Objects;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.ConceptsCacheNotBuiltException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.UnconnectedRefsetRuleException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.xml.XmlRefsetConcept;
import com.ihtsdo.snomed.model.xml.XmlRefsetConcepts;
import com.ihtsdo.snomed.model.xml.XmlRefsetShort;
import com.ihtsdo.snomed.model.xml.XmlRefsets;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.RefsetService;
import com.ihtsdo.snomed.service.UnReferencedReferenceRuleException;
import com.ihtsdo.snomed.web.dto.RefsetErrorDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto;
import com.ihtsdo.snomed.web.dto.RefsetResponseDto.Status;
import com.ihtsdo.snomed.web.service.OntologyService;

@Controller
@RequestMapping("/refsets")
@Transactional(value = "transactionManager")
public class RefsetController {
    public static final String FEEDBACK_MESSAGE = "feedbackMessage";

    private static final Logger LOG = LoggerFactory
            .getLogger(RefsetController.class);

    private static final String FEEDBACK_MESSAGE_KEY_REFSET_ADDED = "feedback.message.refset.added";
    private static final String FEEDBACK_MESSAGE_KEY_REFSET_UPDATED = "feedback.message.refset.updated";
    private static final String FEEDBACK_MESSAGE_KEY_REFSET_DELETED = "feedback.message.refset.deleted";

    @Inject
    RefsetService refsetService;

    @Inject
    OntologyService ontologyService;
    
    @Inject
    ConceptService conceptService;
    
    @Autowired
    org.springframework.oxm.Marshaller marshaller;

    @Resource
    private MessageSource messageSource;
    
    @PersistenceContext(unitName="hibernatePersistenceUnit")
    EntityManager em;

    @PostConstruct
    public void init() {
    }

    
    
    // ALL

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView getRefsets(ModelMap model, HttpServletRequest request,
            Principal principal) {
//        model.addAttribute("user",
//                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        model.addAttribute("refsets", refsetService.findAll());
        return new ModelAndView("/refset/refsets", model);
    }

    // DETAILS

    @RequestMapping(value = "/refset/{pubId}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView getRefset(ModelMap model, HttpServletRequest request,
            Principal principal, @PathVariable String pubId) {
//        model.addAttribute("user",
//                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());

        Refset refset = refsetService.findByPublicId(pubId);
        refset.getPlan().refreshConceptsCache();
        //refset.setRefsetPlan(dummyRefsetPlan());
        
        //model.addAttribute("rules", refset.getPlan().getRules());
        model.addAttribute("refset", refset);
        return new ModelAndView("/refset/refset", model);
    }

    // CREATE FORM

    @RequestMapping(value = "/refset/new", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView showNewRefsetForm(ModelMap model,
            HttpServletRequest request, Principal principal) {
        LOG.debug("Displaying new refset screen");
//        model.addAttribute("user",
//                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        return new ModelAndView("/refset/new.refset", "refset", new RefsetDto());
    }

    // EDIT FORM

    @RequestMapping(value = "/refset/{pubId}/edit", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView showEditRefsetForm(@ModelAttribute("refset") RefsetDto refsetFbo, ModelMap model,
            HttpServletRequest request, Principal principal,
            @PathVariable String pubId) {
        LOG.debug(
                "Displaying edit refset screen for refset with publicId [{}]",
                pubId);
        Refset refset = refsetService.findByPublicId(pubId);
        
        
        RefsetPlanDto planDto = RefsetPlanDto.parse(refset.getPlan());
        List<RefsetRuleDto> autoList = new AutoPopulatingList<>(RefsetRuleDto.class);
        autoList.addAll(planDto.getRefsetRules());
        planDto.setRefsetRules(autoList);
        RefsetDto newRefsetFbo = RefsetDto.getBuilder(
                refset.getId(), 
                refset.getConcept().getSerialisedId(),
                refset.getConcept().getDisplayName(),
                refset.getTitle(), 
                refset.getDescription(), 
                refset.getPublicId(), 
                planDto)
                .build();

        //model.addAttribute("refset", newRefsetFbo);
        model.addAttribute("pubid", pubId);
        model.addAttribute("storedRefset", refset);
        
        
            
        //initDummyPlan(refset);

//        model.addAttribute("user",
//                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        return new ModelAndView("/refset/edit.refset", "refset", newRefsetFbo);
    }

    // DELETE

    @RequestMapping(value = "/refset/{pubId}/delete", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView deleteRefset(ModelMap model,
            HttpServletRequest request, Principal principal,
            @PathVariable String pubId, RedirectAttributes attributes)
            throws RefsetNotFoundException {
        // TODO: Handle RefsetNotFoundException
        LOG.debug("Controller received request to delete refset [{}]", pubId);
        Refset deleted = refsetService.delete(pubId);

        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_REFSET_DELETED, deleted.getPublicId(), deleted.getTitle());

        attributes.addAttribute("server", request.getServerName());
        attributes.addAttribute("port", request.getServerPort());
        return new ModelAndView("redirect:http://{server}:{port}/refsets/");
    }

    // CREATE

    @Transactional
    @RequestMapping(value = "/refset/new", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView ceateRefset(ModelMap model, HttpServletRequest request,
            Principal principal,
            @Valid @ModelAttribute("refset") RefsetDto refsetDto,
            BindingResult result, RedirectAttributes attributes) throws ConceptNotFoundException, RefsetNotFoundException, UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException, RefsetPlanNotFoundException {
        LOG.debug("Controller received request to create new refset [{}]",
                refsetDto.toString());
//        model.addAttribute("user",
//                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        
        Refset refset = refsetService.findByPublicId(refsetDto.getPublicId());
        if (refset != null){
            setPublicIdNotUniqueFieldError(refsetDto, result);
        }
        
        if (result.hasErrors()) {
            return new ModelAndView("/refset/new.refset");
        }
        try {            
            //addDummyData(refsetDto);
            Refset created = refsetService.create(refsetDto);
            addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_REFSET_ADDED, created.getPublicId(), created.getTitle());
            
            attributes.addAttribute("server", request.getServerName());
            attributes.addAttribute("port", request.getServerPort());
            return new ModelAndView("redirect:http://{server}:{port}/refsets/");
        } catch (NonUniquePublicIdException e) {
            setPublicIdNotUniqueFieldError(refsetDto, result);
            return new ModelAndView("/refset/new.refset");
        }
    }
    
    
    @Transactional
    @RequestMapping(value = "/refset/{pubId}/put", method = RequestMethod.POST, 
    produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
    consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public RefsetResponseDto updateRefsetWs(@Valid @RequestBody RefsetDto refsetDto,
            BindingResult result, @PathVariable String pubId){
        int returnCode = RefsetResponseDto.FAIL;
        LOG.debug("Controller received request to update refset {}", refsetDto.toString());
        RefsetResponseDto response = new RefsetResponseDto();
        response.setPublicId(pubId);
        
        Refset refset = refsetService.findById(refsetDto.getId());
        if (!refset.getPublicId().equals(refsetDto.getPublicId())){
            //Assertion: user has updated the public id
            refset = refsetService.findByPublicId(refsetDto.getPublicId());
            if (refset != null){
                returnCode = RefsetResponseDto.FAIL_PUBLIC_ID_NOT_UNIQUE;
                setPublicIdNotUniqueFieldError(refsetDto, result);
            }
        }
        if (!Objects.equal(refsetDto.getPublicId(), pubId)){
            returnCode = RefsetResponseDto.FAIL_URL_AND_BODY_PUBLIC_ID_NOT_MATCHING;
            result.addError(new ObjectError(result.getObjectName(), 
                    getMessage("xml.response.error.url.and.body.public.id.not.matching", 
                            pubId, refsetDto.getPublicId())));
        }
        
        if (result.hasErrors()) {
            return withErrors(result, response, returnCode);
        }
        
        try {
            Refset updated = refsetService.update(refsetDto);
            if (updated != null){
                return success(response, updated, Status.UPDATED, RefsetResponseDto.SUCCESS_UPDATED);
            }
            
            result.addError(new ObjectError(result.getObjectName(), getMessage("xml.response.error.refset.not.updated", refsetDto.getPublicId(), refsetDto.getId())));
            return withErrors(result, response, RefsetResponseDto.FAIL);
        } catch (NonUniquePublicIdException e) {
            LOG.debug("Update failed", e);
            returnCode = RefsetResponseDto.FAIL_PUBLIC_ID_NOT_UNIQUE;
            setPublicIdNotUniqueFieldError(refsetDto, result);
        } catch (UnReferencedReferenceRuleException e) {
            LOG.debug("Update failed", e);
            returnCode = RefsetResponseDto.FAIL_UNREFERENCED_RULE;
           result.addError(new ObjectError(result.getObjectName(), getMessage("xml.response.error.unreferenced.rule")));            
        } catch (RefsetNotFoundException e) {
            LOG.debug("Update failed", e);
            returnCode = RefsetResponseDto.FAIL_REFSET_NOT_FOUND;
            result.addError(new ObjectError(result.getObjectName(), getMessage("xml.response.error.refset.not.found", e.getId())));
        } catch (ConceptNotFoundException e) {
            LOG.debug("Update failed", e);
            returnCode = RefsetResponseDto.FAIL_CONCEPT_NOT_FOUND;
            result.addError(new ObjectError(result.getObjectName(), getMessage("xml.response.error.concept.not.found", e.getId())));
        } catch (UnconnectedRefsetRuleException e) {
            LOG.debug("Update failed", e);
            returnCode = RefsetResponseDto.FAIL_UNCONNECTED_RULE;
            result.addError(new ObjectError(result.getObjectName(), getMessage("xml.response.error.unconnected.rule", e.getId())));
        } catch (RefsetRuleNotFoundException e) {
            LOG.debug("Update failed", e);
            returnCode = RefsetResponseDto.FAIL_RULE_NOT_FOUND;
            result.addError(new ObjectError(result.getObjectName(), getMessage("xml.response.error.rule.not.found", e.getId())));
        } catch (RefsetPlanNotFoundException e) {
            LOG.debug("Update failed", e);
            returnCode = RefsetResponseDto.FAIL_PLAN_NOT_FOUND;
            result.addError(new ObjectError(result.getObjectName(), getMessage("xml.response.error.plan.not.found", e.getId())));
        }
        return withErrors(result, response, returnCode);
    }



    private RefsetResponseDto success(RefsetResponseDto response, Refset updated, Status status, int returnCode) {
        LOG.debug("Refset succesfully updated");
        response.setRefset(RefsetDto.getBuilder(updated.getId(), 
                (updated.getConcept() == null) ? 0 : updated.getConcept().getSerialisedId(),
                (updated.getConcept() == null) ? null : updated.getConcept().getDisplayName(),
                updated.getTitle(), updated.getDescription(), updated.getPublicId(), 
                RefsetPlanDto.parse(updated.getPlan())).build());
        response.setSuccess(true);
        response.setStatus(status);
        response.setCode(returnCode);
        return response;
    }
    
    private RefsetResponseDto withErrors(BindingResult result, RefsetResponseDto response, int returnCode) {
        LOG.error("Found errors: ");
        for (ObjectError error : result.getAllErrors()){
            LOG.error("Error: {}", error.getObjectName() + " - " + error.getDefaultMessage());
        }
        for (FieldError fError : result.getFieldErrors()){
            RefsetErrorDto error = new RefsetErrorDto();
            error.setField(fError.getField());
            error.setDisplayMessage(fError.getDefaultMessage());
            response.addFieldError(fError.getField(), error);
        }
        for (ObjectError gError : result.getGlobalErrors()){
            RefsetErrorDto error = new RefsetErrorDto();
            error.setDisplayMessage(gError.getDefaultMessage());
            error.setCode(returnCode);
            response.addGlobalError(error);
        }
        response.setSuccess(false);
        response.setCode(returnCode);
        response.setStatus(Status.FAIL);
        return response;
    }

    // UPDATE

    @Transactional
    @RequestMapping(value = "/refset/{pubId}/edit", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView updateRefset(ModelMap model,
            HttpServletRequest request, Principal principal,
            RedirectAttributes attributes, @PathVariable String pubId,
            @Valid @ModelAttribute("refset") RefsetDto refsetDto,
            BindingResult result) throws RefsetNotFoundException, ConceptNotFoundException, UnReferencedReferenceRuleException, UnconnectedRefsetRuleException, RefsetRuleNotFoundException, RefsetPlanNotFoundException {
        // TODO: Handle RefsetNotFoundException
        LOG.debug("Controller received request to update refset [{}]", refsetDto.toString());
//        model.addAttribute("user",
//                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        model.addAttribute("pubid", pubId);
        model.addAttribute("refset", refsetDto);
        
        Refset refset = refsetService.findById(refsetDto.getId());
        if (!refset.getPublicId().equals(refsetDto.getPublicId())){
            //Assertion: user has updated the public id
            refset = refsetService.findByPublicId(refsetDto.getPublicId());
            if (refset != null){
                setPublicIdNotUniqueFieldError(refsetDto, result);
            }
        }
        
        if (result.hasErrors()) {
            LOG.error("Found errors: ");
            for (ObjectError error : result.getAllErrors()){
                LOG.error("Error: {}", error.getObjectName() + " - " + error.getDefaultMessage());
            }
            return new ModelAndView("/refset/edit.refset");
        }
        
        try {
            Refset updated = refsetService.update(refsetDto);
            addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_REFSET_UPDATED, updated.getPublicId(), updated.getTitle());
            attributes.addAttribute("server", request.getServerName());
            attributes.addAttribute("port", request.getServerPort());
            return new ModelAndView("redirect:http://{server}:{port}/refsets/refset/" + updated.getPublicId());
            
        } catch (NonUniquePublicIdException e) {
            //defensive coding
            setPublicIdNotUniqueFieldError(refsetDto, result);
            return new ModelAndView("/refset/edit.refset");
        }
    }
    
    // WEB SERVICE API
    
    @Transactional
    @RequestMapping(value = "/refset/{pubId}.xml", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            headers="Accept=*/*",
            produces=MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody RefsetDto getRefsetXml(@PathVariable String pubId) throws Exception {
        RefsetDto refsetDto = blah(pubId);
        
        return refsetDto;
    }
    
    @Transactional
    @RequestMapping(value = "/refset/{pubId}.json", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            headers="Accept=*/*",
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody RefsetDto getRefsetJson(@PathVariable String pubId) throws Exception {
        RefsetDto refsetDto = blah(pubId);
        
        return refsetDto;
    }    
    


    private RefsetDto blah(String pubId) {
        Refset refset = refsetService.findByPublicId(pubId);
        System.out.println("Found refset " + refset);
        RefsetDto refsetDto = RefsetDto.getBuilder(refset.getId(), 
                (refset.getConcept() == null) ? 0 : refset.getConcept().getSerialisedId(),
                (refset.getConcept() == null) ? null : refset.getConcept().getDisplayName(),
                refset.getTitle(), refset.getDescription(), refset.getPublicId(), 
                RefsetPlanDto.parse(refset.getPlan())).build();
        System.out.println("Returning refsetDto " + refsetDto);

        return refsetDto;
    }    
    

    @Transactional
    @RequestMapping(value = "/refset/{pubId}/concepts.json", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody XmlRefsetConcepts getConceptJson(@PathVariable String pubId) throws Exception {
        return new XmlRefsetConcepts(getXmlConceptDtos(pubId));
    }

    @Transactional
    @RequestMapping(value = "/refset/{pubId}/concepts.xml", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody XmlRefsetConcepts getConceptXml(@PathVariable String pubId) throws Exception {
        return new XmlRefsetConcepts(getXmlConceptDtos(pubId));
    }
    
    @Transactional
    @RequestMapping(value = "/refset/{pubId}/export", method = RequestMethod.GET)
    public void exportConcepts(@PathVariable String pubId, HttpServletResponse response) throws JAXBException, ConceptsCacheNotBuiltException {
        try {
            List<XmlRefsetConcept> conceptDtos = getXmlConceptDtos(pubId);
            XmlRefsetConcepts conceptsDto = new XmlRefsetConcepts(conceptDtos);
            //marshaller.marshal(conceptDtos, response.getOutputStream());
            Result result = new StreamResult(response.getOutputStream());
            marshaller.marshal(conceptsDto, result);
            response.flushBuffer();
        } catch (IOException ex) {
          LOG.error("Error writing file to output stream. Public ID was '" + pubId + "'");
          throw new RuntimeException("IOError writing file to output stream");
        }

    }    
    
    
    @Transactional
    @RequestMapping(value = "/refsets.json", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody XmlRefsets findAllRefsetsJson() throws Exception {
        return new XmlRefsets(getRefsetsDto());
    }
    
    @Transactional
    @RequestMapping(value = "/refsets.xml", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            headers="Accept=*/*",
            produces=MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody XmlRefsets findAllRefsetsXml() throws Exception {
        return new XmlRefsets(getRefsetsDto());
    }



    private List<XmlRefsetShort> getRefsetsDto() throws MalformedURLException {
        List<Refset> refsets = refsetService.findAll();
        List<XmlRefsetShort> xmlRefsetShorts = new ArrayList<>();
        for (Refset r : refsets){
            xmlRefsetShorts.add(new XmlRefsetShort(r));
        }
        return xmlRefsetShorts;
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
//
//    private void addDummyData(RefsetDto refsetDto) {
//        ConceptDto c1 = ConceptDto.getBuilder().id(321987003L).build();
//        ConceptDto c2 = ConceptDto.getBuilder().id(441519008L).build();
//        ConceptDto c3 = ConceptDto.getBuilder().id(128665000L).build();
//        
//        ConceptDto c4 = ConceptDto.getBuilder().id(412398008L).build();
//        ConceptDto c5 = ConceptDto.getBuilder().id(118831003L).build();
//        ConceptDto c6 = ConceptDto.getBuilder().id(254597002L).build();
//        
//        RefsetRuleDto listRuleDtoLeft = RefsetRuleDto.getBuilder()
//                .id(-1L)
//                .type(RuleType.LIST)
//                .add(c1).add(c2).add(c3)
//                .build();
//        
//        RefsetRuleDto listRuleDtoRight = RefsetRuleDto.getBuilder()
//                .id(-2L)
//                .add(c4).add(c5).add(c6)
//                .type(RuleType.LIST)
//                .build();
//        
//        RefsetRuleDto unionRuleDto = RefsetRuleDto.getBuilder()
//                .id(-3L)
//                .type(RuleType.UNION)
//                .left(listRuleDtoLeft.getId())
//                .right(listRuleDtoRight.getId())
//                .build();
//        
//        RefsetPlanDto plan = RefsetPlanDto.getBuilder()
//               .terminal(unionRuleDto.getId())
//               .id(-1L)
//               .add(listRuleDtoLeft)
//               .add(listRuleDtoRight)
//               .add(unionRuleDto)
//               .build();
//        
//        refsetDto.setPlan(plan);
//    }
//    
//    

    private BindingResult setPublicIdNotUniqueFieldError(RefsetDto refsetDto, BindingResult result){
        result.addError(new FieldError(
                result.getObjectName(), 
                "publicId", 
                refsetDto.getPublicId(),
                false, 
                null,
                null,
                "xml.response.error.publicid.not.unique"));
        return result;
    }

    private void addFeedbackMessage(RedirectAttributes attributes,
            String messageCode, Object... messageParameters) {
        String localizedFeedbackMessage = getMessage(messageCode,
                messageParameters);
        attributes
                .addFlashAttribute(FEEDBACK_MESSAGE, localizedFeedbackMessage);
    }

    private String getMessage(String messageCode, Object... messageParameters) {
        Locale current = LocaleContextHolder.getLocale();
        return messageSource
                .getMessage(messageCode, messageParameters, current);
    }

    public void setRefsetService(RefsetService refsetService) {
        this.refsetService = refsetService;
    }

    public RefsetService getRefsetService() {
        return refsetService;
    }
}
