package com.ihtsdo.snomed.canonical;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class CanonicalAlgorithm {

    private static final Logger LOG = LoggerFactory.getLogger( CanonicalAlgorithm.class );
    
    /**
     * This method has been optimized with a cache.
     * Therefore, DO NOT call this method with caching until ALL concepts 
     * have been loaded and ALL kindOf relationships have been discovered
     */
    public Set<Concept> getAllImmidiatePrimitiveConcepts(Concept concept, boolean useCache){
        LOG.debug("Attempting to find all immidiate primitive isKindOf concepts for concept {}",  concept.getId());

        Set<Concept> immidiateParentsOnly = new HashSet<Concept>(concept.getKindOfPrimitiveConcepts(useCache));
        
        if (LOG.isDebugEnabled()){
            StringBuffer debugStringBuffer = new StringBuffer("All primitive isKindOf concepts for concept " + concept.getId() + " are {");
            for (Concept c : concept.getKindOfPrimitiveConcepts(useCache)){
                debugStringBuffer.append(c.getId()+ ", ");
            }
            if (!concept.getKindOfPrimitiveConcepts(useCache).isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.debug(debugStringBuffer.toString());
        }
        for (Concept parentUnderTest : concept.getKindOfPrimitiveConcepts(useCache)){
            if (LOG.isDebugEnabled()){
                if (parentUnderTest.getKindOfPrimitiveConcepts(useCache).isEmpty()){
                    LOG.debug("Concept " + parentUnderTest.getId() + " has no isKindOf primitive concepts, continuing");
                }
            }
            for (Concept parentOfParentUnderTest : parentUnderTest.getKindOfPrimitiveConcepts(useCache)){
                LOG.debug("Found that concept {} isKindOf concept {}", parentUnderTest.getId(), parentOfParentUnderTest.getId());
                if (concept.getKindOfPrimitiveConcepts(useCache).contains(parentOfParentUnderTest)){
                    LOG.debug("Since concept {} isKindOf concept {}, concept {} is not an immidiate isKindOf for concept {}", parentUnderTest.getId(), parentOfParentUnderTest.getId(), parentOfParentUnderTest.getId(), concept.getId());
                    immidiateParentsOnly.remove(parentOfParentUnderTest);
                }
            }
        }
        if (LOG.isDebugEnabled()){
            StringBuffer debugStringBuffer = new StringBuffer("Found that concept " + concept.getId() + " has immidiate primitive isKindOf concept(s) of {");
            for (Concept c : immidiateParentsOnly){
                debugStringBuffer.append(c.getId() + ", ");
            }
            if (!immidiateParentsOnly.isEmpty()){
                debugStringBuffer.delete(debugStringBuffer.length() - 2, debugStringBuffer.length());
            }
            debugStringBuffer.append("}");
            LOG.debug(debugStringBuffer.toString());
        }

        return immidiateParentsOnly;
    }
    
    public Set<RelationshipStatement> getAllImmidiatePrimitiveConceptStatements(Concept concept, boolean useCache){
        Set<Concept> allImmidiatePrimitiveConcepts = getAllImmidiatePrimitiveConcepts(concept, useCache);
        Set<RelationshipStatement> returnStatements = new HashSet<RelationshipStatement>();
        for (Concept c : allImmidiatePrimitiveConcepts){
            for (RelationshipStatement r : c.getSubjectOfRelationShipStatements()){
                if (r.isKindOfRelationship()){
                    returnStatements.add(r);
                }
            }
        }
        return returnStatements;
    }
    
    public Set<RelationshipStatement> getAllUnsharedDefiningCharacteristics(Concept concept, boolean useCache){
        Set<RelationshipStatement> unsharedRelationshipStatementsOnly = new HashSet<RelationshipStatement>(concept.getSubjectOfRelationShipStatements());
        for (RelationshipStatement rUnderTest : concept.getSubjectOfRelationShipStatements()){
            if ((rUnderTest.isKindOfRelationship()) || (!rUnderTest.isDefiningCharacteristic())){
                unsharedRelationshipStatementsOnly.remove(rUnderTest);
                continue;
            }
            for (Concept kindOfprimitiveConcept : concept.getKindOfPrimitiveConcepts(useCache)){
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
