package com.ihtsdo.snomed.browse;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:applicationContext.xml", 
        "classpath:test-applicationContext.xml", 
        "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml"})
@Transactional
public class MainControllerTest{
    
    @Autowired private MainController mainController;
    @PersistenceContext private EntityManager em;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();  
    }
    
//    @Transactional
//    @Test
//    public void shouldDeleteOntology() throws Exception {
//  
//        Ontology o = new Ontology();
//        o.setName("name");
//        em.persist(o);
//        em.find(Ontology.class, 1l);
//        assertNotNull(o);
//
//        mockMvc.perform(get("/ontology/1/delete"));
//
//        o = em.find(Ontology.class, 1l);
//        assertNull(o);
//    }
}
