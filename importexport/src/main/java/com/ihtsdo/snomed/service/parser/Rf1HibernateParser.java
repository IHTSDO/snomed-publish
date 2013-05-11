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
import java.util.Set;

import javassist.NotFoundException;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.InvalidInputException;

public class Rf1HibernateParser extends HibernateParser{
    static final Logger LOG = LoggerFactory.getLogger( Rf1HibernateParser.class );

    Rf1HibernateParser(){}
    
    public void setIgnoreInactive(boolean ignore){
        throw new InvalidInputException("RF1 Format does not specify active flag");
    }
    
    protected void populateConcepts(final InputStream stream, EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating concepts");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO CONCEPT (serialisedId, statusId, fullySpecifiedName, type, ctv3id, snomedId, primitive ,ontology_id, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
                    int currentLine = 1;
                    String line = null;
                    try {
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
                                ps.setBoolean(9, false); //otherwise mysql/jpa craps out when we retrieve concept
                                ps.addBatch();
                            } catch (NumberFormatException e) {
                                LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            } catch (IllegalArgumentException e){
                                LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            }
                            line = br.readLine();
                        }
                        ps.executeBatch();
                        LOG.info("Populated " + (currentLine - 1) + " concepts");
                    }finally {
                        br.close();
                    }
                } catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                } 
            }
        });
        tx.commit();
        setKindOfPredicate(em, ontology);
    }
    
    protected void populateConceptsFromStatements(final InputStream stream, EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating concepts");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO CONCEPT (serialisedId, ontology_id, primitive, statusId, active) VALUES (?, ?, ?, ?, ?)");
                Set<Concept> concepts = new HashSet<Concept>();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
                    int currentLine = 1;
                    String line = null;
                    try {
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
                                concepts.add(new Concept(Long.parseLong(splitIt.next()))); //CONCEPTID1
                                concepts.add(new Concept(Long.parseLong(splitIt.next()))); //RELATIONSHIPTYPE
                                concepts.add(new Concept(Long.parseLong(splitIt.next()))); //CONCEPTID2
                            } catch (NumberFormatException e) {
                                LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            } catch (IllegalArgumentException e){
                                LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            }
                            line = br.readLine();
                        }
                        for (Concept c : concepts){
                            ps.setLong(1, c.getSerialisedId());
                            ps.setLong(2, ontology.getId());
                            ps.setBoolean(3, false); //otherwise mysql/jpa craps out when we retrieve concept
                            ps.setInt(4, -1); //otherwise mysql/jpa craps out when we retrieve concept
                            ps.setBoolean(5, false); //otherwise mysql/jpa craps out when we retrieve concept
                            ps.addBatch();  
                        }
                        ps.executeBatch();
                        LOG.info("Populated " + concepts.size() + " concepts");
                    }finally {
                        br.close();
                    }
                } catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                } 
            }
        });
        tx.commit();
        setKindOfPredicate(em, ontology);
    }        

    protected void populateStatements(final InputStream stream, final EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating statements");
        final Map<Long, Long> map = createConceptSerialisedIdMapToDatabaseIdForOntology(ontology, em);
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();

        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psInsert = connection.prepareStatement("INSERT INTO STATEMENT (serialisedId, subject_id, predicate_id, object_id, characteristic_type, refinability, relationship_group, ontology_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
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
                                Long subject = new Long(splitIt.next());
                                if (!map.containsKey(subject)){
                                    throw new NotFoundException("Concept " + subject + " not found in concept definition for statement " +serialisedId );
                                }
                                psInsert.setLong(2, map.get(subject)); //subject
                            }
                            {
                                Long predicate = new Long(splitIt.next());                               
                                if (!map.containsKey(predicate)){
                                    throw new NotFoundException("Concept " + predicate + " not found in concept definition for statement " +serialisedId);
                                }
                                psInsert.setLong(3, map.get(predicate)); //predicate
                            }
                            {
                                Long object = new Long(splitIt.next());
                                if (!map.containsKey(object)){
                                    throw new NotFoundException("Concept " + object + " not found in concept definition for statements " +serialisedId );
                                }
                                psInsert.setLong(4, map.get(object)); //object
                            }
                            psInsert.setInt(5, Integer.parseInt(splitIt.next())); //characteristic type
                            psInsert.setInt(6, Integer.parseInt(splitIt.next())); //refinability
                            psInsert.setInt(7, Integer.parseInt(splitIt.next())); // group
                            psInsert.setLong(8, ontology.getId()); //ontology id
                            psInsert.addBatch();
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                        } catch (NullPointerException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Unable to recover, bailing out", e);
                            throw e;
                        } catch (NotFoundException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
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
