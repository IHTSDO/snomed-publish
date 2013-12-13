package com.ihtsdo.snomed.web.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.SnapshotNotFoundException;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Snapshot;
import com.ihtsdo.snomed.repository.refset.SnapshotRepository;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.refset.RepositorySnapshotService;
import com.ihtsdo.snomed.service.refset.SnapshotService;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@ContextConfiguration(locations = {
        "classpath:sds-applicationContext.xml", 
        "classpath:test-spring-data.xml"})
public class RepositorySnapshotServiceTest {
    private static final Long   SNAPSHOT_ID         = Long.valueOf(5);
    private static final String PUBLIC_ID           = "pubid";
    private static final String PUBLIC_ID_UPDATED   = "pubidUpdated";
    private static final String TITLE               = "Foo";
    private static final String TITLE_UPDATED       = "FooUpdated";
    private static final String DESCRIPTION         = "Bar";
    private static final String DESCRIPTION_UPDATED = "BarUpdated";

    Concept c1, c2, c3, c4;
    ConceptDto cd1, cd2, cd3, cd4;
    
    private Set<Concept>        concepts = new HashSet<Concept>();
    private Set<Concept>        conceptsUpdated = new HashSet<Concept>();
    private Set<ConceptDto>     conceptDtos = new HashSet<ConceptDto>();
    private Set<ConceptDto>     conceptDtosUpdated = new HashSet<ConceptDto>();

    @Inject
    private SnapshotService snapshotService;
    
    @Mock
    private SnapshotRepository repoMock;

    @Mock
    private ConceptService conceptMock;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(
                ((RepositorySnapshotService) SpringProxyUtil.unwrapProxy(snapshotService)), 
                "snapshotRepository", 
                repoMock);            
        
        ReflectionTestUtils.setField(
                ((RepositorySnapshotService) SpringProxyUtil.unwrapProxy(snapshotService)), 
                "conceptService", 
                conceptMock);      
        
        c1 = new Concept(1l);
        c2 = new Concept(2l);
        c3 = new Concept(3l);
        c4 = new Concept(4l);
        
        cd1 = new ConceptDto(c1.getSerialisedId());
        cd2 = new ConceptDto(c2.getSerialisedId());
        cd3 = new ConceptDto(c3.getSerialisedId());
        cd4 = new ConceptDto(c4.getSerialisedId());
        
        concepts.addAll(Arrays.asList(c1, c2));
        conceptsUpdated.addAll(Arrays.asList(c3, c4));
        
        conceptDtos.addAll(Arrays.asList(cd1, cd2));
        conceptDtosUpdated.addAll(Arrays.asList(cd3, cd4));
        
        when(conceptMock.findBySerialisedId(c1.getSerialisedId())).thenReturn(c1);
        when(conceptMock.findBySerialisedId(c2.getSerialisedId())).thenReturn(c2);
        when(conceptMock.findBySerialisedId(c3.getSerialisedId())).thenReturn(c3);
        when(conceptMock.findBySerialisedId(c4.getSerialisedId())).thenReturn(c4);
    }
    
    public RepositorySnapshotServiceTest() {}
    
    @Test
    public void create() throws RefsetConceptNotFoundException, NonUniquePublicIdException{
        SnapshotDto created = SnapshotDto.getBuilder(null, TITLE, DESCRIPTION, PUBLIC_ID, conceptDtos).build();
        Snapshot persisted = Snapshot.getBuilder(PUBLIC_ID, TITLE, DESCRIPTION, concepts).build();
        
        when(repoMock.save(any(Snapshot.class))).thenReturn(persisted);

        Snapshot returned = snapshotService.create(created);

        ArgumentCaptor<Snapshot> refsetArgument = ArgumentCaptor.forClass(Snapshot.class);
        verify(repoMock, times(1)).save(refsetArgument.capture());
        verify(conceptMock, times(1)).findBySerialisedId(c1.getSerialisedId());
        verify(conceptMock, times(1)).findBySerialisedId(c2.getSerialisedId());
        verifyNoMoreInteractions(repoMock);
        verifyNoMoreInteractions(conceptMock);

        assertSnapshot(created, refsetArgument.getValue());
        assertEquals(persisted, returned);
    }
    
    @Test
    public void delete() throws SnapshotNotFoundException{
        Snapshot deleted = Snapshot.getBuilder(PUBLIC_ID, TITLE, DESCRIPTION, concepts).build();

        when(repoMock.findOne(SNAPSHOT_ID)).thenReturn(deleted);
        
        Snapshot returned = snapshotService.delete(SNAPSHOT_ID);
        
        verify(repoMock, times(1)).findOne(SNAPSHOT_ID);
        verify(repoMock, times(1)).delete(deleted);
        verifyNoMoreInteractions(repoMock);
        
        assertEquals(deleted, returned);
    }
    
    @Test(expected = SnapshotNotFoundException.class)
    public void deleteWhenRefsetIsNotFound() throws SnapshotNotFoundException {
        when(repoMock.findOne(SNAPSHOT_ID)).thenReturn(null);
        
        snapshotService.delete(SNAPSHOT_ID);
        
        verify(repoMock, times(1)).findOne(SNAPSHOT_ID);
        verifyNoMoreInteractions(repoMock);
    }
    
    @Test
    public void findAll() {
        List<Snapshot> snapshots = new ArrayList<Snapshot>();
        when(repoMock.findAll()).thenReturn(snapshots);
        
        List<Snapshot> returned = snapshotService.findAll();
        
        verify(repoMock, times(1)).findAll(any(Sort.class));
        verifyNoMoreInteractions(repoMock);
        
        assertEquals(snapshots, returned);
    } 
    
    @Test
    public void findById() {
        Snapshot snapshot = Snapshot.getBuilder(PUBLIC_ID, TITLE, DESCRIPTION, concepts).build();

        when(repoMock.findOne(SNAPSHOT_ID)).thenReturn(snapshot);
        
        Snapshot returned = snapshotService.findById(SNAPSHOT_ID);
        
        verify(repoMock, times(1)).findOne(SNAPSHOT_ID);
        verifyNoMoreInteractions(repoMock);
        
        assertEquals(snapshot, returned);
    }
    
    @Test
    public void update() throws NonUniquePublicIdException, SnapshotNotFoundException, RefsetConceptNotFoundException{
        
        SnapshotDto updatedDto = SnapshotDto.getBuilder(SNAPSHOT_ID, TITLE_UPDATED, DESCRIPTION_UPDATED, PUBLIC_ID_UPDATED, conceptDtosUpdated).build();
        Snapshot updated = Snapshot.getBuilder(PUBLIC_ID_UPDATED, TITLE_UPDATED, DESCRIPTION_UPDATED, conceptsUpdated).build();
        updated.setId(SNAPSHOT_ID);
        Snapshot original = Snapshot.getBuilder(PUBLIC_ID, TITLE, DESCRIPTION, concepts).build();
        original.setId(SNAPSHOT_ID);
        
        when(repoMock.findOne(updatedDto.getId())).thenReturn(original);
        when(repoMock.save(any(Snapshot.class))).thenReturn(updated);
        
        Snapshot returned = snapshotService.update(updatedDto);
        
        verify(repoMock, times(1)).findOne(updatedDto.getId());
        verify(repoMock, times(1)).save(any(Snapshot.class));
        verify(conceptMock, times(1)).findBySerialisedId(c3.getSerialisedId());
        verify(conceptMock, times(1)).findBySerialisedId(c4.getSerialisedId());
        verifyNoMoreInteractions(repoMock);
        verifyNoMoreInteractions(conceptMock);

        assertSnapshot(updatedDto, returned);
    }
    
    @Test(expected = SnapshotNotFoundException.class)
    public void updateWhenRefsetIsNotFound() throws NonUniquePublicIdException, SnapshotNotFoundException, RefsetConceptNotFoundException {        
        SnapshotDto updatedDto = SnapshotDto.getBuilder(SNAPSHOT_ID, TITLE_UPDATED, DESCRIPTION_UPDATED, PUBLIC_ID_UPDATED, conceptDtosUpdated).build();
        when(repoMock.findOne(updatedDto.getId())).thenReturn(null);
        snapshotService.update(updatedDto);
    }

    private void assertSnapshot(SnapshotDto expected, Snapshot actual) {
        SnapshotDto actualDto = RefsetTestUtil.createSnapshotDto(actual.getId(), actual.getPublicId(), actual.getTitle(), 
                actual.getDescription(), RefsetTestUtil.createConceptDtos(actual.getConcepts()));
        
        assertEquals(expected, actualDto);
    }
}