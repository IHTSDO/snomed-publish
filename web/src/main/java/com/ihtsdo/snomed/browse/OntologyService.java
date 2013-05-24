package com.ihtsdo.snomed.browse;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.service.InvalidInputException;

@Service
public class OntologyService {
    
    @PersistenceContext
    EntityManager em;
    
//    HibernateParser importer = HibernateParserFactory.getParser(Parser.RF1);

    @Transactional
    public Ontology deleteOntology(long ontologyId) throws OntologyNotFoundException{
        try {
            Ontology ontology = em.find(Ontology.class, ontologyId);
            em.remove(ontology);
            return ontology;
        } catch (NoResultException e) {
            throw new OntologyNotFoundException(ontologyId);
        }
    }
    
    @Transactional
    public List<Ontology> getAll(){
        return em.createQuery("SELECT o FROM Ontology o", Ontology.class).getResultList();
    }
    
//    @Transactional
//    public void exportCanonical(Ontology ontology, Writer writer) throws IOException{
//        SerialiserFactory.getSerialiser(Form.CANONICAL, writer).write(ontology.getStatements());        
//    }
    
//    @Transactional
//    public Ontology importOntology(InputStream conceptsInputStream, InputStream statementsInputStream, String name) 
//            throws InvalidConceptsException, InvalidStatementsException, InvalidInputException, IOException
//    {
//        PushbackInputStream conceptsInputstream = new PushbackInputStream(conceptsInputStream, 200);
//        byte[] bytes = new  byte[200];
//        conceptsInputstream.read(bytes);
//        conceptsInputstream.unread(bytes);
//        String start = new String (bytes, "UTF-8");            
//        if (!start.trim().startsWith("CONCEPTID\t")){
//            throw new InvalidConceptsException("File format not recognised");
//        }
//
//        //STATEMENTS + IMPORT
//        PushbackInputStream relationshipsInputstream = new PushbackInputStream(statementsInputStream, 200);
//        bytes = new  byte[200];
//        conceptsInputstream.read(bytes);
//        conceptsInputstream.unread(bytes);
//        start = new String (bytes, "UTF-8");  
//        try {
//            if (start.trim().startsWith("RELATIONSHIPID\t")){
//                return importer.populateDb(name, conceptsInputstream, relationshipsInputstream, em);
//            }else if (start.trim().startsWith("CONCEPTID1\t")){
//                return importer.populateDb(name, conceptsInputstream, relationshipsInputstream, em);
//            }else{
//                throw new InvalidStatementsException("File format not recognised");
//            }
//        } catch (Throwable t) {
//            throw new InvalidInputException("Error importing ontology: " + t.getMessage());
//        }
//    }
    
    @Transactional
    public Ontology getOntology(long ontologyId) throws OntologyNotFoundException{
        try {
            return em.find(Ontology.class,ontologyId);
        } catch (NoResultException e) {
            throw new OntologyNotFoundException(ontologyId);
        }
    }
    
    public static class InvalidConceptsException extends InvalidInputException{
        private static final long serialVersionUID = -4552602904716955718L;
        public InvalidConceptsException() {
            super();
        }
        public InvalidConceptsException(String message, Throwable cause) {
            super(message, cause);
        }
        public InvalidConceptsException(String message) {
            super(message);
        }
        public InvalidConceptsException(Throwable cause) {
            super(cause);
        }
    }
    public static class InvalidStatementsException extends InvalidInputException{
        private static final long serialVersionUID = 9031124457443633104L;
        public InvalidStatementsException() {
            super();
        }
        public InvalidStatementsException(String message, Throwable cause) {
            super(message, cause);
        }
        public InvalidStatementsException(String message) {
            super(message);
        }
        public InvalidStatementsException(Throwable cause) {
            super(cause);
        }
    }   
    
    
    public static class OntologyNotFoundException extends Exception{
        private static final long serialVersionUID = 1L;
        private long ontologyId;
        
        public OntologyNotFoundException(long ontologyId){
            this.ontologyId = ontologyId;
        }

        public long getOntologyId() {
            return ontologyId;
        }
    }    
}
