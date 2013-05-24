package com.ihtsdo.snomed.model;


/**
 * select d.serialisedId as DescriptionId, c1.serialisedId as conceptId, d.term 
 * from description d join concept c1 on d.about_id = c1.id 
 * where term like '%(core metadata concept)';
 * 
 * +--------------------+--------------------+---------------------------------------------------------------------------------------------------------------+
 * | DescriptionId      | conceptId          | term                                                                                                          |
 * +--------------------+--------------------+---------------------------------------------------------------------------------------------------------------+
 * |         2898307011 |          449079008 | SNOMED CT to ICD-9CM equivalency mapping module (core metadata concept)                                       |
 * |         2898308018 |          449080006 | SNOMED CT to ICD-10 rule-based mapping module (core metadata concept)                                         |
 * |         2898309014 |          449081005 | SNOMED CT Spanish edition module (core metadata concept)                                                      |
 * | 900000000000005012 | 900000000000002006 | SNOMED CT universally unique identifier (core metadata concept)                                               |
 * | 900000000000008014 | 900000000000003001 | Fully specified name (core metadata concept)                                                                  |
 * | 900000000000014019 | 900000000000006009 | Defining relationship (core metadata concept)                                                                 |
 * | 900000000000027015 | 900000000000010007 | Stated relationship (core metadata concept)                                                                   |
 * | 900000000000032019 | 900000000000011006 | Inferred relationship (core metadata concept)                                                                 |
 * | 900000000000033012 | 900000000000012004 | SNOMED CT model component module (core metadata concept)                                                      |
 * | 900000000000036016 | 900000000000013009 | Synonym (core metadata concept)                                                                               |
 * | 900000000000044016 | 900000000000017005 | Entire term case sensitive (core metadata concept)                                                            |
 * | 900000000000053011 | 900000000000020002 | Only initial character case insensitive (core metadata concept)                                               |
 * | 900000000000166014 | 900000000000073002 | Sufficiently defined concept definition status (core metadata concept)                                        |
 * | 900000000000170018 | 900000000000074008 | Necessary but not sufficient concept definition status (core metadata concept)                                |
 * | 900000000000438011 | 900000000000207008 | SNOMED CT core module (core metadata concept)                                                                 |
 * | 900000000000478016 | 900000000000225001 | Qualifying relationship (core metadata concept)                                                               |
 * | 900000000000486016 | 900000000000227009 | Additional relationship (core metadata concept)                                                               |
 * | 900000000000622013 | 900000000000294009 | SNOMED CT integer identifier (core metadata concept)                                                          |
 * | 900000000000954019 | 900000000000442005 | Core metadata concept (core metadata concept)                                                                 |
 * | 900000000000956017 | 900000000000443000 | Module (core metadata concept)                                                                                |
 * | 900000000000957014 | 900000000000444006 | Definition status (core metadata concept)                                                                     |
 * | 900000000000960019 | 900000000000445007 | International Health Terminology Standards Development Organisation maintained module (core metadata concept) |
 * | 900000000000962010 | 900000000000446008 | Description type (core metadata concept)                                                                      |
 * | 900000000000964011 | 900000000000447004 | Case significance (core metadata concept)                                                                     |
 * | 900000000000967016 | 900000000000448009 | Entire term case insensitive (core metadata concept)                                                          |
 * | 900000000000969018 | 900000000000449001 | Characteristic type (core metadata concept)                                                                   |
 * | 900000000000972013 | 900000000000450001 | Modifier (core metadata concept)                                                                              |
 * | 900000000000975010 | 900000000000451002 | Existential restriction modifier (core metadata concept)                                                      |
 * | 900000000000978012 | 900000000000452009 | Universal restriction modifier (core metadata concept)                                                        |
 * | 900000000000980018 | 900000000000453004 | Identifier scheme (core metadata concept)                                                                     |
 * | 900000000001211010 | 900000000000550004 | Definition (core metadata concept)                                                                            |
 * +--------------------+--------------------+---------------------------------------------------------------------------------------------------------------+
 * 
 * @author henrikpettersen
 */
public class CoreMetadataConcepts {
    
    public static final long CHARACTERISTIC_TYPE_PARENT = 900000000000449001l;
    public static final long CASE_SIGNIFICANCE_PARENT = 900000000000447004l;
    public static final long DESCRIPTION_TYPE_PARENT = 900000000000446008l;
    public static final long DEFINITION_STATUS_PARENT = 900000000000444006l;
    public static final long MODULE_PARENT = 900000000000443000l;
    
    
    public static final long DESCRIPTION_TYPE_FULLY_SPECIFIED_NAME = 900000000000003001l;
    public static final long DESCRIPTION_TYPE_SYNONYM = 900000000000013009l;
    
    public static final long CONCEPT_DEFINITION_STATUS_SUFFICIENTLY_DEFINED = 900000000000073002l;
    public static final long CONCEPT_DEFINITION_STATUS_NESCESSARY_BUT_NOT_SUFFICIENT = 900000000000074008l;
    
    public static final long RELATIONSHIP_TYPE_DEFINING = 900000000000006009l;
    public static final long RELATIONSHIP_TYPE_STATED = 900000000000010007l;
    public static final long RELATIONSHIP_TYPE_INFERRED = 900000000000011006l;
    
    
    public static boolean isFullySpecifiedName(Concept c){
        return c.getSerialisedId() == DESCRIPTION_TYPE_FULLY_SPECIFIED_NAME;
    }

    public static boolean isSynonym(Concept c){
        return c.getSerialisedId() == DESCRIPTION_TYPE_FULLY_SPECIFIED_NAME;
    }    
    
    //This sucks, I give up...
//    private enum DescriptionType{
//        FULLY_SPECIFIED_NAME(900000000000003001l);
//        
//        private static Map<Long, DescriptionType> values = new HashMap<>();
//        static{
//            values.put(FULLY_SPECIFIED_NAME.getConceptSerialisedId(), FULLY_SPECIFIED_NAME);
//        }
//
//        long conceptSerialisedId;
//        
//        DescriptionType(long conceptSerialisedId){
//            this.conceptSerialisedId = conceptSerialisedId;
//        }
//        
//        public long getConceptSerialisedId(){
//            return conceptSerialisedId;
//        }
//        
//        public static DescriptionType getDescriptionType(long conceptSerialisedId){
//            return values.get(conceptSerialisedId);
//        }
//    }
//    

}
