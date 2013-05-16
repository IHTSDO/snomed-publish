package com.ihtsdo.snomed.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.service.serialiser.OntologySerialiser;

class SubjectPredicateObjectDiff implements DiffAlgorithm{

    private static final Logger LOG = LoggerFactory.getLogger( SubjectPredicateObjectDiff.class );

    public void diff(Ontology base, Ontology compare, OntologySerialiser extrasSerialiser, 
            OntologySerialiser missingSerialiser, EntityManager em) throws IOException
    {
        LOG.info("Getting all base statements");
        Stopwatch stopwatch = new Stopwatch().start();
        Set<SubjectPredicateObject> baseSpo = getSubjectPredicateObject(base, em);
        stopwatch.stop();
        LOG.info("Done in {} seconds", stopwatch.elapsed(TimeUnit.SECONDS));

        LOG.info("Getting all compare statements");
        stopwatch = new Stopwatch().start();
        Set<SubjectPredicateObject> compareSpo = getSubjectPredicateObject(compare, em);
        stopwatch.stop();
        LOG.info("Done in {} seconds", stopwatch.elapsed(TimeUnit.SECONDS));
        
        LOG.info("Finding all missing statements");
        stopwatch = new Stopwatch().start();
        SetView<SubjectPredicateObject> missingSpo = Sets.difference(baseSpo, compareSpo);
        stopwatch.stop();
        LOG.info("Found {} missing statements in {} seconds", missingSpo.size(), stopwatch.elapsed(TimeUnit.SECONDS));

        LOG.info("Finding all extra statements");
        stopwatch = new Stopwatch().start();
        SetView<SubjectPredicateObject> extraSpo = Sets.difference(compareSpo, baseSpo);
        stopwatch.stop();
        LOG.info("Found {} extra statements in {} seconds", extraSpo.size(), stopwatch.elapsed(TimeUnit.SECONDS));

        LOG.info("Writing extra statements");
        for (SubjectPredicateObject spo : extraSpo){
            extrasSerialiser.write(new Statement(Statement.SERIALISED_ID_NOT_DEFINED, 
                    new Concept(spo.subject), 
                    new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID), 
                    new Concept(spo.object)));
        }
        LOG.info("Writing missing statements");
        for (SubjectPredicateObject spo : missingSpo){
            missingSerialiser.write(new Statement(Statement.SERIALISED_ID_NOT_DEFINED, 
                    new Concept(spo.subject), 
                    new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID), 
                    new Concept(spo.object)));
        }
    }
    
    public Set<SubjectPredicateObject> getSubjectPredicateObject(final Ontology o, EntityManager em){
        final Set<SubjectPredicateObject> subjectPredicateObjectSet = new HashSet<SubjectPredicateObject>();
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT c1.serialisedId, c2.serialisedId, c3.serialisedId FROM STATEMENT s " + 
                                "JOIN Concept c1 ON s.subject_id = c1.id " +
                                "JOIN Concept c2 ON s.object_id = c2.id " +
                                "JOIN Concept c3 ON s.subject_id = c3.id " +
                        "WHERE s.ontology_id=?");
                ps.setLong(1, o.getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    subjectPredicateObjectSet.add(new SubjectPredicateObject(rs.getLong(1), rs.getLong(2), rs.getLong(3)));

                }
            }
        });
        return subjectPredicateObjectSet;
    }
        
    
    private class SubjectPredicateObject {
        private long subject;
        private long predicate;
        private long object;
        private int hash;
        
        public SubjectPredicateObject(long subject, long predicate, long object){
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
            HashFunction hf = Hashing.md5();
            HashCode hc = hf.newHasher()
                   .putLong(subject)
                   .putLong(predicate)
                   .putLong(object)
                   .hash();
            hash = hc.asInt();
        }
        
        @Override
        public int hashCode(){
            return hash;
        }
        
        @Override
        public boolean equals(Object o){
            if (o instanceof SubjectPredicateObject){
                SubjectPredicateObject s = (SubjectPredicateObject) o;
                if ((s.subject == this.subject) && (s.object == this.object) && (s.predicate == this.predicate)){
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString(){
            return "{s:" + subject + ", p:" + predicate + ", o:" + object + ")";
        }
    }
}
