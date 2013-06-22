package com.ihtsdo.snomed.service.jena;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.InvalidInputException;

public class JenaImporter{

    
    private static final String NS_ONTOLOGY_VARIABLE = "__ONTOLOGY_ID__";
    private static final String NS_CONCEPT = "http://snomed.sparklingideas.co.uk/ontology/" + NS_ONTOLOGY_VARIABLE + "/concept/";
    private static final String NS_TRIPLE = "http://snomed.sparklingideas.co.uk/ontology/" + NS_ONTOLOGY_VARIABLE + "/statement/";
    private static final Logger LOG = LoggerFactory.getLogger(JenaImporter.class);


    public OntModel importJenaModel(Ontology o, EntityManager em){
        LOG.info("Importing ontology '{}' to rdfs", o.getName());
        OntModelSpec ontModelSpec = OntModelSpec.RDFS_MEM;
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        ontModelSpec.setReasoner(reasoner);

        //Model schemaModel = FileManager.get().loadModel("D:/Users/jim/Desktop/ontology/schema.rdf");
        //OntModel schema = ModelFactory.createOntologyModel(ontModelSpec);

        Location location = new Location("TDB");
        Dataset dataset = TDBFactory.createDataset(location);
        Model model = dataset.getDefaultModel();
        //Model model = ModelFactory.createDefaultModel();

        OntModel ontModel = ModelFactory.createOntologyModel(ontModelSpec, model);        
        TDB.sync(dataset);
        
        populateJenaModel(ontModel, o, em);
        
        ontModel.commit();
        return ontModel;
    }

    private void populateJenaModel(OntModel ontModel, Ontology o, EntityManager em) {
        List<Concept> concepts = em.createQuery("SELECT c FROM Concept c WHERE c.ontology.id=" + o.getId(), Concept.class).getResultList();
        
        OntProperty opEffectiveTime = ontModel.createOntProperty("http://snomed.sparklingideas.co.uk/term/effectiveTime");
        opEffectiveTime.setLabel("Effective time", "en-uk");
        
        OntProperty opActive = ontModel.createOntProperty("http://snomed.sparklingideas.co.uk/term/active");
        opActive.setLabel("Active", "en-uk");

        OntProperty opStatus = ontModel.createOntProperty("http://snomed.sparklingideas.co.uk/term/status");
        opStatus.setLabel("Status", "en-uk");
        
        OntProperty opModule = ontModel.createOntProperty("http://snomed.sparklingideas.co.uk/term/module");
        opModule.setLabel("Module", "en-uk");
        
        OntProperty opGroup = ontModel.createOntProperty("http://snomed.sparklingideas.co.uk/term/group");
        opGroup.setLabel("Group", "en-uk");
        
        OntProperty opCharacteristicType = ontModel.createOntProperty("http://snomed.sparklingideas.co.uk/term/characteristictype");
        opCharacteristicType.setLabel("Group", "en-uk");
        
        OntProperty opModifier = ontModel.createOntProperty("http://snomed.sparklingideas.co.uk/term/modifier");
        opModifier.setLabel("Group", "en-uk");        
        
        for (Concept c : concepts){
            if (c.isPredicate()){
                OntProperty opSubject = createJenaProperty(ontModel, opEffectiveTime, opActive, c, o);
                for (Statement statement : c.getSubjectOfStatements()){
                    if (!statement.isKindOfStatement()){
                        throw new InvalidInputException("A property can not have any other attributes than an IsA relationship: " + c.toString()); 
                    }
                    LOG.debug("Setting property [{}]'s parent to [{}]", getName(c.getSerialisedId(), o), getName(statement.getObject().getSerialisedId(), o));
                    opSubject.setSuperProperty(createJenaProperty(ontModel, opEffectiveTime, opActive, statement.getObject(), o));
                }
                setStatusAndModuleTriples(opSubject, c, ontModel, opEffectiveTime, opActive, opStatus, opModule, o);
            }
            else{
                OntClass ocSubject = createJenaClass(ontModel, opEffectiveTime, opActive, c, o);
                for (Statement statement : c.getSubjectOfStatements()){
                    if (statement.isKindOfStatement()){
                        LOG.debug("Setting class [{}]'s parent to [{}]", getName(c.getSerialisedId(), o), getName(statement.getObject().getSerialisedId(), o));
                        ocSubject.setSuperClass(createJenaClass(ontModel, opEffectiveTime, opActive, statement.getObject(), o));
                    }else{
                        LOG.debug("Adding triple ([{}], [{}], [{}])", getName(c.getSerialisedId(), o), getName(statement.getPredicate().getSerialisedId(), o), getName(statement.getObject().getSerialisedId(), o));
                        
                        createJenaStatement(ontModel, opEffectiveTime, opActive, opGroup, 
                                opModule, opCharacteristicType, opModifier, o, ocSubject, statement);
//                        ocSubject.setPropertyValue(
//                                createJenaProperty(ontModel, opEffectiveTime, opActive, statement.getPredicate(), o), 
//                                createJenaClass(ontModel, opEffectiveTime, opActive, statement.getObject(), o));
                    }
                }
                setStatusAndModuleTriples(ocSubject, c, ontModel, opEffectiveTime, opActive, opStatus, opModule, o);
            }
        }
    }

    private void createJenaStatement(OntModel ontModel, OntProperty opEffectiveTime, OntProperty opActive, 
            OntProperty opGroup, OntProperty opModule, OntProperty opCharacteristicType, OntProperty opModifier,
            Ontology o, OntClass ocSubject, Statement statement) 
    {
        ReifiedStatement rs = ontModel.createStatement(
                    ocSubject,
                    createJenaProperty(ontModel, opEffectiveTime, opActive, statement.getPredicate(), o), 
                    createJenaClass(ontModel, opEffectiveTime, opActive, statement.getObject(), o))
                .createReifiedStatement(getTripleName(statement.getSerialisedId(), o));
        
        rs.addLiteral(opEffectiveTime, statement.getEffectiveTime());
        rs.addLiteral(opActive, statement.isActive());
        rs.addLiteral(opGroup, statement.getGroup());
        
        rs.addProperty(opModule, 
                createJenaClass(ontModel, opEffectiveTime, opActive, statement.getModule(), o));
        rs.addProperty(opCharacteristicType, 
                createJenaClass(ontModel, opEffectiveTime, opActive, statement.getCharacteristicType(), o));
        rs.addProperty(opModifier, 
                createJenaClass(ontModel, opEffectiveTime, opActive, statement.getModifier(), o));
    }

    private void setStatusAndModuleTriples(OntResource subject, Concept c, OntModel ontModel,
            OntProperty opEffectiveTime, OntProperty opActive, OntProperty opStatus, 
            OntProperty opModule, Ontology o) 
    {
        subject.setPropertyValue(opStatus, 
                createJenaClass(ontModel, opEffectiveTime, opActive, c.getStatus(), o));
        subject.setPropertyValue(opModule, 
                createJenaClass(ontModel, opEffectiveTime, opActive, c.getModule(), o));
    }

    private OntClass createJenaClass(OntModel ontModel, OntProperty opEffectiveTime, 
            OntProperty opActive, Concept c, Ontology o) {
        LOG.debug("creating class '{}'", getName(c.getSerialisedId(), o));
        OntClass ocSubject = ontModel.createClass(getName(c.getSerialisedId(), o));
        ocSubject.setLabel(c.getFullySpecifiedName(), "en-uk");
        ocSubject.setPropertyValue(opEffectiveTime, ontModel.createTypedLiteral(c.getEffectiveTime()));
        ocSubject.setPropertyValue(opActive, ontModel.createTypedLiteral(c.isActive()));
        return ocSubject;
    }
    
    private OntProperty createJenaProperty(OntModel ontModel, 
            OntProperty opEffectiveTime, OntProperty opActive, Concept p, Ontology o) 
    {
        LOG.debug("creating property '{}'", getName(p.getSerialisedId(), o));
        OntProperty opPredicate = ontModel.createOntProperty(getName(p.getSerialisedId(), o));
        opPredicate.setLabel(p.getFullySpecifiedName(), "en-UK");
        opPredicate.setPropertyValue(opEffectiveTime, ontModel.createTypedLiteral(p.getEffectiveTime()));
        opPredicate.setPropertyValue(opActive, ontModel.createTypedLiteral(p.isActive()));
        return opPredicate;
    }    
    
    private String getName(long id, Ontology o){
        return NS_CONCEPT.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + 's' + id;
    }
    
    private String getTripleName(long id, Ontology o) {
        return NS_TRIPLE.replace(NS_ONTOLOGY_VARIABLE, Long.toString(o.getId())) + 's' + id;
    }
    
}
