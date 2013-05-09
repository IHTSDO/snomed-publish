package com.ihtsdo.snomed.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javassist.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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

public class HibernateDbImporter {
    private static final Logger LOG = LoggerFactory.getLogger( HibernateDbImporter.class );

    public static final String ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML = "persistenceManager";
    private static final int DEFAULT_REFINABILITY = 0;
    private static final int DEFAULT_CHARACTERISTIC_TYPE = 0;

    public Ontology populateDbFromLongForm(String ontologyName, InputStream conceptsStream, 
            InputStream statementsStream, EntityManager em) throws IOException
    {
    	LOG.info("Importing ontology \"" + ontologyName + "\"");
    	Stopwatch stopwatch = new Stopwatch().start();
        
        Ontology ontology = createOntology(em, ontologyName);
        populateConcepts(conceptsStream, em, ontology);
        populateLongFormStatements(statementsStream, em, ontology);
        createIsKindOfHierarchy(em, ontology);
        
        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return em.find(Ontology.class, ontology.getId());
    }

    public Ontology populateDbFromShortForm(String ontologyName, InputStream conceptsStream, 
            InputStream statementsStream, EntityManager em) throws IOException
    {
        LOG.info("Importing ontology \"" + ontologyName + "\"");
        Stopwatch stopwatch = new Stopwatch().start();
        
        Ontology ontology = createOntology(em, ontologyName);
        populateConcepts(conceptsStream, em, ontology);
        populateShortFormStatements(statementsStream, em, ontology);        
        createIsKindOfHierarchy(em, ontology);
        
        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return em.find(Ontology.class, ontology.getId());
    }
    
    public Ontology populateDbFromRf2(String ontologyName, InputStream statementsStream, 
            InputStream statementsStreamAgain, EntityManager em) throws IOException
    {
        LOG.info("Importing ontology \"" + ontologyName + "\"");
        Stopwatch stopwatch = new Stopwatch().start();
        
        Ontology ontology = createOntology(em, ontologyName);
        populateConceptsFromRf2(statementsStream, em, ontology);
        populateStatementsFromRf2(statementsStreamAgain, em, ontology);        
        createIsKindOfHierarchy(em, ontology);
        
        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return em.find(Ontology.class, ontology.getId());
    }  

    protected Ontology createOntology(EntityManager em, final String name) throws IOException {
        final Ontology ontology = new Ontology();
        ontology.setName(name);
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO ONTOLOGY (NAME) values (?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, name);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating ontology failed, no rows affected.");
                }
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ontology.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating ontology failed, no generated key obtained.");
                }
            }
        });
        tx.commit();
        return ontology;
    }          

    protected void populateConcepts(final InputStream stream, EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating concepts");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO CONCEPT (serialisedId, status, fullySpecifiedName, type, ctv3id, snomedId, primitive ,ontology_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
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
                                ps.addBatch();
                            } catch (NumberFormatException e) {
                                LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            } catch (IllegalArgumentException e){
                                LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            }
                            line = br.readLine();
                        }
                        ps.executeBatch();
                        LOG.info("Populated [" + (currentLine - 1) + "] concepts");
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
    
    protected void populateConceptsFromRf2(final InputStream stream, EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating concepts");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO CONCEPT (serialisedId, ontology_id, primitive, status) VALUES (?, ?, ?, ?)");
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
                                splitIt.next(); //effective time
                                splitIt.next(); //active
                                splitIt.next(); //moduleId
                                concepts.add(new Concept(Long.parseLong(splitIt.next()))); //sourceId
                                concepts.add(new Concept(Long.parseLong(splitIt.next()))); //destinationId
                                splitIt.next(); //group
                                concepts.add(new Concept(Long.parseLong(splitIt.next()))); //typeId
                            } catch (NumberFormatException e) {
                                LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            } catch (IllegalArgumentException e){
                                LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            }
                            line = br.readLine();
                        }
                        for (Concept c : concepts){
                            ps.setLong(1, c.getSerialisedId());
                            ps.setLong(2, ontology.getId());
                            ps.setBoolean(3, false); //otherwise mysql/jpa craps out when we retrieve concept
                            ps.setInt(4, -1); //otherwise mysql/jpa craps out when we retrieve concept
                            ps.addBatch();
                            
                        }
                        ps.executeBatch();
                        LOG.info("Populated [" + (currentLine - 1) + "] concepts");
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

    protected void setKindOfPredicate(EntityManager em, Ontology o) throws IllegalStateException{
        try {
            Concept predicate = em.createQuery("SELECT c FROM Concept c WHERE c.serialisedId=" + Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID + " AND c.ontology.id=" + o.getId(), Concept.class).getSingleResult();
            o.setIsKindOfPredicate(predicate);
        } catch (NoResultException e) {
            throw new IllegalStateException("The isA predicate does not exist in this ontology at this point");
        }
    }

    protected void populateLongFormStatements(final InputStream stream, final EntityManager em, final Ontology ontology) throws IOException {
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
                                    throw new NotFoundException("Concept [" + subject + "] not found in concept definition for statements [" +serialisedId + "]");
                                }
                                psInsert.setLong(2, map.get(subject)); //subject
                            }
                            {
                                Long predicate = new Long(splitIt.next());                               
                                if (!map.containsKey(predicate)){
                                    throw new NotFoundException("Concept [" + predicate + "] not found in concept definition for statements [" +serialisedId + "]" );
                                }
                                psInsert.setLong(3, map.get(predicate)); //predicate
                            }
                            {
                                Long object = new Long(splitIt.next());
                                if (!map.containsKey(object)){
                                    throw new NotFoundException("Concept [" + object + "] not found in concept definition for statements [" +serialisedId + "]" );
                                }
                                psInsert.setLong(4, map.get(object)); //object
                            }
                            psInsert.setInt(5, Integer.parseInt(splitIt.next())); //characteristic type
                            psInsert.setInt(6, Integer.parseInt(splitIt.next())); //refinability
                            psInsert.setInt(7, Integer.parseInt(splitIt.next())); // group
                            psInsert.setLong(8, ontology.getId()); //ontology id
                            psInsert.addBatch();
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                        } catch (NullPointerException e){
                            LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Unable to recover, bailing out", e);
                            throw e;
                        } catch (NotFoundException e){
                            LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                        }
                        line = br.readLine();
                        currentLine++;
                    }
                    psInsert.executeBatch();
                    LOG.info("Populated [" + (currentLine - 1) + "] statements");
                } 
                catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                }
            }
        });
        tx.commit();
    }
    
    protected void populateStatementsFromRf2(final InputStream stream, final EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating statements");
        final Map<Long, Long> map = createConceptSerialisedIdMapToDatabaseIdForOntology(ontology, em);
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();

        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psInsert = connection.prepareStatement(
                        "INSERT INTO STATEMENT (serialisedId, subject_id, object_id, relationship_group, predicate_id, characteristic_type, refinability, ontology_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                
                //new: effectiveTime, active, moduleId, modifierId
                
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
                            splitIt.next();//effectiveTime
                            splitIt.next();//active
                            splitIt.next();//moduleid
                            {
                                Long subject = new Long(splitIt.next());
                                if (!map.containsKey(subject)){
                                    throw new NotFoundException("Concept [" + subject + "] not found in concept definition for statements [" +serialisedId + "]");
                                }
                                psInsert.setLong(2, map.get(subject)); //subject
                            }
                            {
                                Long object = new Long(splitIt.next());
                                if (!map.containsKey(object)){
                                    throw new NotFoundException("Concept [" + object + "] not found in concept definition for statements [" +serialisedId + "]" );
                                }
                                psInsert.setLong(3, map.get(object)); //object
                            }
                            psInsert.setInt(4, Integer.parseInt(splitIt.next())); // group
                            {
                                Long predicate = new Long(splitIt.next());                               
                                if (!map.containsKey(predicate)){
                                    throw new NotFoundException("Concept [" + predicate + "] not found in concept definition for statements [" +serialisedId + "]" );
                                }
                                psInsert.setLong(5, map.get(predicate)); //predicate
                            }
                            psInsert.setInt(6, -1); //characteristic type, otherwise JPA 2 craps out when retrieving
                            psInsert.setInt(7, -1); //refinability, otherwise JPA 2 craps out when retrieving
                            psInsert.setLong(8, ontology.getId()); //ontology id
                            psInsert.addBatch();
                        } catch (NumberFormatException e) {
                            LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                        } catch (IllegalArgumentException e){
                            LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                        } catch (NullPointerException e){
                            LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Unable to recover, bailing out", e);
                            throw e;
                        } catch (NotFoundException e){
                            LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                        }
                        line = br.readLine();
                        currentLine++;
                    }
                    psInsert.executeBatch();
                    LOG.info("Populated [" + (currentLine - 1) + "] statements");
                } 
                catch (IOException e1) {
                    LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                }
            }
        });
        tx.commit();
    }

    protected void populateShortFormStatements(final InputStream stream, EntityManager em, final Ontology ontology) throws IOException {
        LOG.info("Populating statements");
        final Map<Long, Long> map = createConceptSerialisedIdMapToDatabaseIdForOntology(ontology, em);
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
                                LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            } catch (IllegalArgumentException e){
                                LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                            }
                            line = br.readLine();
                        }
                        ps.executeBatch();
                        LOG.info("Populated [" + (currentLine - 1) + "] statements");
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

    protected Map<Long, Long> createConceptSerialisedIdMapToDatabaseIdForOntology(final Ontology ontology, EntityManager em){
        final HashMap<Long, Long> map = new HashMap<Long, Long>();
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("SELECT id, serialisedId FROM concept WHERE ontology_id = ?"); 
                ps.setLong(1, ontology.getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    map.put(rs.getLong(2), rs.getLong(1));
                }
            }
        });
        tx.commit();
        return map;
    }

    protected void createIsKindOfHierarchy(EntityManager em, final Ontology o){
        LOG.info("Creating isA hierarchy");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psKindOf = connection.prepareStatement("INSERT INTO KIND_OF (child_id, parent_id) VALUES (?, ?)");
                PreparedStatement psStatements = connection.prepareStatement("SELECT subject_id, predicate_id, object_id FROM STATEMENT WHERE ontology_id = ?");
                int counter = 1;
                psStatements.setLong(1, o.getId());
                ResultSet rs = psStatements.executeQuery();
                while (rs.next()){
                    if (rs.getLong(2) == o.getIsKindOfPredicate().getId()){
                        psKindOf.setLong(1, rs.getLong(1));
                        psKindOf.setLong(2, rs.getLong(3));
                        psKindOf.addBatch();
                        counter++;
                    }
                }
                psKindOf.executeBatch();
                LOG.info("Created [" + counter + "] isA statements");
            }
        });
        tx.commit();
    }

    protected boolean stringToBoolean(String string) throws IllegalArgumentException{
        if (string.trim().equals("0")){
            return false;
        }
        else if (string.trim().equals("1")){
            return true;
        }
        else{
            throw new IllegalArgumentException("Unable to convert value [" + string + "] to boolean value");
        }
    }
}
