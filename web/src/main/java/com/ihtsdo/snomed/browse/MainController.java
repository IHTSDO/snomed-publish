package com.ihtsdo.snomed.browse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.ihtsdo.snomed.browse.ConceptService.ConceptNotFoundException;
import com.ihtsdo.snomed.browse.OntologyService.OntologyNotFoundException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Ontology.Source;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.InvalidInputException;
import com.ihtsdo.snomed.service.ProgrammingException;

@Controller
@RequestMapping("/")
public class MainController {    
    private static final Logger LOG = LoggerFactory.getLogger( MainController.class );

    @Autowired OntologyService ontologyService;
    @Autowired ConceptService conceptService;

    @PersistenceContext
    EntityManager em; 
    
    @RequestMapping(value="/", method = RequestMethod.GET)
    public void landingPage(HttpServletResponse response, HttpServletRequest request) throws IOException{
        response.sendRedirect("ontologies");
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
    
    @RequestMapping(value="/ontology/{ontologyId}/concept/{serialisedId}", method = RequestMethod.GET)
    public ModelAndView conceptDetails(@PathVariable long ontologyId, @PathVariable long serialisedId, ModelMap model,
            HttpServletRequest request) throws ConceptNotFoundException
    {            
        Ontology o = em.createQuery("SELECT o FROM Ontology o WHERE o.id=:oid", Ontology.class)
                .setParameter("oid", ontologyId)
                .getSingleResult();
        Concept c = conceptService.getConcept(serialisedId, ontologyId);
        
        List<Statement> objectOf = new ArrayList<Statement>();
        List<Statement> predicateOf = new ArrayList<Statement>();
        List<Statement> subjectOf = new ArrayList<Statement>();
        List<Description> descriptions = new ArrayList<>(c.getDescription());
        conceptService.populateStatementsForView(c, objectOf, predicateOf, subjectOf);
        Collections.sort(descriptions, byTypeActiveAndTerm.nullsLast());

        
        model.addAttribute("objectOf", objectOf);
        model.addAttribute("predicateOf", predicateOf);
        model.addAttribute("subjectOf", subjectOf);
        model.addAttribute("descriptions", descriptions);
        model.addAttribute("concept", c);
        model.addAttribute("servletPath", request.getServletPath());
        model.addAttribute("ontologies", ontologyService.getAll());
        model.addAttribute("ontologyId", ontologyId);
        //model.addAttribute("fullySpecifiedName", c.getFullySpecifiedName().toLowerCase().substring(0, 1).toUpperCase() + c.getFullySpecifiedName().toLowerCase().substring(1));
        
        String disambiguationType = null;
        String parsedDisplayName = null;
        String displayName = c.getDisplayName();
        if (displayName.lastIndexOf(')') == -1){
            parsedDisplayName = displayName;
        }else{
            parsedDisplayName = displayName.substring(0, displayName.lastIndexOf('(') - 1);
            disambiguationType = displayName.trim().substring(displayName.trim().lastIndexOf('(') + 1, displayName.trim().length() - 1);
        }            

        model.addAttribute("displayName", parsedDisplayName.toLowerCase().substring(0, 1).toUpperCase() + parsedDisplayName.toLowerCase().substring(1));
        model.addAttribute("type", ((disambiguationType == null)) ? null : disambiguationType.toLowerCase().substring(0, 1).toUpperCase() + disambiguationType.toLowerCase().substring(1));

        LOG.debug("descriptions: ", c.getDescription());
        LOG.debug("Concept: {}", c);
        LOG.debug("subjectOf: {}", subjectOf.size());
        LOG.debug("predicateOf: {}", predicateOf.size());
        LOG.debug("objectOf: {}", objectOf.size());
        LOG.debug("kindOf: {}", c.getKindOfs().size());
        
        if (o.getSource().equals(Source.RF2)){
            return new ModelAndView("concept.rf2");
        }else {
            return new ModelAndView("concept");
        } 
    }
    
    @RequestMapping(value="/ontology/{ontologyId}/description/{serialisedId}", method = RequestMethod.GET)
    public ModelAndView descriptionDetails(@PathVariable long ontologyId, @PathVariable long serialisedId, ModelMap model,
            HttpServletRequest request) throws DescriptionNotFoundException
    {            
        Ontology o = em.createQuery("SELECT o FROM Ontology o WHERE o.id=:oid", Ontology.class)
                .setParameter("oid", ontologyId)
                .getSingleResult();
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

    @ExceptionHandler(ConceptNotFoundException.class)
    public ModelAndView handleConceptNotFoundException(HttpServletRequest request, ConceptNotFoundException exception){
        ModelAndView modelAndView = new ModelAndView("concept.not.found");
        modelAndView.addObject("id", exception.getConceptId());
        modelAndView.addObject("ontologyId", exception.getOntologyId());
        return modelAndView;
    }

    @ExceptionHandler(DescriptionNotFoundException.class)
    public ModelAndView handleDescriptionNotFoundException(HttpServletRequest request, DescriptionNotFoundException exception){
        ModelAndView modelAndView = new ModelAndView("description.not.found");
        modelAndView.addObject("id", exception.getDescriptionId());
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
    
    public static class DescriptionNotFoundException extends Exception{
        private static final long serialVersionUID = 1L;
        private long descriptionId;
        private long ontologyId;
        
        public DescriptionNotFoundException(long descriptionId, long ontologyId){
            this.descriptionId = descriptionId;
            this.ontologyId = ontologyId;
        }

        public long getDescriptionId() {
            return descriptionId;
        }

        public long getOntologyId() {
            return ontologyId;
        }
    }    
   
    private Ordering<Description> byTypeActiveAndTerm = new Ordering<Description>() {
        @Override
        public int compare(Description d1, Description d2) {
            if (d1.isFullySpecifiedName() && !d2.isFullySpecifiedName()){                
                return -1;
            }
            else if (!d1.isFullySpecifiedName() && d2.isFullySpecifiedName()){
                return 1;
            }
            else if ((d1.isFullySpecifiedName() && d2.isFullySpecifiedName()) || (!d1.isFullySpecifiedName() && !d2.isFullySpecifiedName())){
                if ((d1.isActive() && d2.isActive()) || (!d1.isActive() && !d2.isActive())){
                    return d1.getTerm().compareTo(d2.getTerm());
                }
                else if (d1.isActive() && !d2.isActive()){
                    return -1;
                }
                else if (!d1.isActive() && d2.isActive()){
                    return 1;
                }
            }
            throw new ProgrammingException("Compare logic should never reach this point");
        }
    };  
    
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
    
    /*
    <form:form name="createCustomer" action="/practicemvc/customers/create/" method="POST" modelAttribute="fileUpload">
    <form:errors />
    <label for="customerName">Name</label>
    <input type="text" name="name" id="customerName" value="${customerBean.name}" />
    <form:errors path="name" />
    */
//    @RequestMapping(value="/ontology/import", method = RequestMethod.POST)
//    public ModelAndView importOntology(FileUpload uploadItem, BindingResult result, ModelMap map, HttpServletRequest request) throws IOException{        
////        if (result.hasErrors()){
////            for(ObjectError error : result.getAllErrors()){
////                throw new InvalidInputException("Error: " + error.getCode() + " - " + error.getDefaultMessage());
////            }
////        }
//        ModelAndView mv = new ModelAndView("ontologies");        
//        try {
//            Ontology o = ontologyService.importOntology(uploadItem.getConcepts().getInputStream(), uploadItem.getRelationships().getInputStream(), uploadItem.getName());
//            LOG.info("Imported ontology " + o.getName());
//        } catch (InvalidConceptsException e) {
//            result.addError(new FieldError("fileUpload", "concepts", "File format not recognised"));
//        }catch (InvalidStatementsException e) {
//            result.addError(new FieldError("fileUpload", "relationships", "File format not recognised"));
//        }catch (InvalidInputException e){
//            result.addError(new ObjectError("fileUpload", e.getMessage()));            
//        }
//        return mv;
//    }

//    @RequestMapping(value="/ontology/{ontologyId}/export", method = RequestMethod.GET)
//    public ModelAndView exportOntology(@PathVariable long ontologyId, ModelMap map, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException, OntologyNotFoundException{
//        Ontology ontology = ontologyService.getOntology(ontologyId);
//        response.setHeader("Content-Disposition", "attachment;filename=" + ontology.getName() + ".ontology.txt");
//        response.setContentType("text/ontology");
//        response.setHeader("Content-Encoding", "UTF-8");
//        ontologyService.exportCanonical(ontology, new OutputStreamWriter(response.getOutputStream(), "UTF-8"));        
//        response.flushBuffer();
//        return null;
//    }        
    
//    @RequestMapping(value="/ontology/{ontologyId}/delete", method = RequestMethod.GET)
//    public ModelAndView deleteOntology(@PathVariable long ontologyId, ModelMap map, HttpServletRequest request) throws OntologyNotFoundException{
//        ontologyService.deleteOntology(ontologyId);
//        ModelAndView mv = new ModelAndView("redirect:../../ontologies");
//        return mv;
//    }  
}
