package com.ihtsdo.snomed.canonical.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.ihtsdo.snomed.canonical.test.model.StatementForCompareWrapper;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;

public class TestingAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( TestingAlgorithm.class );

    private   CanonicalWrappedStatementWriter writer = new CanonicalWrappedStatementWriter();

    public void findDifference(EntityManager em, File extraFile, File missingFile, Ontology originalOntology, 
            Ontology expectedOntology, Ontology generatedOntology) throws IOException
    {
        Stopwatch stopwatch = new Stopwatch().start();
        LOG.info("Starting sanity check");
 
//        int expectedStatementsSize = expectedOntology.getStatements().size();
//        LOG.info("Total number of expected statements: " + expectedStatementsSize);
//        int generatedStatementsSize = generatedOntology.getStatements().size();
//        LOG.info("Total number of generated statements: " + generatedStatementsSize);
//        int originalStatementsSize = originalOntology.getStatements().size();
//        LOG.info("Total number of original statements: " + originalStatementsSize);

        LOG.info("Comparing the inputs");
        
        //LOG.info("Wrapping expected statements");
        Set<StatementForCompareWrapper> expectedWrappedStatements = new HashSet<StatementForCompareWrapper>();
        for (Statement r : expectedOntology.getStatements()){
            expectedWrappedStatements.add(new StatementForCompareWrapper(r));
        }
        //LOG.info("Wrapping generated statements");
        Set<StatementForCompareWrapper> generatedWrappedStatements = new HashSet<StatementForCompareWrapper>();
        for (Statement r : generatedOntology.getStatements()){
            generatedWrappedStatements.add(new StatementForCompareWrapper(r));
        }

        //LOG.info("Splitting generated statements");
        Set<StatementForCompareWrapper> generatedIsKindOfWrappedStatements = new HashSet<StatementForCompareWrapper>();
        for (StatementForCompareWrapper r : generatedWrappedStatements){
            if (r.getStatement().isKindOfStatement()){
                generatedIsKindOfWrappedStatements.add(r);
            }
        }
        LOG.info("Found [" + generatedIsKindOfWrappedStatements.size() + "] generated isKindOf statements");
        
        //LOG.info("Splitting expected statements");
        Set<StatementForCompareWrapper> expectedIsKindOfWrappedStatements = new HashSet<StatementForCompareWrapper>();
        for (StatementForCompareWrapper r : expectedWrappedStatements){
            if (r.getStatement().isKindOfStatement()){
                expectedIsKindOfWrappedStatements.add(r);
            }
        }
        LOG.info("Found [" + expectedIsKindOfWrappedStatements.size() + "] expected isKindOf statements");
        
        Set<StatementForCompareWrapper> generatedUdcWrappedStatements = new HashSet<StatementForCompareWrapper>(generatedWrappedStatements);
        generatedUdcWrappedStatements.removeAll(generatedIsKindOfWrappedStatements);
        LOG.info("Found [" + generatedUdcWrappedStatements.size() + "] generated udc statements");

        Set<StatementForCompareWrapper> expectedUdcWrappedStatements = new HashSet<StatementForCompareWrapper>(expectedWrappedStatements);
        expectedUdcWrappedStatements.removeAll(expectedIsKindOfWrappedStatements);
        LOG.info("Found [" + expectedUdcWrappedStatements.size() + "] expected udc statements");

        SetView<StatementForCompareWrapper> missingIsKindOfFromGeneratedOutput = Sets.difference(expectedIsKindOfWrappedStatements, generatedIsKindOfWrappedStatements);
        LOG.info("There are [{}] missing isA statements", missingIsKindOfFromGeneratedOutput.size());

        SetView<StatementForCompareWrapper> extraIsKindOfInGeneratedOutput = Sets.difference(generatedIsKindOfWrappedStatements, expectedIsKindOfWrappedStatements);
        LOG.info("There are [{}] extra isA statements", extraIsKindOfInGeneratedOutput.size());

        SetView<StatementForCompareWrapper> missingUdcFromGeneratedOutput = Sets.difference(expectedUdcWrappedStatements, generatedUdcWrappedStatements);
        LOG.info("There are [{}] missing unshared defining characteristic statements", missingUdcFromGeneratedOutput.size());

        SetView<StatementForCompareWrapper> extraUdcInGeneratedOutput = Sets.difference(generatedUdcWrappedStatements, expectedUdcWrappedStatements);
        LOG.info("There are [{}] extra unshared defining characteristic statements", extraUdcInGeneratedOutput.size());

        LOG.info("Writing all extra statements to " + extraFile);
        writeErrors(extraFile, extraIsKindOfInGeneratedOutput, extraUdcInGeneratedOutput);
        
//        LOG.info("Testing to see if any of the extra statements have swapped identical groups with one or more of their parents");
//        testForSwappedIdenticalGroups(extraUdcInGeneratedOutput, em);

        LOG.info("Writing all missing statements to " + missingFile);
        writeErrors(missingFile, missingIsKindOfFromGeneratedOutput, missingUdcFromGeneratedOutput);

//        LOG.info("Finding errors");
//        Set<RelationshipStatementForCompareWrapper> erroneousExpectedIsaStatements = new HashSet<RelationshipStatementForCompareWrapper>();
//        Set<RelationshipStatementForCompareWrapper> erroneousExpectedUdcStatements = new HashSet<RelationshipStatementForCompareWrapper>();
//
//        Set<Long> allOriginalIsAStatementObjects = buildSerialisedIdIndexForOriginalIsaStatements(originalOntology);
//
//        Set<Long> allOriginalUdcStatementObjects = buildSerialisedIdIndexForOriginalUdcStatements(originalOntology);
//
//        checkObjectsOfIsaStatementsMustAppearInOriginalStatementsAsObjects(
//                missingIsKindOfFromGeneratedOutput,
//                erroneousExpectedIsaStatements, allOriginalIsAStatementObjects);
//
//
//        checkObjectsOfUdcStatementsMustAppearInOriginalStatementsAsObjects(
//                missingUdcFromGeneratedOutput,
//                erroneousExpectedUdcStatements, allOriginalUdcStatementObjects);
//
//        checkIfAnyUdcStatementsAreNotPrimitiveStatements(missingUdcFromGeneratedOutput);
//
//        checkIfUdcStatementsExistsInPrimitiveParentConcept(missingUdcFromGeneratedOutput);
        
        stopwatch.stop();
        LOG.info("Completed sanity check in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }
    
//    private void testForSwappedIdenticalGroups(Set<RelationshipStatementForCompareWrapper> extraStatements, EntityManager em) throws IOException{
//        Set<SetEquivalent> setEquivalents = new HashSet<SetEquivalent>(4000);
//        Set<Concept> allSubjects = getAllSubjects(extraStatements); 
//        LOG.info("There are unique " + allSubjects.size() + " subject concepts in the extra statements");
//        Set<Concept> swapProblemConcepts = new HashSet<Concept>();
//        for (Concept c : allSubjects){
//            
//            Concept originalVersionOfConcept = em.createQuery(
//                    "select c from Concept c where c.ontology.id = 1 and c.serialisedId = " + c.getSerialisedId(), 
//                    Concept.class).getSingleResult();
//            
//            LOG.debug("Testing if subject " + originalVersionOfConcept.getSerialisedId() + " has at least one swapped group in parent");
//            Set<Integer> subjectGroups = findAllGroups(originalVersionOfConcept);
//            LOG.debug("Subject {} has groups [{}]", originalVersionOfConcept.getSerialisedId(), subjectGroups);
//            
//            for (Integer subjectGroup : subjectGroups){
//                Set<Statement> statementsInGroup = new HashSet<Statement>();
//                for (Statement s : originalVersionOfConcept.getSubjectOfRelationshipStatements()){
//                    if (s.getGroup() == subjectGroup){
//                        statementsInGroup.add(s);
//                    }
//                }
//                LOG.debug("Found a total of {} statements in group {} for concept [{}]", 
//                        statementsInGroup.size(), subjectGroup, c.getSerialisedId());
//                
//                for (Concept parent : originalVersionOfConcept.getAllKindOfPrimitiveConcepts(true)){
//                    LOG.debug("Looking for group in parent concept {} identical to group {} in concept {}", 
//                            parent.getSerialisedId(), subjectGroup, originalVersionOfConcept.getSerialisedId());
//                    int parentGroup = findSetEqualGroup(parent, statementsInGroup);
//                    if ((parentGroup != -1) && (parentGroup != subjectGroup)){
////                        LOG.info("Group {} in child concept {} is set identical to group {} in parent concept {}",
////                                subjectGroup, c.getSerialisedId(), parentGroup, parent.getSerialisedId());
//                        swapProblemConcepts.add(c);
//                        
//                        SetEquivalent se = new SetEquivalent();
//                        se.childGroup = subjectGroup;
//                        se.parentGroup = parentGroup;
//                        se.parentConcept = parent;
//                        se.childConcept = c;
//                        se.childStatements = statementsInGroup;
//                        for (Statement s : parent.getSubjectOfRelationshipStatements()){
//                            if (s.getGroup() == parentGroup){
//                                se.parentStatements.add(s);
//                            }
//                        }
//                        setEquivalents.add(se);
//                        
//                    }else{
//                        LOG.debug("Not found");
//                    }
//                }
//            }
//        }
//        for (SetEquivalent se : setEquivalents){
//            LOG.info(se.toString());
//        }
//        LOG.info("There are " + setEquivalents.size() + " parent-child concepts that have equivalent sets with different group ids");
//        
//        Set<RelationshipStatementForCompareWrapper> statementsWithPossibleGroupSwap = new HashSet<RelationshipStatementForCompareWrapper>();
//        for (RelationshipStatementForCompareWrapper r : extraStatements){
//            for (SetEquivalent se : setEquivalents){
//                if (se.childStatements.contains(r.getRelationshipStatement())){
//                    statementsWithPossibleGroupSwap.add(new RelationshipStatementForCompareWrapper(r.getRelationshipStatement()));
//                }
//            }
//        }
//        Set<RelationshipStatementForCompareWrapper> remainingProblems = new HashSet<RelationshipStatementForCompareWrapper>(extraStatements); 
//        remainingProblems.removeAll(statementsWithPossibleGroupSwap);
//        
//        File file = new File("remaining");
//        if (!file.exists()){
//            file.createNewFile();
//        }            
//        try(FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)){
//            LOG.info("Writing remaining " + remainingProblems.size() + " extra statements to file 'remaining'"); 
//            writer.writeCompareStatements(bw, remainingProblems);
//        }
//        
//        LOG.info("There are " + statementsWithPossibleGroupSwap.size() + " extra statements that are defined in a group that has a set equivalent group in a parent concept, but under a different group id");
//    }
    
//    private class SetEquivalent{
//        public Concept childConcept;
//        public int childGroup;
//        public Set<Statement> childStatements = new HashSet<Statement>();
//        
//        public Concept parentConcept;
//        public int parentGroup;
//        public Set<Statement> parentStatements = new HashSet<Statement>();
//        
//        public String toString(){
//            return "Group " + childGroup + " in child concept " + (childConcept == null ? null : childConcept.getSerialisedId()) + 
//                    " has " + childStatements.size() + " relationships and is set identical to group " + 
//                    parentGroup + " in parent concept " + (parentConcept == null ? null : parentConcept.getSerialisedId() + 
//                            " with " + parentStatements.size() + " relationships");
//        }
//    }
//
//    private Set<Concept> getAllSubjects(Set<RelationshipStatementForCompareWrapper> udcStatements) {
//        Set<Concept> allSubjects = new HashSet<Concept>();
//        for (RelationshipStatementForCompareWrapper r : udcStatements){
//            allSubjects.add(r.getRelationshipStatement().getSubject());
//        }
//        return allSubjects;
//    }
//
//    private int findSetEqualGroup(Concept concept, Set<Statement> incomingStatementsInGroup) {
//        for (int group : findAllGroups(concept)){
//            Set<StatementWrapperForAttributeCompare> statementsInGroupToCompare = new HashSet<StatementWrapperForAttributeCompare>();
//            for (Statement s : concept.getSubjectOfRelationshipStatements()){
//                if (s.getGroup() == group){
//                    statementsInGroupToCompare.add(new StatementWrapperForAttributeCompare(s));
//                }
//                if (statementsInGroupToCompare.equals(wrapStatements(incomingStatementsInGroup))){
//                    return group;
//                }
//            }
//        }
//        return -1;
//    }

//    private Set<StatementWrapperForAttributeCompare> wrapStatements(
//            Set<Statement> incomingStatementsInGroup) {
//        Set<StatementWrapperForAttributeCompare> wrappedIncomingStatementsInGroup = new HashSet<StatementWrapperForAttributeCompare>();
//        for (Statement r : incomingStatementsInGroup){
//            wrappedIncomingStatementsInGroup.add(new StatementWrapperForAttributeCompare(r));
//        }
//        return wrappedIncomingStatementsInGroup;
//    }
//
//    private Set<Integer> findAllGroups(Concept concept) {
//        Set<Integer> groups = new HashSet<Integer>();
//        for (Statement s : concept.getSubjectOfRelationshipStatements()){
//            groups.add(s.getGroup());
//        }
//        return groups;
//    }
//
//    private class StatementWrapperForAttributeCompare{
//        Statement statement;
//        
//        public StatementWrapperForAttributeCompare(Statement statement){
//            this.statement = statement;
//        }
//        
//        @Override
//        public boolean equals(Object o){
//            if (o instanceof StatementWrapperForAttributeCompare){
//                StatementWrapperForAttributeCompare r = (StatementWrapperForAttributeCompare) o;            
//                if (r.getRelationshipStatement().getPredicate().equals(this.getRelationshipStatement().getPredicate()) &&
//                    r.getRelationshipStatement().getObject().equals(this.getRelationshipStatement().getObject()))
//                {
//                    return true;
//                }
//            }
//            return false;
//        }
//        
//        @Override
//        public int hashCode(){
//            return Longs.hashCode(getRelationshipStatement().getPredicate().getSerialisedId());
//        }
//        
//        public String toString(){
//            return "wrapped: " + statement.toString();
//        }
//        
//        public Statement getRelationshipStatement(){
//            return statement;
//        }
//        
//    }
//    
//    private Set<Long> buildSerialisedIdIndexForOriginalUdcStatements(
//            Ontology originalOntology) {
//        Set<Long> allOriginalUdcStatementObjects = new HashSet<Long>();
//        for (Statement r : originalOntology.getRelationshipStatements()){
//            if (!r.isKindOfRelationship()) allOriginalUdcStatementObjects.add(r.getObject().getSerialisedId());
//        }
//        return allOriginalUdcStatementObjects;
//    }
//
//    private Set<Long> buildSerialisedIdIndexForOriginalIsaStatements(
//            Ontology originalOntology) {
//        Set<Long> allOriginalIsAStatementObjects = new HashSet<Long>();
//        for (Statement r : originalOntology.getRelationshipStatements()){
//            if (r.isKindOfRelationship()) allOriginalIsAStatementObjects.add(r.getObject().getSerialisedId());
//        }
//        return allOriginalIsAStatementObjects;
//    }
//
//    private void checkObjectsOfIsaStatementsMustAppearInOriginalStatementsAsObjects(
//            SetView<RelationshipStatementForCompareWrapper> missingIsKindOfFromGeneratedOutput,
//            Set<RelationshipStatementForCompareWrapper> erroneousExpectedIsaStatements,
//            Set<Long> allOriginalIsAStatementObjects) {
//        LOG.info("Objects of isA statements in the expected output must also appear as objects of isA statements in the input");
//        for (RelationshipStatementForCompareWrapper rUnderTest : missingIsKindOfFromGeneratedOutput){
//            if (rUnderTest.getRelationshipStatement().isKindOfRelationship() && (!allOriginalIsAStatementObjects.contains(rUnderTest.getRelationshipStatement().getObject().getSerialisedId()))){
//                erroneousExpectedIsaStatements.add(rUnderTest);
//            }
//        }
//        LOG.info("Number of IsA statements in the expected output in violation of this : {}", erroneousExpectedIsaStatements.size());
//    }
//
//    private void checkObjectsOfUdcStatementsMustAppearInOriginalStatementsAsObjects(
//            SetView<RelationshipStatementForCompareWrapper> missingUdcFromGeneratedOutput,
//            Set<RelationshipStatementForCompareWrapper> erroneousExpectedUdcStatements,
//            Set<Long> allOriginalUdcStatementObjects) {
//        LOG.info("Objects of udc statements in the expected output must also appear as objects of udc statements in the input");
//        for (RelationshipStatementForCompareWrapper rUnderTest : missingUdcFromGeneratedOutput){
//            if (!rUnderTest.getRelationshipStatement().isKindOfRelationship() && (!allOriginalUdcStatementObjects.contains(rUnderTest.getRelationshipStatement().getObject().getSerialisedId()))){
//                erroneousExpectedUdcStatements.add(rUnderTest);
//            }
//        }
//        LOG.info("Number of udc statements in the expected output in violation of this : {}", erroneousExpectedUdcStatements.size());
//    }
//
//    private void checkIfAnyUdcStatementsAreNotPrimitiveStatements(
//            SetView<RelationshipStatementForCompareWrapper> missingUdcFromGeneratedOutput) {
//        Set<RelationshipStatementForCompareWrapper> notCharacteristicType = new HashSet<RelationshipStatementForCompareWrapper>();
//        for (RelationshipStatementForCompareWrapper rUnderTest : missingUdcFromGeneratedOutput){
//            if (!rUnderTest.getRelationshipStatement().isDefiningCharacteristic()){
//                notCharacteristicType.add(rUnderTest);
//            }
//        }
//        LOG.info("Number of udc statements that are not charateristic types: " + notCharacteristicType.size());
//    }
//
//    private void checkIfUdcStatementsExistsInPrimitiveParentConcept(
//            SetView<RelationshipStatementForCompareWrapper> missingUdcFromGeneratedOutput) {
//        LOG.info("For all missing udc statements, check if udc exists in primitive parent concept");
//        Set<RelationshipStatementForCompareWrapper> udcInPrimitiveParentConcept = new HashSet<RelationshipStatementForCompareWrapper>();
//        for (RelationshipStatementForCompareWrapper rUnderTest : missingUdcFromGeneratedOutput){
//            if (!rUnderTest.getRelationshipStatement().isKindOfRelationship()){
//                for (Concept parentConcept : rUnderTest.getRelationshipStatement().getSubject().getAllKindOfPrimitiveConcepts(true)){
//                    for (Statement parentRelationshipStatement : parentConcept.getSubjectOfRelationshipStatements()){
//                        if (!parentRelationshipStatement.isDefiningCharacteristic()){
//                            continue;
//                        }
//                        if ((parentRelationshipStatement.getPredicate() == rUnderTest.getRelationshipStatement().getPredicate()) &&
//                                parentRelationshipStatement.getObject().equals(rUnderTest.getRelationshipStatement().getObject())){
//                            udcInPrimitiveParentConcept.add(rUnderTest);
//                        }
//
//                    }
//                }
//            }
//        }
//        LOG.info("Found expected [{}] udc statements already defined in a primitive parent subject concept as a characteristic type", udcInPrimitiveParentConcept.size());
//    }

    private void writeErrors(File file, SetView<StatementForCompareWrapper> isKindOfStatements, 
            SetView<StatementForCompareWrapper> udcfStatements) throws IOException 
            {
        Set <StatementForCompareWrapper> allStatements = new HashSet<StatementForCompareWrapper>(isKindOfStatements);
        allStatements.addAll(udcfStatements);
        if (!file.exists()){
            file.createNewFile();
        }            
        try(FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)){
            writer.writeCompareStatements(bw, allStatements);
        }
    }
}


//LOG.info("Writing these errors to 'expected.isa.output.never.object.errors.txt'");
//File file = new File("expected.isa.output.never.object.errors.txt");
//if (!file.exists()) file.createNewFile();
//try(FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)){
//  writer.writeCompareStatements(bw, erroneousExpectedIsaStatements);
//}

//LOG.info("Writing errors to 'expected.udc.output.never.object.errors.txt'");
//File udcFile = new File("expected.udc.output.never.object.errors.txt");
//if (!udcFile.exists()) udcFile.createNewFile();
//try(FileWriter fw = new FileWriter(udcFile); BufferedWriter bw = new BufferedWriter(fw)){
//  writer.writeCompareStatements(bw, erroneousExpectedUdcStatements);
//}

//LOG.info("Writing errors to 'expected.udc.output.parent.defined.errors.txt'");
//File udcFileParent = new File("expected.udc.output.parent.defined.errors.txt");
//if (!udcFileParent.exists()) udcFileParent.createNewFile();
//try(FileWriter fw = new FileWriter(udcFileParent); BufferedWriter bw = new BufferedWriter(fw)){
//    writer.writeCompareStatements(bw, udcInPrimitiveParentConcept);
//}