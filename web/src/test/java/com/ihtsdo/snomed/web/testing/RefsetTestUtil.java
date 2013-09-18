package com.ihtsdo.snomed.web.testing;

import com.ihtsdo.snomed.dto.RefsetDto;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.Refset;

/**
 * An utility class which contains useful methods for unit testing person related functions.
 * @author Henrik Pettersen / Sparkling Ideas (henrik@sparklingideas.co.uk)
 */
public class RefsetTestUtil {

    public static RefsetDto createDto(Long id, Long concept, String publicId, String title, String description) {
        RefsetDto dto = new RefsetDto();

        dto.setId(id);
        dto.setPublicId(publicId);
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setConcept(concept);
        
        return dto;
    }

    public static Refset createModelObject(Long id, Concept concept, String publicId, String title, String description) {
        Refset model = Refset.getBuilder(concept, publicId, title, description).build();
        model.setId(id);
        return model;
    }
}

