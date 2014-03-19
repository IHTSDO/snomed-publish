package com.ihtsdo.snomed.service.refset;

import java.util.List;

import com.ihtsdo.snomed.exception.MemberNotFoundException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.model.refset.Member;

public interface MemberService {

    public abstract List<Member> findByMemberPublicIdAndRefsetPublicId(String memberPublicId, String refsetPublicId) throws MemberNotFoundException;
    
    public abstract List<Member> findByRefsetPublicId(String refsetPublicId) throws RefsetNotFoundException;

    public abstract Member findById(Long id);

    public abstract Member findByPublicId(String publicId);
    
    public abstract Member delete(Long memberId) throws MemberNotFoundException;

    
}
