package com.ihtsdo.snomed.browse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.Ontology;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

@Controller
@RequestMapping("/")
public class MainController {    
    private static final Logger LOG = LoggerFactory.getLogger( MainController.class );

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;
    
    private Map<Long, List<RelationshipStatement>> subjectOfCache = new HashMap<Long, List<RelationshipStatement>>();
    private Map<Long, List<RelationshipStatement>> objectOfCache = new HashMap<Long, List<RelationshipStatement>>();
    private Map<Long, List<RelationshipStatement>> predicateOfCache = new HashMap<Long, List<RelationshipStatement>>();

    
    private Ordering<RelationshipStatement> byGroupAndSubjectFsn = new Ordering<RelationshipStatement>() {
        @Override
        public int compare(RelationshipStatement r1, RelationshipStatement r2) {
            if (r1.getGroup() == r2.getGroup()){
                return r1.getSubject().getFullySpecifiedName().compareTo(r2.getSubject().getFullySpecifiedName());
            }
            else{
                return Ints.compare(r1.getGroup(), r2.getGroup());
            }
        }
    };
    
//    private Ordering<RelationshipStatement> byGroupAndObjectFsn = new Ordering<RelationshipStatement>() {
//        @Override
//        public int compare(RelationshipStatement r1, RelationshipStatement r2) {
//            if (r1.getGroup() == r2.getGroup()){
//                return r1.getObject().getFullySpecifiedName().compareTo(r2.getObject().getFullySpecifiedName());
//            }
//            else{
//                return Ints.compare(r1.getGroup(), r2.getGroup());
//            }
//        }
//    };   
    
    private Ordering<RelationshipStatement> byGroupAndPredicateFsn = new Ordering<RelationshipStatement>() {
        @Override
        public int compare(RelationshipStatement r1, RelationshipStatement r2) {
            if (r1.getGroup() == r2.getGroup()){
                return r1.getPredicate().getFullySpecifiedName().compareTo(r2.getPredicate().getFullySpecifiedName());
            }
            else{
                return Ints.compare(r1.getGroup(), r2.getGroup());
            }
        }
    };     

    @Transactional
    @RequestMapping(value="/", method = RequestMethod.GET)
    public void landingPage(HttpServletResponse response, HttpServletRequest request) throws IOException{
        response.sendRedirect("ontology/1/concept/138875005");
    }
    
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
        model.addAttribute("fullySpecifiedName", c.getFullySpecifiedName().toLowerCase().substring(0, 1).toUpperCase() + c.getFullySpecifiedName().toLowerCase().substring(1));
        model.addAttribute("type", ((c.getType() == null) || c.getType().isEmpty()) ? "Type not specified" : c.getType().toLowerCase().substring(0, 1).toUpperCase() + c.getType().toLowerCase().substring(1));
        
        if (subjectOfCache.get(c.getId()) == null){
            List<RelationshipStatement> subjectOf = new ArrayList<RelationshipStatement>();
            for (RelationshipStatement r : c.getSubjectOfRelationshipStatements()){
                if (!r.isKindOfRelationship()){
                    subjectOf.add(r);
                }
            }
            Collections.sort(subjectOf, byGroupAndPredicateFsn.nullsLast());
            subjectOfCache.put(c.getId(), subjectOf);
        }
        
        if (objectOfCache.get(c.getId()) == null){
            List<RelationshipStatement> objectOf = new ArrayList<RelationshipStatement>();
            for (RelationshipStatement r : c.getObjectOfRelationshipStatements()){
                if (!r.isKindOfRelationship()){
                    objectOf.add(r);
                }
            }
            Collections.sort(objectOf, byGroupAndSubjectFsn.nullsLast());
            objectOfCache.put(c.getId(), objectOf);
        }
        
        if (predicateOfCache.get(c.getId()) == null){
            List<RelationshipStatement> predicateOf = new ArrayList<RelationshipStatement>();
            for (RelationshipStatement r : c.getPredicateOfRelationshipStatements()){
                if (!r.isKindOfRelationship()){
                    predicateOf.add(r);
                }
            }
            Collections.sort(predicateOf, byGroupAndSubjectFsn.nullsLast());
            predicateOfCache.put(c.getId(), predicateOf);
        }        
        
        model.addAttribute("objectOf", objectOfCache.get(c.getId()));
        model.addAttribute("predicateOf", predicateOfCache.get(c.getId()));
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
