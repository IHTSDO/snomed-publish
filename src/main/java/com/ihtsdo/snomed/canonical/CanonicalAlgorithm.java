package com.ihtsdo.snomed.canonical;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class CanonicalAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( CanonicalAlgorithm.class );
    
    protected static final long RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS = 100000000000000l; 
    
    private long idCounter = RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS;

    private long getNewId(){
        return idCounter++;
    }

    Set<RelationshipStatement> runAlgorithm(Collection<Concept> concepts) {
        Stopwatch stopwatch = new Stopwatch().start();
        LOG.info("Running algorithm");
    
        LOG.info("Calculating immidiate primitive concepts");
        Set<RelationshipStatement> allImmidiatePrimitiveConceptStatements = new HashSet<RelationshipStatement>();
        for (Concept concept : concepts){
            allImmidiatePrimitiveConceptStatements.addAll(
                    createProximalPrimitiveStatementsForConcept(concept, true));
        }
        LOG.info("Found [" + allImmidiatePrimitiveConceptStatements.size() + "] immidiate primitive concepts");
       
        
        LOG.info("Calculating unshared defining characteristics");
        Set<RelationshipStatement> allUndefinedCharacteristicsStatements = new HashSet<RelationshipStatement>();
        for (Concept concept : concepts){
            allUndefinedCharacteristicsStatements.addAll(
                    getAllUnsharedDefiningCharacteristics(concept, true));
        }
        LOG.info("Found [" + allUndefinedCharacteristicsStatements.size() + "] unshared defining characteristics");
     
        Set<RelationshipStatement> returnSet = new HashSet<RelationshipStatement>();
        returnSet.addAll(allUndefinedCharacteristicsStatements);
        returnSet.addAll(allImmidiatePrimitiveConceptStatements);
        
        stopwatch.stop();
        LOG.info("Completed algorithm in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds with [" + returnSet.size() + "] statements");            
        return returnSet;
    }    
    
    protected Set<Concept> getProximalPrimitiveConcepts(Concept concept, boolean useCache){
        LOG.debug("Attempting to find all proximal primitive isKindOf concepts for concept {}",  concept.getId());

        Set<Concept> proximalParentsOnly = new HashSet<Concept>(concept.getKindOfProximalConcepts(useCache));
        
        if (LOG.isDebugEnabled()){
            StringBuffer debugStringBuffer = new StringBuffer("All primitive isA concepts for concept " + concept.getId() + " are {");
            for (Concept c : concept.getKindOfProximalConcepts(useCache)){
                debugStringBuffer.append(c.getId()+ ", ");
            }
            if (!concept.getKindOfProximalConcepts(useCache).isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.debug(debugStringBuffer.toString());
        }
        
        for (Concept parentUnderTest : concept.getKindOfProximalConcepts(useCache)){
            if (LOG.isDebugEnabled()){
                if (parentUnderTest.getKindOfProximalConcepts(useCache).isEmpty()){
                    LOG.debug("Concept " + parentUnderTest.getId() + " has no isA relationships, continuing");
                }
            }
            for (Concept parentOfParentUnderTest : parentUnderTest.getKindOfProximalConcepts(useCache)){
                LOG.debug("Found that concept {} isA concept {}", parentUnderTest.getId(), parentOfParentUnderTest.getId());
                if (concept.getKindOfProximalConcepts(useCache).contains(parentOfParentUnderTest)){
                    LOG.debug("Since concept {} isA concept {}, concept {} is not an proximal isA for concept {}", parentUnderTest.getId(), parentOfParentUnderTest.getId(), parentOfParentUnderTest.getId(), concept.getId());
                    proximalParentsOnly.remove(parentOfParentUnderTest);
                }
            }
        }
        if (LOG.isDebugEnabled()){
            StringBuffer debugStringBuffer = new StringBuffer("Found that concept " + concept.getId() + " has proximal primitive isA concept(s) of {");
            for (Concept c : proximalParentsOnly){
                debugStringBuffer.append(c.getId() + ", ");
            }
            if (!proximalParentsOnly.isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.debug(debugStringBuffer.toString());
        }

        return proximalParentsOnly;
    }
    
    protected Set<RelationshipStatement> createProximalPrimitiveStatementsForConcept(Concept concept, boolean useCache){
        Set<RelationshipStatement> returnStatements = new HashSet<RelationshipStatement>();
        for (Concept proximalPrimitiveConcept : getProximalPrimitiveConcepts(concept, useCache)){
            RelationshipStatement r = new RelationshipStatement();
            r.setId(getNewId());
            r.setSubject(concept);
            r.setObject(proximalPrimitiveConcept);
            r.setRelationshipType(RelationshipStatement.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
            r.setRelationShipGroup(0);
            returnStatements.add(r);
        }
        return returnStatements;
    }
    
    protected Set<RelationshipStatement> getAllUnsharedDefiningCharacteristics(Concept concept, boolean useCache){
        Set<RelationshipStatement> unsharedRelationshipStatementsOnly = new HashSet<RelationshipStatement>(concept.getSubjectOfRelationShipStatements());
        for (RelationshipStatement rUnderTest : concept.getSubjectOfRelationShipStatements()){
            if ((rUnderTest.isKindOfRelationship()) || (!rUnderTest.isDefiningCharacteristic())){
                unsharedRelationshipStatementsOnly.remove(rUnderTest);
                continue;
            }
            for (Concept kindOfprimitiveConcept : concept.getKindOfProximalConcepts(useCache)){
                for (RelationshipStatement r : kindOfprimitiveConcept.getSubjectOfRelationShipStatements()){
                    if ((rUnderTest.getRelationshipType() == r.getRelationshipType())
                            && rUnderTest.getObject().equals(r.getObject()))
                    {
                        unsharedRelationshipStatementsOnly.remove(rUnderTest);
                    }
                }
            }
        }
        return unsharedRelationshipStatementsOnly;
    }
}
