package com.ihtsdo.snomed.web.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.server.samples.context.SecurityRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.service.RefsetService;
import com.ihtsdo.snomed.web.model.Role;
import com.ihtsdo.snomed.web.model.User;
import com.ihtsdo.snomed.web.service.security.OpenIdUserDetailsService;
import com.ihtsdo.snomed.web.testing.RefsetTestUtil;
import com.ihtsdo.snomed.web.testing.SpringProxyUtil;

/**
 * @author Henrik Pettersen @ http://sparklingideas.co.uk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {
        "classpath:applicationContext.xml",
        "classpath:sds-applicationContext.xml",
        "classpath:sds-spring-data.xml",
        "classpath:spring-mvc.xml",
        "classpath:spring-security.xml",
        "classpath:spring-data.xml",
        "classpath:test-applicationContext.xml",
        "classpath:test-spring-data.xml"})
public class RefsetControllerTest {
    private static final String DEFAULT_ACCESS_ROLE = "ROLE_USER";

    private MockMvc mockMvc;
    
    @Inject
    private RefsetController refsetController;

    @Inject
    private WebApplicationContext webApplicationContext;
    
    @Inject
    private FilterChainProxy springSecurityFilterChain;

    @Mock
    private RefsetService refsetServiceMock;
    
    @Mock
    private OpenIdUserDetailsService openIdUserDetailsService;
    
    private Concept concept = new Concept(1l);

    private User user = new UserBuilder()
        .firstname("firstname")
        .lastname("lastname")
        .roles(Arrays.asList(new Role(DEFAULT_ACCESS_ROLE)))
        .build();
        
    private Refset r1 = new RefsetBuilder()
        .id(1)
        .title("title1")
        .concept(concept)
        .description("description1")
        .publicId("pub1")
        .build();
    
    private Refset r2 = new RefsetBuilder()
        .id(2)
        .concept(concept)
        .title("title2")
        .description("description2")
        .publicId("pub2")
        .build();    
    
    public RefsetControllerTest(){
        concept.setSerialisedId(1234l);
    }
    
    @PostConstruct
    public void init() throws Exception{
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
        
        //this must be called for the @Mock annotations above to be processed.
        MockitoAnnotations.initMocks(this);
        
        //Controllers are proxy classes, because of some of the annotations on the class/methods
        //Because we are referencing proxies, we can not set the mock directly. Instead, 
        //we have to unwrap the instance hiding behind the proxy, and set the mock on this instead. 
        ((RefsetController) SpringProxyUtil.unwrapProxy(refsetController)).refsetService = refsetServiceMock;
        
        when (openIdUserDetailsService.loadUserByUsername("bob")).thenReturn(user);
    }
    
    @Before
    public void setUp() throws Exception {
        Mockito.reset(refsetServiceMock);
    }
    
    @Test
    //DBUNIT: @ExpectedDatabase(value="toDoData-add-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void shouldGetAllRefsets() throws Exception{
        when(refsetServiceMock.findAll()).thenReturn(Arrays.asList(r1, r2));
        
        mockMvc.perform(get("/refsets")
            .with(SecurityRequestPostProcessors.createUserDetailsRequestPostProcessor("bob")
                        .userDetailsService(openIdUserDetailsService)))
            .andExpect(status().isOk())
            .andExpect(view().name("/refset/refsets"))
            .andExpect(content().string(containsString("refsets")))
//            .andExpect(model().attribute("user", notNullValue()))
//            .andExpect(model().attribute("user", allOf(
//                            hasProperty("firstname", is("firstname")),
//                            hasProperty("lastname", is("lastname"))
//                    )
//            ))
            .andExpect(model().attribute("refsets", hasSize(2)))
            .andExpect(model().attribute("refsets", hasItem(
                    allOf(
                            hasProperty("id", is(1L)),
                            hasProperty("concept", is(concept)),
                            hasProperty("description", is("description1")),
                            hasProperty("title", is("title1")),
                            hasProperty("publicId", is("pub1"))
                    )
            )))
            .andExpect(model().attribute("refsets", hasItem(
                    allOf(
                            hasProperty("id", is(2L)),
                            hasProperty("concept", is(concept)),
                            hasProperty("description", is("description2")),
                            hasProperty("title", is("title2")),
                            hasProperty("publicId", is("pub2"))
                    )
            )));

        verify(refsetServiceMock, times(1)).findAll();
        verifyNoMoreInteractions(refsetServiceMock);
    }
    
    @Test
    public void shouldGetSpecificRefset() throws Exception{
        when(refsetServiceMock.findByPublicId(any(String.class))).thenReturn(r1);
        mockMvc.perform(get("/refset/" + r1.getPublicId())
            .with(SecurityRequestPostProcessors.createUserDetailsRequestPostProcessor("bob")
                        .userDetailsService(openIdUserDetailsService)))
            .andExpect(status().isOk())
            .andExpect(view().name("/refset/refset"))
            .andExpect(content().string(containsString("")))
            .andExpect(model().attribute("refset", 
                    allOf(
                        hasProperty("id", is(1L)),
                        hasProperty("concept", is(concept)),
                        hasProperty("description", is("description1")),
                        hasProperty("title", is("title1")),
                        hasProperty("publicId", is("pub1"))
                    )
                ));
        verify(refsetServiceMock, times(1)).findByPublicId("pub1");
        verifyNoMoreInteractions(refsetServiceMock);
    }

    
    @Test
    public void shouldDisplayNewRefsetPage() throws Exception{
        mockMvc.perform(get("/refset/new")
            .with(SecurityRequestPostProcessors.createUserDetailsRequestPostProcessor("bob")
                        .userDetailsService(openIdUserDetailsService)))
            .andExpect(status().isOk())
            .andExpect(view().name("/refset/new.refset"))
            .andExpect(content().string(containsString("")))
            .andExpect(model().attribute("refset", notNullValue()))
            //.andExpect(model().attribute("user", notNullValue()));
            ;
    } 
    
    @Test
    public void shouldDeleteRefset() throws Exception{
        when(refsetServiceMock.delete(any(String.class))).thenReturn(r1);
        
        mockMvc.perform(post("/refset/pub1/delete")
            .with(SecurityRequestPostProcessors
                        .createUserDetailsRequestPostProcessor("bob")
                        .userDetailsService(openIdUserDetailsService)))
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/refsets"))
            .andExpect(flash().attribute(RefsetController.FEEDBACK_MESSAGE, 
                    is("Deleted refset pub1: title1")));

            verify(refsetServiceMock, times(1)).delete("pub1");
            verifyNoMoreInteractions(refsetServiceMock);
    }     
    
    @Test
    public void shouldDisplayEditRefsetPage() throws Exception{
        when(refsetServiceMock.findByPublicId(any(String.class))).thenReturn(r1);
        
        mockMvc.perform(get("/refset/pub1/edit")
            .with(SecurityRequestPostProcessors
                        .createUserDetailsRequestPostProcessor("bob")
                        .userDetailsService(openIdUserDetailsService)))
            .andExpect(status().isOk())
            .andExpect(view().name("/refset/edit.refset"))
            .andExpect(content().string(containsString("")))
            .andExpect(model().attribute("refset", 
                    allOf(
                        hasProperty("id", is(1L)),
                        hasProperty("concept", is(concept.getSerialisedId())),
                        hasProperty("description", is("description1")),
                        hasProperty("title", is("title1")),
                        hasProperty("publicId", is("pub1"))
                    )
                ))
            //.andExpect(model().attribute("user", notNullValue()))
                ;
        
        verify(refsetServiceMock, times(1)).findByPublicId("pub1");
        verifyNoMoreInteractions(refsetServiceMock);
    }  
    
    @Test
    public void shouldCreateNewRefset() throws Exception{
        when(refsetServiceMock.create(any(RefsetDto.class))).thenReturn(r1);
        RefsetDto refsetDto = RefsetTestUtil.createDto(1L, 1234l, "pub2", "title2", "description2");

        mockMvc.perform(post("/refset/new")
                .with(SecurityRequestPostProcessors
                            .createUserDetailsRequestPostProcessor("bob")
                            .userDetailsService(openIdUserDetailsService)
                     )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", refsetDto.getId().toString())
                .param("concept", refsetDto.getConcept().toString())
                .param("publicId", refsetDto.getPublicId())
                .param("title", refsetDto.getTitle())
                .param("description", refsetDto.getDescription())
            )
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/refsets"))
            .andExpect(flash().attribute(RefsetController.FEEDBACK_MESSAGE, 
                    is("Added refset pub1: title1")));
        
        verify(refsetServiceMock, times(1)).create(refsetDto);
        verify(refsetServiceMock, times(1)).findByPublicId(refsetDto.getPublicId());
        verifyNoMoreInteractions(refsetServiceMock);
    }  
    
    @Test
    public void shouldUpdateRefset() throws Exception{
        when(refsetServiceMock.update(any(RefsetDto.class))).thenReturn(r2);
        when(refsetServiceMock.findById(any(Long.class))).thenReturn(r1);
        RefsetDto refsetDto = RefsetTestUtil.createDto(1L, 1234l, "pub2", "title2", "description2");
        
        mockMvc.perform(post("/refset/pub1/edit")
                .with(SecurityRequestPostProcessors
                            .createUserDetailsRequestPostProcessor("bob")
                            .userDetailsService(openIdUserDetailsService)
                     )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", refsetDto.getId().toString())
                .param("concept", refsetDto.getConcept().toString())
                .param("publicId", refsetDto.getPublicId())
                .param("title", refsetDto.getTitle())
                .param("description", refsetDto.getDescription())
            )
            .andExpect(status().isFound())
            .andExpect(view().name("redirect:/refset/" + r2.getPublicId()))
            .andExpect(flash().attribute(RefsetController.FEEDBACK_MESSAGE, 
                    is("Updated refset pub2: title2")));
        
        verify(refsetServiceMock, times(1)).update(refsetDto);
        verify(refsetServiceMock, times(1)).findById(1L);
        verify(refsetServiceMock, times(1)).findByPublicId(refsetDto.getPublicId());
        verifyNoMoreInteractions(refsetServiceMock);
    }     
        
    
    @Test public void failOnExistingPublicIdOnCreate() throws Exception{
        when(refsetServiceMock.findByPublicId(any(String.class))).thenReturn(r1);

        mockMvc.perform(post("/refset/new")
                .with(SecurityRequestPostProcessors
                            .createUserDetailsRequestPostProcessor("bob")
                            .userDetailsService(openIdUserDetailsService)
                     )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("concept", "1234")
                .param("publicId", "pub1")
                .param("title", "title1")
                .param("description", "description1")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/refset/new.refset"));
        
        verify(refsetServiceMock, times(1)).findByPublicId(any(String.class));
        verifyNoMoreInteractions(refsetServiceMock);
    }
    
    @Test public void failOnExistingPublicIdOnUpdate() throws Exception{
        when(refsetServiceMock.findByPublicId(any(String.class))).thenReturn(r2);
        when(refsetServiceMock.findById(any(Long.class))).thenReturn(r1);
        mockMvc.perform(post("/refset/pub1/edit")
                .with(SecurityRequestPostProcessors
                            .createUserDetailsRequestPostProcessor("bob")
                            .userDetailsService(openIdUserDetailsService)
                     )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("concept", "1234")
                .param("publicId", "pub2")
                .param("title", "title2")
                .param("description", "description2")
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/refset/edit.refset"));
        
        verify(refsetServiceMock, times(1)).findById(any(Long.class));
        verify(refsetServiceMock, times(1)).findByPublicId(any(String.class));
        verifyNoMoreInteractions(refsetServiceMock);        
    }
    
    public static class UserBuilder{
        private User u;
        
        public UserBuilder(){
            u = new User();
        }
        
        public UserBuilder firstname(String firstname){
            u.setFirstname(firstname);
            return this;
        }
        
        public UserBuilder lastname(String lastname){
            u.setLastname(lastname);
            return this;
        }
        
        public UserBuilder roles(List<Role> roles){
            u.setRoles(roles);
            return this;
        }
        
        public User build(){
            return u;
        }
    }
    
    public static class RefsetBuilder{
        
        private Refset r;
        
        public RefsetBuilder(){
            r = new Refset();
        }
        
        public RefsetBuilder concept(Concept concept){
            r.setConcept(concept);
            return this;
        }
        
        public RefsetBuilder id(long id){
            r.setId(id);
            return this;
        }
        
        public RefsetBuilder title(String title){
            r.setTitle(title);
            return this;
        }
        
        public RefsetBuilder description(String description){
            r.setDescription(description);
            return this;
        }
        
        public RefsetBuilder publicId(String publicId){
            r.setPublicId(publicId);
            return this;
        }        
        
        public Refset build(){
            return r;
        }
    }


}

//OpenIDAuthenticationProvider provider = new OpenIDAuthenticationProvider();
//provider.setUserDetailsService(userDetailsService);

//OpenIDAuthenticationToken o = new OpenIDAuthenticationToken(u, Arrays.asList(new GrantedAuthorityImpl("DEFAULT_ACCESS_ROLE")), 
//      "https://www.google.com/accounts/o8/id", new ArrayList<OpenIDAttribute>());
//
//OpenIDAuthenticationToken token = new OpenIDAuthenticationToken(
//      OpenIDAuthenticationStatus.SUCCESS,NON_REGISTERED_OPENID_USER, 
//      MESSAGE, new ArrayList<OpenIDAttribute>());

//import static org.hamcrest.Matchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



//Mockito.reset(refsetServiceMock);
//principal = new Principal() {
//    @Override
//    public String getName() {
//        return "TEST_PRINCIPAL";
//    }
//};


//mockMvc = MockMvcBuilders.standaloneSetup(refsetController)
//      .addFilter(springSecurityFilterChain)
//      .build();




//@Test
//@ExpectedDatabase(value="toDoData-add-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
//public void shouldGetAllRefsets() throws Exception{



//JSON
//.contentType(IntegrationTestUtil.APPLICATION_JSON_UTF8)
//.content(IntegrationTestUtil.convertObjectToJsonBytes(formObject))

//FORM OBJECT





//.andExpect(model().attributeHasFieldErrors("todo", "title"))
//.andExpect(model().attribute("refset", hasProperty("id", nullValue())))
//.andExpect(model().attribute("refset", hasProperty("publicId", is("pub1"))))
//.andExpect(model().attribute("refset", hasProperty("title", is("title1"))))
//.andExpect(model().attribute("refset", hasProperty("description", is("description1"))))
