package com.ihtsdo.snomed.service.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.InvalidInputException;

public class Rf2HibernateParser extends HibernateParser{
    private static final Logger LOG = LoggerFactory.getLogger( Rf2HibernateParser.class );
    
    /**
     * Use factory
     */
    Rf2HibernateParser(){}
    
    @Override
    protected void populateConcepts(final InputStream stream, EntityManager em, final Ontology ontology) throws IOException{
        LOG.info("Populating concepts");
        Stopwatch stopwatch = new Stopwatch().start();
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        final Map<Long, Long> conceptIdToModuleIdMap = new HashMap<Long, Long>();
        final Map<Long, Long> conceptIdToDefinitionStatusIdMap = new HashMap<Long, Long>();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO CONCEPT (serialisedId, effectiveTime, active, primitive, statusId, version, ontology_id) VALUES (?, ?, ?, ?, ?, ?, ?)");
                try (@SuppressWarnings("resource") BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
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
                        Iterable<String> split = Splitter.on('\t').split(line);
                        Iterator<String> splitIt = split.iterator();
                        try {
                            Long serialisedId = new Long(splitIt.next());
                            ps.setLong(1, serialisedId); //serialisedid
                            ps.setInt(2, Integer.parseInt(splitIt.next())); //effectiveTime
                            ps.setBoolean(3, stringToBoolean(splitIt.next())); // active
                            conceptIdToModuleIdMap.put(serialisedId, Long.parseLong(splitIt.next()));
                            conceptIdToDefinitionStatusIdMap.put(serialisedId, Long.parseLong(splitIt.next()));
                            ps.setBoolean(4, DEFAULT_CONCEPT_PRIMITIVE);
                            ps.setInt(5, DEFAULT_CONCEPT_STATUS_ID);
                            ps.setInt(6, DEFAULT_VERSION);
                            ps.setLong(7, ontology.getId());
                            ps.addBatch();
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        }
                        line = br.readLine();
                    }
                    ps.executeBatch();
                    LOG.info("Populated " + (currentLine - 1) + " concepts");
                } catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                } 
            }
        });
        tx.commit();
        setKindOfPredicate(em, ontology);
        populateModuleAndDefinitionStatus(em, ontology, conceptIdToModuleIdMap, conceptIdToDefinitionStatusIdMap);        
        stopwatch.stop();
        LOG.info("Completed concepts import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
    }
    
    private void populateModuleAndDefinitionStatus(EntityManager em, final Ontology ontology, 
            final Map<Long, Long> conceptIdToModuleIdMap, final Map<Long, Long> conceptIdToDefinitionStatusIdMap){
        final Map<Long, Long> map = createConceptSerialisedIdToDatabaseIdMap(ontology, em);
        LOG.debug("{} concepts to update", map.keySet().size());
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("UPDATE CONCEPT SET module_id=?, status_id=? WHERE id=? AND ontology_id=?");
                for (long serialisedId : map.keySet()){
                    try{
                        ps.setLong(1, map.get(conceptIdToModuleIdMap.get(serialisedId)));
                        ps.setLong(2, map.get(conceptIdToDefinitionStatusIdMap.get(serialisedId)));
                        ps.setLong(3, map.get(serialisedId));
                        ps.setLong(4, ontology.getId());
                        ps.addBatch();
                    }
                    catch (Exception e){
                        LOG.error("Problem populating module and definition status for description {}", serialisedId, e);
                        if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                    }
                }
                ps.executeBatch();
            }
        });
        tx.commit();
    }
    
    @Override
    protected void populateDescriptions(final InputStream stream, final EntityManager em, 
            final Ontology ontology) throws IOException
    {
        LOG.info("Populating descriptions");
        Stopwatch stopwatch = new Stopwatch().start();
        final Map<Long, Long> map = createConceptSerialisedIdToDatabaseIdMap(ontology, em);
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psInsert = connection.prepareStatement(
                        "INSERT INTO DESCRIPTION (serialisedId, effectiveTime, active, module_id, about_id, languageCode, type_id, term, caseSignificance_id, initialCapitalStatus, status, descriptionTypeId, ontology_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                try (@SuppressWarnings("resource") BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
                    int currentLine = 1;
                    String line = br.readLine();
                    //skip the headers
                    line = br.readLine();
                    while (line != null) {
                        if (line.isEmpty()){
                            line = br.readLine();
                            continue;
                        }
                        Iterable<String> split = Splitter.on('\t').trimResults().split(line);
                        Iterator<String> splitIt = split.iterator();
                        try {
                            long serialisedId = Long.parseLong(splitIt.next());
                            psInsert.setLong(1, serialisedId); //serialisedid
                            psInsert.setInt(2, Integer.parseInt(splitIt.next())); //effectivetime
                            psInsert.setBoolean(3, stringToBoolean(splitIt.next())); //active
                            {
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for description " + serialisedId);
                                }
                                psInsert.setLong(4, conceptId);//module
                            }
                            {
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for description " + serialisedId);
                                }
                                psInsert.setLong(5, conceptId);//about,concept
                            }                            
                            psInsert.setString(6, splitIt.next()); //languagecode
                            {
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for description " + serialisedId);
                                }
                                psInsert.setLong(7, conceptId);//type
                            }
                            psInsert.setString(8, splitIt.next()); //term
                            {
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for description " + serialisedId);
                                }
                                psInsert.setLong(9, conceptId);//casesignificanceid
                            }
                            psInsert.setInt(10, DEFAULT_DESCRIPTION_INITIAL_CAPITAL_STATUS);
                            psInsert.setInt(11, DEFAULT_DESCRIPTION_STATUS);
                            psInsert.setInt(12, DEFAULT_DESCRIPTION_TYPE_ID);
                            psInsert.setLong(13, ontology.getId());//ontologyid
                            psInsert.addBatch();
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (NullPointerException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (InvalidInputException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw e;}
                        } catch (NoSuchElementException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } 
                        line = br.readLine();
                        currentLine++;
                    }
                    psInsert.executeBatch();
                    LOG.info("Populated " + (currentLine - 1) + " descriptions");                    
                }catch (IOException e){
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e.getMessage(), e);
                }
            }
        });
        tx.commit(); 
        stopwatch.stop();
        LOG.info("Completed descriptions import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");        
    }

    @Override
    protected void populateConceptsFromStatements(final InputStream stream, EntityManager em, 
            final Ontology ontology) throws IOException 
    {
        LOG.info("Populating concepts");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO CONCEPT (serialisedId, ontology_id, primitive, statusId, active, effectiveTime, version) VALUES (?, ?, ?, ?, ?, ?, ?)");
                Set<Concept> concepts = new HashSet<Concept>();
                try (@SuppressWarnings("resource") BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
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
                        Iterable<String> split = Splitter.on('\t').split(line);
                        Iterator<String> splitIt = split.iterator();
                        try {                            
                            splitIt.next(); //id
                            splitIt.next(); //effective time
                            splitIt.next(); //active
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //moduleId
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //sourceId
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //destinationId
                            splitIt.next(); //group
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //typeId
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //characteristicType
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //modifier
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        }
                        line = br.readLine();
                    }
                    for (Concept c : concepts){
                        ps.setLong(1, c.getSerialisedId());
                        ps.setLong(2, ontology.getId());
                        ps.setBoolean(3, DEFAULT_CONCEPT_PRIMITIVE);
                        ps.setInt(4, DEFAULT_CONCEPT_STATUS_ID);
                        ps.setBoolean(5, DEFAULT_CONCEPT_ACTIVE);
                        ps.setInt(6, DEFAULT_CONCEPT_EFFECTIVE_TIME);
                        ps.setInt(7, DEFAULT_VERSION);
                        ps.addBatch();
                        
                    }
                    ps.executeBatch();
                    LOG.info("Populated " + concepts.size() + " concepts");
                } catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                } 
            }
        });
        tx.commit();
        setKindOfPredicate(em, ontology);
    }
    
    protected void populateConceptsFromStatementsAndDescriptions(final InputStream statementsStream, 
            final InputStream descriptionsStream, EntityManager em, final Ontology ontology) throws IOException
    {
        LOG.info("Populating concepts");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO CONCEPT (serialisedId, ontology_id, primitive, statusId, active, effectiveTime, version) VALUES (?, ?, ?, ?, ?, ?, ?)");
                Set<Concept> concepts = new HashSet<Concept>();
                try (@SuppressWarnings("resource") BufferedReader brc = new BufferedReader(new InputStreamReader(statementsStream))){
                    int currentLine = 1;
                    String line = null;
                    line = brc.readLine();
                    //skip the headers
                    line = brc.readLine();
                    while (line != null) {
                        currentLine++;
                        if (line.isEmpty()){
                            line = brc.readLine();
                            continue;
                        }
                        Iterable<String> split = Splitter.on('\t').split(line);
                        Iterator<String> splitIt = split.iterator();
                        try {
                            splitIt.next(); //id
                            splitIt.next(); //effective time
                            splitIt.next(); //active
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //moduleId
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //sourceId
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //destinationId
                            splitIt.next(); //group
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //typeId
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //characteristicType
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //modifier
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        }
                        line = brc.readLine();
                    }
                }catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                } 
                try (@SuppressWarnings("resource") BufferedReader brd = new BufferedReader(new InputStreamReader(descriptionsStream))){
                    int currentLine = 1;
                    String line = null;
                    line = brd.readLine();
                    //skip the headers
                    line = brd.readLine();
                    while (line != null) {
                        currentLine++;
                        if (line.isEmpty()){
                            line = brd.readLine();
                            continue;
                        }
                        Iterable<String> split = Splitter.on('\t').split(line);
                        Iterator<String> splitIt = split.iterator();
                        try {                            
                            splitIt.next(); //id
                            splitIt.next(); //effective time
                            splitIt.next(); //active
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //module
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //about
                            splitIt.next(); //languagecode
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //type
                            splitIt.next(); //term
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //case significance
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        }
                        line = brd.readLine();
                    }                    
                } catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                } 
                for (Concept c : concepts){
                    ps.setLong(1, c.getSerialisedId());
                    ps.setLong(2, ontology.getId());
                    ps.setBoolean(3, DEFAULT_CONCEPT_PRIMITIVE);
                    ps.setInt(4, DEFAULT_CONCEPT_STATUS_ID);
                    ps.setBoolean(5, DEFAULT_CONCEPT_ACTIVE);
                    ps.setInt(6, DEFAULT_CONCEPT_EFFECTIVE_TIME);
                    ps.setInt(7, DEFAULT_VERSION);
                    ps.addBatch();
                    
                }
                ps.executeBatch();
                LOG.info("Populated " + concepts.size() + " concepts");
            }
        });
        tx.commit();
        setKindOfPredicate(em, ontology);
    }
    
    
    @Override
    protected void populateStatements(final InputStream stream, final EntityManager em, 
            final Ontology ontology) throws IOException 
    {
        LOG.info("Populating statements");
        final Map<Long, Long> map = createConceptSerialisedIdToDatabaseIdMap(ontology, em);
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psInsert = connection.prepareStatement(
                        "INSERT INTO STATEMENT (serialisedId, effectiveTime, active, module_id, subject_id, object_id, groupId, predicate_id, characteristicType_id, modifier_id, characteristicTypeIdentifier, refinability, ontology_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                try (@SuppressWarnings("resource") BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
                    int currentLine = 1;
                    String line = br.readLine();
                    //skip the headers
                    line = br.readLine();
                    while (line != null) {
                        if (line.isEmpty()){
                            line = br.readLine();
                            continue;
                        }
                        Iterable<String> split = Splitter.on('\t').trimResults().split(line);
                        Iterator<String> splitIt = split.iterator();
                        try {
                            long serialisedId = Long.parseLong(splitIt.next());
                            psInsert.setLong(1, serialisedId); // serialised id
                            psInsert.setLong(2, Long.parseLong(splitIt.next()));//effectiveTime
                            psInsert.setBoolean(3, stringToBoolean(splitIt.next()));//active
                            {//module
                                Long concept = map.get(Long.parseLong(splitIt.next()));
                                if (concept == null){
                                    throw new InvalidInputException("Concept " + concept + " not found for statement " +serialisedId);
                                }
                                psInsert.setLong(4, concept);
                            }
                            {//subject
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for statement " +serialisedId);
                                }
                                psInsert.setLong(5, conceptId);
                            }
                            {//object
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for statement " +serialisedId);
                                }
                                psInsert.setLong(6, conceptId);
                            }
                            psInsert.setInt(7, Integer.parseInt(splitIt.next()));//groupid
                            {//type
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for statement " +serialisedId);
                                }
                                psInsert.setLong(8, conceptId);
                            }
                            {//characteristicType
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for statement " +serialisedId);
                                }
                                psInsert.setLong(9, conceptId);
                            }
                            {//modifier
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for statement " +serialisedId);
                                }
                                psInsert.setLong(10, conceptId);
                            }                            
                            psInsert.setInt(11, DEFAULT_STATEMENT_CHARACTERISTIC_TYPE_IDENTIFIER);
                            psInsert.setInt(12, DEFAULT_STATEMENT_REFINABILITY);
                            psInsert.setLong(13, ontology.getId());
                            psInsert.addBatch();
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (NullPointerException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Unable to recover, bailing out", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        } catch (InvalidInputException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]", e);
                            if (parseMode.equals(Mode.STRICT)){throw new InvalidInputException(e);}
                        }
                        line = br.readLine();
                        currentLine++;
                    }
                    psInsert.executeBatch();
                    LOG.info("Populated " + (currentLine - 1) + " statements");
                } 
                catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                }
            }
        });
        tx.commit();
    }
}
