package com.ihtsdo.snomed.browse;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.browse.exception.ConceptNotFoundException;
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
//                    "LEFT JOIN FETCH c.subjectOfStatements " +
//                    "LEFT JOIN FETCH c.predicateOfStatements " + 
//                    "LEFT JOIN FETCH c.objectOfStatements " +
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

    }  
    

  

}
