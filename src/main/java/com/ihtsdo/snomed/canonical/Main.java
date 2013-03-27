package com.ihtsdo.snomed.canonical;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.ejb.HibernateEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.ihtsdo.snomed.canonical.model.Concept;

public class Main {
	
	private static final Logger LOG = LoggerFactory.getLogger( Main.class );
	protected static final String CONCEPTS_INPUT = "sct1_Concepts_Core_INT_20130131.ont.txt";
	private static final String ENTITY_MANAGER_NAME = "persistenceManager";
	private EntityManagerFactory emf = null;
	protected EntityManager em = null;
	
	public Main(){
	}
	
	protected void initDb(){
		LOG.info("Initialising Database");
		emf = Persistence.createEntityManagerFactory(ENTITY_MANAGER_NAME);
		em = emf.createEntityManager();
	}
	
	protected void closeDb(){
		LOG.info("Closing database");
		emf.close();
	}
	
	protected void populateDb() throws IOException{
		LOG.info("Populating database");
		populateConcepts(ClassLoader.getSystemResourceAsStream(CONCEPTS_INPUT));
	}

	protected void populateConcepts(InputStream stream) throws FileNotFoundException, IOException {
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
	
	private boolean stringToBoolean(String string){
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


	protected void runMain(){
		try{
			initDb();
			
			//Stuff goes here
			
		}finally{
			closeDb();
		}
	}
	
	public static void main(String[] args){
		new Main().runMain();
	}
	
}

//session.doWork(new Work(){
//@Override
//public void execute(Connection connection) throws SQLException {
//	//connection, finally!
//	//INSERT INTO RELATIONSHIP SELECT * FROM CSVREAD('input.csv')
//}
//});
