package com.ihtsdo.snomed.browse.controller;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ihtsdo.snomed.browse.service.ConceptService;
import com.ihtsdo.snomed.browse.service.OntologyService;
import com.ihtsdo.snomed.browse.service.OntologyService.OntologyNotFoundException;

@Controller
@RequestMapping("/")
@Transactional (value = "transactionManager", readOnly = true)
public class OntologyController {    

    private static final Logger LOG = LoggerFactory.getLogger( OntologyController.class );

    @Autowired OntologyService ontologyService;
    @Autowired ConceptService conceptService;

    @PersistenceContext(unitName="hibernatePersistenceUnit")
    EntityManager em;    
    
    @PostConstruct
    public void init(){
       
    }
    
    @RequestMapping(value="/ontology/{ontologyId}", method = RequestMethod.GET)
    public ModelAndView ontologyDetails(@PathVariable long ontologyId, ModelMap model, HttpServletRequest request){            
        return new ModelAndView("redirect:" + ontologyId + "/concept/138875005");
 
    }  
    
    @RequestMapping(value="/ontologies", method = RequestMethod.GET)
    public ModelAndView getOntologies(ModelMap map, HttpServletRequest request){
        ModelAndView mv = new ModelAndView("ontologies");
        map.put("ontologies", ontologyService.getAll());
        return mv;
    }
    
    @ExceptionHandler(OntologyNotFoundException.class)
    public ModelAndView handleConceptNotFoundException(HttpServletRequest request, OntologyNotFoundException exception){
        LOG.error("Redirecting to error page", exception);
        ModelAndView modelAndView = new ModelAndView("ontology.not.found");
        modelAndView.addObject("id", exception.getOntologyId());
        return modelAndView;
    }    
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleErrors(Exception exception){
        LOG.error("Redirecting to error page", exception);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }
}
