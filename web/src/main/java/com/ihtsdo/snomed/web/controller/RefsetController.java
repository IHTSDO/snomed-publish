package com.ihtsdo.snomed.web.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.AutoPopulatingList;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.dto.refset.RefsetPlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto;
import com.ihtsdo.snomed.dto.refset.RefsetRuleDto.RuleType;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetRuleNotFoundException;
import com.ihtsdo.snomed.exception.UnconnectedRefsetRuleException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.RefsetPlan;
import com.ihtsdo.snomed.model.refset.rule.ListConceptsRefsetRule;
import com.ihtsdo.snomed.model.refset.rule.UnionRefsetRule;
import com.ihtsdo.snomed.service.RefsetService;
import com.ihtsdo.snomed.service.UnReferencedReferenceRuleException;
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
    
    private void initDummyPlan(Refset refset){
        Concept cc1 = new Concept(184933002L);
        Concept cc2 = new Concept(210009005L);
        Concept cc3 = new Concept(104641002L);
        Concept cc4 = new Concept(118831003L);
        Concept cc5 = new Concept(305101000L);
        Concept cc6 = new Concept(321987003L);
        
        ListConceptsRefsetRule listRuleLeft = new ListConceptsRefsetRule();
//        listRuleLeft.setId(-1L);
        listRuleLeft.addConcept(cc1);
        listRuleLeft.addConcept(cc2);
        listRuleLeft.addConcept(cc3);
        
        ListConceptsRefsetRule listRuleRight = new ListConceptsRefsetRule();
//        listRuleRight.setId(-2L);
        listRuleRight.addConcept(cc4);
        listRuleRight.addConcept(cc5);
        listRuleRight.addConcept(cc6);
        
        UnionRefsetRule unionRule = new UnionRefsetRule();
//        unionRule.setId(-3L);
        unionRule.setLeftRule(listRuleLeft);
        unionRule.setRightRule(listRuleRight);
        
        RefsetPlan plan = RefsetPlan.getBuilder(unionRule).build();
        refset.setPlan(plan);
        em.merge(refset);
        em.flush();
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

        return new ModelAndView("redirect:/refsets");
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
            result.addError(createFieldError(refsetDto, result));
        }
        
        if (result.hasErrors()) {
            return new ModelAndView("/refset/new.refset");
        }
        try {            
            addDummyData(refsetDto);
            Refset created = refsetService.create(refsetDto);
            addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_REFSET_ADDED, created.getPublicId(), created.getTitle());
            return new ModelAndView("redirect:/refsets");
        } catch (NonUniquePublicIdException e) {
            result.addError(createFieldError(refsetDto, result));
            return new ModelAndView("/refset/new.refset");
        }
    }

    private void addDummyData(RefsetDto refsetDto) {
        ConceptDto c1 = ConceptDto.getBuilder().id(321987003L).build();
        ConceptDto c2 = ConceptDto.getBuilder().id(441519008L).build();
        ConceptDto c3 = ConceptDto.getBuilder().id(128665000L).build();
        
        ConceptDto c4 = ConceptDto.getBuilder().id(412398008L).build();
        ConceptDto c5 = ConceptDto.getBuilder().id(118831003L).build();
        ConceptDto c6 = ConceptDto.getBuilder().id(254597002L).build();
        
        RefsetRuleDto listRuleDtoLeft = RefsetRuleDto.getBuilder()
                .id(-1L)
                .type(RuleType.LIST)
                .add(c1).add(c2).add(c3)
                .build();
        
        RefsetRuleDto listRuleDtoRight = RefsetRuleDto.getBuilder()
                .id(-2L)
                .add(c4).add(c5).add(c6)
                .type(RuleType.LIST)
                .build();
        
        RefsetRuleDto unionRuleDto = RefsetRuleDto.getBuilder()
                .id(-3L)
                .type(RuleType.UNION)
                .left(listRuleDtoLeft.getId())
                .right(listRuleDtoRight.getId())
                .build();
        
        RefsetPlanDto plan = RefsetPlanDto.getBuilder()
               .terminal(unionRuleDto.getId())
               .id(-1L)
               .add(listRuleDtoLeft)
               .add(listRuleDtoRight)
               .add(unionRuleDto)
               .build();
        
        refsetDto.setPlan(plan);
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
        LOG.debug("Controller received request to update refset [{}]",
                refsetDto.toString());
//        model.addAttribute("user",
//                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        model.addAttribute("pubid", pubId);
        model.addAttribute("refset", refsetDto);
        
        Refset refset = refsetService.findById(refsetDto.getId());
        if (!refset.getPublicId().equals(refsetDto.getPublicId())){
            //Assertion: user has updated the public id
            refset = refsetService.findByPublicId(refsetDto.getPublicId());
            if (refset != null){
                result.addError(createFieldError(refsetDto, result));
            }
        }
        
        if (result.hasErrors()) {
            return new ModelAndView("/refset/edit.refset");
        }
        
        try {
            Refset updated = refsetService.update(refsetDto);
            addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_REFSET_UPDATED, updated.getPublicId(), updated.getTitle());
            return new ModelAndView("redirect:/refset/" + updated.getPublicId());
        } catch (NonUniquePublicIdException e) {
            //defensive coding
            result.addError(createFieldError(refsetDto, result));
            return new ModelAndView("/refset/edit.refset");
        }
    }

    private FieldError createFieldError(RefsetDto refsetDto,
            BindingResult result) {
        return new FieldError(
                result.getObjectName(), 
                "publicId", 
                refsetDto.getPublicId(),
                false, 
                null,
                null,
                "validation.refset.publicid.notunique");
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
