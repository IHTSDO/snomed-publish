package com.ihtsdo.snomed.browse.controller;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ihtsdo.snomed.browse.exception.DescriptionNotFoundException;
import com.ihtsdo.snomed.browse.service.OntologyService;
import com.ihtsdo.snomed.browse.service.RdfService;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Ontology.Source;
import com.ihtsdo.snomed.model.xml.XmlDescription;
import com.ihtsdo.snomed.service.InvalidInputException;

@Controller
@RequestMapping("/ontology/{ontologyId}/description")
@Transactional (value = "transactionManager", readOnly = true)
public class DescriptionController {    

    private static final Logger LOG = LoggerFactory.getLogger( DescriptionController.class );

    @Inject OntologyService ontologyService;
    @Inject RdfService rdfService;

    @PersistenceContext(unitName="hibernatePersistenceUnit")
    EntityManager em;    
    
    @PostConstruct
    public void init(){
       
    }
   
    @RequestMapping(value="{serialisedId}", method = RequestMethod.GET)
    public ModelAndView descriptionDetails(@PathVariable long ontologyId, @PathVariable long serialisedId, ModelMap model,
            HttpServletRequest request) throws DescriptionNotFoundException
    {            
        Ontology o = em.createQuery("SELECT o FROM Ontology o WHERE o.id=:oid", Ontology.class)
                .setParameter("oid", ontologyId)
                .getSingleResult();
        Description d = getDescription(ontologyId, serialisedId);
        model.addAttribute("description", d);
        model.addAttribute("servletPath", request.getServletPath());
        model.addAttribute("ontologies", ontologyService.getAll());
        model.addAttribute("ontologyId", ontologyId);
        if (o.getSource().equals(Source.RF2)){
            return new ModelAndView("description.rf2");
        }else {
            throw new InvalidInputException("Only RF2 ontologies supports this view");
        }
    }

    private Description getDescription(long ontologyId, long serialisedId) {
        TypedQuery<Description> getDescriptionQuery = em.createQuery(
                "SELECT d from Description d " + 
                "LEFT JOIN FETCH d.about " +
                "LEFT JOIN FETCH d.module " +
                "LEFT JOIN FETCH d.type " +
                "LEFT JOIN FETCH d.caseSignificance " +
                "where d.ontology.id=:oid AND d.serialisedId=:serialisedId", Description.class);
        getDescriptionQuery.setParameter("oid", ontologyId);
        getDescriptionQuery.setParameter("serialisedId", serialisedId);
        Description d = getDescriptionQuery.getSingleResult();
        return d;
    }
    
    @ExceptionHandler(DescriptionNotFoundException.class)
    public ModelAndView handleDescriptionNotFoundException(HttpServletRequest request, DescriptionNotFoundException exception){
        ModelAndView modelAndView = new ModelAndView("description.not.found");
        modelAndView.addObject("id", exception.getDescriptionId());
        modelAndView.addObject("ontologyId", exception.getOntologyId());
        return modelAndView;
    }
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleErrors(Exception exception){
        LOG.error("Redirecting to error page", exception);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }
    
  
    
  @Transactional
  @RequestMapping(value = "/json/{serialisedId}", 
          method = RequestMethod.GET, 
          produces=MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public XmlDescription getDescriptionJson(@PathVariable long ontologyId, @PathVariable long serialisedId) throws Exception {
      System.out.println("JSON!!");
      Description d = getDescription(ontologyId, serialisedId);
      XmlDescription xd = new XmlDescription(d);
      return xd;
  }
  
  @Transactional
  @RequestMapping(value = "/xml/{serialisedId}", 
          method = RequestMethod.GET, 
          produces=MediaType.APPLICATION_XML_VALUE)
  @ResponseBody
  public XmlDescription getDescriptionXml(@PathVariable long ontologyId, @PathVariable long serialisedId) throws Exception {
      System.out.println("XML!!");
      Description d = getDescription(ontologyId, serialisedId);
      XmlDescription xd = new XmlDescription(d);
      return xd;
  }

  @Transactional
  @RequestMapping(value = "/rdfs/{serialisedId}", 
          method = RequestMethod.GET, 
          produces=MediaType.APPLICATION_XML_VALUE)
  @ResponseBody
  public void getDescriptionRdfsXml(@PathVariable long ontologyId, @PathVariable long serialisedId,
          HttpServletResponse response, OutputStream os) 
          throws Exception 
  {
      System.out.println("RDFS!!");
      Description d = getDescription(ontologyId, serialisedId);
      response.setContentType("application/rdf+xml");
      try (OutputStreamWriter ow = new OutputStreamWriter(os)){
          rdfService.writeDescription(d, ow, new Ontology(ontologyId));
      }
  }     
}
