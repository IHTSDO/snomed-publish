package com.ihtsdo.snomed.canonical.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.Ontology;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;
import com.ihtsdo.snomed.canonical.test.model.RelationshipStatementForCompareWrapper;

public class TestingAlgorithm {
    private static final Logger LOG = LoggerFactory.getLogger( TestingAlgorithm.class );

    private   CanonicalWrappedStatementWriter writer = new CanonicalWrappedStatementWriter();

    public void findDifference(EntityManager em, File extraFile, File missingFile, Ontology originalOntology, 
            Ontology expectedOntology, Ontology generatedOntology) throws IOException
    {
        LOG.info("Finding set differences");
        //em.getTransaction().begin();
        // UNCOMMENT THESE - USEFULL
        //        int expectedStatementsSize = expectedOntology.getRelationshipStatements().size();
        //        LOG.info("Total number of expected statements: " + expectedStatementsSize);
        //        int generatedStatementsSize = generatedOntology.getRelationshipStatements().size();
        //        LOG.info("Total number of generated statements: " + generatedStatementsSize);
        //        int originalStatementsSize = originalOntology.getRelationshipStatements().size();
        //        LOG.info("Total number of original statements: " + originalStatementsSize);

        Set<RelationshipStatementForCompareWrapper> expectedWrappedStatements = new HashSet<RelationshipStatementForCompareWrapper>();
        LOG.info("Wrapping expected statements");
        for (RelationshipStatement r : expectedOntology.getRelationshipStatements()){
            expectedWrappedStatements.add(new RelationshipStatementForCompareWrapper(r));
        }
        Set<RelationshipStatementForCompareWrapper> generatedWrappedStatements = new HashSet<RelationshipStatementForCompareWrapper>();
        LOG.info("Wrapping generated statements");
        for (RelationshipStatement r : generatedOntology.getRelationshipStatements()){
            generatedWrappedStatements.add(new RelationshipStatementForCompareWrapper(r));
        }
        //        Set<RelationshipStatementForCompareWrapper> originalWrappedStatements = new HashSet<RelationshipStatementForCompareWrapper>();
        //        LOG.info("Wrapping original statements");
        //        counter = 0;
        //        for (RelationshipStatement r : originalOntology.getRelationshipStatements()){
        //            originalWrappedStatements.add(new RelationshipStatementForCompareWrapper(r));
        //            if (counter % 25000 == 0){
        //                LOG.info(Integer.toString(counter));
        //            }
        //        }

        //LOG.info("Splitting generated statements");
        Set<RelationshipStatementForCompareWrapper> generatedIsKindOfWrappedStatements = new HashSet<RelationshipStatementForCompareWrapper>();
        for (RelationshipStatementForCompareWrapper r : generatedWrappedStatements){
            if (r.getRelationshipStatement().isKindOfRelationship()){
                generatedIsKindOfWrappedStatements.add(r);
            }
        }
        LOG.info("Found [" + generatedIsKindOfWrappedStatements.size() + "] generated isKindOf statements");
        Set<RelationshipStatementForCompareWrapper> generatedUdcWrappedStatements = new HashSet<RelationshipStatementForCompareWrapper>(generatedWrappedStatements);
        generatedUdcWrappedStatements.removeAll(generatedIsKindOfWrappedStatements);
        LOG.info("Found [" + generatedUdcWrappedStatements.size() + "] generated udc statements");

        //LOG.info("Splitting expected statements");
        Set<RelationshipStatementForCompareWrapper> expectedIsKindOfWrappedStatements = new HashSet<RelationshipStatementForCompareWrapper>();
        for (RelationshipStatementForCompareWrapper r : expectedWrappedStatements){
            if (r.getRelationshipStatement().isKindOfRelationship()){
                expectedIsKindOfWrappedStatements.add(r);
            }
        }
        LOG.info("Found [" + expectedIsKindOfWrappedStatements.size() + "] expected isKindOf statements");
        Set<RelationshipStatementForCompareWrapper> expectedUdcWrappedStatements = new HashSet<RelationshipStatementForCompareWrapper>(expectedWrappedStatements);
        expectedUdcWrappedStatements.removeAll(expectedIsKindOfWrappedStatements);
        LOG.info("Found [" + expectedUdcWrappedStatements.size() + "] expected udc statements");

        //        Set<RelationshipStatementForCompareWrapper> originalIsKindOfWrappedStatements = new HashSet<RelationshipStatementForCompareWrapper>();
        //        for (RelationshipStatementForCompareWrapper r : originalWrappedStatements){
        //            if (r.getRelationshipStatement().isKindOfRelationship()){
        //                originalIsKindOfWrappedStatements.add(r);
        //            }
        //        }
        //        LOG.info("Found [" + originalIsKindOfWrappedStatements.size() + "] original isKindOf statements");
        //        Set<RelationshipStatementForCompareWrapper> originalUdcWrappedStatements = new HashSet<RelationshipStatementForCompareWrapper>(originalWrappedStatements);
        //        originalUdcWrappedStatements.removeAll(originalIsKindOfWrappedStatements);
        //        LOG.info("Found [" + originalUdcWrappedStatements.size() + "] original udc statements");
        //        

        SetView<RelationshipStatementForCompareWrapper> missingIsKindOfFromGeneratedOutput = Sets.difference(expectedIsKindOfWrappedStatements, generatedIsKindOfWrappedStatements);
        LOG.info("There are [{}] missing isA statements", missingIsKindOfFromGeneratedOutput.size());

        SetView<RelationshipStatementForCompareWrapper> extraIsKindOfInGeneratedOutput = Sets.difference(generatedIsKindOfWrappedStatements, expectedIsKindOfWrappedStatements);
        LOG.info("There are [{}] extra isA statements", extraIsKindOfInGeneratedOutput.size());

        SetView<RelationshipStatementForCompareWrapper> missingUdcFromGeneratedOutput = Sets.difference(expectedUdcWrappedStatements, generatedUdcWrappedStatements);
        LOG.info("There are [{}] missing unshared defining characteristic statements", missingUdcFromGeneratedOutput.size());

        SetView<RelationshipStatementForCompareWrapper> extraUdcInGeneratedOutput = Sets.difference(generatedUdcWrappedStatements, expectedUdcWrappedStatements);
        LOG.info("There are [{}] extra unshared defining characteristic statements", extraUdcInGeneratedOutput.size());

        LOG.info("Writing all extra statements to " + extraFile);
        writeErrors(extraFile, extraIsKindOfInGeneratedOutput, extraUdcInGeneratedOutput);

        LOG.info("Writing all missing statements to " + missingFile);
        writeErrors(missingFile, missingIsKindOfFromGeneratedOutput, missingUdcFromGeneratedOutput);

        LOG.info("Finding errors");
        Set<RelationshipStatementForCompareWrapper> erroneousExpectedIsaStatements = new HashSet<RelationshipStatementForCompareWrapper>();
        Set<RelationshipStatementForCompareWrapper> erroneousExpectedUdcStatements = new HashSet<RelationshipStatementForCompareWrapper>();

        //LOG.info("Building isA object index from original input statements");
        Set<Long> allOriginalIsAStatementObjects = new HashSet<Long>();
        for (RelationshipStatement r : originalOntology.getRelationshipStatements()){
            if (r.isKindOfRelationship()) allOriginalIsAStatementObjects.add(r.getObject().getSerialisedId());
        }
        //LOG.info("Building udc object index from original input statements");
        Set<Long> allOriginalUdcStatementObjects = new HashSet<Long>();
        for (RelationshipStatement r : originalOntology.getRelationshipStatements()){
            if (!r.isKindOfRelationship()) allOriginalUdcStatementObjects.add(r.getObject().getSerialisedId());
        }        

        LOG.info("Objects of isA statements in the expected output, _must_ also appear as objects of isA statements in the input");
        for (RelationshipStatementForCompareWrapper rUnderTest : missingIsKindOfFromGeneratedOutput){
            if (rUnderTest.getRelationshipStatement().isKindOfRelationship() && (!allOriginalIsAStatementObjects.contains(rUnderTest.getRelationshipStatement().getObject().getSerialisedId()))){
                erroneousExpectedIsaStatements.add(rUnderTest);
            }
        }
        LOG.info("Number of IsA statements in the expected output in violation of this : {}", erroneousExpectedIsaStatements.size());
//        LOG.info("Writing these errors to 'expected.isa.output.never.object.errors.txt'");
//        File file = new File("expected.isa.output.never.object.errors.txt");
//        if (!file.exists()) file.createNewFile();
//        try(FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)){
//            writer.writeCompareStatements(bw, erroneousExpectedIsaStatements);
//        }

        LOG.info("Objects of udc statements in the expected output, _must_ also appear as objects of udc statements in the input");
        for (RelationshipStatementForCompareWrapper rUnderTest : missingUdcFromGeneratedOutput){
            if (!rUnderTest.getRelationshipStatement().isKindOfRelationship() && (!allOriginalUdcStatementObjects.contains(rUnderTest.getRelationshipStatement().getObject().getSerialisedId()))){
                erroneousExpectedUdcStatements.add(rUnderTest);
            }
        }

        LOG.info("Number of udc statements in the expected output in violation of this : {}", erroneousExpectedIsaStatements.size());
//        LOG.info("Writing errors to 'expected.udc.output.never.object.errors.txt'");
//        File udcFile = new File("expected.udc.output.never.object.errors.txt");
//        if (!udcFile.exists()) udcFile.createNewFile();
//        try(FileWriter fw = new FileWriter(udcFile); BufferedWriter bw = new BufferedWriter(fw)){
//            writer.writeCompareStatements(bw, erroneousExpectedUdcStatements);
//        }

        Set<RelationshipStatementForCompareWrapper> notCharacteristicType = new HashSet<RelationshipStatementForCompareWrapper>();
        for (RelationshipStatementForCompareWrapper rUnderTest : missingUdcFromGeneratedOutput){
            if (!rUnderTest.getRelationshipStatement().isDefiningCharacteristic()){
                notCharacteristicType.add(rUnderTest);
            }
        }
        LOG.info("Number of udc statements that are not charateristic types: " + notCharacteristicType.size());

        
        LOG.info("For all missing udc statements, check if udc exists in primitive parent concept");
        Set<RelationshipStatementForCompareWrapper> udcInPrimitiveParentConcept = new HashSet<RelationshipStatementForCompareWrapper>();
        for (RelationshipStatementForCompareWrapper rUnderTest : missingUdcFromGeneratedOutput){
            if (!rUnderTest.getRelationshipStatement().isKindOfRelationship()){
                for (Concept parentConcept : rUnderTest.getRelationshipStatement().getSubject().getAllKindOfPrimitiveConcepts(true)){
                    for (RelationshipStatement parentRelationshipStatement : parentConcept.getSubjectOfRelationShipStatements()){
                        if (!parentRelationshipStatement.isDefiningCharacteristic()){
                            continue;
                        }
                        if ((parentRelationshipStatement.getRelationshipType() == rUnderTest.getRelationshipStatement().getRelationshipType()) &&
                                parentRelationshipStatement.getObject().equals(rUnderTest.getRelationshipStatement().getObject())){
                            udcInPrimitiveParentConcept.add(rUnderTest);
                        }

                    }
                }
            }
        }
        LOG.info("Found expected [{}] udc statements already defined in a primitive parent subject concept as a characteristic type", udcInPrimitiveParentConcept.size());
//        LOG.info("Writing errors to 'expected.udc.output.parent.defined.errors.txt'");
//        File udcFileParent = new File("expected.udc.output.parent.defined.errors.txt");
//        if (!udcFileParent.exists()) udcFileParent.createNewFile();
//        try(FileWriter fw = new FileWriter(udcFileParent); BufferedWriter bw = new BufferedWriter(fw)){
//            writer.writeCompareStatements(bw, udcInPrimitiveParentConcept);
//        }

    }

    private void writeErrors(File file, SetView<RelationshipStatementForCompareWrapper> isKindOfStatements, 
            SetView<RelationshipStatementForCompareWrapper> udcfStatements) throws IOException 
    {
        Set <RelationshipStatementForCompareWrapper> allStatements = new HashSet<RelationshipStatementForCompareWrapper>(isKindOfStatements);
        allStatements.addAll(udcfStatements);
        if (!file.exists()){
            file.createNewFile();
        }            
        try(FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)){
            writer.writeCompareStatements(bw, allStatements);
        }
    }
}
