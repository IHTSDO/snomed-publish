package com.ihtsdo.snomed.dto.refset;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.ihtsdo.snomed.model.Concept;

@XmlRootElement(name="concept")
@JsonRootName("concept")
public class ConceptDto {

    public ConceptDto(){}
    
    public ConceptDto(String id){
        this.id = id;
    }
    
    public ConceptDto(Long id){
    	if (id == null){
    		return;
    	}
    	this.id = Long.toString(id);
    }    
    
    @NotNull(message="You must specify a concept id")
    private String id;
    
    private String title;
    private boolean active;
    private long effectiveTime;
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof ConceptDto){
            ConceptDto dto = (ConceptDto) o;
            if (Objects.equal(dto.getId(), getId())){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        return getId() == null ? 0 : getId().hashCode();
    } 
    
    
    @JsonIgnore
    public Long getIdAsLong() throws NumberFormatException{
    	if (getId() == null) return null;
        return new Long(getId());
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public static Builder getBuilder(){
        return new Builder();
    }
    
    public static ConceptDto parse(Concept c){
    	if (c == null){
    		return null;
    	}
        return getBuilder()
                .id(Long.toString(c.getSerialisedId()))
                .displayName(c.getDisplayName())
                .active(c.isActive())
                .effectiveTime(c.getEffectiveTime())
                .build();
    }
    
    public static class Builder{
        private ConceptDto built;

        Builder() {
            built = new ConceptDto();
        }
        
        public Builder id(String id){
            built.setId(id);
            return this;
        }
        
        public Builder id(Long id){
        	if (id == null) return this;
            built.setId(id.toString());
            return this;
        }        
        
        public Builder active(boolean active){
            built.setActive(active);
            return this;
        }
        
        public Builder effectiveTime(long effectiveTime){
            built.setEffectiveTime(effectiveTime);
            return this;
        }

        public Builder displayName(String displayName){
            built.setTitle(displayName);
            return this;
        }

        public ConceptDto build() {
            return built;
        }
    }
}
