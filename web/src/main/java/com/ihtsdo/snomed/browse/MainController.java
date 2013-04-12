package com.ihtsdo.snomed.browse;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ihtsdo.snomed.canonical.model.Concept;



@Controller
@RequestMapping("/")
public class MainController {
    
    private static final Logger LOG = LoggerFactory.getLogger( MainController.class );

    
    @PersistenceContext
    EntityManager em;

    @RequestMapping(value="/ontology/{ontologyId}/concept/{conceptId}", method = RequestMethod.GET)
    //@RequestMapping(value="/hello", method = RequestMethod.GET)
    public String printWelcome(@PathVariable long ontologyId, @PathVariable long conceptId, ModelMap model) {
 
        LOG.info("HELLOOOOOOO!!!!! Ontology id is [" + ontologyId + "] and concept id is [" + conceptId +"]");
        
        
        Concept c = em.find(Concept.class, 63671006l);
        //Concept c = em.createQuery("SELECT c from Concept c WHERE id=63671006", Concept.class).getSingleResult();
        
        LOG.info("Found concept [" + c + "]");
        
        model.addAttribute("message", "Spring 3 MVC Hello World");
        return "hello";
 
    }

}
