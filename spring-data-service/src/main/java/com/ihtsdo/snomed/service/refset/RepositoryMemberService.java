package com.ihtsdo.snomed.service.refset;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.exception.MemberNotFoundException;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.repository.refset.MemberRepository;
import com.ihtsdo.snomed.service.refset.RefsetService.SortOrder;

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
    
//    @Override
//    @Transactional(readOnly = true)
//    public Member findById(Long id){
//        LOG.debug("Finding member by id: " + id);
//        return memberRepository.findOne(id);
//    }
    
//    @Override
//    @Transactional(rollbackFor = MemberNotFoundException.class)
//    public Member delete(Long memberId) throws MemberNotFoundException {
//        LOG.debug("Deleting member with id: " + memberId);
//        Member deleted = memberRepository.findOne(memberId);
//        if (deleted == null) {
//            throw new MemberNotFoundException(memberId, "No member found with id: " + memberId);
//        }
//        memberRepository.delete(deleted);
//        return deleted;
//    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> findByRefsetPublicId(String refsetPublicId, String sortBy, SortOrder sortOrder){
        LOG.debug("Getting all members for refset with publicId={} sorted by {} {}", refsetPublicId, sortBy, sortOrder);
        return memberRepository.findByRefsetPublicIdAndIsActive(refsetPublicId, 
                new Sort(RepositoryRefsetService.sortDirection(sortOrder), sortBy)); 
                        //"component.fullySpecifiedName"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Member> findBySnapshotPublicId(String refsetPublicId, String snapshotPublicId, String sortBy, SortOrder sortOrder){
        LOG.debug("Getting all members for snapshot {} for refset {} sorted by {} {}", snapshotPublicId, refsetPublicId, sortBy, sortOrder);
        return memberRepository.findByRefsetPublicIdAndSnapshotPublicIdAndIsActive(refsetPublicId, snapshotPublicId, 
                new Sort(RepositoryRefsetService.sortDirection(sortOrder), sortBy));
                
                //new Sort(Sort.Direction.ASC, "component.fullySpecifiedName"));
    }    

    @Override
    @Transactional(readOnly = true)
    public Member findByMemberPublicIdAndRefsetPublicId(String memberPublicId, String refsetPublicId) throws MemberNotFoundException{
        LOG.debug("Getting member with publicId {} for refset with publicId {}", memberPublicId, refsetPublicId);
        Member m = memberRepository.findByMemberPublicIdAndRefsetPublicIdAndIsActive(
                refsetPublicId, memberPublicId);
        if (m == null){
            throw new MemberNotFoundException(memberPublicId, refsetPublicId);
        }
        return m;
    }    
    
        
}
