package com.ihtsdo.snomed.model.xml;

import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.dto.refset.RefsetDto;
import com.ihtsdo.snomed.model.refset.Refset;

@XmlRootElement(name="refset")
public class RefsetDtoShort {

    private long id;
    private XmlRefsetConcept concept;
    private String publicId;
    private String title;
    private String description;
    private Date created;
    private Date lastModified;
    private int memberSize;
    private String snomedExtension;
    private String snomedReleaseDate;    
    
    private boolean pendingChanges;    
    
    public RefsetDtoShort(Refset r){
        setId(r.getId());
        setConcept(new XmlRefsetConcept(r.getRefsetConcept()));
        setPublicId(r.getPublicId());
        setTitle(r.getTitle());
        setDescription(r.getDescription());
        setCreated(r.getCreationTime());
        setLastModified(r.getModificationTime());
        setPendingChanges(r.isPendingChanges());
        setMemberSize(r.getMemberSize());
        setSnomedExtension(r.getOntologyVersion().getFlavour().getPublicId());
        setSnomedReleaseDate(RefsetDto.dateFormat.format(r.getOntologyVersion().getTaggedOn()));
    }
    
    public RefsetDtoShort(){}

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("concept", getConcept())
                .add("publicId", getPublicId())
                .add("title", getTitle())
                .add("description", getDescription())
                .add("created", getCreated())
                .add("lastModified", getLastModified())
                .add("pendingChanges", isPendingChanges())
                .add("memberSize", getMemberSize())
                .add("snomedExtension", getSnomedExtension())
                .add("snomedReleaseDate", getSnomedReleaseDate())
                .toString();
    }
    
    @Override
    public int hashCode(){
        return Longs.hashCode(getId());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof RefsetDtoShort){
            RefsetDtoShort r = (RefsetDtoShort) o;
            if (r.getId() == this.getId()){
                return true;
            }
        }
        return false;
    } 

    public boolean isPendingChanges() {
        return pendingChanges;
    }

    public void setPendingChanges(boolean pendingChanges) {
        this.pendingChanges = pendingChanges;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public XmlRefsetConcept getConcept() {
        return concept;
    }
    public void setConcept(XmlRefsetConcept concept) {
        this.concept = concept;
    }
    public String getPublicId() {
        return publicId;
    }
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    public int getMemberSize() {
        return memberSize;
    }
    public void setMemberSize(int memberSize) {
        this.memberSize = memberSize;
    }
    
    

    public String getSnomedExtension() {
        return snomedExtension;
    }

    public void setSnomedExtension(String snomedExtension) {
        this.snomedExtension = snomedExtension;
    }

    public String getSnomedReleaseDate() {
        return snomedReleaseDate;
    }

    public void setSnomedReleaseDate(String snomedReleaseDate) {
        this.snomedReleaseDate = snomedReleaseDate;
    }

    public static RefsetDtoShort parse(Refset r){
        return getBuilder(new XmlRefsetConcept(r.getRefsetConcept()),
                r.getPublicId(),
                r.getTitle(),
                r.getDescription(),
                r.getCreationTime(),
                r.getModificationTime(),
                r.isPendingChanges(),
                r.getMemberSize(),
                r.getOntologyVersion().getFlavour().getPublicId(),
                r.getOntologyVersion().getTaggedOn()).build();
    }
    
    public static Builder getBuilder(XmlRefsetConcept concept, String publicId, String title, 
            String description, Date created, Date lastModified, boolean pendingChanges, int memberSize, 
            String snomedExtension, Date snomedReleaseDate) {
        return new Builder(concept, publicId, title, description, created, lastModified, pendingChanges, 
                memberSize, snomedExtension, snomedReleaseDate);
    }
    
    public static class Builder {
        private RefsetDtoShort built;

        Builder(XmlRefsetConcept concept, String publicId, String title, String description, 
                Date created, Date lastModified, boolean pendingChanges, int memberSize,
                String snomedExtension, Date snomedReleaseDate){
            built = new RefsetDtoShort();
            built.concept = concept;
            built.publicId = publicId;
            built.title = title;
            built.description = description;
            built.created = created;
            built.lastModified = lastModified;
            built.pendingChanges = pendingChanges;
            built.memberSize = memberSize;
            built.setSnomedExtension(snomedExtension);
            built.setSnomedReleaseDate(RefsetDto.dateFormat.format(snomedReleaseDate));            
        }
        
        public RefsetDtoShort build() {
            return built;
        }
    }    
}
