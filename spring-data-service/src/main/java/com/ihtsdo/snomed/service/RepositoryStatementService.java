package com.ihtsdo.snomed.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.model.Statement;
import com.ihtsdo.snomed.repository.StatementRepository;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
//@Transactional (value = "transactionManager", readOnly = true)
@Service
//@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS)
public class RepositoryStatementService implements com.ihtsdo.snomed.service.StatementService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryStatementService.class);

    protected static final int NUMBER_OF_REFSETS_PER_PAGE = 5;

    @Inject
    StatementRepository statementRepository;
    
    @PostConstruct
    public void init(){}
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findAll(int)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Statement> findAll(int pageIndex){
        LOG.debug("TBD: Retrieving all refsets");
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findAll()
     */
    @Override
    @Transactional(readOnly = true)
    public List<Statement> findAll(){
        LOG.debug("Retrieving all statements");
        return statementRepository.findAll(sortByAscendingTitle());
    }    
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findById(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public Statement findById(Long id) {
        LOG.debug("Finding statement by id: " + id);
        return statementRepository.findOne(id);
    }
    
    /* (non-Javadoc)
     * @see com.ihtsdo.snomed.service.RefsetService#findByPublicId(java.lang.String)
     */
    @Override
    @Transactional
    public Statement findBySerialisedId(Long serialisedId){
        LOG.debug("Getting statement with publicId=" + serialisedId);
        return statementRepository.findBySerialisedId(serialisedId);
    }
    
    private Sort sortByAscendingTitle() {
        return new Sort(Sort.Direction.ASC, "title");
    }
    
}
