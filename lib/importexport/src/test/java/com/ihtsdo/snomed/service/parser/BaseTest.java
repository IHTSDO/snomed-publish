package com.ihtsdo.snomed.service.parser;

import java.sql.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.service.parser.HibernateParser.Mode;
import com.ihtsdo.snomed.service.parser.HibernateParserFactory.Parser;


public abstract class BaseTest {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);
    
    protected static final Date DEFAULT_TAGGED_ON_DATE;
    
    static{
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();
        DEFAULT_TAGGED_ON_DATE = new Date(utilDate.getTime());        
    }
    
    private static final String DATA_FOLDER = "data/";
    
    protected static final String TEST_RF1_GROUP_STATEMENTS         = DATA_FOLDER + "test.group.statements.rf1";
    protected static final String TEST_RF1_GROUP_CONCEPTS           = DATA_FOLDER + "test.group.concepts.rf1";

    protected static final String TEST_CANONICAL_STATEMENTS         = DATA_FOLDER + "test.statements.canonical";
    protected static final String TEST_CANONICAL_STATEMENTS_ERROR   = DATA_FOLDER + "test.error.statements.canonical";
    
    protected static final String TEST_RF1_IS_KIND_OF_STATEMENTS    = DATA_FOLDER + "test.iskindof.statements.rf1";
    protected static final String TEST_RF1_IS_KIND_OF_CONCEPTS      = DATA_FOLDER + "test.iskindof.concept.rf1";
    
    protected static final String TEST_RF1_STATEMENTS               = DATA_FOLDER + "test.statements.rf1";
    protected static final String TEST_RF1_CONCEPTS                 = DATA_FOLDER + "test.concepts.rf1";
    protected static final String TEST_RF1_DESCRIPTIONS             = DATA_FOLDER + "test.descriptions.rf1";
    protected static final String TEST_RF1_CONCEPTS_ERROR           = DATA_FOLDER + "test.error.concepts.rf1";
    protected static final String TEST_RF1_STATEMENTS_ERROR         = DATA_FOLDER + "test.error.statements.rf1";
    protected static final String TEST_RF1_DESCRIPTIONS_ERROR       = DATA_FOLDER + "test.error.descriptions.rf1";
    
    protected static final String TEST_CP_STATEMENTS                = DATA_FOLDER + "test.statements.childparent";
    protected static final String TEST_CP_STATEMENTS_ERROR          = DATA_FOLDER + "test.error.statements.childparent";
    
    protected static final String TEST_RF2_STATEMENTS               = DATA_FOLDER + "test.statements.rf2";
    protected static final String TEST_RF2_CONCEPTS                 = DATA_FOLDER + "test.concepts.rf2";
    protected static final String TEST_RF2_DESCRIPTIONS             = DATA_FOLDER + "test.descriptions.rf2";
    protected static final String TEST_RF2_STATEMENTS_ERROR         = DATA_FOLDER + "test.error.statements.rf2";
    protected static final String TEST_RF2_CONCEPTS_ERROR           = DATA_FOLDER + "test.error.concepts.rf2";
    protected static final String TEST_RF2_DESCRIPTIONS_ERROR       = DATA_FOLDER + "test.error.descriptions.rf2";

    protected static EntityManagerFactory emf = null;
    protected static EntityManager em = null;

    protected static OntologyVersion ontologyVersion;




}
