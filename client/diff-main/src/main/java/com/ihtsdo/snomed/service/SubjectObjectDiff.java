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

class SubjectObjectDiff implements DiffAlgorithm{

    private static final Logger LOG = LoggerFactory.getLogger( SubjectObjectDiff.class );

    public void diff(Ontology base, Ontology compare, OntologySerialiser extrasSerialiser, 
            OntologySerialiser missingSerialiser, EntityManager em) throws IOException
    {
        LOG.info("Getting all base statements");
        Stopwatch stopwatch = new Stopwatch().start();
        Set<SubjectObject> baseSo = getSubjectObject(base, em);
        stopwatch.stop();
        LOG.info("Done in {} seconds", stopwatch.elapsed(TimeUnit.SECONDS));

        LOG.info("Getting all compare statements");
        stopwatch = new Stopwatch().start();
        Set<SubjectObject> compareSo = getSubjectObject(compare, em);
        stopwatch.stop();
        LOG.info("Done in {} seconds", stopwatch.elapsed(TimeUnit.SECONDS));
        
        LOG.info("Finding all missing statements");
        stopwatch = new Stopwatch().start();
        SetView<SubjectObject> missingSo = Sets.difference(baseSo, compareSo);
        stopwatch.stop();
        LOG.info("Found {} missing statements in {} seconds", missingSo.size(), stopwatch.elapsed(TimeUnit.SECONDS));

        LOG.info("Finding all extra statements");
        stopwatch = new Stopwatch().start();
        SetView<SubjectObject> extraSo = Sets.difference(compareSo, baseSo);
        stopwatch.stop();
        LOG.info("Found {} extra statements in {} seconds", extraSo.size(), stopwatch.elapsed(TimeUnit.SECONDS));

        LOG.info("Writing extra statements");
        for (SubjectObject so : extraSo){
            extrasSerialiser.write(new Statement(Statement.SERIALISED_ID_NOT_DEFINED, 
                    new Concept(so.subject), 
                    new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID), 
                    new Concept(so.object)));
        }
        LOG.info("Writing missing statements");
        for (SubjectObject so : missingSo){
            missingSerialiser.write(new Statement(Statement.SERIALISED_ID_NOT_DEFINED, 
                    new Concept(so.subject), 
                    new Concept(Concept.IS_KIND_OF_RELATIONSHIP_TYPE_ID), 
                    new Concept(so.object)));
        }
    }
    
    public Set<SubjectObject> getSubjectObject(final Ontology o, EntityManager em){
        final Set<SubjectObject> subjectObjectSet = new HashSet<SubjectObject>();
        HibernateEntityManager hem = em.unwrap(HibernateEntityManager.class);
        Session session = ((Session) hem.getDelegate()).getSessionFactory().openSession();
        session.doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT c1.serialisedId, c2.serialisedId FROM Statement s " + 
                                "JOIN Concept c1 ON s.subject_id = c1.id "+
                                "JOIN Concept c2 ON s.object_id = c2.id " +
                        "WHERE s.ontology_id=?");
                ps.setLong(1, o.getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    subjectObjectSet.add(new SubjectObject(rs.getLong(1), rs.getLong(2)));

                }
            }
        });
        return subjectObjectSet;
    }
        
    
    private class SubjectObject {
        private long subject;
        private long object;
        private int hash;
        
        public SubjectObject(long subject, long object){
            this.subject = subject;
            this.object = object;
            HashFunction hf = Hashing.md5();
            HashCode hc = hf.newHasher()
                   .putLong(subject)
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
            if (o instanceof SubjectObject){
                SubjectObject s = (SubjectObject) o;
                if ((s.subject == this.subject) && (s.object == this.object)){
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString(){
            return "{s:" + subject + ", o:" + object + ")";
        }
    }
}




//TypedQuery<Statement> query = em.createQuery("SELECT s FROM Statement s WHERE s.ontology.id=:ontologyId", Statement.class)
//.setParameter("ontologyId", o.getId())
//.setHint("org.hibernate.cacheable", Boolean.TRUE)
//.setHint("org.hibernate.readOnly", Boolean.TRUE)
//.setHint("org.hibernate.cacheMode", CacheMode.GET);
//
//int firstResult = 0;
//boolean done = false;
//query.setFirstResult(firstResult);
//query.setMaxResults(pageSize);         
//List<Statement> statements = query.getResultList();
//Set<SubjectObject> so = new HashSet<SubjectObject>();
//while (!done){
//LOG.info("Running Statements batch with pagesize {}", pageSize);
//Stopwatch stopwatch = new Stopwatch().start();
//if (statements.size() < pageSize) {
//    done = true;
//}                    
//for (Statement s : statements){
//    so.add(new SubjectObject(s.getSubject().getSerialisedId(), s.getObject().getSerialisedId()));
//}
//em.clear();
//firstResult = firstResult + pageSize;
//if (!done){
//    query.setFirstResult(firstResult);
//    statements = query.getResultList();
//}
//stopwatch.stop();
//LOG.info("Completed batch in {} seconds", stopwatch.elapsed(TimeUnit.SECONDS));
//
//}
//return so;