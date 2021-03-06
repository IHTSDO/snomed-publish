package test.com.ihtsdo.snomed.web;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import com.ihtsdo.snomed.dto.refset.ConceptDto;
import com.ihtsdo.snomed.dto.refset.MemberDto;
import com.ihtsdo.snomed.dto.refset.SnapshotDto;
import com.ihtsdo.snomed.model.Ontology;
import com.ihtsdo.snomed.model.OntologyFlavour;
import com.ihtsdo.snomed.model.OntologyVersion;
import com.ihtsdo.snomed.model.SnomedFlavours;
import com.ihtsdo.snomed.model.SnomedOntology;
import com.ihtsdo.snomed.model.refset.Member;

/**
 * An utility class which contains useful methods for unit testing refset related functions.
 */
public class RefsetTestUtil {

    static Date DEFAULT_TAGGED_ON;
    
    static{
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();
        DEFAULT_TAGGED_ON = new Date(utilDate.getTime());        
    }    
        
    
    public static Ontology createOntology() {
        OntologyVersion ov = new OntologyVersion();
        ov.setTaggedOn(DEFAULT_TAGGED_ON);
        OntologyFlavour of = new OntologyFlavour();
        of.setPublicId(SnomedFlavours.INTERNATIONAL.getPublicIdString());
        of.setLabel(SnomedFlavours.INTERNATIONAL.getLabel());
        Ontology o = new Ontology();
        o.setLabel(SnomedOntology.LABEL);
        o.setPublicId(SnomedOntology.PUBLIC_ID);
        o.addFlavour(of);
        of.addVersion(ov);
        return o;
    }     
    
//    public static RefsetDto createRefsetDto(Long id, Long concept, String publicId, String title, String description) {
//        RefsetDto dto = new RefsetDto();
//
//        dto.setId(id);
//        dto.setPublicId(publicId);
//        dto.setTitle(title);
//        dto.setDescription(description);
//        dto.setConcept(concept);
//        
//        return dto;
//    }

//    public static Refset createRefset(Long id, Concept concept, String publicId, String title, String description) {
//        Refset model = Refset.getBuilder(concept, publicId, title, description, new Plan()).build();
//        model.setId(id);
//        return model;
//    }
    
    public static SnapshotDto createSnapshotDto(String publicId, String title, String description,
            Set<ConceptDto> concepts) {
        SnapshotDto dto = new SnapshotDto();

        dto.setPublicId(publicId);
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setMemberDtos(MemberDto.createFromConcepts(concepts));
        
        return dto;
    }   
    
    public static Set<ConceptDto> createConceptDtos(Set<Member> source){
        if ((source == null) || (source.size() == 0)){
            return new HashSet<ConceptDto>();
        }
        Set<ConceptDto> conceptDtos = new HashSet<ConceptDto>(source.size());
        for (Member m : source){
            conceptDtos.add(new ConceptDto(m.getComponent().getSerialisedId()));
        }
        return conceptDtos;
    }
    
}

