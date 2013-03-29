package com.ihtsdo.snomed.canonical;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger( Main.class );
    protected static final String CONCEPTS_INPUT = "sct1_Concepts_Core_INT_20130131.ont.txt";
    private static final String ENTITY_MANAGER_NAME = "persistenceManager";
    private EntityManagerFactory emf = null;
    protected EntityManager em = null;

    protected void initDb(){
        LOG.info("Initialising Database");
        emf = Persistence.createEntityManagerFactory(ENTITY_MANAGER_NAME);
        em = emf.createEntityManager();
    }

    protected void closeDb(){
        LOG.info("Closing database");
        emf.close();
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
