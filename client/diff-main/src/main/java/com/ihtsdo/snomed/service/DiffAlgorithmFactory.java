package com.ihtsdo.snomed.service;


public class DiffAlgorithmFactory {

    public enum DiffStrategy{
        SUBJECT_OBJECT, SUBJECT_PREDICATE_OBJECT, SERIALISED_ID;
    }
    
    public static DiffAlgorithm getAlgorithm(DiffStrategy diffStrategy){
        switch (diffStrategy){
            case SUBJECT_OBJECT:
                return new SubjectObjectDiff();
            case SUBJECT_PREDICATE_OBJECT:
                return new SubjectObjectDiff();
            case SERIALISED_ID:
                return new SerialisedIdDiff();
            default:
                throw new InvalidInputException("Diff algorithm for " + diffStrategy + " not found");
        }
    }
}
