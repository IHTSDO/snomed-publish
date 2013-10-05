package com.ihtsdo.snomed.model.xml;

import java.net.MalformedURLException;
import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.refset.Refset;

@XmlRootElement(name="refset")
public class XmlRefsetShort {

    private long id;
    private XmlRefsetConcept concept;
    private String publicId;
    private String title;
    private String description;
    private Date created;
    private Date lastModified;
    
    public XmlRefsetShort(Refset r) throws MalformedURLException{
        setId(r.getId());
        setConcept(new XmlRefsetConcept(r.getConcept()));
        setPublicId(r.getPublicId());
        setTitle(r.getTitle());
        setDescription(r.getDescription());
        setCreated(r.getCreationTime());
        setLastModified(r.getModificationTime());
    }
    
    public XmlRefsetShort(){}

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
                .toString();
    }
    
    @Override
    public int hashCode(){
        return Longs.hashCode(getId());
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof XmlRefsetShort){
            XmlRefsetShort r = (XmlRefsetShort) o;
            if (r.getId() == this.getId()){
                return true;
            }
        }
        return false;
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
    
    

}
