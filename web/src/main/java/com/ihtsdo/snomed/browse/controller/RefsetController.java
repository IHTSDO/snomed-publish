package com.ihtsdo.snomed.browse.controller;

import java.security.Principal;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ihtsdo.snomed.browse.dto.RefsetDto;
import com.ihtsdo.snomed.browse.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.browse.model.User;
import com.ihtsdo.snomed.browse.service.OntologyService;
import com.ihtsdo.snomed.browse.service.RefsetService;
import com.ihtsdo.snomed.model.Refset;

/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
@Controller
@RequestMapping("/")
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

    @PostConstruct
    public void init() {
    }

    // ALL

    @RequestMapping(value = "/refsets", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView getRefsets(ModelMap model, HttpServletRequest request,
            Principal principal) {
        model.addAttribute("user",
                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        model.addAttribute("refsets", refsetService.findAll());
        return new ModelAndView("refsets", model);
    }

    // DETAILS

    @RequestMapping(value = "/refset/{pubId}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView getRefset(ModelMap model, HttpServletRequest request,
            Principal principal, @PathVariable String pubId) {
        model.addAttribute("user",
                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        model.addAttribute("refset", refsetService.findByPublicId(pubId));
        return new ModelAndView("refset", model);
    }

    // CREATE FORM

    @RequestMapping(value = "/refset/new", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView showNewRefsetForm(ModelMap model,
            HttpServletRequest request, Principal principal) {
        LOG.debug("Displaying new refset screen");
        model.addAttribute("user",
                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        return new ModelAndView("refset.new", "refset", new RefsetDto());
    }

    // EDIT FORM

    @RequestMapping(value = "/refset/{pubId}/edit", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView showEditRefsetForm(ModelMap model,
            HttpServletRequest request, Principal principal,
            @PathVariable String pubId) {
        LOG.debug(
                "Displaying edit refset screen for refset with publicId [{}]",
                pubId);
        Refset refset = refsetService.findByPublicId(pubId);
        model.addAttribute("pubid", pubId);
        model.addAttribute("user",
                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        return new ModelAndView("refset.edit", "refset", new RefsetDto(
                refset.getId(), refset.getPublicId(), refset.getTitle(),
                refset.getDescription()));
    }

    // DELETE

    @RequestMapping(value = "/refset/{pubId}", method = RequestMethod.DELETE, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView deleteRefset(ModelMap model,
            HttpServletRequest request, Principal principal,
            @PathVariable String pubId, RedirectAttributes attributes)
            throws RefsetNotFoundException {
        // TODO: Handle RefsetNotFoundException
        LOG.debug("Controller received request to delete refset [{}]", pubId);
        Refset deleted = refsetService.delete(pubId);

        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_REFSET_DELETED,
                deleted.getPublicId(), deleted.getTitle());

        return new ModelAndView("redirect:/refsets");

    }

    // CREATE

    @Transactional
    @RequestMapping(value = "/refset/new", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE })
    public ModelAndView ceateRefset(ModelMap model, HttpServletRequest request,
            Principal principal,
            @Valid @ModelAttribute("refset") RefsetDto refsetDto,
            BindingResult result, RedirectAttributes attributes) {
        LOG.debug("Controller received request to create new refset [{}]",
                refsetDto.toString());
        model.addAttribute("user",
                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        if (result.hasErrors()) {
            return new ModelAndView("refset.new");
        }
        Refset created = refsetService.create(refsetDto);
        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_REFSET_ADDED,
                created.getPublicId(), created.getTitle());
        return new ModelAndView("redirect:/refsets");
    }

    // UPDATE

    @Transactional
    @RequestMapping(value = "/refset/{pubId}/edit", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView updateRefset(ModelMap model,
            HttpServletRequest request, Principal principal,
            RedirectAttributes attributes, @PathVariable String pubId,
            @Valid @ModelAttribute("refset") RefsetDto refsetDto,
            BindingResult result) throws RefsetNotFoundException {
        // TODO: Handle RefsetNotFoundException
        LOG.debug("Controller received request to update refset [{}]",
                refsetDto.toString());
        model.addAttribute("user",
                (User) ((OpenIDAuthenticationToken) principal).getPrincipal());
        model.addAttribute("pubid", pubId);
        if (result.hasErrors()) {
            model.addAttribute("refset",
                    //refsetService.findById(refsetDto.getId()));
                    refsetDto);
            return new ModelAndView("refset.edit");

        }
        Refset updated = refsetService.update(refsetDto);
        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_REFSET_UPDATED,
                updated.getPublicId(), updated.getTitle());
        return new ModelAndView("redirect:/refset/" + updated.getPublicId());
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