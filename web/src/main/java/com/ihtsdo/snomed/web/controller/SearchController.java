package com.ihtsdo.snomed.web.controller;

import java.security.Principal;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ihtsdo.snomed.web.model.User;
import com.ihtsdo.snomed.web.service.OntologyService;

@Controller
@RequestMapping("/")
@Transactional(value="transactionManager", readOnly=true)
public class SearchController {
    private static final Logger LOG = LoggerFactory.getLogger( SearchController.class );

    @Inject OntologyService ontologyService;

    @PostConstruct
    public void init(){}
    
    @RequestMapping(value="/ontology/{ontologyId}/search", method = RequestMethod.GET)
    public ModelAndView searchPage(ModelMap model, HttpServletRequest request, 
            @PathVariable long ontologyId, Principal principal)
    {   
        LOG.debug("Rendering search page for ontology " + ontologyId);
        model.addAttribute("ontologies", ontologyService.getAll()); 
        model.addAttribute("ontologyId", ontologyId);
        model.addAttribute("user", (User)((OpenIDAuthenticationToken)principal).getPrincipal());

        return new ModelAndView("/search/search", model);
    }
}
