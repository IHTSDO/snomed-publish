package com.ihtsdo.snomed.service.refset;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.repository.refset.MemberRepository;

//http://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-three-custom-queries-with-query-methods/
@Transactional (value = "transactionManager", readOnly = false)
@Service
public class RepositoryMemberService implements MemberService {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryMemberService.class);

    protected static final int NUMBER_OF_REFSETS_PER_PAGE = 5;

    @Inject
    MemberRepository memberRepository;
        
    @PersistenceContext(unitName="hibernatePersistenceUnit") 
    private EntityManager em;
    
    @PostConstruct
    public void init(){}
    
    @Override
    @Transactional(readOnly = true)
    public Member findById(Long id){
        LOG.debug("Finding member by id: " + id);
        return memberRepository.findOne(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Member findByPublicId(String publicId){
        LOG.debug("Getting member with publicId=" + publicId);
        return memberRepository.findByPublicId(publicId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> findByRefsetPublicId(String refsetPublicId) throws RefsetNotFoundException {
        LOG.debug("Getting all members for refset with publicId=" + refsetPublicId);
        List<Member> found = memberRepository.findByRefsetPublicId(refsetPublicId);
        if (found == null){
            throw new RefsetNotFoundException(refsetPublicId);
        }
        return found;
    }    
    
        
}
