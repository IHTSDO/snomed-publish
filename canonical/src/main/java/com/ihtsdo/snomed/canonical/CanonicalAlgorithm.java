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

    public Set<RelationshipStatement> runAlgorithm(Collection<Concept> concepts, boolean showDetails, Set<Long> showDetailsConceptIds) {
        Stopwatch stopwatch = new Stopwatch().start();
        LOG.info("Running algorithm");
    
        LOG.info("Calculating immidiate primitive concepts");
        Set<RelationshipStatement> allImmidiatePrimitiveConceptStatements = new HashSet<RelationshipStatement>();
        for (Concept concept : concepts){
            allImmidiatePrimitiveConceptStatements.addAll(
                    createProximalPrimitiveStatementsForConcept(concept, true, showDetails, showDetailsConceptIds));
        }
        LOG.info("Found [" + allImmidiatePrimitiveConceptStatements.size() + "] immidiate primitive concepts");
       
        
        LOG.info("Calculating unshared defining characteristics");
        Set<RelationshipStatement> allUndefinedCharacteristicsStatements = new HashSet<RelationshipStatement>();
        for (Concept concept : concepts){
            allUndefinedCharacteristicsStatements.addAll(
                    getUnsharedDefiningCharacteristicsForConcept(concept, true, showDetails, showDetailsConceptIds));
        }
        LOG.info("Found [" + allUndefinedCharacteristicsStatements.size() + "] unshared defining characteristics");
     
        Set<RelationshipStatement> returnSet = new HashSet<RelationshipStatement>();
        returnSet.addAll(allUndefinedCharacteristicsStatements);
        returnSet.addAll(allImmidiatePrimitiveConceptStatements);
        
        stopwatch.stop();
        LOG.info("Completed algorithm in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds with [" + returnSet.size() + "] statements");            
        return returnSet;
    }    
    
    protected Set<RelationshipStatement> createProximalPrimitiveStatementsForConcept(Concept concept, boolean useCache, boolean showDetails, Set<Long> showDetailsConceptIds){
        Set<RelationshipStatement> returnStatements = new HashSet<RelationshipStatement>();
        for (Concept proximalPrimitiveConcept : getProximalPrimitiveConcepts(concept, useCache, showDetails, showDetailsConceptIds)){
            returnStatements.add(new RelationshipStatement(getNewId(), concept, Concept.getKindOfPredicate(), proximalPrimitiveConcept));
        }
        return returnStatements;
    }    
    
    protected Set<Concept> getProximalPrimitiveConcepts(Concept concept, boolean useCache, boolean showDetails, Set<Long> showDetailsConceptIds){
        boolean shouldShowDetails = showDetails && ((showDetailsConceptIds == null) || (showDetailsConceptIds.contains(concept.getSerialisedId())));

        if (shouldShowDetails){
            LOG.info("Attempting to find all proximal primitive isKindOf concepts for concept {}",  concept.getSerialisedId());    
        }
        Set<Concept> proximalParentsOnly = new HashSet<Concept>(concept.getAllKindOfPrimitiveConcepts(useCache));
        
        if (shouldShowDetails){
            StringBuffer debugStringBuffer = new StringBuffer("All primitive super concepts for concept " + concept.getSerialisedId() + " are {");
            for (Concept c : concept.getAllKindOfPrimitiveConcepts(useCache)){
                debugStringBuffer.append(c.getSerialisedId()+ ", ");
            }
            if (!concept.getAllKindOfPrimitiveConcepts(useCache).isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.info(debugStringBuffer.toString());
        }
        
        for (Concept parentUnderTest : concept.getAllKindOfPrimitiveConcepts(useCache)){
            if (shouldShowDetails){
                if (parentUnderTest.getAllKindOfPrimitiveConcepts(useCache).isEmpty()){
                    LOG.info("Concept " + parentUnderTest.getSerialisedId() + " has no isA relationships, continuing");
                }
            }
            for (Concept parentOfParentUnderTest : parentUnderTest.getAllKindOfPrimitiveConcepts(useCache)){
                if (shouldShowDetails) LOG.info("Found that concept {} isA concept {}", parentUnderTest.getSerialisedId(), parentOfParentUnderTest.getSerialisedId());
                if (concept.getAllKindOfPrimitiveConcepts(useCache).contains(parentOfParentUnderTest)){
                    if (shouldShowDetails) LOG.info("Since concept {} isA concept {}, concept {} is not an proximal isA for concept {}", parentUnderTest.getSerialisedId(), parentOfParentUnderTest.getSerialisedId(), parentOfParentUnderTest.getSerialisedId(), concept.getSerialisedId());
                    proximalParentsOnly.remove(parentOfParentUnderTest);
                }
            }
        }
        if (shouldShowDetails){
            StringBuffer debugStringBuffer = new StringBuffer("Found that concept " + concept.getSerialisedId() + " has proximal primitive isA concept(s) of {");
            for (Concept c : proximalParentsOnly){
                debugStringBuffer.append(c.getSerialisedId() + ", ");
            }
            if (!proximalParentsOnly.isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.info(debugStringBuffer.toString());
        }

        return proximalParentsOnly;
    }
    
    protected Set<RelationshipStatement> getUnsharedDefiningCharacteristicsForConcept(Concept concept, boolean useCache, boolean showDetails, Set<Long> showDetailsConceptIds){
        boolean shouldShowDetails = showDetails && ((showDetailsConceptIds == null) || (showDetailsConceptIds.contains(concept.getSerialisedId())));
        if(shouldShowDetails) LOG.info("Attempting to find all unshared defining characteristics for concept [{}]",  concept.getSerialisedId());
        
        Set<RelationshipStatement> allStatementsForConcept = new HashSet<RelationshipStatement>(concept.getSubjectOfRelationshipStatements());
        
        if (shouldShowDetails){
            StringBuffer debugStringBuffer = new StringBuffer("Relationships for concept [" + concept.getSerialisedId() + "] are {");
            for (RelationshipStatement rs : concept.getSubjectOfRelationshipStatements()){
                debugStringBuffer.append(rs.shortToString() + ", ");
            }

            if (!concept.getSubjectOfRelationshipStatements().isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.info(debugStringBuffer.toString());
        }
        
        for (RelationshipStatement rUnderTest : concept.getSubjectOfRelationshipStatements()){
            if ((rUnderTest.isKindOfRelationship()) || (!rUnderTest.isDefiningCharacteristic())){
                allStatementsForConcept.remove(rUnderTest);
                continue;
            }
            
            if (shouldShowDetails){
                if (!rUnderTest.isDefiningCharacteristic()){
                    LOG.info("Skipping non-defining characteristic relationship {}", rUnderTest);
                }
                else if (rUnderTest.isKindOfRelationship()){
                    LOG.info("Skipping non-characteristic isA relationship {}", rUnderTest);
                }
                else{
                    LOG.info("Testing for existence of relationship [{}] in parent primitive concepts", rUnderTest.getSerialisedId());
                }
                if (concept.getAllKindOfPrimitiveConcepts(useCache).isEmpty()){
                    LOG.info("Concept [" + concept.getSerialisedId() + "] has no isA relationships, continuing");
                }
            }
            for (Concept parentConcept : concept.getAllKindOfPrimitiveConcepts(useCache)){
                if (shouldShowDetails){
                    LOG.info("Concept [{}] has a primitive parent concept of [{}]", concept.getSerialisedId(), parentConcept.getSerialisedId());
                    if (parentConcept.getSubjectOfRelationshipStatements().isEmpty()){
                        LOG.info("Concept [{}] is not the subject of any relationship statements. Continuing", parentConcept.getSerialisedId());
                    }
                }
                for (RelationshipStatement rParent : parentConcept.getSubjectOfRelationshipStatements()){
                    if (shouldShowDetails) LOG.info("Found that parent concept [{}] has relationship {}", parentConcept.getSerialisedId(), rParent.shortToString());
                    if ((rUnderTest.getPredicate() == rParent.getPredicate())&& 
                            rUnderTest.getObject().equals(rParent.getObject())&& 
                            rParent.isDefiningCharacteristic())
                    {
                        if (shouldShowDetails) {
                            LOG.info("Found that relationship under test {} is also defined in parent concept as {}", rUnderTest.shortToString(), rParent.shortToString());
                            LOG.info("Removing statement [{}] from output because parent concept [{}] has defined relationship [{}]",  rUnderTest.shortToString(), parentConcept.getSerialisedId(), rParent.shortToString());
                        }
                        allStatementsForConcept.remove(rUnderTest);
                    }
                }
            }
            if (shouldShowDetails){
                StringBuffer debugStringBuffer = new StringBuffer("Found that concept " + concept.getSerialisedId() + " has defining characteristic relationships {");
                for (RelationshipStatement r : allStatementsForConcept){
                    debugStringBuffer.append(r.shortToString() + ", ");
                }
                if (!allStatementsForConcept.isEmpty()){
                    debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
                }
                debugStringBuffer.append("}");
                LOG.info(debugStringBuffer.toString());
            }
        }
        
        return allStatementsForConcept;
    }
}
