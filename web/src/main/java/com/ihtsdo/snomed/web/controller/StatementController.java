package com.ihtsdo.snomed.web.controller;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Ontology.Source;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.model.xml.XmlStatement;
import com.ihtsdo.snomed.service.InvalidInputException;
import com.ihtsdo.snomed.web.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.web.exception.DescriptionNotFoundException;
import com.ihtsdo.snomed.web.exception.StatementNotFoundException;
import com.ihtsdo.snomed.web.service.OntologyService;
import com.ihtsdo.snomed.web.service.RdfService;

@Controller
@RequestMapping("/version/{ontologyId}/triple")
@Transactional (value = "transactionManager", readOnly = true)
public class StatementController {
    private static final Logger LOG = LoggerFactory.getLogger( StatementController.class );

    @Autowired OntologyService ontologyService;
    @Inject RdfService rdfService;

    @PersistenceContext(unitName="hibernatePersistenceUnit")
    EntityManager em;
    
    CriteriaBuilder builder;
    ParameterExpression<Long> cid;
    CriteriaQuery<Statement> valueOfQuery;
    CriteriaQuery<Long> valueOfCountQuery;
    CriteriaQuery<Statement> objectOfQuery;
    CriteriaQuery<Long> objectOfCountQuery;
    CriteriaQuery<Statement> attributeOfQuery;
    CriteriaQuery<Long> attributeOfCountQuery;
    
    @PostConstruct
    public void init(){
        
    }
    
    @RequestMapping(value="{serialisedId}", method = RequestMethod.GET)
    public ModelAndView tripleDetails(@PathVariable long ontologyId, @PathVariable long serialisedId, ModelMap model,
            HttpServletRequest request) throws DescriptionNotFoundException
    {            
        Ontology o = em.createQuery("SELECT o FROM Ontology o WHERE o.id=:oid", Ontology.class)
                .setParameter("oid", ontologyId)
                .getSingleResult();
        Statement s = getStatement(ontologyId, serialisedId);
        model.addAttribute("statement", s);
        model.addAttribute("servletPath", request.getServletPath());
        model.addAttribute("ontologies", ontologyService.getAll());
        model.addAttribute("ontologyId", ontologyId);
        if (o.getSource().equals(Source.RF2)){
            return new ModelAndView("statement.rf2");
        }else {
            throw new InvalidInputException("Only RF2 ontologies supports this view");
        }
    }

    private Statement getStatement(long ontologyId, long serialisedId) {
        TypedQuery<Statement> getStatementQuery = em.createQuery(
                "SELECT s from Statement s " + 
                "LEFT JOIN FETCH s.characteristicType " +
                "LEFT JOIN FETCH s.module " +
                "LEFT JOIN FETCH s.modifier " +
                "LEFT JOIN FETCH s.subject " +
                "LEFT JOIN FETCH s.predicate " +
                "LEFT JOIN FETCH s.object " +
                "where s.ontology.id=:oid AND s.serialisedId=:serialisedId", Statement.class);
        getStatementQuery.setParameter("oid", ontologyId);
        getStatementQuery.setParameter("serialisedId", serialisedId);
        Statement s = getStatementQuery.getSingleResult();
        return s;
    }       

    
    
    @ExceptionHandler(StatementNotFoundException.class)
    public ModelAndView handleStatementNotFoundException(HttpServletRequest request, ConceptNotFoundException exception){
        ModelAndView modelAndView = new ModelAndView("statement.not.found");
        modelAndView.addObject("id", exception.getConceptId());
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
  public XmlStatement getConceptJson(@PathVariable long ontologyId, @PathVariable long serialisedId) throws Exception {
      System.out.println("JSON!!");
      Statement s = getStatement(ontologyId, serialisedId);
      XmlStatement xs = new XmlStatement(s);
      return xs;
  }
  
  @Transactional
  @RequestMapping(value = "/xml/{serialisedId}", 
          method = RequestMethod.GET, 
          produces=MediaType.APPLICATION_XML_VALUE)
  @ResponseBody
  public XmlStatement getTripleXml(@PathVariable long ontologyId, @PathVariable long serialisedId) throws Exception {
      System.out.println("XML!!");
      Statement s = getStatement(ontologyId, serialisedId);
      XmlStatement xs = new XmlStatement(s);
      return xs;
  }    

  @Transactional
  @RequestMapping(value = "/rdfs/{serialisedId}", 
          method = RequestMethod.GET, 
          produces=MediaType.APPLICATION_XML_VALUE)
  @ResponseBody
  public void getConceptRdfsXml(@PathVariable long ontologyId, @PathVariable long serialisedId,
          HttpServletResponse response, OutputStream os) 
          throws Exception 
  {
      System.out.println("RDFS!!");
      Statement s = getStatement(ontologyId, serialisedId);
      response.setContentType("application/rdf+xml");
      try (OutputStreamWriter ow = new OutputStreamWriter(os)){
          rdfService.writeStatement(s, ow, new Ontology(ontologyId));
      }
  }    
}
