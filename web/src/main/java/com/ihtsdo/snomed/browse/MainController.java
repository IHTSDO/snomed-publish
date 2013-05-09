package com.ihtsdo.snomed.browse;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ihtsdo.snomed.browse.ConceptService.ConceptNotFoundException;
import com.ihtsdo.snomed.browse.OntologyService.InvalidConceptsException;
import com.ihtsdo.snomed.browse.OntologyService.InvalidStatementsException;
import com.ihtsdo.snomed.browse.OntologyService.OntologyNotFoundException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.InvalidInputException;

@Controller
@RequestMapping("/")
public class MainController {    
    private static final Logger LOG = LoggerFactory.getLogger( MainController.class );

    @Autowired OntologyService ontologyService;
    @Autowired ConceptService conceptService;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public void landingPage(HttpServletResponse response, HttpServletRequest request) throws IOException{
        response.sendRedirect("ontologies");
    }
    
    @RequestMapping(value="/ontology/{ontologyId}", method = RequestMethod.GET)
    public ModelAndView ontologyDetails(@PathVariable long ontologyId, ModelMap model, HttpServletRequest request){            
        return new ModelAndView("redirect:" + ontologyId + "/concept/138875005");
 
    }        
        
    /*
    <form:form name="createCustomer" action="/practicemvc/customers/create/" method="POST" modelAttribute="fileUpload">
    <form:errors />
    <label for="customerName">Name</label>
    <input type="text" name="name" id="customerName" value="${customerBean.name}" />
    <form:errors path="name" />
    */
    @RequestMapping(value="/ontology/import", method = RequestMethod.POST)
    public ModelAndView importOntology(FileUpload uploadItem, BindingResult result, ModelMap map, HttpServletRequest request) throws IOException{        
//        if (result.hasErrors()){
//            for(ObjectError error : result.getAllErrors()){
//                throw new InvalidInputException("Error: " + error.getCode() + " - " + error.getDefaultMessage());
//            }
//        }
        ModelAndView mv = new ModelAndView("ontologies");        
        try {
            Ontology o = ontologyService.importOntology(uploadItem.getConcepts().getInputStream(), uploadItem.getRelationships().getInputStream(), uploadItem.getName());
            LOG.info("Imported ontology " + o.getName());
        } catch (InvalidConceptsException e) {
            result.addError(new FieldError("fileUpload", "concepts", "File format not recognised"));
        }catch (InvalidStatementsException e) {
            result.addError(new FieldError("fileUpload", "relationships", "File format not recognised"));
        }catch (InvalidInputException e){
            result.addError(new ObjectError("fileUpload", e.getMessage()));            
        }
        return mv;
    }

    @RequestMapping(value="/ontology/{ontologyId}/export", method = RequestMethod.GET)
    public ModelAndView exportOntology(@PathVariable long ontologyId, ModelMap map, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException, OntologyNotFoundException{
        Ontology ontology = ontologyService.getOntology(ontologyId);
        response.setHeader("Content-Disposition", "attachment;filename=" + ontology.getName() + ".ontology.txt");
        response.setContentType("text/ontology");
        response.setHeader("Content-Encoding", "UTF-8");
        ontologyService.exportCanonical(ontology, new OutputStreamWriter(response.getOutputStream(), "UTF-8"));        
        response.flushBuffer();
        return null;
    }        
    
    @RequestMapping(value="/ontology/{ontologyId}/delete", method = RequestMethod.GET)
    public ModelAndView deleteOntology(@PathVariable long ontologyId, ModelMap map, HttpServletRequest request) throws OntologyNotFoundException{
        ontologyService.deleteOntology(ontologyId);
        ModelAndView mv = new ModelAndView("redirect:../../ontologies");
        return mv;
    }     
    
    @RequestMapping(value="/ontologies", method = RequestMethod.GET)
    public ModelAndView getOntologies(ModelMap map, HttpServletRequest request){
        ModelAndView mv = new ModelAndView("ontologies");
        map.put("ontologies", ontologyService.getAll());
        return mv;
    }
    
    @RequestMapping(value="/ontology/{ontologyId}/concept/{serialisedId}", method = RequestMethod.GET)
    public ModelAndView conceptDetails(@PathVariable long ontologyId, @PathVariable long serialisedId, ModelMap model,
            HttpServletRequest request) throws ConceptNotFoundException
    {            
        Concept c = conceptService.getConcept(serialisedId, ontologyId);
        
        List<Statement> objectOf = new ArrayList<Statement>();
        List<Statement> predicateOf = new ArrayList<Statement>();
        List<Statement> subjectOf = new ArrayList<Statement>();
        conceptService.populateStatementsForView(c, objectOf, predicateOf, subjectOf);

        model.addAttribute("objectOf", objectOf);
        model.addAttribute("predicateOf", predicateOf);
        model.addAttribute("subjectOf", subjectOf);
        model.addAttribute("servletPath", request.getServletPath());
        model.addAttribute("concept", c);
        model.addAttribute("ontologies", ontologyService.getAll());
        model.addAttribute("ontologyId", ontologyId);
        model.addAttribute("fullySpecifiedName", c.getFullySpecifiedName().toLowerCase().substring(0, 1).toUpperCase() + c.getFullySpecifiedName().toLowerCase().substring(1));
        model.addAttribute("type", ((c.getType() == null) || c.getType().isEmpty()) ? "Type not specified" : c.getType().toLowerCase().substring(0, 1).toUpperCase() + c.getType().toLowerCase().substring(1));

        LOG.debug("Concept: {}", c);
        LOG.debug("subjectOf: {}", subjectOf.size());
        LOG.debug("predicateOf: {}", predicateOf.size());
        LOG.debug("objectOf: {}", objectOf.size());
        LOG.debug("kindOf: {}", c.getKindOfs().size());
        
        return new ModelAndView("concept");
    }

    @ExceptionHandler(ConceptNotFoundException.class)
    public ModelAndView handleConceptNotFoundException(HttpServletRequest request, ConceptNotFoundException exception){
        ModelAndView modelAndView = new ModelAndView("concept.not.found");
        modelAndView.addObject("id", exception.getConceptId());
        modelAndView.addObject("ontologyId", exception.getOntologyId());
        return modelAndView;
    }
    
    @ExceptionHandler(OntologyNotFoundException.class)
    public ModelAndView handleConceptNotFoundException(HttpServletRequest request, OntologyNotFoundException exception){
        ModelAndView modelAndView = new ModelAndView("ontology.not.found");
        modelAndView.addObject("id", exception.getOntologyId());
        return modelAndView;
    }    
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleErrors(Exception exception){
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }    
    
//  @Transactional
//  @RequestMapping(value = "/ontology/{ontologyId}/concept/{serialisedId}/json", 
//          method = RequestMethod.GET, 
//          produces=MediaType.APPLICATION_JSON_VALUE)
//  @ResponseBody
//  public Concept getConceptJson(@PathVariable long ontologyId, @PathVariable long serialisedId) throws Exception {
//      System.out.println("JSON!!");
//      Concept c = getConcept(ontologyId, serialisedId);
//      return c;
//  }
//  
//  @Transactional
//  @RequestMapping(value = "/ontology/{ontologyId}/concept/{serialisedId}/xml", 
//          method = RequestMethod.GET, 
//          produces=MediaType.APPLICATION_XML_VALUE)
//  @ResponseBody
//  public Concept getConceptXml(@PathVariable long ontologyId, @PathVariable long serialisedId) throws Exception {
//      System.out.println("XML!!");
//      Concept c = getConcept(ontologyId, serialisedId);
//      return c;
//  }    
//  
}
