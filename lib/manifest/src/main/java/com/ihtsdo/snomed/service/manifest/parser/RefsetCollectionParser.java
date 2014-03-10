package com.ihtsdo.snomed.service.manifest.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.CacheMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.ihtsdo.snomed.exception.InvalidInputException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.service.manifest.model.BaseRefsetCollection;
import com.ihtsdo.snomed.service.manifest.model.Refset;
import com.ihtsdo.snomed.service.manifest.model.RefsetModule;

@Named
public class RefsetCollectionParser {
    private static final Logger LOG = LoggerFactory.getLogger( RefsetCollectionParser.class );
        
    private Map<Long, String> conceptMap; 
    protected Mode parseMode = Mode.FORGIVING;
        
    public enum Mode{
        STRICT, FORGIVING
    }
    
    @PostConstruct
    public void init(){

    }
    
    public RefsetCollectionParser setParseMode(Mode parseMode){
        this.parseMode = parseMode;
        return this;
    }
    

    public void buildCache(EntityManager em, OntologyVersion o) {
        conceptMap = new HashMap<>();
        TypedQuery<Concept> conceptQuery = em.createQuery("SELECT c FROM Concept c " +
                //"LEFT JOIN FETCH c.description " +
                "WHERE c.ontology.id=:ontologyId", 
                Concept.class);
        conceptQuery.setParameter("ontologyId", o.getId());
        List<Concept> concepts = conceptQuery.getResultList();
        for (Concept concept : concepts){
            conceptMap.put(concept.getSerialisedId(), concept.getFullySpecifiedName());
        }
    }    
    
   
    //@Transactional
    protected void parseFirstFourItems(String line, int lineNumber, 
            BaseRefsetCollection refsetCollection, OntologyVersion o, TypedQuery<Concept> getConceptQuery)
    {
        Iterable<String> split = Splitter.on('\t').split(line);
        Iterator<String> splitIt = split.iterator();
        try {
            splitIt.next(); //id
            splitIt.next(); //effectivetime
            splitIt.next(); //active
            
            //module id;
            long moduleId = Long.parseLong(splitIt.next()); //module id
            RefsetModule module = refsetCollection.getModule(moduleId);
            if (module == null){
//                if (conceptMap != null){
//                    module = new RefsetModule(getConceptQuery.setParameter("serialisedId", moduleId).getSingleResult());
//                }
                module = new RefsetModule(getConceptQuery.setParameter("serialisedId", moduleId).getSingleResult());
                refsetCollection.addModule(module);
            }
            
            //refset id
            long refsetId = Long.parseLong(splitIt.next());
            Refset refset = module.getRefset(refsetId);
            if (refset == null){
                refset = new Refset(getConceptQuery.setParameter("serialisedId", refsetId).getSingleResult());
                module.addRefset(refset);
            }

        } catch (NumberFormatException e) {
            LOG.error("Unable to parse line number " + lineNumber + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
        } catch (IllegalArgumentException e){
            LOG.error("Unable to parse line number " + lineNumber + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
        }catch (NoSuchElementException e){
            LOG.error("Unable to parse line number " + lineNumber + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
        }catch (NoResultException e){
            LOG.error("Unable to parse line number " + lineNumber + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
        }
    }
    
    //@Transactional
    public BaseRefsetCollection parse(InputStream iStream, OntologyVersion ov, BaseRefsetCollection refsetCollection, EntityManager em){
        //EntityManager em = emf.createEntityManager();
        
        TypedQuery<Concept> getConceptQuery = em.createQuery("SELECT c FROM Concept c " +
                //"LEFT JOIN FETCH c.description " +
                "WHERE c.serialisedId=:serialisedId AND c.ontologyVersion.id=:ontologyVersionId", 
                Concept.class);
        getConceptQuery.setHint("org.hibernate.cacheable", Boolean.TRUE);
        getConceptQuery.setHint("org.hibernate.readOnly", Boolean.TRUE);
        getConceptQuery.setHint("org.hibernate.cacheMode", CacheMode.GET);
        
        getConceptQuery.setParameter("ontologyVersionId", ov.getId());
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(iStream))){
            int currentLine = 1;
            String line = null;
            line = br.readLine();
            //skip the headers
            line = br.readLine();
            while (line != null) {
                currentLine++;
                if (line.isEmpty()){
                    line = br.readLine();
                    continue;
                }
                parseFirstFourItems(line, currentLine, refsetCollection, ov, getConceptQuery);
                line = br.readLine();
            }
            LOG.info("Parsed " + (currentLine - 1) + " lines");
        } catch (IOException e1) {
            LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
        }
        
        return refsetCollection;
    }

}
