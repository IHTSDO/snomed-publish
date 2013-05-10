package com.ihtsdo.snomed.service;

import java.io.IOException;
import java.util.Collection;

import javax.persistence.EntityManager;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Statement;

public class TransitiveClosureAlgorithm {
    //private static final Logger LOG = LoggerFactory.getLogger( TransitiveClosureAlgorithm.class );
    
    public void runAlgorithm(Collection<Concept> concepts, OntologySerialiser serialiser) throws IOException{
        runAlgorithm(concepts, serialiser, null);
    }
    
    public void runAlgorithm(Collection<Concept> concepts, OntologySerialiser serialiser, EntityManager em) throws IOException{
        Concept kindOfConcept = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        int counter = 0;
        for (Concept c : concepts){
            for (Concept p : c.getAllKindOfConcepts(true)){
                serialiser.write(new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c, kindOfConcept, p));
                counter++;
                if ((em != null) && (counter % 10000 == 0)){
                    em.getEntityManagerFactory().getCache().evictAll();
                }
            }
        }
    }
}
