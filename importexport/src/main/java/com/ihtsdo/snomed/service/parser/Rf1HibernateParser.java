package com.ihtsdo.snomed.service.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

public class Rf1HibernateParser extends HibernateParser{
    static final Logger LOG = LoggerFactory.getLogger( Rf1HibernateParser.class );

    /**
     * Use the factory
     */
    Rf1HibernateParser(){}
    
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
                        "INSERT INTO DESCRIPTION (serialisedId, status, about_id, term, initialCapitalStatus, descriptionTypeId, languageCode, effectiveTime, active, ontology_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
                            psInsert.setInt(2, Integer.parseInt(splitIt.next())); //status
                            {
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for description " + serialisedId);
                                }
                                psInsert.setLong(3, conceptId);//about
                            }
                            psInsert.setString(4, splitIt.next());//term
                            psInsert.setInt(5, Integer.parseInt(splitIt.next())); //initial capital status
                            psInsert.setInt(6, Integer.parseInt(splitIt.next())); //description type id
                            psInsert.setString(7, splitIt.next());//language code
                            psInsert.setInt(8, DEFAULT_DESCRIPTION_EFFECTIVETIME); //description type id
                            psInsert.setBoolean(9, DEFAULT_DESCRIPTION_ACTIVE);
                            psInsert.setLong(10, ontology.getId());//ontologyid
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
    protected void populateConcepts(final InputStream stream, EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating concepts");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO Concept (serialisedId, statusId, fullySpecifiedName, type, ctv3id, snomedId, primitive ,ontology_id, active, version, effectiveTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                try (@SuppressWarnings("resource") BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
                    int currentLine = 1;
                    String line = br.readLine();
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
                            ps.setLong(1, Long.parseLong(splitIt.next())); //serialisedid
                            ps.setInt(2, Integer.parseInt(splitIt.next())); //status
                            String fsn = splitIt.next();
                            if (fsn.lastIndexOf(')') == -1){
                                ps.setString(3, fsn); //fsn
                                ps.setString(4, ""); //type
                            }else{
                                ps.setString(3, fsn.substring(0, fsn.lastIndexOf('(') - 1)); //fsn
                                ps.setString(4, fsn.trim().substring(fsn.trim().lastIndexOf('(') + 1, fsn.trim().length() - 1)); //type
                            }
                            ps.setString(5, splitIt.next()); //ctv3id
                            ps.setString(6, splitIt.next()); //snomedid
                            ps.setBoolean(7, stringToBoolean(splitIt.next())); // primitive
                            ps.setLong(8, ontology.getId()); //ontologyid
                            ps.setBoolean(9, DEFAULT_CONCEPT_ACTIVE);
                            ps.setInt(10, DEFAULT_VERSION);
                            ps.setInt(11, DEFAULT_CONCEPT_EFFECTIVE_TIME);
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
    }
    
    @Override
    protected void populateConceptsFromStatements(final InputStream stream, EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating concepts");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO Concept (serialisedId, ontology_id, primitive, statusId, active, version, effectiveTime) VALUES (?, ?, ?, ?, ?, ?, ?)");
                Set<Concept> concepts = new HashSet<Concept>();
                try (@SuppressWarnings("resource") BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
                    int currentLine = 1;
                    String line = br.readLine();
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
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //CONCEPTID1
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //RELATIONSHIPTYPE
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //CONCEPTID2
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
                        ps.setInt(6, DEFAULT_VERSION);
                        ps.setInt(7, DEFAULT_CONCEPT_EFFECTIVE_TIME);
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
    
    @Override
    protected void populateConceptsFromStatementsAndDescriptions(final InputStream statementsStream, final InputStream descriptionsStream, EntityManager em, final Ontology ontology) throws IOException {
        throw new UnsupportedOperationException("Not supported");
    }            

    @Override
    protected void populateStatements(final InputStream stream, final EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating statements");
        final Map<Long, Long> map = createConceptSerialisedIdToDatabaseIdMap(ontology, em);
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psInsert = connection.prepareStatement("INSERT INTO Statement (serialisedId, subject_id, predicate_id, object_id, characteristicTypeIdentifier, refinability, groupId, ontology_id, active, effectiveTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                try (@SuppressWarnings("resource") BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
                    int currentLine = 1;
                    String line = null;

                    line = br.readLine();
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
                            {
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for statement " + serialisedId);
                                }
                                psInsert.setLong(2, conceptId);//subject
                            }
                            {
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for statement " + serialisedId);
                                }
                                psInsert.setLong(3, conceptId);//predicate
                            }
                            {
                                String conceptSerialisedId = splitIt.next();
                                Long conceptId = map.get(new Long(conceptSerialisedId));
                                if (conceptId == null){
                                    throw new InvalidInputException("Concept " + conceptSerialisedId + " not found for statement " + serialisedId);
                                }
                                psInsert.setLong(4, conceptId);//object
                            }
                            psInsert.setInt(5, Integer.parseInt(splitIt.next())); //characteristic type
                            psInsert.setInt(6, Integer.parseInt(splitIt.next())); //refinability
                            psInsert.setInt(7, Integer.parseInt(splitIt.next())); // group
                            psInsert.setLong(8, ontology.getId()); //ontology id
                            psInsert.setBoolean(9, DEFAULT_STATEMENT_ACTIVE);//active
                            psInsert.setInt(10, DEFAULT_STATEMENT_EFFECTIVE_TIME); //effectiveTime
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
