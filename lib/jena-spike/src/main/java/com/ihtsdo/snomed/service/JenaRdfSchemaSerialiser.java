package com.ihtsdo.snomed.service;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;
import com.ihtsdo.snomed.exception.InvalidInputException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;

public class JenaRdfSchemaSerialiser{

    private final static String NS_SNOMED_BASE = "http://sct.snomed.info#";

    private static final String NS_ONTOLOGY_VARIABLE = "__ONTOLOGY_ID__";
    private static final String NS_CONCEPT = NS_SNOMED_BASE";
    private static final String NS_TRIPLE = NS_SNOMED_BASE";
    private static final String NS_DESCRIPTION = NS_SNOMED_BASE";
    private static final Logger LOG = LoggerFactory.getLogger(JenaRdfSchemaSerialiser.class);


    public OntModel importJenaModel(Ontology o, EntityManager em, File tdbFolder){
        LOG.info("Importing ontology '{}' to rdfs", o.getName());
        OntModelSpec ontModelSpec = OntModelSpec.RDFS_MEM;
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        
        reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
        
        ontModelSpec.setReasoner(reasoner);

//        Model schemaModel = FileManager.get().loadModel("D:/Users/jim/Desktop/ontology/schema.rdf");
//        OntModel schema = ModelFactory.createOntologyModel(ontModelSpec);
//        Location location = new Location(tdbFolder.getAbsolutePath());
//        Dataset dataset = TDBFactory.createDataset(location);
//        Model model = dataset.getDefaultModel();
//        Model model = ModelFactory.createDefaultModel();
//        TDB.sync(dataset);
//        ontModel.commit();
        
        OntModel ontModel = ModelFactory.createOntologyModel(ontModelSpec);        
        populateJenaModel(ontModel, o, em);
        return ontModel;
    }

    private void populateJenaModel(OntModel ontModel, Ontology o, EntityManager em) {
        LOG.info("Populating RDF Schema model");
        Stopwatch stopwatch = new Stopwatch().start();

        LOG.info("Loading concepts");
        TypedQuery<Concept> conceptsQuery = em.createQuery("SELECT c FROM Concept c " +
                "LEFT JOIN FETCH c.subjectOfStatements " +
                //"LEFT JOIN FETCH c.predicateOfStatements " + 
                //"LEFT JOIN FETCH c.kindOfs " + 
                //"LEFT JOIN FETCH c.objectOfStatements " + 
                //"LEFT JOIN FETCH c.parentOf " +
                "LEFT JOIN FETCH c.description " +
                "WHERE c.ontology.id=:ontologyId", 
                Concept.class);
        conceptsQuery.setParameter("ontologyId", o.getId());
        List<Concept> concepts = conceptsQuery.getResultList();
        LOG.info("Loaded " + concepts.size() + " concepts in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");

        OntProperty opEffectiveTime = ontModel.createOntProperty(NS_SNOMED_BASE + "effectiveTime");
        opEffectiveTime.setLabel("Effective time", "en-uk");
        
        OntProperty opActive = ontModel.createOntProperty(NS_SNOMED_BASE + "active");
        opActive.setLabel("Active", "en-uk");

        OntProperty opStatus = ontModel.createOntProperty(NS_SNOMED_BASE + "status");
        opStatus.setLabel("Status", "en-uk");
        
        OntProperty opModule = ontModel.createOntProperty(NS_SNOMED_BASE + "module");
        opModule.setLabel("Module", "en-uk");
        
        OntProperty opGroup = ontModel.createOntProperty(NS_SNOMED_BASE + "group");
        opGroup.setLabel("Group", "en-uk");
        
        OntProperty opCharacteristicType = ontModel.createOntProperty(NS_SNOMED_BASE + "characteristicType");
        opCharacteristicType.setLabel("CharacteristicType", "en-uk");
        
        OntProperty opModifier = ontModel.createOntProperty(NS_SNOMED_BASE + "modifier");
        opModifier.setLabel("Modifier", "en-uk");
        
        OntProperty opDescription = ontModel.createOntProperty(NS_SNOMED_BASE + "description");
        opModifier.setLabel("Decsription", "en-uk");
        
        OntProperty opCaseSignificance = ontModel.createOntProperty(NS_SNOMED_BASE + "caseSignificance");
        opModifier.setLabel("CaseSignificance", "en-uk"); 
        
        OntProperty opType = ontModel.createOntProperty(NS_SNOMED_BASE + "type");
        opModifier.setLabel("Type", "en-uk");
        
        int counter = 1;
        for (Concept c : concepts){
            if (c.isPredicate()){
                OntProperty opSubject = getJenaPropertyForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                        opDescription, opCaseSignificance, opType,  c, o);
                for (Statement statement : c.getSubjectOfStatements()){
                    if (!statement.isKindOfStatement()){
                        throw new InvalidInputException("A property can not have any other attributes than an IsA relationship: " + c.toString()); 
                    }
                    LOG.debug("Setting property [{}]'s parent to [{}]", getConceptName(c.getSerialisedId(), o), getConceptName(statement.getObject().getSerialisedId(), o));
                    opSubject.setSuperProperty(
                            getJenaPropertyForConcept(ontModel, opEffectiveTime, opActive,  opStatus, opModule, 
                                    opDescription, opCaseSignificance, opType, statement.getObject(), o));
                }
            }
            else{
                OntClass ocSubject = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                        opDescription, opCaseSignificance, opType, c, o);
                for (Statement statement : c.getSubjectOfStatements()){
                    if (statement.isKindOfStatement()){
                        LOG.debug("Setting class [{}]'s parent to [{}]", getConceptName(c.getSerialisedId(), o), getConceptName(statement.getObject().getSerialisedId(), o));
                        ocSubject.setSuperClass(getJenaClassForConcept(
                                ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                                opDescription, opCaseSignificance, opType, statement.getObject(), o));
                    }else{
                        LOG.debug("Creating triple ([{}], [{}], [{}])", getConceptName(c.getSerialisedId(), o), getConceptName(statement.getPredicate().getSerialisedId(), o), getConceptName(statement.getObject().getSerialisedId(), o));
                        createJenaStatement(ontModel, opEffectiveTime, opActive, opGroup, 
                                opModule, opCharacteristicType, opModifier, opStatus, 
                                opDescription, opCaseSignificance, opType,o, ocSubject, statement);
                    }
                }
            }
            //if (counter % 10000 == 0){
                LOG.info("Processed {} concepts", counter);
            //}
            counter++;
        }
        stopwatch.stop();
        LOG.info("Completed RDF Schema import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }


    private void createJenaStatement(OntModel ontModel, OntProperty opEffectiveTime, OntProperty opActive, 
            OntProperty opGroup, OntProperty opModule, OntProperty opCharacteristicType, OntProperty opModifier,
            OntProperty opStatus, OntProperty opDescription, OntProperty opCaseSignificance, OntProperty opType, 
            Ontology o, OntClass ocSubject, Statement statement) 
    {
        ReifiedStatement rs = ontModel.createStatement(
                    ocSubject,
                    getJenaPropertyForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                            opDescription, opCaseSignificance, opType, statement.getPredicate(), o), 
                            getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                            opDescription, opCaseSignificance, opType, statement.getObject(), o))
                .createReifiedStatement(getTripleName(statement.getSerialisedId(), o));
        
        rs.addLiteral(opEffectiveTime, statement.getEffectiveTime());
        rs.addLiteral(opActive, statement.isActive());
        rs.addLiteral(opGroup, statement.getGroupId());
        
        rs.addProperty(opModule, 
                getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                        opDescription, opCaseSignificance, opType, statement.getModule(), o));
        rs.addProperty(opCharacteristicType, 
                getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                        opDescription, opCaseSignificance, opType, statement.getCharacteristicType(), o));
        rs.addProperty(opModifier, 
                getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                        opDescription, opCaseSignificance, opType, statement.getModifier(), o));
    }
    
    private OntProperty getJenaPropertyForConcept(OntModel ontModel, OntProperty opEffectiveTime, 
            OntProperty opActive, OntProperty opStatus, OntProperty opModule, OntProperty opDescription, 
            OntProperty opCaseSignificance, OntProperty opType, Concept c, Ontology o) 
    {
//        return getJenaResourceForConcept(ontModel.getOntProperty(getConceptName(c.getSerialisedId(), o)), ontModel, opEffectiveTime, opActive, opStatus, 
//                opModule, opDescription, opCaseSignificance, opType, c, o).asProperty();
        
        String uri = getConceptName(c.getSerialisedId(), o);
        OntProperty orSubject = ontModel.getOntProperty(uri);
        
        if (orSubject != null){
            return orSubject;
        }

        LOG.debug("creating resource '{}'", uri);
        orSubject = ontModel.createOntProperty(uri);
        orSubject.setLabel(c.getFullySpecifiedName(), "en-uk");
        orSubject.setPropertyValue(opEffectiveTime, ontModel.createTypedLiteral(c.getEffectiveTime()));
        orSubject.setPropertyValue(opActive, ontModel.createTypedLiteral(c.isActive()));
    
        OntClass ocStatus = ontModel.getOntClass(getConceptName(c.getStatus().getSerialisedId(), o));
        if (ocStatus == null){
            ocStatus = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                    opDescription, opCaseSignificance, opType, c.getStatus(), o);
        }
        orSubject.setPropertyValue(opStatus, ocStatus);
        
        OntClass ocModule = ontModel.getOntClass(getConceptName(c.getModule().getSerialisedId(), o));
        if (ocModule == null){
            ocModule = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                    opDescription, opCaseSignificance, opType, c.getModule(), o);
        }
        orSubject.setPropertyValue(opModule, ocModule);
        
        createJenaClassForDescription(ontModel, c, orSubject, opDescription, opEffectiveTime, opActive,
                opCaseSignificance, opType, opModule, opStatus, o);
    
        return orSubject;   
        
    }
    
    private OntClass getJenaClassForConcept(OntModel ontModel, OntProperty opEffectiveTime, 
            OntProperty opActive, OntProperty opStatus, OntProperty opModule, OntProperty opDescription, 
            OntProperty opCaseSignificance, OntProperty opType, Concept c, Ontology o) 
    {
//        return getJenaResourceForConcept(ontModel.getOntClass(getConceptName(c.getSerialisedId(), o)), ontModel, opEffectiveTime, opActive, opStatus, 
//                opModule, opDescription, opCaseSignificance, opType, c, o).asClass();
        
        String uri = getConceptName(c.getSerialisedId(), o);
        OntClass orSubject = ontModel.getOntClass(uri);
        
        if (orSubject != null){
            return orSubject;
        }

        LOG.debug("creating resource '{}'", uri);
        orSubject = ontModel.createClass(uri);
        orSubject.setLabel(c.getFullySpecifiedName(), "en-uk");
        orSubject.setPropertyValue(opEffectiveTime, ontModel.createTypedLiteral(c.getEffectiveTime()));
        orSubject.setPropertyValue(opActive, ontModel.createTypedLiteral(c.isActive()));
    
        OntClass ocStatus = ontModel.getOntClass(getConceptName(c.getStatus().getSerialisedId(), o));
        if (ocStatus == null){
            ocStatus = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                    opDescription, opCaseSignificance, opType, c.getStatus(), o);
        }
        orSubject.setPropertyValue(opStatus, ocStatus);
        
        OntClass ocModule = ontModel.getOntClass(getConceptName(c.getModule().getSerialisedId(), o));
        if (ocModule == null){
            ocModule = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                    opDescription, opCaseSignificance, opType, c.getModule(), o);
        }
        orSubject.setPropertyValue(opModule, ocModule);
        
        createJenaClassForDescription(ontModel, c, orSubject, opDescription, opEffectiveTime, opActive,
                opCaseSignificance, opType, opModule, opStatus, o);
    
        return orSubject;        
    }    
    

//    private OntResource getJenaResourceForConcept(OntResource orSubject, OntModel ontModel, OntProperty opEffectiveTime, 
//            OntProperty opActive, OntProperty opStatus, OntProperty opModule, OntProperty opDescription, 
//            OntProperty opCaseSignificance, OntProperty opType, Concept c, Ontology o) 
//    {
//        String uri = getConceptName(c.getSerialisedId(), o);
//        
//        if (orSubject != null){
//            return orSubject;
//        }
//
//        LOG.debug("creating resource '{}'", uri);
//        orSubject = ontModel.createClass(uri);
//        orSubject.setLabel(c.getFullySpecifiedName(), "en-uk");
//        orSubject.setPropertyValue(opEffectiveTime, ontModel.createTypedLiteral(c.getEffectiveTime()));
//        orSubject.setPropertyValue(opActive, ontModel.createTypedLiteral(c.isActive()));
//    
//        OntClass ocStatus = ontModel.getOntClass(getConceptName(c.getStatus().getSerialisedId(), o));
//        if (ocStatus == null){
//            ocStatus = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
//                    opDescription, opCaseSignificance, opType, c.getStatus(), o);
//        }
//        orSubject.setPropertyValue(opStatus, ocStatus);
//        
//        OntClass ocModule = ontModel.getOntClass(getConceptName(c.getModule().getSerialisedId(), o));
//        if (ocModule == null){
//            ocModule = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
//                    opDescription, opCaseSignificance, opType, c.getModule(), o);
//        }
//        orSubject.setPropertyValue(opModule, ocModule);
//        
//        createJenaClassForDescription(ontModel, c, orSubject, opDescription, opEffectiveTime, opActive,
//                opCaseSignificance, opType, opModule, opStatus, o);
//    
//        return orSubject;
//    }

//    private OntProperty getJenaProperty(OntModel ontModel, OntProperty opEffectiveTime, 
//            OntProperty opActive, OntProperty opStatus, OntProperty opModule, OntProperty opDescription, 
//            OntProperty opCaseSignificance, OntProperty opType, Concept c, Ontology o) 
//    {
//        String uri = getConceptName(c.getSerialisedId(), o);
//        OntProperty opSubject = ontModel.getOntProperty(uri);
//        
//        if (opSubject != null){
//            return opSubject;
//        }
//        
//        LOG.debug("creating property '{}'", uri);
//        opSubject = ontModel.createOntProperty(uri);
//        opSubject.setLabel(c.getFullySpecifiedName(), "en-uk");
//        opSubject.setPropertyValue(opEffectiveTime, ontModel.createTypedLiteral(c.getEffectiveTime()));
//        opSubject.setPropertyValue(opActive, ontModel.createTypedLiteral(c.isActive()));
//
//        OntClass ocStatus = ontModel.getOntClass(getConceptName(c.getStatus().getSerialisedId(), o));
//        if (ocStatus == null){
//            ocStatus = getJenaResource(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
//                    opDescription, opCaseSignificance, opType, c.getStatus(), o);
//        }
//        opSubject.setPropertyValue(opStatus, ocStatus);
//        
//        OntClass ocModule = ontModel.getOntClass(getConceptName(c.getModule().getSerialisedId(), o));
//        if (ocModule == null){
//            ocModule = getJenaResource(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
//                    opDescription, opCaseSignificance, opType, c.getModule(), o);
//        }
//        opSubject.setPropertyValue(opModule, ocModule);
//        
//        createJenaClassForDescription(ontModel, c, opSubject, opDescription, opEffectiveTime, opActive,
//                opCaseSignificance, opType, opModule, opStatus, o);
//    
//        return opSubject;
//    }

    private void createJenaClassForDescription(OntModel ontModel, Concept c, OntResource subject, OntProperty opDescription, 
            OntProperty opEffectiveTime, OntProperty opActive, OntProperty opCaseSignificance, 
            OntProperty opType, OntProperty opModule, OntProperty opStatus, Ontology o)
    {
        for (Description d : c.getDescription()){
            String uri = getDescriptionName(d.getSerialisedId(), o);
            
            LOG.debug("creating description '{}'", uri);
            OntClass ontDesc = ontModel.createClass(uri);
            ontDesc.setLabel(d.getTerm(), d.getLanguageCode());
            ontDesc.setPropertyValue(opEffectiveTime, ontModel.createTypedLiteral(d.getEffectiveTime()));
            ontDesc.setPropertyValue(opActive, ontModel.createTypedLiteral(d.isActive()));
            
            OntClass ocCaseSignificance = ontModel.getOntClass(getConceptName(d.getCaseSignificance().getSerialisedId(), o)); 
            if (ocCaseSignificance == null){
                ocCaseSignificance = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                        opDescription, opCaseSignificance, opType, d.getCaseSignificance(), o);
            }
            ontDesc.setPropertyValue(opCaseSignificance, ocCaseSignificance);
            
            OntClass ocType = ontModel.getOntClass(getConceptName(d.getType().getSerialisedId(), o));
            if (ocType == null){
                ocType = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                        opDescription, opCaseSignificance, opType, d.getType(), o);
            }
            ontDesc.setPropertyValue(opType, ocType);
            
            OntClass ocModule = ontModel.getOntClass(getConceptName(d.getModule().getSerialisedId(), o));
            if (ocModule == null){
                ocModule = getJenaClassForConcept(ontModel, opEffectiveTime, opActive, opStatus, opModule, 
                        opDescription, opCaseSignificance, opType, d.getModule(), o);
            }
            ontDesc.setPropertyValue(opModule, ocModule);

            subject.addProperty(opDescription, ontDesc);
        }
    }      
    
    private String getDescriptionName(long id, Ontology o){
        return NS_DESCRIPTION.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + 'd' + id;
    }

    private String getConceptName(long id, Ontology o){
        return NS_CONCEPT.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + 'c' + id;
    }
    
    private String getTripleName(long id, Ontology o) {
        return NS_TRIPLE.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + 's' + id;
    }   
}
