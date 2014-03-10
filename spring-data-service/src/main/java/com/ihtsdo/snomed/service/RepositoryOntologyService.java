package com.ihtsdo.snomed.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.repository.OntologyRepository;

@Transactional (value = "transactionManager", readOnly = true)
@Service
public class RepositoryOntologyService implements OntologyService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryOntologyService.class);

    @Inject
    OntologyRepository ontologyRepository;    

    @PostConstruct
    public void init(){}
    
    @Override
    @Transactional(readOnly = true)
    public List<OntologyVersion> findAll(){
        LOG.debug("Retrieving all ontologies");
        return ontologyRepository.findAll();
    }
}
