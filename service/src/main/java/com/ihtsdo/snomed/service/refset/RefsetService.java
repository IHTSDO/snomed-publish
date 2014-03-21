package com.ihtsdo.snomed.service.refset;

import java.util.List;
import java.util.Set;

import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.exception.ConceptIdNotFoundException;
import com.ihtsdo.snomed.exception.InvalidSnomedDateFormatException;
import com.ihtsdo.snomed.exception.MemberNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.OntologyFlavourNotFoundException;
import com.ihtsdo.snomed.exception.OntologyNotFoundException;
import com.ihtsdo.snomed.exception.OntologyVersionNotFoundException;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.model.refset.Refset;

public interface RefsetService {
    
    public abstract Refset addMembers(Set<MemberDto> members, String publicId) throws RefsetNotFoundException, ConceptIdNotFoundException;

    public abstract Member deleteMembership(String refsetId, String memberId) throws RefsetNotFoundException, MemberNotFoundException, NonUniquePublicIdException;
    
    public abstract List<Refset> findAll(int pageIndex);

    public abstract List<Refset> findAll();

    public abstract Refset findById(Long id);

    public abstract Refset findByPublicId(String publicId) throws RefsetNotFoundException;

    public abstract Refset update(RefsetDto updated) throws RefsetNotFoundException, RefsetConceptNotFoundException, ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException, NonUniquePublicIdException, OntologyNotFoundException, InvalidSnomedDateFormatException, OntologyVersionNotFoundException, OntologyFlavourNotFoundException;

    public abstract Refset create(RefsetDto created) throws RefsetConceptNotFoundException, ValidationException, NonUniquePublicIdException, OntologyNotFoundException, InvalidSnomedDateFormatException, OntologyVersionNotFoundException, OntologyFlavourNotFoundException;

    
    public abstract Refset delete(Long refsetId) throws RefsetNotFoundException;

    public abstract Refset delete(String publicId)
            throws RefsetNotFoundException;

    public Refset update(Refset refset);

    public SnapshotDto takeSnapshot(String refsetPublicId, SnapshotDto snapshotDto) throws RefsetNotFoundException, NonUniquePublicIdException;

    public SnapshotDto importSnapshot(String refsetPublicId, SnapshotDto snapshotDto) throws RefsetNotFoundException, NonUniquePublicIdException, ConceptIdNotFoundException;
}