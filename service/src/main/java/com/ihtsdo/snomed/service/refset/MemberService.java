package com.ihtsdo.snomed.service.refset;

import java.util.List;

import com.ihtsdo.snomed.exception.MemberNotFoundException;
import com.ihtsdo.snomed.model.refset.Member;

public interface MemberService {

    public abstract Member findByMemberPublicIdAndRefsetPublicId(String memberPublicId, String refsetPublicId) throws MemberNotFoundException;
    
    public abstract List<Member> findByRefsetPublicId(String refsetPublicId);

    //public abstract Member findById(Long id);
    
    //public abstract Member delete(Long memberId) throws MemberNotFoundException;

    public abstract List<Member> findBySnapshotPublicId(String refsetPublicId,String snapshotPublicId);

    
}
