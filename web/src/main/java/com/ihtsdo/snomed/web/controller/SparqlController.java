package com.ihtsdo.snomed.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.web.dto.SparqlQueryFormObject;
import com.ihtsdo.snomed.web.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.web.model.SparqlResults;
import com.ihtsdo.snomed.web.model.User;
import com.ihtsdo.snomed.web.service.OntologyService;
import com.ihtsdo.snomed.web.service.SparqlService;

@Controller
@RequestMapping("/")
@Transactional(value="transactionManager", readOnly=true)
public class SparqlController {

    private static final Logger LOG = LoggerFactory.getLogger( SparqlController.class );

    @Inject SparqlService sparql;
    @Inject OntologyService ontologyService;
    
    @PostConstruct
    public void init(){}
    
    @RequestMapping(value="/ontology/{ontologyId}/sparql", method = RequestMethod.POST)
    public ModelAndView runSparqlQuery(ModelMap model, HttpServletRequest request, 
            @PathVariable long ontologyId, Principal principal,
            @ModelAttribute("sparql") SparqlQueryFormObject query) 
                    throws ConceptNotFoundException, RestClientException, XPathExpressionException, 
                    URISyntaxException, ParserConfigurationException, SAXException, IOException
                    
    {   
        //LOG.info("Executing query:\n {}", query.getQuery());
        Stopwatch overAllstopwatch = new Stopwatch().start();
        SparqlResults results = sparql.runQuery(paddQueryWithPrefixes(query.getQuery(), ontologyId), ontologyId);
        model.put("results", results);
        
        model.addAttribute("ontologies", ontologyService.getAll()); 
        model.addAttribute("ontologyId", ontologyId);
//        model.addAttribute("user", (User)((OpenIDAuthenticationToken)principal).getPrincipal());
        
        
        overAllstopwatch.stop();
        LOG.info("Query completed in " + overAllstopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return new ModelAndView("sparql", model);
    }
    
    private String paddQueryWithPrefixes(String query, long ontologyId){
        return 
              "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
              "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
              "PREFIX c: <http://browser.sparklingideas.co.uk/ontology/" + ontologyId + "/concept/rdfs/ >\n" +
              "PREFIX d: <http://browser.sparklingideas.co.uk/ontology/" + ontologyId + "/description/rdfs >\n" +
              "PREFIX s: <http://browser.sparklingideas.co.uk/ontology/" + ontologyId + "/statement/rdfs/ >\n" +
              "PREFIX sn: <http://browser.sparklingideas.co.uk/term/>\n" +
              "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
              query; 
    }
    
    @RequestMapping(value="/ontology/{ontologyId}/sparql", method = RequestMethod.GET)
    public ModelAndView displaySparqlQueryDialogue(ModelMap model, HttpServletRequest request, 
            @PathVariable long ontologyId, Principal principal) throws ConceptNotFoundException
    {
        //System.out.println("Rendering query page for ontology [" + ontologyId + "]");
        
        model.addAttribute("ontologies", ontologyService.getAll()); 
        model.addAttribute("ontologyId", ontologyId);
//        model.addAttribute("user", (User)((OpenIDAuthenticationToken)principal).getPrincipal());
        
        SparqlQueryFormObject formObject = new SparqlQueryFormObject();
        formObject.setQuery(
//                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
//                "PREFIX c: <http://browser.sparklingideas.co.uk/ontology/" + ontologyId + "/concept/>\n" +
//                "PREFIX d: <http://browser.sparklingideas.co.uk/ontology/" + ontologyId + "/description/>\n" +
//                "PREFIX s: <http://browser.sparklingideas.co.uk/ontology/" + ontologyId + "/statement/>\n\n" +
//                
                "SELECT * WHERE {\n" +
                " ?s ?p ?o\n" +
                "} LIMIT 10\n");
        return new ModelAndView("sparql", "sparql", formObject);
    }    
}
