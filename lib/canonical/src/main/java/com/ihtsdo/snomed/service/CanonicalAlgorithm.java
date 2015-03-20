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

public class CanonicalAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( CanonicalAlgorithm.class );
    
    protected static final long RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS = 100000000000000l; 
    
    private long idCounter = RELATIONSHIP_IDS_ARE_NEVER_LARGER_THAN_THIS;

    protected long getNewId(){
        return idCounter++;
    }
    
    public Set<Statement> runAlgorithm(Collection<Concept> concepts, boolean showDetails) {
    	return runAlgorithm(concepts, showDetails, null);
    	
    }
    
    public Set<Statement> runAlgorithm(Collection<Concept> concepts, boolean showDetails, Set<Long> showDetailsConceptIds) {
        Stopwatch stopwatch = new Stopwatch().start();
        LOG.info("Running algorithm");
    
        LOG.info("Calculating immidiate primitive concepts");
        Set<Statement> allImmidiatePrimitiveConceptStatements = new HashSet<Statement>();
        for (Concept concept : concepts){
            allImmidiatePrimitiveConceptStatements.addAll(
                    createProximalPrimitiveStatementsForConcept(concept, true, showDetails, showDetailsConceptIds));
        }
        LOG.info("Found [" + allImmidiatePrimitiveConceptStatements.size() + "] immidiate primitive concepts");
       
        
        LOG.info("Calculating unshared defining characteristics");
        Set<Statement> allUndefinedCharacteristicsStatements = new HashSet<Statement>();
        for (Concept concept : concepts){
            allUndefinedCharacteristicsStatements.addAll(
                    getUnsharedDefiningCharacteristicsForConcept(concept, true, showDetails, showDetailsConceptIds));
        }
        LOG.info("Found [" + allUndefinedCharacteristicsStatements.size() + "] unshared defining characteristics");
     
        Set<Statement> returnSet = new HashSet<Statement>();
        returnSet.addAll(allUndefinedCharacteristicsStatements);
        returnSet.addAll(allImmidiatePrimitiveConceptStatements);
        
        stopwatch.stop();
        LOG.info("Completed algorithm in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds with [" + returnSet.size() + "] statements");            
        return returnSet;
    }    
    
    protected Set<Statement> createProximalPrimitiveStatementsForConcept(Concept concept, boolean useCache, boolean showDetails, Set<Long> showDetailsConceptIds){
        Set<Statement> returnStatements = new HashSet<Statement>();
        for (Concept proximalPrimitiveConcept : getProximalPrimitiveConcepts(concept, useCache, showDetails, showDetailsConceptIds)){
            returnStatements.add(new Statement(getNewId(), concept, concept.getOntologyVersion().getIsKindOfPredicate(), proximalPrimitiveConcept));
        }
        return returnStatements;
    }    
    
    protected Set<Concept> getProximalPrimitiveConcepts(Concept concept, boolean useCache, boolean showDetails, Set<Long> showDetailsConceptIds){
        boolean shouldShowDetails = showDetails && ((showDetailsConceptIds == null) || (showDetailsConceptIds.contains(concept.getSerialisedId())));

        if (shouldShowDetails){
            LOG.info("Attempting to find all proximal primitive isKindOf concepts for concept {}",  concept.getSerialisedId());    
        }
        Set<Concept> proximalParentsOnly = new HashSet<Concept>(concept.getAllKindOfPrimitiveConcepts(useCache));
        
        if (shouldShowDetails && LOG.isDebugEnabled()){
            StringBuffer debugStringBuffer = new StringBuffer("All primitive super concepts for concept " + concept.getSerialisedId() + " are {");
            for (Concept c : concept.getAllKindOfPrimitiveConcepts(useCache)){
                debugStringBuffer.append(c.getSerialisedId()+ ", ");
            }
            if (!concept.getAllKindOfPrimitiveConcepts(useCache).isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.debug(debugStringBuffer.toString());
        }
        
        for (Concept parentUnderTest : concept.getAllKindOfPrimitiveConcepts(useCache)){
            if (shouldShowDetails && LOG.isDebugEnabled()){
                if (parentUnderTest.getAllKindOfPrimitiveConcepts(useCache).isEmpty()){
                    LOG.debug("Concept " + parentUnderTest.getSerialisedId() + " has no isA relationships, continuing");
                }
            }
            for (Concept parentOfParentUnderTest : parentUnderTest.getAllKindOfPrimitiveConcepts(useCache)){
                if (shouldShowDetails) LOG.debug("Found that concept {} isA concept {}", parentUnderTest.getSerialisedId(), parentOfParentUnderTest.getSerialisedId());
                if (concept.getAllKindOfPrimitiveConcepts(useCache).contains(parentOfParentUnderTest)){
                    if (shouldShowDetails) LOG.info("Since concept {} isA concept {}, concept {} is not an proximal isA for concept {}", parentUnderTest.getSerialisedId(), parentOfParentUnderTest.getSerialisedId(), parentOfParentUnderTest.getSerialisedId(), concept.getSerialisedId());
                    proximalParentsOnly.remove(parentOfParentUnderTest);
                }
            }
        }
        if (shouldShowDetails && LOG.isDebugEnabled()){
            StringBuffer debugStringBuffer = new StringBuffer("Found that concept " + concept.getSerialisedId() + " has proximal primitive isA concept(s) of {");
            for (Concept c : proximalParentsOnly){
                debugStringBuffer.append(c.getSerialisedId() + ", ");
            }
            if (!proximalParentsOnly.isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.debug(debugStringBuffer.toString());
        }

        return proximalParentsOnly;
    }
    
    protected Set<Statement> getUnsharedDefiningCharacteristicsForConcept(Concept concept, boolean useCache, boolean showDetails, Set<Long> showDetailsConceptIds){
        boolean shouldShowDetails = showDetails && ((showDetailsConceptIds == null) || (showDetailsConceptIds.contains(concept.getSerialisedId())));
        if(shouldShowDetails) LOG.info("Attempting to find all unshared defining characteristics for concept [{}]",  concept.getSerialisedId());
        
        Set<Statement> allStatementsForConcept = new HashSet<Statement>(concept.getSubjectOfStatements());
        
        if (shouldShowDetails && LOG.isDebugEnabled()){
            StringBuffer debugStringBuffer = new StringBuffer("Relationships for concept [" + concept.getSerialisedId() + "] are {");
            for (Statement rs : concept.getSubjectOfStatements()){
                debugStringBuffer.append(rs.shortToString() + ", ");
            }

            if (!concept.getSubjectOfStatements().isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.debug(debugStringBuffer.toString());
        }
        
        for (Statement rUnderTest : concept.getSubjectOfStatements()){
            if ((rUnderTest.isKindOfStatement()) || (!rUnderTest.isDefiningCharacteristic())){
                allStatementsForConcept.remove(rUnderTest);
                continue;
            }
            
            if (shouldShowDetails && LOG.isDebugEnabled()){
                if (!rUnderTest.isDefiningCharacteristic()){
                    LOG.debug("Skipping non-defining characteristic relationship {}", rUnderTest);
                }
                else if (rUnderTest.isKindOfStatement()){
                    LOG.debug("Skipping non-characteristic isA relationship {}", rUnderTest);
                }
                else{
                    LOG.debug("Testing for existence of relationship [{}] in parent primitive concepts", rUnderTest.getSerialisedId());
                }
                if (concept.getAllKindOfPrimitiveConcepts(useCache).isEmpty()){
                    LOG.debug("Concept [" + concept.getSerialisedId() + "] has no isA relationships, continuing");
                }
            }
            for (Concept parentConcept : concept.getAllKindOfPrimitiveConcepts(useCache)){
                if (shouldShowDetails && LOG.isDebugEnabled()){
                    LOG.debug("Concept [{}] has a primitive parent concept of [{}]", concept.getSerialisedId(), parentConcept.getSerialisedId());
                    if (parentConcept.getSubjectOfStatements().isEmpty()){
                        LOG.debug("Concept [{}] is not the subject of any relationship statements. Continuing", parentConcept.getSerialisedId());
                    }
                }
                for (Statement rParent : parentConcept.getSubjectOfStatements()){
                    if (shouldShowDetails) LOG.debug("Found that parent concept [{}] has relationship {}", parentConcept.getSerialisedId(), rParent.shortToString());
                    if ((rUnderTest.getPredicate().equals(rParent.getPredicate()))&& 
                            rUnderTest.getObject().equals(rParent.getObject())&& 
                            rParent.isDefiningCharacteristic())
                    {
                        if (shouldShowDetails) {
                            LOG.info("Found that relationship under test {} is also defined in parent concept as {}", rUnderTest.shortToString(), rParent.shortToString());
                        }

                        
                        if ((!rUnderTest.isMemberOfGroup()) && (!rParent.isMemberOfGroup())){
                            if (shouldShowDetails){
                                LOG.info("Both parent concept statement and child concept statement are members of group 0 (no group), so statement is removed");
                                LOG.info("Removing statement [{}] from output because parent concept [{}] has defined relationship [{}]",  rUnderTest.shortToString(), parentConcept.getSerialisedId(), rParent.shortToString());
                            }
                            allStatementsForConcept.remove(rUnderTest);
                        }
                        else  if ((rUnderTest.isMemberOfGroup()) && (!rParent.isMemberOfGroup())){
                            if (shouldShowDetails){
                                LOG.info("Parent concept statement is a member of group 0 (no group), but the child concept statement is not, so statement is kept");
                            }
                        }
                        else if ((!rUnderTest.isMemberOfGroup()) && (rParent.isMemberOfGroup())){
                            if (shouldShowDetails){
                                LOG.info("Child concept statement is a member of group 0 (no group), but the parent concept statement is not, so statement is kept");
                            }
                        }
                        else if (rUnderTest.getGroup().equals(rParent.getGroup()))
                        {
                            if (shouldShowDetails) {
                                LOG.info("Because parent concept statement is part of a group that is identical to the group of the child concept statement, and because both the parent and child group id is not 0 (no group), we can remove");
                                LOG.info("Removing statement [{}] from output because parent concept [{}] has defined relationship [{}]",  rUnderTest.shortToString(), parentConcept.getSerialisedId(), rParent.shortToString());
                            }

                            allStatementsForConcept.remove(rUnderTest);
                        }else if (shouldShowDetails){
                            LOG.info("But the child concept statement's group is not the same as the parent concept statement's group, and neither of the groups are 0 (no group), so the statement is kept");                            
                        }
                    }
                }
            }
            if (shouldShowDetails){
                StringBuffer debugStringBuffer = new StringBuffer("Found that concept " + concept.getSerialisedId() + " has defining characteristic relationships {");
                for (Statement r : allStatementsForConcept){
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
