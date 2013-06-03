package com.ihtsdo.snomed.browse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Ordering;
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
    private static final int INDEX_NOT_SPECIFIED = -1;
    private static final String INDEX_NOT_SPECIFIED_STRING = "-1";
    private static final int MAX_STATEMENT_RESULTS = 1000;

    
    private static final Logger LOG = LoggerFactory.getLogger( MainController.class );

    @Autowired OntologyService ontologyService;
    @Autowired ConceptService conceptService;

    @PersistenceContext
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
        
        builder = em.getCriteriaBuilder();
        cid =  builder.parameter(Long.class);
        
        valueOfQuery = builder.createQuery(Statement.class);
        Root<Statement> valueOfRoot = valueOfQuery.from(Statement.class);            
        valueOfQuery.select(valueOfRoot).where(builder.equal(valueOfRoot.get("object").get("id"), cid));
        valueOfQuery.orderBy(
                builder.asc(valueOfRoot.get("groupId")), 
                builder.desc(valueOfRoot.get("active")), 
                builder.asc(valueOfRoot.get("object").get("id")));

        objectOfQuery = builder.createQuery(Statement.class);
        Root<Statement> objectOfRoot = objectOfQuery.from(Statement.class);            
        objectOfQuery.select(objectOfRoot).where(builder.equal(objectOfRoot.get("subject").get("id"), cid));
        objectOfQuery.orderBy(
                builder.asc(objectOfRoot.get("groupId")), 
                builder.desc(objectOfRoot.get("active")), 
                builder.asc(objectOfRoot.get("object").get("id")));
        
        attributeOfQuery = builder.createQuery(Statement.class);
        Root<Statement> attributeOfRoot = attributeOfQuery.from(Statement.class);            
        attributeOfQuery.select(attributeOfRoot).where(builder.equal(attributeOfRoot.get("predicate").get("id"), cid));
        attributeOfQuery.orderBy(
                builder.asc(attributeOfRoot.get("groupId")), 
                builder.desc(attributeOfRoot.get("active")), 
                builder.asc(attributeOfRoot.get("object").get("id")));        
        
        attributeOfCountQuery = builder.createQuery(Long.class);
        attributeOfCountQuery.select(builder.count(attributeOfCountQuery.from(Statement.class)))
            .where(builder.equal(valueOfRoot.get("predicate").get("id"), cid));
        
        objectOfCountQuery = builder.createQuery(Long.class);
        objectOfCountQuery.select(builder.count(objectOfCountQuery.from(Statement.class)))
            .where(builder.equal(valueOfRoot.get("subject").get("id"), cid));

        valueOfCountQuery = builder.createQuery(Long.class);
        valueOfCountQuery.select(builder.count(valueOfCountQuery.from(Statement.class)))
            .where(builder.equal(valueOfRoot.get("object").get("id"), cid));
    }
    
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
    
    @Transactional
    @RequestMapping(value="/ontology/{ontologyId}/concept/{serialisedId}", method = RequestMethod.GET)
    public ModelAndView conceptDetails(
            ModelMap model,
            HttpServletRequest request,
            @PathVariable long ontologyId, 
            @PathVariable long serialisedId, 
            @RequestParam(value="objectOfStartIndex", defaultValue=INDEX_NOT_SPECIFIED_STRING, required=false) int objectOfStartIndex,
            @RequestParam(value="objectOfCount", defaultValue=INDEX_NOT_SPECIFIED_STRING, required=false) int objectOfCount,
            @RequestParam(value="valueOfStartIndex", defaultValue=INDEX_NOT_SPECIFIED_STRING, required=false) int valueOfStartIndex,
            @RequestParam(value="valueOfCount", defaultValue=INDEX_NOT_SPECIFIED_STRING, required=false) int valueOfCount,
            @RequestParam(value="attributeOfStartIndex", defaultValue=INDEX_NOT_SPECIFIED_STRING, required=false) int attributeOfStartIndex,
            @RequestParam(value="attributeOfCount", defaultValue=INDEX_NOT_SPECIFIED_STRING, required=false) int attributeOfCount
            
            ) throws ConceptNotFoundException
    {            
        Ontology o = em.createQuery("SELECT o FROM Ontology o WHERE o.id=:oid", Ontology.class)
                .setParameter("oid", ontologyId)
                .getSingleResult();
        
        Concept c = null;
        try {
            TypedQuery<Concept> getConceptQuery = em.createQuery("SELECT c FROM Concept c " +
                    "LEFT JOIN FETCH c.description " +
                    "WHERE c.serialisedId=:serialisedId AND c.ontology.id=:ontologyId", 
                    Concept.class);
            getConceptQuery.setParameter("serialisedId", serialisedId);
            getConceptQuery.setParameter("ontologyId", ontologyId);
            c = getConceptQuery.getSingleResult();
            c.getAllKindOfConcepts(true); //build the cache
        } catch (NoResultException e) {
            throw new ConceptNotFoundException(serialisedId, ontologyId);
        }
        
        String disambiguationType = null;
        String parsedDisplayName = null;
        String displayName = c.getDisplayName();
        if (displayName.lastIndexOf(')') == -1){
            parsedDisplayName = displayName;
        }else{
            parsedDisplayName = displayName.substring(0, displayName.lastIndexOf('(') - 1);
            disambiguationType = displayName.trim().substring(displayName.trim().lastIndexOf('(') + 1, displayName.trim().length() - 1);
        } 

        //VALUE OF
        {
            int startIndex = (valueOfStartIndex == INDEX_NOT_SPECIFIED) ? 0 : valueOfStartIndex;
            int count = ((valueOfCount == INDEX_NOT_SPECIFIED) || (valueOfCount > MAX_STATEMENT_RESULTS)) ? MAX_STATEMENT_RESULTS : valueOfCount;

            List<Statement> statements = em.createQuery(valueOfQuery)
                    .setParameter(cid, c.getId())
                    .setMaxResults(count)
                    .setFirstResult(startIndex)
                    .getResultList();
            
            long total = em.createQuery(valueOfCountQuery)
                    .setParameter(cid, c.getId())
                    .getSingleResult();
            
            model.addAttribute("valueOf", statements);
            model.addAttribute("valueOfStartIndex", startIndex);
            model.addAttribute("valueOfCount", count);
            model.addAttribute("valueOfTotal", total);
            
            if (valueOfCount < total){
                String valueOfOthersAndCount = "?";
                if (objectOfStartIndex != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "objectOfStartIndex=" + objectOfStartIndex + "&";
                }
                if (objectOfCount != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "objectOfCount=" + objectOfCount + "&";
                }
                if (attributeOfStartIndex != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "attributeOfStartIndex=" + attributeOfStartIndex + "&";
                }
                if (attributeOfCount != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "attributeOfCount=" + attributeOfCount + "&";
                }
                if (count != MAX_STATEMENT_RESULTS){
                    valueOfOthersAndCount += "valueOfCount=" + count + "&";
                }
                String valueOfPrevious = valueOfOthersAndCount;
                String valueOfFirst = valueOfOthersAndCount;
                String valueOfNext = valueOfOthersAndCount;
                String valueOfLast = valueOfOthersAndCount;
                
                int last = (int) (Math.floor(total/count) * count);
                last = (last == total) ? last -1 : last;
                
                if (startIndex - count > 0){
                    valueOfPrevious += "valueOfStartIndex=" + (startIndex - count) + "&";
                }else{
                    valueOfPrevious += "valueOfStartIndex=" + last + "&";
                }
                if (startIndex + count < total){
                    valueOfNext += "valueOfStartIndex=" + (startIndex + count) + "&";
                }

                valueOfLast += "valueOfStartIndex=" + last + "&";
                
                model.addAttribute("valueOfPreviousParams", valueOfPrevious.substring(0, valueOfPrevious.length() - 1));
                model.addAttribute("valueOfNextParams", valueOfNext.substring(0, valueOfNext.length() - 1));
                model.addAttribute("valueOfFirstParams", valueOfFirst.substring(0, valueOfFirst.length() - 1));
                model.addAttribute("valueOfLastParams", valueOfLast.substring(0, valueOfLast.length() - 1));
            }
        }
        
        //OBJECT OF
        {
            int startIndex = (objectOfStartIndex == INDEX_NOT_SPECIFIED) ? 0 : objectOfStartIndex;
            int count = ((objectOfCount == INDEX_NOT_SPECIFIED) || (objectOfCount > MAX_STATEMENT_RESULTS)) ? MAX_STATEMENT_RESULTS : objectOfCount;

            List<Statement> statements = em.createQuery(objectOfQuery)
                    .setParameter(cid, c.getId())
                    .setMaxResults(count)
                    .setFirstResult(startIndex)
                    .getResultList();
            
            long total = em.createQuery(objectOfCountQuery)
                    .setParameter(cid, c.getId())
                    .getSingleResult();
            
            model.addAttribute("objectOf", statements);
            model.addAttribute("objectOfStartIndex", startIndex);
            model.addAttribute("objectOfCount", count);
            model.addAttribute("objectOfTotal", total);
            
            if (startIndex - count > 0){
                model.addAttribute("objectOfPreviousParam", "objectOfstartIndex=" + (startIndex - count) + "&"); 
            }
            if (startIndex + count < total){
                model.addAttribute("objectOfNextParam", "objectOfStartIndex=" + (startIndex + count) + "&");
            }
            if (count != MAX_STATEMENT_RESULTS){
                model.addAttribute("objectOfCountForParam", "objectOfCount=" + count + "&");
            }
            if (objectOfCount < total){
                String valueOfOthersAndCount = "?";
                if (valueOfStartIndex != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "valueOfStartIndex=" + valueOfStartIndex + "&";
                }
                if (valueOfCount != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "valueOfCount=" + valueOfCount + "&";
                }
                if (attributeOfStartIndex != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "attributeOfStartIndex=" + attributeOfStartIndex + "&";
                }
                if (attributeOfCount != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "attributeOfCount=" + attributeOfCount + "&";
                }
                if (count != MAX_STATEMENT_RESULTS){
                    valueOfOthersAndCount += "objectOfCount=" + count + "&";
                }
                String objectOfPrevious = valueOfOthersAndCount;
                String objectOfFirst = valueOfOthersAndCount;
                String objectOfNext = valueOfOthersAndCount;
                String objectOfLast = valueOfOthersAndCount;
                
                int last = (int) (Math.floor(total/count) * count);
                last = (last == total) ? last -1 : last;                
                
                if (startIndex - count > 0){
                    objectOfPrevious += "objectOfStartIndex=" + (startIndex - count) + "&";
                }
                else{
                    objectOfPrevious += "objectOfStartIndex=" + last + "&";
                }                
                if (startIndex + count < total){
                    objectOfNext += "objectOfStartIndex=" + (startIndex + count) + "&";
                }
                
                objectOfLast += "objectOfStartIndex=" + last + "&";
                
                model.addAttribute("objectOfPreviousParams", objectOfPrevious.substring(0, objectOfPrevious.length() - 1));
                model.addAttribute("objectOfNextParams", objectOfNext.substring(0, objectOfNext.length() - 1));
                model.addAttribute("objectOfFirstParams", objectOfFirst.substring(0, objectOfFirst.length() - 1));
                model.addAttribute("objectOfLastParams", objectOfLast.substring(0, objectOfLast.length() - 1));
            }            
        }        

        //ATTRIBUTE OF
        {
            int startIndex = (attributeOfStartIndex == INDEX_NOT_SPECIFIED) ? 0 : attributeOfStartIndex;
            int count = ((attributeOfCount == INDEX_NOT_SPECIFIED) || (attributeOfCount > MAX_STATEMENT_RESULTS)) ? MAX_STATEMENT_RESULTS : attributeOfCount;

            List<Statement> statements = em.createQuery(attributeOfQuery)
                    .setParameter(cid, c.getId())
                    .setMaxResults(count)
                    .setFirstResult(startIndex)
                    .getResultList();
            
            long total = em.createQuery(attributeOfCountQuery)
                    .setParameter(cid, c.getId())
                    .getSingleResult();            
            
            model.addAttribute("attributeOf", statements);
            model.addAttribute("attributeOfStartIndex", startIndex);
            model.addAttribute("attributeOfCount", count);
            model.addAttribute("attributeOfTotal", total);
            
            if (startIndex - count > 0){
                model.addAttribute("attributeOfPreviousParam", "attributeOfstartIndex=" + (startIndex - count) + "&");
            }
            if (startIndex + count < total){
                model.addAttribute("attributeOfNextParam", "attributeOfStartIndex=" + (startIndex + count) + "&");
            }
            if (count != MAX_STATEMENT_RESULTS){
                model.addAttribute("attributeOfCountForParam", "attributeOfCount=" + count + "&");
            }
            
            if (attributeOfCount < total){
                String valueOfOthersAndCount = "?";
                if (valueOfStartIndex != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "valueOfStartIndex=" + valueOfStartIndex + "&";
                }
                if (valueOfCount != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "valueOfCount=" + valueOfCount + "&";
                }
                if (objectOfStartIndex != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "objectOfStartIndex=" + objectOfStartIndex + "&";
                }
                if (objectOfCount != INDEX_NOT_SPECIFIED){
                    valueOfOthersAndCount += "objectOfCount=" + objectOfCount + "&";
                }
                if (count != MAX_STATEMENT_RESULTS){
                    valueOfOthersAndCount += "attributeOfCount=" + count + "&";
                }
                String attributeOfPrevious = valueOfOthersAndCount;
                String attributeOfFirst = valueOfOthersAndCount;
                String attributeOfNext = valueOfOthersAndCount;
                String attributeOfLast = valueOfOthersAndCount;
                
                int last = (int) (Math.floor(total/count) * count);
                last = (last == total) ? last -1 : last;
                
                if (startIndex - count > 0){
                    attributeOfPrevious += "attributeOfStartIndex=" + (startIndex - count) + "&";
                }
                else{
                    attributeOfPrevious += "attributeOfStartIndex=" + last + "&";
                }                
                if (startIndex + count < total){
                    attributeOfNext += "attributeOfStartIndex=" + (startIndex + count) + "&";
                }
                
                attributeOfLast += "attributeOfStartIndex=" + last + "&";
                
                model.addAttribute("attributeOfPreviousParams", attributeOfPrevious.substring(0, attributeOfPrevious.length() - 1));
                model.addAttribute("attributeOfNextParams", attributeOfNext.substring(0, attributeOfNext.length() - 1));
                model.addAttribute("attributeOfFirstParams", attributeOfFirst.substring(0, attributeOfFirst.length() - 1));
                model.addAttribute("attributeOfLastParams", attributeOfLast.substring(0, attributeOfLast.length() - 1));
            }              
        }
        
        List<Description> descriptions = new ArrayList<>(c.getDescription());
        List<Concept> kindOfs = new ArrayList<>(c.getKindOfs());
        List<Concept> parentOf = new ArrayList<>(c.getParentOf());
        List<Concept> allPrimitiveSupertypes = new ArrayList<>(c.getAllKindOfPrimitiveConcepts(true));
        
        Collections.sort(descriptions, byTypeActiveAndTerm.nullsLast());
        Collections.sort(kindOfs, byActiveAndName.nullsLast());
        Collections.sort(parentOf, byActiveAndName.nullsLast());
        Collections.sort(allPrimitiveSupertypes, byActiveAndName.nullsLast());        
       
        
        model.addAttribute("kindOfs", kindOfs);
        model.addAttribute("parentOf", parentOf);
        model.addAttribute("allPrimitiveSupertypes", allPrimitiveSupertypes);
        model.addAttribute("descriptions", descriptions);
        model.addAttribute("concept", c);
        model.addAttribute("servletPath", request.getServletPath());
        model.addAttribute("ontologies", ontologyService.getAll());
        model.addAttribute("ontologyId", ontologyId);
        model.addAttribute("displayName", parsedDisplayName.toLowerCase().substring(0, 1).toUpperCase() + parsedDisplayName.toLowerCase().substring(1));
        model.addAttribute("type", ((disambiguationType == null)) ? null : disambiguationType.toLowerCase().substring(0, 1).toUpperCase() + disambiguationType.toLowerCase().substring(1));

        if (LOG.isDebugEnabled()){
            LOG.debug("descriptions: ", c.getDescription());
            LOG.debug("Concept: {}", c);
            LOG.debug("kindOf: {}", c.getKindOfs().size());
        }
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
    
    @RequestMapping(value="/ontology/{ontologyId}/triple/{serialisedId}", method = RequestMethod.GET)
    public ModelAndView tripleDetails(@PathVariable long ontologyId, @PathVariable long serialisedId, ModelMap model,
            HttpServletRequest request) throws DescriptionNotFoundException
    {            
        Ontology o = em.createQuery("SELECT o FROM Ontology o WHERE o.id=:oid", Ontology.class)
                .setParameter("oid", ontologyId)
                .getSingleResult();
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
    
    public static class StatementNotFoundException extends Exception{
        private static final long serialVersionUID = 1L;
        private long statementId;
        private long ontologyId;
        
        public StatementNotFoundException(long statementId, long ontologyId){
            this.statementId = statementId;
            this.ontologyId = ontologyId;
        }

        public long getDescriptionId() {
            return statementId;
        }

        public long getOntologyId() {
            return ontologyId;
        }
    }     
    
    private Ordering<Concept> byActiveAndName = new Ordering<Concept>() {
        @Override
        public int compare(Concept c1, Concept c2){
            if ((c1.isActive() && c2.isActive()) || !c1.isActive() && !c2.isActive()){
                return c1.getDisplayName().compareTo(c2.getDisplayName());   
            }
            else if (c1.isActive()){
                return -1;
            }
            else{
                return 1;
            }
        }
    };    
   
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
    
    
//    private Ordering<Statement> byGroupActiveAndSubjectFsn = new Ordering<Statement>() {
//        @Override
//        public int compare(Statement r1, Statement r2) {
//            if (r1.getGroupId() == r2.getGroupId()){
//                if ((r1.isActive() && r2.isActive()) || (!r1.isActive() && !r2.isActive())){
//                    return r1.getSubject().getDisplayName().compareTo(r2.getSubject().getDisplayName());
//                }else if (r1.isActive()){
//                    return -1;
//                }else{
//                    return 1;
//                }
//            }
//            else{
//                return Ints.compare(r1.getGroupId(), r2.getGroupId());
//            }
//        }
//    };
//    
//    private Ordering<Statement> byGroupActiveAndPredicateFsn = new Ordering<Statement>() {
//        @Override
//        public int compare(Statement r1, Statement r2) {
//            if (r1.getGroupId() == r2.getGroupId()){
//                if ((r1.isActive() && r2.isActive()) || (!r1.isActive() && !r2.isActive())){
//                    return r1.getPredicate().getDisplayName().compareTo(r2.getPredicate().getDisplayName());
//                }else if (r1.isActive()){
//                    return -1;
//                }else{
//                    return 1;
//                }
//            }
//            else{
//                return Ints.compare(r1.getGroupId(), r2.getGroupId());
//            }
//        }
//    };          
    
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
