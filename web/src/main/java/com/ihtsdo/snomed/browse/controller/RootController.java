package com.ihtsdo.snomed.browse.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ihtsdo.snomed.browse.ConceptService;
import com.ihtsdo.snomed.browse.OntologyService;

@Controller
@RequestMapping("/")
public class RootController {    

    //private static final Logger LOG = LoggerFactory.getLogger( RootController.class );

    @Autowired OntologyService ontologyService;
    @Autowired ConceptService conceptService;

    @PersistenceContext
    EntityManager em;    
    
    @PostConstruct
    public void init(){
       
    }
    
    @RequestMapping(value="/", method = RequestMethod.GET)
    public void landingPage(HttpServletResponse response, HttpServletRequest request) throws IOException{
        response.sendRedirect("ontologies");
    }
}
