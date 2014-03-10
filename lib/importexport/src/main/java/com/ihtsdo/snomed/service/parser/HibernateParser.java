package com.ihtsdo.snomed.service.parser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.ihtsdo.snomed.model.OntologyFlavour;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.OntologyVersion.Source;
import com.ihtsdo.snomed.model.SnomedFlavours.SnomedFlavour;
import com.ihtsdo.snomed.model.SnomedOntology;

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
            final OntologyVersion ontologyVersion) throws IOException;

    protected abstract void populateConceptsFromStatements(final InputStream stream, EntityManager em, 
            final OntologyVersion ontologyVersion) throws IOException;
    
    protected abstract void populateConceptsFromStatementsAndDescriptions(final InputStream statementsStream, 
            final InputStream descriptionsStream, EntityManager em, final OntologyVersion ontologyVersion) throws IOException;

    protected abstract void populateStatements(final InputStream stream, final EntityManager em, 
            final OntologyVersion ontologyVersion) throws IOException;
    
    protected abstract void populateDescriptions(final InputStream stream, final EntityManager em, 
            final OntologyVersion ontologyVersion) throws IOException;
    
    protected abstract Source getSource();

    public OntologyVersion populateDb(SnomedFlavour snomedFlavour, Date taggedOn, InputStream conceptsStream, 
            InputStream statementStream, EntityManager em) throws IOException
    {
        LOG.info("Importing snomed flavour \"" + snomedFlavour.getLabel() + "\", version " + taggedOn);
        Stopwatch stopwatch = new Stopwatch().start();

        boolean doCommit = false;
        if (!em.getTransaction().isActive()){
            em.getTransaction().begin();
            doCommit=true;
        }

        OntologyVersion ontologyVersion = createOntologyVersion(em, snomedFlavour, taggedOn);
        populateConcepts(conceptsStream, em, ontologyVersion);
        populateStatements(statementStream, em, ontologyVersion);
        createIsKindOfHierarchy(em, ontologyVersion);
        if(doCommit){em.getTransaction().commit();}
        
        em.clear();
        OntologyVersion o = em.find(OntologyVersion.class, ontologyVersion.getId());

        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return o;
    }
    
    public OntologyVersion populateConceptAndDescriptions(SnomedFlavour snomedFlavour, Date taggedOn, InputStream conceptsStream, 
            InputStream descriptionStream, EntityManager em) throws IOException
    {
        LOG.info("Importing snomed flavour \"" + snomedFlavour.getLabel() + "\", version " + taggedOn);
        Stopwatch stopwatch = new Stopwatch().start();

        boolean doCommit = false;
        if (!em.getTransaction().isActive()){
            em.getTransaction().begin();
            doCommit=true;
        }

        OntologyVersion ontologyVersion = createOntologyVersion(em, snomedFlavour, taggedOn);
        populateConcepts(conceptsStream, em, ontologyVersion);
        populateDescriptions(descriptionStream, em, ontologyVersion);
        //createIsKindOfHierarchy(em, ontology);
        populateDisplaynameCache(em, ontologyVersion);
        if(doCommit){em.getTransaction().commit();}
        
        em.clear();
        OntologyVersion o = em.find(OntologyVersion.class, ontologyVersion.getId());

        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return o;
    }    
    
    public OntologyVersion populateDbWithDescriptions(SnomedFlavour snomedFlavour, Date taggedOn, 
            InputStream conceptsStream, InputStream statementStream, InputStream descriptionStream, 
            EntityManager em) throws IOException
    {
        LOG.info("Importing snomed flavour \"" + snomedFlavour.getLabel() + "\", version " + taggedOn);
        Stopwatch stopwatch = new Stopwatch().start();

        boolean doCommit = false;
        if (!em.getTransaction().isActive()){
            em.getTransaction().begin();
            doCommit=true;
        }
        
        OntologyVersion ontologyVersion = createOntologyVersion(em, snomedFlavour, taggedOn);
        
        populateConcepts(conceptsStream, em, ontologyVersion);
        populateDescriptions(descriptionStream, em, ontologyVersion);
        populateStatements(statementStream, em, ontologyVersion);
        createIsKindOfHierarchy(em, ontologyVersion);
        populateDisplaynameCache(em, ontologyVersion);
        if(doCommit){em.getTransaction().commit();}
        em.clear();
        OntologyVersion o = em.find(OntologyVersion.class, ontologyVersion.getId());

        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return o;
    }    

    public OntologyVersion populateDbFromStatementsOnly(SnomedFlavour snomedFlavour, Date taggedOn, InputStream statementStream, 
            InputStream statementStreamAgain, EntityManager em) throws IOException
            {
        LOG.info("Importing snomed flavour \"" + snomedFlavour.getLabel() + "\", version " + taggedOn);
        Stopwatch stopwatch = new Stopwatch().start();

        boolean doCommit = false;
        if (!em.getTransaction().isActive()){
            em.getTransaction().begin();
            doCommit=true;
        }
        
        OntologyVersion ontologyVersion = createOntologyVersion(em, snomedFlavour, taggedOn);
        populateConceptsFromStatements(statementStream, em, ontologyVersion);
        populateStatements(statementStreamAgain, em, ontologyVersion);
        createIsKindOfHierarchy(em, ontologyVersion);
        if(doCommit){em.getTransaction().commit();}
        em.clear();
        OntologyVersion o = em.find(OntologyVersion.class, ontologyVersion.getId());

        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return o;
    } 
    
    public OntologyVersion populateDbFromStatementsAndDescriptionsOnly(SnomedFlavour snomedFlavour, Date taggedOn, InputStream statementStream, 
            InputStream statementStreamAgain, InputStream descriptionStream, InputStream descriptionStreamAgain,
            EntityManager em) throws IOException
    {
        LOG.info("Importing snomed flavour \"" + snomedFlavour.getLabel() + "\", version " + taggedOn);
        Stopwatch stopwatch = new Stopwatch().start();

        boolean doCommit = false;
        if (!em.getTransaction().isActive()){
            em.getTransaction().begin();
            doCommit=true;
        }
        
        OntologyVersion ontologyVersion = createOntologyVersion(em, snomedFlavour, taggedOn);
        populateConceptsFromStatementsAndDescriptions(statementStream, descriptionStream, em, ontologyVersion);
        populateStatements(statementStreamAgain, em, ontologyVersion);
        populateDescriptions(descriptionStreamAgain, em, ontologyVersion);
        createIsKindOfHierarchy(em, ontologyVersion);
        if(doCommit){em.getTransaction().commit();}
        em.clear();
        OntologyVersion o = em.find(OntologyVersion.class, ontologyVersion.getId());

        stopwatch.stop();
        LOG.info("Completed import in " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds");
        return o;
    }     


//    protected OntologyVersion createOntology(EntityManager em, SnomedFlavour snomedFlavour, OntologyVersion version) throws IOException {
//        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
//        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
//        Transaction tx = session.beginTransaction();
//        session.doWork(new Work() {
//            public void execute(Connection connection) throws SQLException {
//                PreparedStatement statement = connection.prepareStatement(
//                        "INSERT INTO OntologyVersion (NAME, SOURCE) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
//                statement.setString(1, name);
//                statement.setInt(2, getSource().ordinal());
//                int affectedRows = statement.executeUpdate();
//                if (affectedRows == 0) {
//                    throw new SQLException("Creating ontology failed, no rows affected.");
//                }
//                ResultSet generatedKeys = statement.getGeneratedKeys();
//                if (generatedKeys.next()) {
//                    version.setId(generatedKeys.getLong(1));
//                } else {
//                    throw new SQLException("Creating ontology failed, no generated key obtained.");
//                }
//            }
//        });
//        tx.commit();
//        return version;
//    }
    
    public OntologyVersion createOntologyVersion(EntityManager em, SnomedFlavour snomedFlavour, Date taggedOn) throws IOException {
        Ontology o = createSnomedOntology(em);
        OntologyFlavour of = createSnomedOntologyFlavour(em, o, snomedFlavour);
        
        OntologyVersion ov;
        try{
            ov = em.createQuery("SELECT ov FROM OntologyVersion ov, OntologyFlavour f WHERE ov MEMBER OF f.versions AND f.publicId=:flavourPublicId AND ov.taggedOn=:taggedOn", OntologyVersion.class)
                    .setParameter("taggedOn", taggedOn)
                    .setParameter("flavourPublicId", of.getPublicId())
                    .getSingleResult();
            ov.setSource(getSource());
            //throw new OntologyVersionAlreadyExistsException(taggedOn);
        } catch (NoResultException e) {
            ov = new OntologyVersion();
            ov.setTaggedOn(taggedOn);
            of.addVersion(ov);
            em.persist(ov);
            em.merge(of);
        }

        return ov;
    }

    protected Ontology createSnomedOntology(EntityManager em) {
        Ontology o;
        try {
            o = em.createQuery("SELECT o FROM Ontology o WHERE o.publicId=:publicId", Ontology.class)
                .setParameter("publicId",  SnomedOntology.PUBLIC_ID)
                .getSingleResult();
        } catch (NoResultException e) {
            o = new Ontology();
            o.setPublicId(SnomedOntology.PUBLIC_ID);
            o.setLabel(SnomedOntology.LABEL);
            em.persist(o);
        }
        return o;
    }
    
    protected OntologyFlavour createSnomedOntologyFlavour(EntityManager em, Ontology o, SnomedFlavour snomedFlavour) {
        OntologyFlavour of;
        try {
            of = em.createQuery("SELECT f FROM OntologyFlavour f, Ontology o WHERE f MEMBER OF o.flavours AND f.publicId=:publicId", OntologyFlavour.class)
                    .setParameter("publicId",  snomedFlavour.getPublicIdString())
                    .getSingleResult();
        } catch (NoResultException e) {
            of = new OntologyFlavour();
            of.setPublicId(snomedFlavour.getPublicIdString());
            of.setLabel(snomedFlavour.getLabel());
            o.addFlavour(of);
            em.persist(of);
            em.merge(o);
        }
        return of;
    }
        
    protected void populateDisplaynameCache(EntityManager em, final OntologyVersion o) {
        LOG.info("Populating display name cache");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psUpdate = connection.prepareStatement("UPDATE Concept SET fullyspecifiedname=? WHERE id=?");
                PreparedStatement psSelect = connection.prepareStatement("SELECT d.about_id, d.term FROM Description d LEFT OUTER JOIN Concept c1 on d.type_id = c1.id WHERE c1.serialisedId=? and d.ontologyVersion_id=? and d.active != 0;");
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


    protected void createIsKindOfHierarchy(EntityManager em, final OntologyVersion o) {
        LOG.info("Creating isA hierarchy");
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement psKindOf = connection.prepareStatement("INSERT INTO Concept_Concept (child_id, parent_id) VALUES (?, ?)");
                PreparedStatement psStatements = connection.prepareStatement("SELECT subject_id, predicate_id, object_id FROM Statement WHERE ontologyVersion_id = ?");
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

    protected void setKindOfPredicate(EntityManager em, OntologyVersion o)
            throws IllegalStateException {
        try {
            Concept predicate = em.createQuery("SELECT c FROM Concept c WHERE c.serialisedId=" + Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID + " AND c.ontologyVersion.id=" + o.getId(), Concept.class).getSingleResult();
            o.setIsKindOfPredicate(predicate);
        } catch (NoResultException e) {
            throw new IllegalStateException("The isA predicate does not exist in this ontology at this point");
        }
    }

    protected Map<Long, Long> createConceptSerialisedIdToDatabaseIdMap(
            final OntologyVersion ontologyVersion, EntityManager em) {
        final HashMap<Long, Long> map = new HashMap<Long, Long>();
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("SELECT id, serialisedId FROM Concept WHERE ontologyVersion_id = ?"); 
                ps.setLong(1, ontologyVersion.getId());
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
