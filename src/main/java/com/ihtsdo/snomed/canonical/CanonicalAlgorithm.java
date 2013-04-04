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
                    getUnsharedDefiningCharacteristicsForConcept(concept, true));
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

        Set<Concept> proximalParentsOnly = new HashSet<Concept>(concept.getAllKindOfPrimitiveConcepts(useCache));
        
        if (LOG.isDebugEnabled()){
            StringBuffer debugStringBuffer = new StringBuffer("All primitive isA concepts for concept " + concept.getId() + " are {");
            for (Concept c : concept.getAllKindOfPrimitiveConcepts(useCache)){
                debugStringBuffer.append(c.getId()+ ", ");
            }
            if (!concept.getAllKindOfPrimitiveConcepts(useCache).isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.debug(debugStringBuffer.toString());
        }
        
        for (Concept parentUnderTest : concept.getAllKindOfPrimitiveConcepts(useCache)){
            if (LOG.isDebugEnabled()){
                if (parentUnderTest.getAllKindOfPrimitiveConcepts(useCache).isEmpty()){
                    LOG.debug("Concept " + parentUnderTest.getId() + " has no isA relationships, continuing");
                }
            }
            for (Concept parentOfParentUnderTest : parentUnderTest.getAllKindOfPrimitiveConcepts(useCache)){
                LOG.debug("Found that concept {} isA concept {}", parentUnderTest.getId(), parentOfParentUnderTest.getId());
                if (concept.getAllKindOfPrimitiveConcepts(useCache).contains(parentOfParentUnderTest)){
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
    
    protected Set<RelationshipStatement> getUnsharedDefiningCharacteristicsForConcept(Concept concept, boolean useCache){
        LOG.debug("Attempting to find all unshared defining characteristics for concept [{}]",  concept.getId());
        
        Set<RelationshipStatement> allStatementsForConcept = new HashSet<RelationshipStatement>(concept.getSubjectOfRelationShipStatements());
        
        if (LOG.isDebugEnabled()){
            StringBuffer debugStringBuffer = new StringBuffer("Relationships for concept [" + concept.getId() + "] are {");
            for (RelationshipStatement rs : concept.getSubjectOfRelationShipStatements()){
                debugStringBuffer.append(rs.shortToString() + ", ");
            }

            if (!concept.getSubjectOfRelationShipStatements().isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.debug(debugStringBuffer.toString());
        }
        
        for (RelationshipStatement rUnderTest : concept.getSubjectOfRelationShipStatements()){
            if ((rUnderTest.isKindOfRelationship()) || (!rUnderTest.isDefiningCharacteristic())){
                allStatementsForConcept.remove(rUnderTest);
                continue;
            }
            
            if (LOG.isDebugEnabled()){
                if (!rUnderTest.isDefiningCharacteristic()){
                    LOG.debug("Skipping non-defining characteristic relationship {}", rUnderTest);
                }
                else if (rUnderTest.isKindOfRelationship()){
                    LOG.debug("Skipping non-characteristic isA relationship {}", rUnderTest);
                }
                else{
                    LOG.debug("Testing for existence of relationship [{}] in parent primitive concepts", rUnderTest.getId());
                }
                if (concept.getAllKindOfPrimitiveConcepts(useCache).isEmpty()){
                    LOG.debug("Concept [" + concept.getId() + "] has no isA relationships, continuing");
                }
            }
            for (Concept parentConcept : concept.getAllKindOfPrimitiveConcepts(useCache)){
                LOG.debug("Concept [{}] has a primitive parent concept of [{}]", concept.getId(), parentConcept.getId());
                if (LOG.isDebugEnabled()){
                    if (parentConcept.getSubjectOfRelationShipStatements().isEmpty()){
                        LOG.debug("Concept [{}] is not the subject of any relationship statements. Continuing", parentConcept);
                    }
                }
                for (RelationshipStatement rParent : parentConcept.getSubjectOfRelationShipStatements()){
                    if (LOG.isDebugEnabled()) LOG.debug("Found that parent concept [{}] has relationship {}", parentConcept, rParent.shortToString());
                    if ((rUnderTest.getRelationshipType() == rParent.getRelationshipType())&& 
                            rUnderTest.getObject().equals(rParent.getObject())&& 
                            rParent.isDefiningCharacteristic())
                    {
                        if (LOG.isDebugEnabled()) LOG.debug("Found that relationship under test {} is also defined in parent concept as {}", rUnderTest.shortToString(), rParent.shortToString());
                        allStatementsForConcept.remove(rUnderTest);
                    }
                }
            }
            if (LOG.isDebugEnabled()){
                StringBuffer debugStringBuffer = new StringBuffer("Found that concept " + concept.getId() + " has defining characteristic relationships {");
                for (RelationshipStatement r : allStatementsForConcept){
                    debugStringBuffer.append(r.shortToString() + ", ");
                }
                if (!allStatementsForConcept.isEmpty()){
                    debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
                }
                debugStringBuffer.append("}");
                LOG.debug(debugStringBuffer.toString());
            }
        }
        
        return allStatementsForConcept;
    }
}
