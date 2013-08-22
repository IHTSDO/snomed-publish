package com.ihtsdo.snomed.browse.service;

import java.util.List;

import com.ihtsdo.snomed.browse.dto.RefsetDto;
import com.ihtsdo.snomed.browse.exception.RefsetNotFoundException;
import com.ihtsdo.snomed.model.Refset;

/**
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
public interface RefsetService {

    public abstract List<Refset> findAll(int pageIndex);

    public abstract List<Refset> findAll();

    public abstract Refset findById(Long id);

    public abstract Refset findByPublicId(String publicId);

    public abstract Refset update(RefsetDto updated)
            throws RefsetNotFoundException;

    public abstract Refset create(RefsetDto created);

    public abstract Refset delete(Long refsetId) throws RefsetNotFoundException;

    public abstract Refset delete(String publicId)
            throws RefsetNotFoundException;

}