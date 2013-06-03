package com.ihtsdo.snomed.service.parser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.CoreMetadataConcepts;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Ontology.Source;

public abstract class HibernateParser {
    static final Logger LOG = LoggerFactory.getLogger(HibernateParser.class);
    
    public static final String ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML = "persistenceManager";
    
    protected static final int      DEFAULT_VERSION = 1;
    protected static final boolean  DEFAULT_CONCEPT_ACTIVE = true;
    protected static final boolean  DEFAULT_CONCEPT_PRIMITIVE = true;
    protected static final int      DEFAULT_CONCEPT_EFFECTIVE_TIME = -1;
    protected static final int      DEFAULT_CONCEPT_STATUS_ID = -1;
    protected static final int      DEFAULT_DESCRIPTION_INITIAL_CAPITAL_STATUS = -1;
    protected static final int      DEFAULT_DESCRIPTION_STATUS = -1;
    protected static final int      DEFAULT_DESCRIPTION_TYPE_ID = -1;
    protected static final int      DEFAULT_DESCRIPTION_EFFECTIVETIME = -1;
    protected static final boolean  DEFAULT_DESCRIPTION_ACTIVE = true;
    protected static final int      DEFAULT_STATEMENT_CHARACTERISTIC_TYPE_IDENTIFIER = -1;
    protected static final int      DEFAULT_STATEMENT_REFINABILITY = -1;
    protected static final int      DEFAULT_STATEMENT_EFFECTIVE_TIME = -1;
    protected static final boolean  DEFAULT_STATEMENT_ACTIVE = true;
    protected static final int      DEFAULT_STATEMENT_GROUP = 0;
    
    protected EntityManagerFactory emf = Persistence.createEntityManagerFactory(HibernateParser.ENTITY_MANAGER_NAME_FROM_PERSISTENCE_XML);

    protected Mode parseMode = Mode.FORGIVING;
    
    public enum Mode{
        STRICT, FORGIVING
    }

    public HibernateParser setParseMode(Mode parseMode){
        this.parseMode = parseMode;
        return this;
    }

    protected abstract void populateConcepts(final InputStream stream, EntityManager em, 
            final Ontology ontology) throws IOException;

    protected abstract void populateConceptsFromStatements(final InputStream stream, EntityManager em, 
            final Ontology ontology) throws IOException;
    
    protected abstract void populateConceptsFromStatementsAndDescriptions(final InputStream statementsStream, 
            final InputStream descriptionsStream, EntityManager em, final Ontology ontology) throws IOException;

    protected abstract void populateStatements(final InputStream stream, final EntityManager em, 
            final Ontology ontology) throws IOException;
    
    protected abstract void populateDescriptions(final InputStream stream, final EntityManager em, 
            final Ontology ontology) throws IOException;
    
    protected abstract Source getSource();

    public Ontology populateDb(String ontologyName, InputStream conceptsStream, 
            InputStream statementStream, EntityManager em) throws IOException
    {
        LOG.info("Importing ontology \"" + ontologyName + "\"");
        Stopwatch stopwatch = new Stopwatch().start();

        boolean doCommit = false;
        if (!em.getTransaction().isActive()){
            em.getTransaction().begin();
            doCommit=true;
        }

        Ontology ontology = createOntology(em, ontologyName);
        populateConcepts(conceptsStream, em, ontology);
        populateStatements(statementStream, em, ontology);
        createIsKindOfHierarchy(em, ontology);
        if(doCommit){em.getTransaction().commit();}
        
        em.clear();
        Ontology o = em.find(Ontology.class, ontology.getId());

        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return o;
    }
    
    public Ontology populateDbWithDescriptions(String ontologyName, InputStream conceptsStream, 
            InputStream statementStream, InputStream descriptionStream, EntityManager em) throws IOException
    {
        LOG.info("Importing ontology \"" + ontologyName + "\"");
        Stopwatch stopwatch = new Stopwatch().start();

        boolean doCommit = false;
        if (!em.getTransaction().isActive()){
            em.getTransaction().begin();
            doCommit=true;
        }
        
        Ontology ontology = createOntology(em, ontologyName);
        populateConcepts(conceptsStream, em, ontology);
        populateDescriptions(descriptionStream, em, ontology);
        populateStatements(statementStream, em, ontology);
        createIsKindOfHierarchy(em, ontology);
        populateDisplaynameCache(em, ontology);
        if(doCommit){em.getTransaction().commit();}
        em.clear();
        Ontology o = em.find(Ontology.class, ontology.getId());

        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return o;
    }    

    public Ontology populateDbFromStatementsOnly(String ontologyName, InputStream statementStream, 
            InputStream statementStreamAgain, EntityManager em) throws IOException
            {
        LOG.info("Importing ontology \"" + ontologyName + "\"");
        Stopwatch stopwatch = new Stopwatch().start();

        boolean doCommit = false;
        if (!em.getTransaction().isActive()){
            em.getTransaction().begin();
            doCommit=true;
        }
        
        Ontology ontology = createOntology(em, ontologyName);
        populateConceptsFromStatements(statementStream, em, ontology);
        populateStatements(statementStreamAgain, em, ontology);
        createIsKindOfHierarchy(em, ontology);
        if(doCommit){em.getTransaction().commit();}
        em.clear();
        Ontology o = em.find(Ontology.class, ontology.getId());

        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return o;
    } 
    
    public Ontology populateDbFromStatementsAndDescriptionsOnly(String ontologyName, InputStream statementStream, 
            InputStream statementStreamAgain, InputStream descriptionStream, InputStream descriptionStreamAgain,
            EntityManager em) throws IOException
    {
        LOG.info("Importing ontology \"" + ontologyName + "\"");
        Stopwatch stopwatch = new Stopwatch().start();

        boolean doCommit = false;
        if (!em.getTransaction().isActive()){
            em.getTransaction().begin();
            doCommit=true;
        }
        
        Ontology ontology = createOntology(em, ontologyName);
        populateConceptsFromStatementsAndDescriptions(statementStream, descriptionStream, em, ontology);
        populateStatements(statementStreamAgain, em, ontology);
        populateDescriptions(descriptionStreamAgain, em, ontology);
        createIsKindOfHierarchy(em, ontology);
        if(doCommit){em.getTransaction().commit();}
        em.clear();
        Ontology o = em.find(Ontology.class, ontology.getId());

        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return o;
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
                        "INSERT INTO Ontology (NAME, SOURCE) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, name);
                statement.setInt(2, getSource().ordinal());
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
    
    protected void populateDisplaynameCache(EntityManager em, final Ontology o) {
        LOG.info("Populating display name cache");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psUpdate = connection.prepareStatement("UPDATE Concept SET fullyspecifiedname=? WHERE id=?");
                PreparedStatement psSelect = connection.prepareStatement("SELECT d.about_id, d.term FROM Description d LEFT OUTER JOIN Concept c1 on d.type_id = c1.id WHERE c1.serialisedId=? and d.ontology_id=?;");
                int counter = 1;
                psSelect.setLong(1, CoreMetadataConcepts.DESCRIPTION_TYPE_FULLY_SPECIFIED_NAME);
                psSelect.setLong(2, o.getId());
                ResultSet rs = psSelect.executeQuery();
                while (rs.next()){
                    psUpdate.setString(1, rs.getString(2));
                    psUpdate.setLong(2, rs.getLong(1));
                    psUpdate.addBatch();
                }
                psUpdate.executeBatch();
                LOG.info("Updated " + counter + " display names");
            }
        });
        tx.commit();
    }


    protected void createIsKindOfHierarchy(EntityManager em, final Ontology o) {
        LOG.info("Creating isA hierarchy");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psKindOf = connection.prepareStatement("INSERT INTO Concept_Concept (child_id, parent_id) VALUES (?, ?)");
                PreparedStatement psStatements = connection.prepareStatement("SELECT subject_id, predicate_id, object_id FROM Statement WHERE ontology_id = ?");
                int counter = 1;
                psStatements.setLong(1, o.getId());
                ResultSet rs = psStatements.executeQuery();
                Map<Long, Set<Long>> childParentMap = new HashMap<Long, Set<Long>>();
                while (rs.next()){
                    if (rs.getLong(2) == o.getIsKindOfPredicate().getId()){
                        Set<Long> parents = childParentMap.get(rs.getLong(1));
                        if (parents == null){
                            parents = new HashSet<Long>();
                            childParentMap.put(rs.getLong(1), parents);
                        }
                        parents.add(rs.getLong(3));
                    }
                }
                for (Long child : childParentMap.keySet()){
                    for (Long parent : childParentMap.get(child)){
                        psKindOf.setLong(1, child);
                        psKindOf.setLong(2, parent);
                        psKindOf.addBatch();
                        counter++;                        
                    }

                }
                psKindOf.executeBatch();
                LOG.info("Created " + counter + " isA statements");
            }
        });
        tx.commit();
    }

    protected void setKindOfPredicate(EntityManager em, Ontology o)
            throws IllegalStateException {
        try {
            Concept predicate = em.createQuery("SELECT c FROM Concept c WHERE c.serialisedId=" + Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID + " AND c.ontology.id=" + o.getId(), Concept.class).getSingleResult();
            o.setIsKindOfPredicate(predicate);
        } catch (NoResultException e) {
            throw new IllegalStateException("The isA predicate does not exist in this ontology at this point");
        }
    }

    protected Map<Long, Long> createConceptSerialisedIdToDatabaseIdMap(
            final Ontology ontology, EntityManager em) {
        final HashMap<Long, Long> map = new HashMap<Long, Long>();
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("SELECT id, serialisedId FROM Concept WHERE ontology_id = ?"); 
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

    protected boolean stringToBoolean(String string)
            throws IllegalArgumentException {
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
