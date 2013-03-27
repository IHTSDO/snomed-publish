package com.ihtsdo.snomed.canonical;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.ejb.HibernateEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.ihtsdo.snomed.canonical.model.Concept;
import com.ihtsdo.snomed.canonical.model.Relationship;

public class HibernateDatabaseImporter {
	
	private static final Logger LOG = LoggerFactory.getLogger( HibernateDatabaseImporter.class );
	
	public static void populateDb(InputStream conceptsStream, EntityManager em) throws IOException{
		LOG.info("Populating database");
		populateConcepts(conceptsStream, em);
		//populateConcepts(ClassLoader.getSystemResourceAsStream(CONCEPTS_INPUT), em);
	}
	
	protected static void populateConcepts(InputStream stream, EntityManager em) throws FileNotFoundException, IOException {
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

	protected static void populateRelationships(InputStream stream, EntityManager em) throws FileNotFoundException, IOException {
		LOG.info("Populating Relationships");
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
	
						Relationship relationship = new Relationship();
						
						try {
							relationship.setId(Long.parseLong(splitIt.next()));
							System.out.println("Relationship id: " + relationship.getId());
							
							long conceptId1 = Long.parseLong(splitIt.next());
							System.out.println("Looking for concept id 1: " + conceptId1);
							Concept concept1 = em.find(Concept.class, conceptId1);
							System.out.println("Found concept 1: [" + concept1.toString() + "]");
							relationship.setConcept1(concept1);
							
							relationship.setRelationshipType(Long.parseLong(splitIt.next()));
							
							long conceptId2 = Long.parseLong(splitIt.next());
							System.out.println("Looking for concept id 2: " + conceptId2);
							Concept concept2 = em.find(Concept.class, conceptId2);
							System.out.println("Found concept 2: [" + concept2.toString() + "]");
							relationship.setConcept2(concept2);
							
							relationship.setCharacteristicType(Integer.parseInt(splitIt.next()));
							relationship.setRefinability(stringToBoolean(splitIt.next()));
							relationship.setRelationShipGroup(Integer.parseInt(splitIt.next()));
							
							System.out.println("Inserting relationship: [" + relationship.toString() + "]");
							session.insert(relationship);
							System.out.println("Relationship inserted ok\n");
	
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
