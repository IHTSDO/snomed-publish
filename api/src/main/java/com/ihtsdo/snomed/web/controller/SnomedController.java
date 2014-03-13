package com.ihtsdo.snomed.web.controller;

import java.sql.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ihtsdo.snomed.dto.refset.OntologyFlavourDto;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Description;
import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.model.xml.XmlConcept;
import com.ihtsdo.snomed.model.xml.XmlDescription;
import com.ihtsdo.snomed.model.xml.XmlStatement;
import com.ihtsdo.snomed.service.ConceptService;
import com.ihtsdo.snomed.service.DescriptionService;
import com.ihtsdo.snomed.service.OntologyVersionService;
import com.ihtsdo.snomed.service.StatementService;

@Controller
@RequestMapping("/snomed")
@Transactional(value = "transactionManager")
public class SnomedController {
    private static final Logger LOG = LoggerFactory.getLogger(SnomedController.class);

    @Inject
    ConceptService conceptService;
    
    @Inject
    DescriptionService descriptionService;
    
    @Inject
    StatementService statementService;
    
    @Inject
    OntologyVersionService ontologyVersionService;    

    @Transactional
    @RequestMapping(value = "extensions", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<OntologyFlavourDto> getAllSnomedExtensions(){
        return null;
    }
         
    @Transactional
    @RequestMapping(value = "extension/{publicId}", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Date> getAllSnomedReleasesForExtension(String publicId){
        return null;
    }    
    
    @Transactional
    @RequestMapping(value = "concept/{id}", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<XmlConcept> getConcept(@PathVariable Long id) throws Exception {
        LOG.debug("Handling json request for concept id {}", id);
        Concept c = conceptService.findBySerialisedId(id);
        if (c == null){
            return new ResponseEntity<XmlConcept>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<XmlConcept>(new XmlConcept(c), HttpStatus.OK);
    }
    
    @Transactional
    @RequestMapping(value = "description/{id}", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<XmlDescription> getDescription(@PathVariable Long id) throws Exception {
        LOG.debug("Handling json request for description id {}", id);
        Description d = descriptionService.findBySerialisedId(id);
        if (d == null){
            return new ResponseEntity<XmlDescription>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<XmlDescription>(new XmlDescription(d), HttpStatus.OK);
    }
    
    @Transactional
    @RequestMapping(value = "statement/{id}", 
            method = RequestMethod.GET, 
            consumes=MediaType.ALL_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<XmlStatement> getStatement(@PathVariable Long id) throws Exception {
        LOG.debug("Handling json request for statement id {}", id);
        Statement c = statementService.findBySerialisedId(id);
        if (c == null){
            return new ResponseEntity<XmlStatement>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<XmlStatement>(new XmlStatement(c), HttpStatus.OK);
    }
}