package com.ihtsdo.snomed.service.manifest.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Concept;

@XmlRootElement(name="refset")
@XmlType(propOrder = { "name", "sid" })
public class Refset {
    
    @XmlTransient
    private Concept concept;

    public Refset(Concept concept) {
        this.concept = concept;
    }
    
    public Refset(){}
    
    public boolean equals(Object o){
        if (o instanceof Refset){
            Refset r = (Refset)o;
            if (r.getSid() == getSid()){
                return true;
            }
        }
        return false;
    }
    
    public int hashCode(){
        return Longs.hashCode(getSid());
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("sid", getSid())
                .add("name", getName())
                .toString();
    }
    
    @XmlAttribute
    public String getName(){
        return getConcept().getFullySpecifiedName();
    }
    
    @XmlAttribute
    public long getSid(){
        return getConcept().getSerialisedId();
    }

    public Concept getConcept() {
        return concept;
    }
    
    

}
