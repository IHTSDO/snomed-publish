package com.ihtsdo.snomed.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.xml.sax.SAXException;

import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.web.model.SparqlResults;
import com.ihtsdo.snomed.web.model.SparqlResults.Binding;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={
//        "classpath:applicationContext.xml",
//        "classpath:sds-applicationContext.xml",
//        "classpath:sds-spring-data.xml",
//        "classpath:spring-mvc.xml",
//        "classpath:spring-security.xml",
//        "classpath:spring-data.xml",
//        "classpath:test-applicationContext.xml",
//        "classpath:test-spring-data.xml"})
//@Transactional
public class SparqlServiceTest {
    
    String validQuery = 
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
        "SELECT * WHERE {\n" +
        " ?s ?p ?o\n" +
        "} LIMIT 10\n";
    
    String validResponse;
    
    @Value("${sparql.server.url}")
    private URL sparqlServerUrl;
    
    @Inject
    private SparqlService sparql;

    private MockRestServiceServer mockServer;
    
    @PersistenceContext(unitName="hibernatePersistenceUnit")
    EntityManager em;
    
    private Ontology ontology;
    
    private Set<String> variables = new HashSet<>(Arrays.asList("s", "p", "o"));

    @Before
    public void setUp() throws Exception {
        mockServer = MockRestServiceServer.createServer(sparql.restTemplate);
        validResponse = FileUtils.readFileToString(new File(getClass().getClassLoader().getResource("sample.sparql.response.xml").toURI()));
        initDatabase();
    }
    
    private void initDatabase(){
        ontology = new Ontology();
        em.persist(ontology);
        
        Statement s1 = new Statement(4576818021l);
        Statement s2 = new Statement(4216986021l);
        Statement s3 = new Statement(4502954025l);
        Statement s4 = new Statement(4112168023l);
        Statement s5 = new Statement(4033296024l);
        Statement s6 = new Statement(4000115029l);
        Statement s7 = new Statement(4514413021l);
        Statement s8 = new Statement(4492804029l);
        Statement s9 = new Statement(4346696020l);
        Statement s10 = new Statement(4507824029l);
        
        Concept c1 = new Concept(173722008l);
        Concept c2 = new Concept(407878008l);
        Concept c3 = new Concept(405388000l);
        Concept c4 = new Concept(200102005l);
        Concept c5 = new Concept(54678006l);
        Concept c6 = new Concept(27246003l);
        Concept c7 = new Concept(426391000l);
        Concept c8 = new Concept(232797005l);
        Concept c9 = new Concept(35655004l);
        Concept c10 = new Concept(274784001l);        
        
        ontology.addStatement(s1);
        ontology.addStatement(s2);
        ontology.addStatement(s3);
        ontology.addStatement(s4);
        ontology.addStatement(s5);
        ontology.addStatement(s6);
        ontology.addStatement(s7);
        ontology.addStatement(s8);
        ontology.addStatement(s9);
        ontology.addStatement(s10);
        
        ontology.addConcept(c1);
        ontology.addConcept(c2);
        ontology.addConcept(c3);
        ontology.addConcept(c4);
        ontology.addConcept(c5);
        ontology.addConcept(c6);
        ontology.addConcept(c7);
        ontology.addConcept(c8);
        ontology.addConcept(c9);
        ontology.addConcept(c10);
        
        s1.setOntology(ontology);
        s2.setOntology(ontology);
        s3.setOntology(ontology);
        s4.setOntology(ontology);
        s5.setOntology(ontology);
        s6.setOntology(ontology);
        s7.setOntology(ontology);
        s8.setOntology(ontology);
        s9.setOntology(ontology);
        s10.setOntology(ontology);
        
        c1.setOntology(ontology);
        c2.setOntology(ontology);
        c3.setOntology(ontology);
        c4.setOntology(ontology);
        c5.setOntology(ontology);
        c6.setOntology(ontology);
        c7.setOntology(ontology);
        c8.setOntology(ontology);
        c9.setOntology(ontology);
        c10.setOntology(ontology);
        
        em.persist(s1);
        em.persist(s2);
        em.persist(s3);
        em.persist(s4);
        em.persist(s5);
        em.persist(s6);
        em.persist(s7);
        em.persist(s8);
        em.persist(s9);
        em.persist(s10);
        
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);
        em.persist(c4);
        em.persist(c5);
        em.persist(c6);
        em.persist(c7);
        em.persist(c8);
        em.persist(c9);
        em.persist(c10);
        
        em.flush();
        em.clear();
    }

    //@Test
    public void shouldExecuteSparqlQuery() throws RestClientException, XPathExpressionException, URISyntaxException, ParserConfigurationException, SAXException, IOException {
        mockServer.expect(requestTo(sparqlServerUrl.toString()))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().string("query=" + URLEncoder.encode(validQuery, "UTF-8")))
            .andRespond(withSuccess(validResponse, MediaType.TEXT_XML));

        SparqlResults result = sparql.runQuery(validQuery, ontology.getId());
        mockServer.verify();
        
        Map<String, Binding> map = result.getResults().iterator().next();
        for (String var : map.keySet()){
            String source = map.get(var).source.toString();
            Object sourceBackedObject = map.get(var).sourceBackedObject;
            assertTrue(result.getVariables().contains(var));
            assertTrue(variables.contains(var));
            if (source.contains("statement")){
                assertTrue(sourceBackedObject instanceof Statement);
                assertEquals(((Statement)sourceBackedObject).getSerialisedId(), sparql.getSerialisedId(source));
                assertTrue(map.get(var).source.toString().startsWith("http://browser.sparklingideas.co.uk/ontology/" + ontology.getId() + "/statement/"));
            }
            else if (source.contains("concept")){
                assertTrue(sourceBackedObject instanceof Concept);
                assertEquals(((Concept)sourceBackedObject).getSerialisedId(), sparql.getSerialisedId(source));
                assertTrue(map.get(var).source.toString().startsWith("http://browser.sparklingideas.co.uk/ontology/" + ontology.getId() + "/concept/"));
            }
            else if (source.contains("description")){
                assertTrue(sourceBackedObject instanceof Description);
                assertEquals(((Description)sourceBackedObject).getSerialisedId(), sparql.getSerialisedId(source));
                assertTrue(map.get(var).source.toString().startsWith("http://browser.sparklingideas.co.uk/ontology/" + ontology.getId() + "/description/"));
            }
        }
    }   
}
