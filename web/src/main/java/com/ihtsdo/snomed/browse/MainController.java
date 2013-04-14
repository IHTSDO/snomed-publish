package com.ihtsdo.snomed.browse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.Ontology;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;



@Controller
@RequestMapping("/")
public class MainController {    
    private static final Logger LOG = LoggerFactory.getLogger( MainController.class );

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;
    
    private Map<Long, Set<RelationshipStatement>> subjectOfCache = new HashMap<Long, Set<RelationshipStatement>>();
    private Map<Long, Set<RelationshipStatement>> objectOfCache = new HashMap<Long, Set<RelationshipStatement>>();

    @Transactional
    @RequestMapping(value="/ontology/{ontologyId}/concept/{serialisedId}", method = RequestMethod.GET)
    public String printWelcome(@PathVariable long ontologyId, 
            @PathVariable long serialisedId, ModelMap model,
            HttpServletRequest request) {        
        Concept c = em.createQuery("SELECT c FROM Concept c WHERE c.serialisedId=" + serialisedId + " AND c.ontology.id=" + ontologyId, Concept.class).getSingleResult();
        List<Ontology> ontologies = em.createQuery("SELECT o from Ontology o", Ontology.class).getResultList();
        
        model.addAttribute("servletPath", request.getServletPath());
        model.addAttribute("concept", c);
        model.addAttribute("ontologies", ontologies);
        model.addAttribute("ontologyId", ontologyId);
        
        if (subjectOfCache.get(c.getId()) == null){
            Set<RelationshipStatement> subjectOf = new HashSet<RelationshipStatement>();
            for (RelationshipStatement r : c.getSubjectOfRelationshipStatements()){
                if (!r.isKindOfRelationship()){
                    subjectOf.add(r);
                }
            }
            subjectOfCache.put(c.getId(), subjectOf);
        }
        
        if (objectOfCache.get(c.getId()) == null){
            Set<RelationshipStatement> objectOf = new HashSet<RelationshipStatement>();
            for (RelationshipStatement r : c.getObjectOfRelationshipStatements()){
                if (!r.isKindOfRelationship()){
                    objectOf.add(r);
                }
            }
            objectOfCache.put(c.getId(), objectOf);
        }
        
        model.addAttribute("objectOf", objectOfCache.get(c.getId()));
        model.addAttribute("predicateOf", c.getPredicateOfRelationshipStatements());
        model.addAttribute("subjectOf", subjectOfCache.get(c.getId()));
        
//        model.addAttribute("predicateOf", c.getPredicateOfRelationshipStatements());
//        model.addAttribute("subjectOf", c.getSubjectOfRelationshipStatements());
//        model.addAttribute("objectOf", c.getObjectOfRelationshipStatements());
        
        LOG.debug("Concept: " + c);
        LOG.debug("subjectOf: " + subjectOfCache.get(c.getId()).size());
        LOG.debug("predicateOf: " + c.getPredicateOfRelationshipStatements().size());
        LOG.debug("objectOf: " + objectOfCache.get(c.getId()).size());
        LOG.debug("kindOf: " + c.getKindOfs().size());
        
        return "concept";
 
    }

}
