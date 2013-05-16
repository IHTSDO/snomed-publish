package com.ihtsdo.snomed.service.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.InvalidInputException;

public class CanonicalHibernateParser extends HibernateParser{
    private static final Logger LOG = LoggerFactory.getLogger( CanonicalHibernateParser.class );

    private static final int DEFAULT_REFINABILITY = 0;
    private static final int DEFAULT_CHARACTERISTIC_TYPE = 0;

    CanonicalHibernateParser(){}
    
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
                PreparedStatement ps = connection.prepareStatement("INSERT INTO STATEMENT (serialisedid, subject_id, predicate_id, object_id, relationship_group, characteristic_type, refinability, ontology_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
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
                                ps.setLong(1, com.ihtsdo.snomed.model.Statement.SERIALISED_ID_NOT_DEFINED);
                                ps.setLong(2, map.get(Long.parseLong(splitIt.next()))); //subject
                                ps.setLong(3, map.get(Long.parseLong(splitIt.next())));//predicate
                                ps.setLong(4, map.get(Long.parseLong(splitIt.next()))); //object
                                ps.setInt(5, Integer.parseInt(splitIt.next()));
                                ps.setInt(6, DEFAULT_CHARACTERISTIC_TYPE);
                                ps.setInt(7,  DEFAULT_REFINABILITY);
                                ps.setLong(8, ontology.getId());
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
            Ontology ontology) throws IOException {
        throw new InvalidInputException("Not implemented yet");
    }

    @Override
    protected void populateConceptsFromStatements(InputStream stream,
            EntityManager em, Ontology ontology) throws IOException {
        throw new InvalidInputException("Not implemented yet");        
    }

    @Override
    protected void populateDescriptions(InputStream stream, EntityManager em,
            Ontology ontology) throws IOException {
        throw new InvalidInputException("Not implemented yet");        
    }
}
