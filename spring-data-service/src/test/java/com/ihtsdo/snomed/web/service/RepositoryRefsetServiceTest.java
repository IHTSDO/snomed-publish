package com.ihtsdo.snomed.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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

import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.service.RefsetService;
import com.ihtsdo.snomed.web.repository.ConceptRepository;
import com.ihtsdo.snomed.web.repository.RefsetRepository;
import com.ihtsdo.snomed.web.testing.RefsetTestUtil;
import com.ihtsdo.snomed.web.testing.SpringProxyUtil;


/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
    /*,DbUnitTestExecutionListener.class*/ })
@ContextConfiguration(locations = {
        "classpath:sds-applicationContext.xml", 
        "classpath:sds-spring-data.xml",
        "classpath:test-spring-data.xml"})
public class RepositoryRefsetServiceTest {

    private static final Long   REFSET_ID           = Long.valueOf(5);
    private static final String PUBLIC_ID           = "pubid";
    private static final String PUBLIC_ID_UPDATED   = "pubidUpdated";
    private static final String TITLE               = "Foo";
    private static final String TITLE_UPDATED       = "FooUpdated";
    private static final String DESCRIPTION         = "Bar";
    private static final String DESCRIPTION_UPDATED = "BarUpdated";
    private final Concept concept;
    
    @Inject
    private RefsetService refsetService;

    @Mock
    private RefsetRepository repoMock;
    
    @Mock
    private ConceptRepository conceptMock;

    public RepositoryRefsetServiceTest() {
        concept = new Concept(1);
        concept.setSerialisedId(1234l);
    }
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetService) SpringProxyUtil.unwrapProxy(refsetService)), 
                "refsetRepository", 
                repoMock);
        
        ReflectionTestUtils.setField(
                ((RepositoryRefsetService) SpringProxyUtil.unwrapProxy(refsetService)), 
                "conceptRepository", 
                conceptMock);        
    }
    
    @Test
    public void create() throws NonUniquePublicIdException, ConceptNotFoundException {
        RefsetDto created = RefsetTestUtil.createDto(null, concept.getSerialisedId(), PUBLIC_ID, TITLE, DESCRIPTION);
        Refset persisted = RefsetTestUtil.createModelObject(REFSET_ID, concept, PUBLIC_ID, TITLE, DESCRIPTION);
        
        when(repoMock.save(any(Refset.class))).thenReturn(persisted);
        when(conceptMock.findBySerialisedId(concept.getSerialisedId())).thenReturn(concept);

        Refset returned = refsetService.create(created);

        ArgumentCaptor<Refset> refsetArgument = ArgumentCaptor.forClass(Refset.class);
        verify(repoMock, times(1)).save(refsetArgument.capture());
        verify(conceptMock, times(1)).findBySerialisedId(concept.getSerialisedId());
        verifyNoMoreInteractions(repoMock);
        verifyNoMoreInteractions(conceptMock);

        assertRefset(created, refsetArgument.getValue());
        assertEquals(persisted, returned);
    }
    
    @Test
    public void delete() throws RefsetNotFoundException {
        Refset deleted = RefsetTestUtil.createModelObject(REFSET_ID, concept, PUBLIC_ID, TITLE, DESCRIPTION);
        when(repoMock.findOne(REFSET_ID)).thenReturn(deleted);
        
        Refset returned = refsetService.delete(REFSET_ID);
        
        verify(repoMock, times(1)).findOne(REFSET_ID);
        verify(repoMock, times(1)).delete(deleted);
        verifyNoMoreInteractions(repoMock);
        
        assertEquals(deleted, returned);
    }
    
    @Test(expected = RefsetNotFoundException.class)
    public void deleteWhenRefsetIsNotFound() throws RefsetNotFoundException {
        when(repoMock.findOne(REFSET_ID)).thenReturn(null);
        
        refsetService.delete(REFSET_ID);
        
        verify(repoMock, times(1)).findOne(REFSET_ID);
        verifyNoMoreInteractions(repoMock);
    }
    
    @Test
    public void findAll() {
        List<Refset> refsets = new ArrayList<Refset>();
        when(repoMock.findAll()).thenReturn(refsets);
        
        List<Refset> returned = refsetService.findAll();
        
        verify(repoMock, times(1)).findAll(any(Sort.class));
        verifyNoMoreInteractions(repoMock);
        
        assertEquals(refsets, returned);
    } 
    
    @Test
    public void findById() {
        Refset refset = RefsetTestUtil.createModelObject(REFSET_ID, concept, PUBLIC_ID, TITLE, DESCRIPTION);
        when(repoMock.findOne(REFSET_ID)).thenReturn(refset);
        
        Refset returned = refsetService.findById(REFSET_ID);
        
        verify(repoMock, times(1)).findOne(REFSET_ID);
        verifyNoMoreInteractions(repoMock);
        
        assertEquals(refset, returned);
    }
    
    @Test
    public void update() throws RefsetNotFoundException, NonUniquePublicIdException, ConceptNotFoundException {
        RefsetDto updatedDto = RefsetTestUtil.createDto(REFSET_ID, concept.getSerialisedId(), PUBLIC_ID_UPDATED, TITLE_UPDATED, DESCRIPTION_UPDATED);
        Refset updatedRefset = Refset.getBuilder(concept, PUBLIC_ID_UPDATED, TITLE_UPDATED, DESCRIPTION_UPDATED).build();
        updatedRefset.setId(REFSET_ID);
        Refset refsetOriginal = RefsetTestUtil.createModelObject(REFSET_ID, concept, PUBLIC_ID, TITLE, DESCRIPTION);
        
        when(repoMock.findOne(updatedDto.getId())).thenReturn(refsetOriginal);
        when(repoMock.save(any(Refset.class))).thenReturn(updatedRefset);
        when(conceptMock.findBySerialisedId(concept.getSerialisedId())).thenReturn(concept);
        
        Refset returned = refsetService.update(updatedDto);
        
        verify(repoMock, times(1)).findOne(updatedDto.getId());
        verify(repoMock, times(1)).save(any(Refset.class));
        verify(conceptMock, times(1)).findBySerialisedId(concept.getSerialisedId());
        verifyNoMoreInteractions(repoMock);
        verifyNoMoreInteractions(conceptMock);
        
        assertRefset(updatedDto, returned);
    }
    
    @Test(expected = RefsetNotFoundException.class)
    public void updateWhenRefsetIsNotFound() throws RefsetNotFoundException, NonUniquePublicIdException, ConceptNotFoundException {
        RefsetDto updated = RefsetTestUtil.createDto(REFSET_ID, concept.getSerialisedId(), PUBLIC_ID_UPDATED, TITLE_UPDATED, DESCRIPTION_UPDATED);
        
        when(repoMock.findOne(updated.getId())).thenReturn(null);
//        when(conceptMock.findBySerialisedId(concept.getSerialisedId())).thenReturn(concept);

        refsetService.update(updated);
//
//        verify(repoMock, times(1)).findOne(updated.getId());
//        verify(conceptMock, times(1)).findBySerialisedId(concept.getSerialisedId());
//        verifyNoMoreInteractions(repoMock);
//        verifyNoMoreInteractions(conceptMock);
    }

    private void assertRefset(RefsetDto expected, Refset actual) {
        assertNotNull(actual.getConcept());
        assertEquals((expected.getConcept() == null ? 0 : expected.getConcept()), actual.getConcept().getSerialisedId());
        assertEquals((expected.getId() == null ? 0 : expected.getId()), actual.getId());
        assertEquals(expected.getPublicId(), actual.getPublicId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

}