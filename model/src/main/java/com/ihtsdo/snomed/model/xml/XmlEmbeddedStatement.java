package com.ihtsdo.snomed.model.xml;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;
import com.ihtsdo.snomed.model.Statement;

public class XmlEmbeddedStatement {

    @XmlTransient
    private long id;
    
    @XmlAttribute
    private URL href;
    
    private long serialisedId = Statement.SERIALISED_ID_NOT_DEFINED;
    private int groupId;
    private boolean active;
    
    public XmlEmbeddedStatement(){}
    
    public XmlEmbeddedStatement(Statement s) throws MalformedURLException{
        setSerialisedId(s.getSerialisedId());
        setId(s.getId());
        setGroupId(s.getGroupId());
        setActive(s.isActive());
        setHref(UrlBuilder.createStatementUrl(s));
    }
        
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("id", getId())
            .add("serialisedId", getSerialisedId())
            .add("groupId", getGroupId())
            .add("active", isActive())
            .add("href", getHref())
            .toString();
    }    
    
//    @Override
//    public int hashCode(){
//        return Longs.hashCode(getSerialisedId());
//    }
//
//    @Override
//    public boolean equals(Object o){
//        if (o instanceof Statement){
//            Statement r = (Statement) o;
//            if (r.getSerialisedId() == this.getSerialisedId()){
//                return true;
//            }
//        }
//        return false;
//    }
//    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getSerialisedId() {
        return serialisedId;
    }
    public void setSerialisedId(long serialisedId) {
        this.serialisedId = serialisedId;
    }
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public URL getHref() {
        return href;
    }

    public void setHref(URL href) {
        this.href = href;
    }
    
}
