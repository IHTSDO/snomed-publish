package com.ihtsdo.snomed.browse;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Statement;

@Service
public class ConceptService {
    
    @PersistenceContext
    EntityManager em;
    
    //TypedQuery<Concept> getConceptQuery;
    
    @PostConstruct
    public void init(){
//        getConceptQuery = em.createQuery("SELECT c FROM Concept c " +
//                "LEFT JOIN FETCH c.subjectOfStatements " +
//                "LEFT JOIN FETCH c.predicateOfStatements " + 
//                "LEFT JOIN FETCH c.kindOfs " + 
//                "LEFT JOIN FETCH c.objectOfStatements " + 
//                "LEFT JOIN FETCH c.parentOf " +
//                "LEFT JOIN FETCH c.description " +
//                "WHERE c.serialisedId=:serialisedId AND c.ontology.id=:ontologyId", 
//                Concept.class);
    }
    
    @Transactional
    public Concept getConcept(long serialisedId, long ontologyId) throws ConceptNotFoundException{
        try {
            TypedQuery<Concept> getConceptQuery = em.createQuery("SELECT c FROM Concept c " +
                    "LEFT JOIN FETCH c.subjectOfStatements " +
                    "LEFT JOIN FETCH c.predicateOfStatements " + 
                    "LEFT JOIN FETCH c.objectOfStatements " +
                    //"LEFT JOIN FETCH c.kindOfs " + 
                    //"LEFT JOIN FETCH c.parentOf " +
                    "LEFT JOIN FETCH c.description " +
                    "WHERE c.serialisedId=:serialisedId AND c.ontology.id=:ontologyId", 
                    Concept.class);
            getConceptQuery.setParameter("serialisedId", serialisedId);
            getConceptQuery.setParameter("ontologyId", ontologyId);
            Concept c = getConceptQuery.getSingleResult();
            c.getAllKindOfConcepts(true); //build the cache
            return c;
        } catch (NoResultException e) {
            throw new ConceptNotFoundException(serialisedId, ontologyId);
        }
    }
    
    @Transactional
    public void populateStatementsForView(Concept c, List<Statement> objectOf,
            List<Statement> predicateOf, List<Statement> subjectOf) 
    {
//        for (Statement r : c.getObjectOfStatements()){
//            if (!r.isKindOfStatement()){
//                objectOf.add(r);
//            }
//        }
        objectOf.addAll(c.getObjectOfStatements());
        Collections.sort(objectOf, byGroupActiveAndSubjectFsn.nullsLast());
//        for (Statement r : c.getSubjectOfStatements()){
//            if (!r.isKindOfStatement()){
//                subjectOf.add(r);
//            }
//        }
        subjectOf.addAll(c.getSubjectOfStatements());
        Collections.sort(subjectOf, byGroupActiveAndPredicateFsn.nullsLast());
//        for (Statement r : c.getPredicateOfStatements()){
//            if (!r.isKindOfStatement()){
//                predicateOf.add(r);
//            }
//        }
        predicateOf.addAll(c.getPredicateOfStatements());
        Collections.sort(predicateOf, byGroupActiveAndSubjectFsn.nullsLast());
    }  
    

    
    private Ordering<Statement> byGroupActiveAndSubjectFsn = new Ordering<Statement>() {
        @Override
        public int compare(Statement r1, Statement r2) {
            if (r1.getGroupId() == r2.getGroupId()){
                if ((r1.isActive() && r2.isActive()) || (!r1.isActive() && !r2.isActive())){
                    return r1.getSubject().getDisplayName().compareTo(r2.getSubject().getDisplayName());
                }else if (r1.isActive()){
                    return -1;
                }else{
                    return 1;
                }
            }
            else{
                return Ints.compare(r1.getGroupId(), r2.getGroupId());
            }
        }
    };
    
    private Ordering<Statement> byGroupActiveAndPredicateFsn = new Ordering<Statement>() {
        @Override
        public int compare(Statement r1, Statement r2) {
            if (r1.getGroupId() == r2.getGroupId()){
                if ((r1.isActive() && r2.isActive()) || (!r1.isActive() && !r2.isActive())){
                    return r1.getPredicate().getDisplayName().compareTo(r2.getPredicate().getDisplayName());
                }else if (r1.isActive()){
                    return -1;
                }else{
                    return 1;
                }
            }
            else{
                return Ints.compare(r1.getGroupId(), r2.getGroupId());
            }
        }
    };       
    
    public static class ConceptNotFoundException extends Exception{
        private static final long serialVersionUID = 1L;
        private long conceptId;
        private long ontologyId;
        
        public ConceptNotFoundException(long conceptId, long ontologyId){
            this.conceptId = conceptId;
            this.ontologyId = ontologyId;
        }

        public long getConceptId() {
            return conceptId;
        }

        public long getOntologyId() {
            return ontologyId;
        }
    }
}
