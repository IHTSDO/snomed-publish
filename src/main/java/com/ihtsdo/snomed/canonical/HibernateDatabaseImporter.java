package com.ihtsdo.snomed.canonical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class HibernateDatabaseImporter {

    private static final Logger LOG = LoggerFactory.getLogger( HibernateDatabaseImporter.class );
    public static final long IMPORTED_ONTOLOGY_ID = 2;

    public List<Concept> populateDb(InputStream conceptsStream, InputStream relationshipsStream, EntityManager em) throws IOException{
        Stopwatch stopwatch = new Stopwatch().start();
        LOG.info("Populating database");
        populateConcepts(conceptsStream, em);
        populateRelationships(relationshipsStream, em);
        createIsKindOfHierarchy(em);
        List<Concept> concepts = em.createQuery("SELECT c FROM Concept c", Concept.class).getResultList();
        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return concepts;
    }

    protected void populateConcepts(InputStream stream, EntityManager em) throws IOException {
        LOG.info("Populating Concepts");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        StatelessSession session = ((Session) hem.getDelegate()).getSessionFactory().openStatelessSession();
        try {
            Transaction tx = session.beginTransaction();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
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
                    Concept concept = new Concept();
                    try {
                        concept.setId(Long.parseLong(splitIt.next()));
                        concept.setStatus(Integer.parseInt(splitIt.next()));
                        concept.setFullySpecifiedName(splitIt.next());
                        concept.setCtv3id(splitIt.next());
                        concept.setSnomedId(splitIt.next());
                        concept.setPrimitive(stringToBoolean(splitIt.next()));
                        session.insert(concept);

                    } catch (NumberFormatException e) {
                        LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing");
                    } catch (IllegalArgumentException e){
                        LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing");
                    }
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
            tx.commit();
            LOG.info("Populated [" + (currentLine - 1) + "] concepts");
        } finally {
            session.close();
        }
    }

    protected void populateRelationships(final InputStream stream, EntityManager em) throws IOException {
        LOG.info("Populating relationships");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO RELATIONSHIP_STATEMENT (id, subject_id, relationship_type, object_id, characteristic_type, refinability, relationship_group) VALUES (?, ?, ?, ?, ?, ?, ?)");
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
                                    ps.setLong(1, Long.parseLong(splitIt.next()));
                                    ps.setLong(2, Long.parseLong(splitIt.next()));
                                    ps.setLong(3, Long.parseLong(splitIt.next()));
                                    ps.setLong(4, Long.parseLong(splitIt.next()));
                                    ps.setInt(5, Integer.parseInt(splitIt.next()));
                                    ps.setInt(6, Integer.parseInt(splitIt.next()));
                                    ps.setInt(7, Integer.parseInt(splitIt.next()));
                                    ps.addBatch();
                                } catch (NumberFormatException e) {
                                    LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                                } catch (IllegalArgumentException e){
                                    LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing", e);
                                }
                                line = br.readLine();
                            }
                            ps.executeBatch();
                            LOG.info("Populated [" + (currentLine - 1) + "] relationships");
                        }finally {
                            br.close();
                        }
                    } catch (IOException e1) {
                        LOG.error("Unable to read from the input stream. Bailing out. Message is: " + e1.getMessage(), e1);
                    } 
                }
            });
            tx.commit();
        } finally {
            session.close();
        }
    }

    protected void createIsKindOfHierarchy(EntityManager em){
        LOG.info("Creating isA hierarchy");
        Query query = em.createQuery("SELECT r FROM RelationshipStatement r");
        @SuppressWarnings("unchecked") List<RelationshipStatement> statements = (List<RelationshipStatement>) query.getResultList();
        final Iterator<RelationshipStatement> stIt = statements.iterator();
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    PreparedStatement psKindOf = connection.prepareStatement("INSERT INTO KIND_OF (child_id, parent_id) VALUES (?, ?)");
                    int counter = 1;
                    while(stIt.hasNext()){
                        RelationshipStatement statement = stIt.next();
                        if (statement.isKindOfRelationship()){
                            psKindOf.setLong(1, statement.getSubject().getId());
                            psKindOf.setLong(2, statement.getObject().getId());
                            psKindOf.addBatch();
                            counter++;
                        }
                    }
                    psKindOf.executeBatch();
                    LOG.info("Created [" + counter + "] isA relationships");
                }
            });
            tx.commit();
        } finally {
            session.close();
        }
    }

    protected boolean stringToBoolean(String string){
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

    //    protected static void createAlgorithmStructures(EntityManager em){
    //        EntityTransaction tx = em.getTransaction();
    //        tx.begin();
    //        //Ontology o = em.find(Ontology.class, HibernateDatabaseImporter.IMPORTED_ONTOLOGY_ID);
    //        //LOG.info("Processing Step 2 for ontology [" + o +"]");
    //
    //        Query query = em.createQuery("SELECT r FROM RelationshipStatement r");
    //        List<RelationshipStatement> c = (List<RelationshipStatement>) query.getResultList();
    //
    //
    //        Iterator<RelationshipStatement> stIt = c.iterator();
    //        //= o.getRelationshipStatements().iterator();
    //        int counter = 0;
    //        while (stIt.hasNext()){
    //            RelationshipStatement statement = stIt.next();
    //
    //            if (statement.getRelationshipType() == IS_KIND_OF_RELATIONSHIP_TYPE_ID){
    //                statement.getSubject().addKindOf(statement.getObject());
    //                statement.getObject().addParentOf(statement.getSubject());
    //            }
    //
    //            statement.getSubject().addSubjectOfRelationShipStatements(statement);
    //
    ////            em.merge(statement.getSubject());
    ////            em.merge(statement.getObject());
    //
    ////            em.merge(statement);
    //
    //            if (counter % 10000 == 0){
    //                em.flush();
    //                LOG.debug("Processed [" + counter + "] concepts");
    //                //em.clear();
    //            }
    //            counter++;
    //
    ////            if (counter % 10000 == 0){
    ////                LOG.debug("Processed [" + counter + "] concepts");
    ////            }
    //        }
    //        LOG.debug("Done. Processed [" + counter + "] concepts");
    //        tx.commit();
    //    }



    //  protected void populateRelationships2(InputStream stream, EntityManager em) throws IOException {
    //      LOG.info("Populating Relationships");
    //      HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
    //      StatelessSession session = ((Session) hem.getDelegate()).getSessionFactory().openStatelessSession();
    //      Transaction tx = session.beginTransaction();
    //
    //      try {
    //          BufferedReader br = new BufferedReader(new InputStreamReader(stream));
    //          int currentLine = 1;
    //          String line = null;
    //
    //          try {
    //              line = br.readLine();
    //              //skip the headers
    //              line = br.readLine();
    //
    //              while (line != null) {
    //                  currentLine++;
    //                  if (line.isEmpty()){
    //                      line = br.readLine();
    //                      continue;
    //                  }
    //                  Iterable<String> split = Splitter.on('\t').split(line);
    //                  Iterator<String> splitIt = split.iterator();
    //
    //                  RelationshipStatement statement = new RelationshipStatement();
    //
    //                  try {
    //                      statement.setId(Long.parseLong(splitIt.next()));
    //                      long subjectId = Long.parseLong(splitIt.next());
    //                      Concept subject = em.find(Concept.class, subjectId);
    //                      if (subject == null){
    //                          throw new InvalidInputException("Concept [" + subjectId + "] not found");
    //                      }
    //                      statement.setSubject(subject);
    //                      statement.setRelationshipType(Long.parseLong(splitIt.next()));
    //                      long objectId = Long.parseLong(splitIt.next());
    //                      Concept object = em.find(Concept.class, objectId);
    //                      if (object == null){
    //                          throw new InvalidInputException("Concept [" + objectId + "] not found");
    //                      }
    //
    //                      statement.setObject(object);
    //                      statement.setCharacteristicType(Integer.parseInt(splitIt.next()));
    //                      statement.setRefinability(Integer.parseInt(splitIt.next()));
    //                      statement.setRelationShipGroup(Integer.parseInt(splitIt.next()));
    //
    //                      session.insert(statement);
    //
    //                  } catch (NumberFormatException e) {
    //                      LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing");
    //                  } catch (IllegalArgumentException e){
    //                      LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing");
    //                  } catch (InvalidInputException e) {
    //                      LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing");
    //                  }
    //
    //                  line = br.readLine();
    //              }
    //          } finally {
    //              br.close();
    //          }
    //          tx.commit();
    //          LOG.info("Populated [" + (currentLine - 1) + "] relationships");
    //      } finally {
    //          session.close();
    //      }
    //  }    

}
