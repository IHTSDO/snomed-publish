package com.ihtsdo.snomed.service;

import java.util.List;

import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.exception.ConceptNotFoundException;
import com.ihtsdo.snomed.exception.NonUniquePublicIdException;
import com.ihtsdo.snomed.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.model.refset.Refset;

/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
public interface RefsetService {

    public abstract List<Refset> findAll(int pageIndex);

    public abstract List<Refset> findAll();

    public abstract Refset findById(Long id);

    public abstract Refset findByPublicId(String publicId);

    public abstract Refset update(RefsetDto updated)
            throws RefsetNotFoundException, NonUniquePublicIdException, ConceptNotFoundException;

    public abstract Refset create(RefsetDto created) throws NonUniquePublicIdException, ConceptNotFoundException;

    public abstract Refset delete(Long refsetId) throws RefsetNotFoundException;

    public abstract Refset delete(String publicId)
            throws RefsetNotFoundException;
}