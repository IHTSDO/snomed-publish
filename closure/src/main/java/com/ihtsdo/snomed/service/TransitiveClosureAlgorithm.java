package com.ihtsdo.snomed.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Statement;

public class TransitiveClosureAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( TransitiveClosureAlgorithm.class );
    
    public Set<Statement> runAlgorithm(Collection<Concept> concepts) {
        Stopwatch stopwatch = new Stopwatch().start();
        LOG.info("Running algorithm");
        Concept kindOfConcept = new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
        Set<Statement> transitiveClosureSet = new HashSet<Statement>(concepts.size()*6); //a guess at size, average of 6 parents per concept
        for (Concept c : concepts){
            for (Concept p : c.getAllKindOfConcepts(true)){
                transitiveClosureSet.add(new Statement(Statement.SERIALISED_ID_NOT_DEFINED, c, kindOfConcept, p));
            }
        }
        stopwatch.stop();
        LOG.info("Completed algorithm in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds with [" + transitiveClosureSet.size() + "] statements");                    
        return transitiveClosureSet;
    }
}
