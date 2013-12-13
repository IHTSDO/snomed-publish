package test.com.ihtsdo.snomed.web;

import java.util.HashSet;
import java.util.Set;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.model.Concept;
import com.ihtsdo.snomed.model.refset.Refset;
import com.ihtsdo.snomed.model.refset.Plan;

/**
 * An utility class which contains useful methods for unit testing person related functions.
 */
public class RefsetTestUtil {

    public static RefsetDto createRefsetDto(Long id, Long concept, String publicId, String title, String description) {
        RefsetDto dto = new RefsetDto();

        dto.setId(id);
        dto.setPublicId(publicId);
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setConcept(concept);
        
        return dto;
    }

    public static Refset createRefset(Long id, Concept concept, String publicId, String title, String description) {
        Refset model = Refset.getBuilder(concept, publicId, title, description, new Plan()).build();
        model.setId(id);
        return model;
    }
    
    public static SnapshotDto createSnapshotDto(Long id, String publicId, String title, String description,
            Set<ConceptDto> concepts) {
        SnapshotDto dto = new SnapshotDto();

        dto.setId(id);
        dto.setPublicId(publicId);
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setConceptDtos(concepts);
        
        return dto;
    }   
    
    public static Set<ConceptDto> createConceptDtos(Set<Concept> source){
        if ((source == null) || (source.size() == 0)){
            return new HashSet<ConceptDto>();
        }
        Set<ConceptDto> conceptDtos = new HashSet<ConceptDto>(source.size());
        for (Concept c : source){
            conceptDtos.add(new ConceptDto(c.getSerialisedId()));
        }
        return conceptDtos;
    }
    
}

