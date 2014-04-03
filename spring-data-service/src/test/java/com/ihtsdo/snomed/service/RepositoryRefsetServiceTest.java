package com.ihtsdo.snomed.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import test.com.ihtsdo.snomed.web.RefsetTestUtil;
import test.com.ihtsdo.snomed.web.SpringProxyUtil;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.PlanDto;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.exception.InvalidSnomedDateFormatException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.OntologyFlavourNotFoundException;
import com.ihtsdo.snomed.exception.OntologyNotFoundException;
import com.ihtsdo.snomed.exception.OntologyVersionNotFoundException;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.OntologyFlavour;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.refset.Plan;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Status;
import com.ihtsdo.snomed.repository.ConceptRepository;
import com.ihtsdo.snomed.repository.refset.RefsetRepository;
import com.ihtsdo.snomed.service.refset.PlanService;
import com.ihtsdo.snomed.service.refset.RefsetService;
import com.ihtsdo.snomed.service.refset.RefsetService.SortOrder;
import com.ihtsdo.snomed.service.refset.RepositoryRefsetService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
    /*,DbUnitTestExecutionListener.class*/ })
@ContextConfiguration(locations = {
        "classpath:sds-applicationContext.xml",
        "classpath:test-spring-data.xml"})
public class RepositoryRefsetServiceTest {

    private static final Long   REFSET_ID           = Long.valueOf(5);
    private static final String PUBLIC_ID           = "pubid";
    private static final String PUBLIC_ID_UPDATED   = "pubidUpdated";
    private static final String TITLE               = "Foo";
    private static final String TITLE_UPDATED       = "FooUpdated";
    private static final String DESCRIPTION         = "Bar";
    private static final String DESCRIPTION_UPDATED = "BarUpdated";
    private final Concept refsetConcept;
    private final Concept moduleConcept = new Concept(333L);
    
    
    private static Ontology o = RefsetTestUtil.createOntology();
    private static OntologyFlavour of = o.getFlavours().iterator().next();
    private static OntologyVersion ov = of.getVersions().iterator().next();
    
    @Inject
    private RefsetService refsetService;

    @Mock
    private RefsetRepository refsetRepoMock;
    
    @Mock
    private ConceptRepository conceptRepoMock;
    
    @Mock
    private OntologyVersionService ontologyVersionServiceMock;
    
    
    @Mock
    PlanService planServiceMock;    

    public RepositoryRefsetServiceTest() {
        refsetConcept = new Concept(1L);
        refsetConcept.setSerialisedId(1234L);
    }
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetService) SpringProxyUtil.unwrapProxy(refsetService)), 
                "ontologyVersionService", 
                ontologyVersionServiceMock);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetService) SpringProxyUtil.unwrapProxy(refsetService)), 
                "refsetRepository", 
                refsetRepoMock);

        ReflectionTestUtils.setField(
                ((RepositoryRefsetService) SpringProxyUtil.unwrapProxy(refsetService)), 
                "planService", 
                planServiceMock);             
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetService) SpringProxyUtil.unwrapProxy(refsetService)), 
                "conceptRepository", 
                conceptRepoMock);        
    }
    
    @Test
    public void create() throws ValidationException, RefsetConceptNotFoundException, NonUniquePublicIdException, OntologyNotFoundException, InvalidSnomedDateFormatException, ParseException, OntologyVersionNotFoundException, OntologyFlavourNotFoundException{
        
        RefsetDto created = RefsetDto.getBuilder(
                Refset.Source.LIST, 
                Refset.Type.CONCEPT,
                false,
                of.getPublicId(),
                ov.getTaggedOn(),
                ConceptDto.parse(refsetConcept),
                ConceptDto.parse(moduleConcept), 
                TITLE,
                DESCRIPTION, 
                PUBLIC_ID,
                new PlanDto()).build();
        
        Refset persisted = Refset.getBuilder(
                Refset.Source.LIST, 
                Refset.Type.CONCEPT,  
                ov, 
                refsetConcept, 
                moduleConcept, 
                TITLE, 
                DESCRIPTION, 
                PUBLIC_ID, 
                new Plan()).build();
        
        when(refsetRepoMock.save(any(Refset.class))).thenReturn(persisted);
        when(planServiceMock.findById(any(Long.class))).thenReturn(new Plan());
        when(planServiceMock.create(any(PlanDto.class))).thenReturn(new Plan());
        
        when(conceptRepoMock.findByOntologyVersionAndSerialisedId(ov, refsetConcept.getSerialisedId())).thenReturn(refsetConcept);
        when(conceptRepoMock.findByOntologyVersionAndSerialisedId(ov, moduleConcept.getSerialisedId())).thenReturn(moduleConcept);
        
        when(ontologyVersionServiceMock.findByFlavourAndTaggedOn(
                created.getSnomedExtension(), 
                created.getSnomedReleaseDateAsDate())).thenReturn(ov);

        Refset returned = refsetService.create(created);

        ArgumentCaptor<Refset> refsetArgument = ArgumentCaptor.forClass(Refset.class);
        verify(refsetRepoMock, times(1)).save(refsetArgument.capture());
        verify(refsetRepoMock, times(1)).memberSize(anyString());
        //verify(refsetRepoMock, times(1)).findByPublicId(created.getPublicId());
        verify(conceptRepoMock, times(1)).findByOntologyVersionAndSerialisedId(ov, refsetConcept.getSerialisedId());
        verify(conceptRepoMock, times(1)).findByOntologyVersionAndSerialisedId(ov, moduleConcept.getSerialisedId());
        //verify(planServiceMock, times(1)).findById(any(Long.class));
        verify(planServiceMock, times(1)).create(any(PlanDto.class));
        verify(ontologyVersionServiceMock, times(1)).findByFlavourAndTaggedOn(
                created.getSnomedExtension(), created.getSnomedReleaseDateAsDate());
        verifyNoMoreInteractions(refsetRepoMock);
        verifyNoMoreInteractions(conceptRepoMock);
        verifyNoMoreInteractions(planServiceMock);
        verifyNoMoreInteractions(ontologyVersionServiceMock);

        assertRefset(created, refsetArgument.getValue());
        assertEquals(persisted, returned);
    }
    
//    @Test
//    public void delete() throws RefsetNotFoundException {
//        
//        Refset deleted = Refset.getBuilder(
//                REFSET_ID,
//                Refset.Source.LIST, 
//                Refset.Type.CONCEPT,  
//                ov, 
//                refsetConcept, 
//                moduleConcept, 
//                TITLE, 
//                DESCRIPTION, 
//                PUBLIC_ID, 
//                new Plan()).build();
//        
//        when(refsetRepoMock.findOne(REFSET_ID)).thenReturn(deleted);
//        
//        Refset returned = refsetService.delete(REFSET_ID);
//        
//        verify(refsetRepoMock, times(1)).findOne(REFSET_ID);
//        verify(refsetRepoMock, times(1)).delete(deleted);
//        verifyNoMoreInteractions(refsetRepoMock);
//        
//        assertEquals(deleted, returned);
//    }
    
//    @Test(expected = RefsetNotFoundException.class)
//    public void deleteWhenRefsetIsNotFound() throws RefsetNotFoundException {
//        when(refsetRepoMock.findOne(REFSET_ID)).thenReturn(null);
//        
//        refsetService.delete(REFSET_ID);
//        
//        verify(refsetRepoMock, times(1)).findOne(REFSET_ID);
//        verifyNoMoreInteractions(refsetRepoMock);
//    }
    
    @Test
    public void findAll() {
        List<Refset> refsets = new ArrayList<Refset>();
        when(refsetRepoMock.findAll()).thenReturn(refsets);
        
        List<Refset> returned = refsetService.findAll("title", SortOrder.ASC);
        
        verify(refsetRepoMock, times(1)).findByStatus(eq(Status.ACTIVE), any(Sort.class));
        verifyNoMoreInteractions(refsetRepoMock);
        
        assertEquals(refsets, returned);
    } 
    
//    @Test
//    public void findById() {
//        
//        Refset refset = Refset.getBuilder(
//                REFSET_ID,
//                Refset.Source.LIST, 
//                Refset.Type.CONCEPT,  
//                ov, 
//                refsetConcept, 
//                moduleConcept, 
//                TITLE, 
//                DESCRIPTION, 
//                PUBLIC_ID, 
//                new Plan()).build();        
//        
//        when(refsetRepoMock.findOne(REFSET_ID)).thenReturn(refset);
//        
//        Refset returned = refsetService.findById(REFSET_ID);
//        
//        verify(refsetRepoMock, times(1)).findOne(REFSET_ID);
//        verifyNoMoreInteractions(refsetRepoMock);
//        
//        assertEquals(refset, returned);
//    }
    
    @Test
    public void update() throws ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException, RefsetNotFoundException, RefsetConceptNotFoundException, NonUniquePublicIdException, OntologyNotFoundException, InvalidSnomedDateFormatException, ParseException, OntologyVersionNotFoundException, OntologyFlavourNotFoundException{
                
        RefsetDto updatedDto = RefsetDto.getBuilder(
                Refset.Source.LIST, 
                Refset.Type.CONCEPT, 
                false,
                of.getPublicId(), 
                ov.getTaggedOn(),
                ConceptDto.parse(refsetConcept),
                ConceptDto.parse(moduleConcept), 
                TITLE_UPDATED,
                DESCRIPTION_UPDATED, 
                PUBLIC_ID_UPDATED,
                new PlanDto()).build();         
        
        
        Refset updatedRefset = Refset.getBuilder(
                Refset.Source.LIST, 
                Refset.Type.CONCEPT,  
                ov, 
                refsetConcept, 
                moduleConcept, 
                TITLE_UPDATED, 
                DESCRIPTION_UPDATED, 
                PUBLIC_ID_UPDATED, 
                new Plan()).build();  
        
        
        updatedRefset.setId(REFSET_ID);
        
        Refset refsetOriginal = Refset.getBuilder(
                REFSET_ID,
                Refset.Source.LIST, 
                Refset.Type.CONCEPT,  
                ov, 
                refsetConcept, 
                moduleConcept, 
                TITLE, 
                DESCRIPTION, 
                PUBLIC_ID, 
                new Plan()).build();        
        
        //when(refsetRepoMock.findOne(updatedDto.getId())).thenReturn(refsetOriginal);
        when(refsetRepoMock.findByPublicIdAndStatus(updatedDto.getPublicId(), Status.ACTIVE)).thenReturn(refsetOriginal);
        when(refsetRepoMock.save(refsetOriginal)).thenReturn(updatedRefset);
        when(planServiceMock.findById(any(Long.class))).thenReturn(new Plan());
        when(planServiceMock.update(any(PlanDto.class))).thenReturn(new Plan());
        when(conceptRepoMock.findByOntologyVersionAndSerialisedId(ov, refsetConcept.getSerialisedId())).thenReturn(refsetConcept);
        when(conceptRepoMock.findByOntologyVersionAndSerialisedId(ov, moduleConcept.getSerialisedId())).thenReturn(moduleConcept);
        when(ontologyVersionServiceMock.findByFlavourAndTaggedOn(
                updatedDto.getSnomedExtension(), 
                updatedDto.getSnomedReleaseDateAsDate())).thenReturn(ov);
        
        Refset returned = refsetService.update(updatedDto);
        
        //verify(refsetRepoMock, times(1)).findOne(updatedDto.getId());
        verify(refsetRepoMock, times(1)).save(any(Refset.class));
        verify(refsetRepoMock, times(1)).findByPublicIdAndStatus(updatedDto.getPublicId(), Status.ACTIVE);
        verify(refsetRepoMock, times(1)).memberSize(anyString());
        verify(conceptRepoMock, times(1)).findByOntologyVersionAndSerialisedId(ov, refsetConcept.getSerialisedId());
        verify(conceptRepoMock, times(1)).findByOntologyVersionAndSerialisedId(ov, moduleConcept.getSerialisedId());
        verify(planServiceMock, times(1)).findById(any(Long.class));
        verify(planServiceMock, times(1)).update(any(PlanDto.class));

        verify(ontologyVersionServiceMock, times(1)).findByFlavourAndTaggedOn(
                updatedDto.getSnomedExtension(), updatedDto.getSnomedReleaseDateAsDate());        
        
        verifyNoMoreInteractions(refsetRepoMock);
        verifyNoMoreInteractions(conceptRepoMock);
        verifyNoMoreInteractions(planServiceMock);
        verifyNoMoreInteractions(ontologyVersionServiceMock);
        
        assertRefset(updatedDto, returned);
    }
    
    @Test(expected = RefsetNotFoundException.class)
    public void updateWhenRefsetIsNotFound() throws RefsetNotFoundException, RefsetConceptNotFoundException, ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException, NonUniquePublicIdException, OntologyNotFoundException, InvalidSnomedDateFormatException, OntologyVersionNotFoundException, OntologyFlavourNotFoundException {

        RefsetDto updated = RefsetDto.getBuilder(
                Refset.Source.LIST, 
                Refset.Type.CONCEPT, 
                false,
                of.getPublicId(),
                ov.getTaggedOn(),
                ConceptDto.parse(refsetConcept),
                ConceptDto.parse(moduleConcept), 
                TITLE_UPDATED,
                DESCRIPTION_UPDATED, 
                PUBLIC_ID_UPDATED,
                new PlanDto()).build();        
        
        //when(refsetRepoMock.findOne(updated.getId())).thenReturn(null);
//        when(conceptMock.findBySerialisedId(concept.getSerialisedId())).thenReturn(concept);

        refsetService.update(updated);
//
//        verify(repoMock, times(1)).findOne(updated.getId());
//        verify(conceptMock, times(1)).findBySerialisedId(concept.getSerialisedId());
//        verifyNoMoreInteractions(repoMock);
//        verifyNoMoreInteractions(conceptMock);
    }

    private void assertRefset(RefsetDto expected, Refset actual) {
        assertNotNull(actual.getRefsetConcept());
        assertEquals((expected.getRefsetConcept() == null ? 0 : expected.getRefsetConcept().getIdAsLong()), actual.getRefsetConcept().getSerialisedId());
        //assertEquals((expected.getId() == null ? null : expected.getId()), actual.getId());
        assertEquals(expected.getPublicId(), actual.getPublicId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        RepositoryRefsetPlanServiceTest.assertRefsetPlan(expected.getPlan(), actual.getPlan());
    }

}