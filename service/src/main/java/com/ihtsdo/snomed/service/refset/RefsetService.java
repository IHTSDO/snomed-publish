package com.ihtsdo.snomed.service.refset;

import java.util.List;

import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.exception.RefsetConceptNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.exception.RefsetPlanNotFoundException;
import com.ihtsdo.snomed.exception.RefsetTerminalRuleNotFoundException;
import com.ihtsdo.snomed.exception.validation.ValidationException;
import com.ihtsdo.snomed.model.refset.Refset;

public interface RefsetService {

    public abstract List<Refset> findAll(int pageIndex);

    public abstract List<Refset> findAll();

    public abstract Refset findById(Long id);

    public abstract Refset findByPublicId(String publicId);

    public abstract Refset update(RefsetDto updated) throws RefsetNotFoundException, RefsetConceptNotFoundException, ValidationException, RefsetPlanNotFoundException, RefsetTerminalRuleNotFoundException, NonUniquePublicIdException;

    public abstract Refset create(RefsetDto created) throws RefsetConceptNotFoundException, ValidationException, NonUniquePublicIdException;

    
    public abstract Refset delete(Long refsetId) throws RefsetNotFoundException;

    public abstract Refset delete(String publicId)
            throws RefsetNotFoundException;

    Refset update(Refset refset);
}