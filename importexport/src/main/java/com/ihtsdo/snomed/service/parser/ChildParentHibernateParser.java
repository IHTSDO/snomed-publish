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

public class ChildParentHibernateParser extends HibernateParser{
    private static final Logger LOG = LoggerFactory.getLogger( ChildParentHibernateParser.class );

    ChildParentHibernateParser(){}
    
    protected void populateStatements(final InputStream stream, EntityManager em, 
            final Ontology ontology) throws IOException 
    {
        LOG.info("Populating statements");
        final Map<Long, Long> map = createConceptSerialisedIdToDatabaseIdMap(ontology, em);
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO STATEMENT (serialisedid, subject_id, predicate_id, object_id, groupId, characteristicTypeIdentifier, refinability, effectiveTime, active, ontology_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
                    int currentLine = 1;
                    String line = null;
                    try {
                        line = br.readLine();
                        long isAPredicateDatabaseId = map.get(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
                        while (line != null) {
                            currentLine++;
                            if (line.isEmpty()){
                                line = br.readLine();
                                continue;
                            }
                            Iterable<String> split = Splitter.on('\t').split(line);
                            Iterator<String> splitIt = split.iterator();
                            try {
                                ps.setLong(1, com.ihtsdo.snomed.model.Statement.SERIALISED_ID_NOT_DEFINED);
                                ps.setLong(2, map.get(Long.parseLong(splitIt.next()))); //subject
                                ps.setLong(3, isAPredicateDatabaseId);//predicate
                                ps.setLong(4, map.get(Long.parseLong(splitIt.next()))); //object
                                ps.setInt(5, 0); //group
                                ps.setInt(6, 0); //characteristic type
                                ps.setInt(7,  0); //refinability
                                ps.setInt(8, -1); //effectivetime
                                ps.setBoolean(9, true); //active
                                ps.setLong(10, ontology.getId());
                                ps.addBatch();
                            } catch (NumberFormatException e) {
                                LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            } catch (IllegalArgumentException e){
                                LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            }
                            line = br.readLine();
                        }
                        ps.executeBatch();
                        LOG.info("Populated " + (currentLine - 1) + " statements");
                    }finally {
                        br.close();
                    }
                } catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                } 
            }
        });
        tx.commit();
    }

    @Override
    protected void populateConcepts(InputStream stream, EntityManager em,
            final Ontology ontology) throws IOException {
        throw new InvalidInputException("Not implemented yet");
    }

    @Override
    protected void populateConceptsFromStatements(final InputStream stream,
            EntityManager em, final Ontology ontology) throws IOException 
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
                    String line = br.readLine();
                    while (line != null) {
                        currentLine++;
                        if (line.isEmpty()){
                            line = br.readLine();
                            continue;
                        }
                        Iterable<String> split = Splitter.on('\t').split(line);
                        Iterator<String> splitIt = split.iterator();
                        try {                            
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //child
                            concepts.add(new Concept(Long.parseLong(splitIt.next()))); //parent
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            if (parseMode.equals(Mode.STRICT)){throw e;}
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number " + currentLine + ". Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            if (parseMode.equals(Mode.STRICT)){throw e;}
                        }
                        line = br.readLine();
                    }
                    
                    for (Concept c : concepts){
                        ps.setLong(1, c.getSerialisedId());
                        ps.setLong(2, ontology.getId());
                        ps.setBoolean(3, true); //primitive
                        ps.setInt(4, -1); //otherwise mysql/jpa craps out when we retrieve concept
                        ps.setBoolean(5, true); //active
                        ps.setInt(6, -1);//effectivetime
                        ps.setInt(7, 1);//version
                        ps.addBatch();
                    }
                    
                    //Special case for isA concept
//                    ps.setLong(1, Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID);
//                    ps.setLong(2, ontology.getId());
//                    ps.setBoolean(3, true); //primitive
//                    ps.setInt(4, -1); //otherwise mysql/jpa craps out when we retrieve concept
//                    ps.setBoolean(5, true); //active
//                    ps.setInt(6, -1);//effectivetime
//                    ps.setInt(7, 1);//version
//                    ps.addBatch();
                    
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
    protected void populateDescriptions(InputStream stream, EntityManager em,
            Ontology ontology) throws IOException {
        throw new InvalidInputException("Not implemented yet");        
    }
}
