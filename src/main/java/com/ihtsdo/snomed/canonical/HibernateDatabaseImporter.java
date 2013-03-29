package com.ihtsdo.snomed.canonical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.ejb.HibernateEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.Ontology;
import com.ihtsdo.snomed.canonical.model.RelationshipStatement;

public class HibernateDatabaseImporter {

    private static final Logger LOG = LoggerFactory.getLogger( HibernateDatabaseImporter.class );
    public static final long IMPORTED_ONTOLOGY_ID = 2;

    protected static final long IS_KIND_OF_RELATIONSHIP_TYPE_ID = 116680003;

    public static Ontology populateDb(InputStream conceptsStream, InputStream relationshipsStream, EntityManager em) throws IOException{
        LOG.info("Populating database");
        populateConcepts(conceptsStream, em);
        return populateRelationshipsAndCreateOntology(relationshipsStream, em);
    }

    protected static void populateConcepts(InputStream stream, EntityManager em) throws IOException {
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
                            concept.setPrimitive(HibernateDatabaseImporter.stringToBoolean(splitIt.next()));
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

    protected static Ontology populateRelationshipsAndCreateOntology(InputStream stream, EntityManager em) throws IOException {
        LOG.info("Populating Relationships");
        //HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        //StatelessSession session = ((Session) hem.getDelegate()).getSessionFactory().openStatelessSession();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        //Set<RelationshipStatement> statements = new HashSet<RelationshipStatement>();

        Ontology ontology = new Ontology();
        ontology.setId(IMPORTED_ONTOLOGY_ID);
        ontology.setDescription("Incoming ontology from text files");
        ontology.setName("incoming");

        try {
            //EntityTransaction tx = em.beginTransaction();
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

                    RelationshipStatement statement = new RelationshipStatement();

                    try {
                        statement.setId(Long.parseLong(splitIt.next()));
                        Concept subject = em.find(Concept.class, Long.parseLong(splitIt.next()));
                        statement.setSubject(subject);
                        statement.setRelationshipType(Long.parseLong(splitIt.next()));
                        Concept object = em.find(Concept.class, Long.parseLong(splitIt.next()));
                        statement.setObject(object);
                        statement.setCharacteristicType(Integer.parseInt(splitIt.next()));
                        statement.setRefinability(stringToBoolean(splitIt.next()));
                        statement.setRelationShipGroup(Integer.parseInt(splitIt.next()));

                        if (statement.getRelationshipType() == IS_KIND_OF_RELATIONSHIP_TYPE_ID){
                            subject.addKindOf(object);
                            object.addParentOf(subject);
                        }

                        subject.addSubjectOfRelationShipStatements(statement);

                        //session.insert(relationshipStatement);
                        em.persist(statement);

                        //statements.add(statement);
                        ontology.addRelationshipStatement(statement);

                    } catch (NumberFormatException e) {
                        LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing");
                    } catch (IllegalArgumentException e){
                        LOG.error("Unable to parse line number [" + currentLine + "]. Line was [" + line + "]. Message is [" + e.getMessage() + "]. Skipping entry and continuing");
                    }

                    line = br.readLine();

                    if (currentLine % 500 == 0){
                        em.flush();
                        em.clear();
                    }

                }
            } finally {
                br.close();
            }
            tx.commit();
            LOG.info("Populated [" + (currentLine - 1) + "] relationships");
        } finally {
            //session.close();
        }

        //ontology.setRelationshipStatements(statements);

        em.persist(ontology);

        LOG.info("Populated Ontology [" + ontology.toString() + "]");
        return ontology;
    }

    protected static boolean stringToBoolean(String string){
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
