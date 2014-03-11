package com.ihtsdo.snomed.service;

import java.sql.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.repository.OntologyVersionRepository;

@Transactional (value = "transactionManager", readOnly = true)
@Service
public class RepositoryOntologyVersionService implements OntologyVersionService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryOntologyVersionService.class);

    @Inject
    OntologyVersionRepository ontologyVersionRepository;    

    @PostConstruct
    public void init(){}
    
    @Override
    @Transactional(readOnly = true)
    public List<OntologyVersion> findAll(){
        LOG.debug("Retrieving all ontologies");
        return ontologyVersionRepository.findAll();
    }

    @Override
    public OntologyVersion findByFlavourAndTaggedOn(String flavourPublicId,
            Date taggedOn) {
        LOG.debug("Retrieving Ontology Version with flavour [{}] and version date of [{}]", flavourPublicId, taggedOn);
        return ontologyVersionRepository.findByFlavourAndTaggedOn(flavourPublicId, taggedOn);
    }
    
    
}
