package com.ihtsdo.snomed.web.controller;

import java.security.Principal;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@Transactional(value="transactionManager", readOnly=true)
public class GraphController {
    private static final Logger LOG = LoggerFactory.getLogger( GraphController.class );

    @PostConstruct
    public void init(){}
    
    @RequestMapping(value="/version/{ontologyId}/graph", method = RequestMethod.GET)
    public ModelAndView searchPage(ModelMap model, HttpServletRequest request, 
            @PathVariable long ontologyId, Principal principal)
    {   
        LOG.debug("Rendering graph / structured search page for ontology " + ontologyId); 


        return new ModelAndView("/graph/graph", model);
    }
}
