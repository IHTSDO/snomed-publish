package com.ihtsdo.snomed.dto.refset;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.Concept;


public class ConceptDto {

    public ConceptDto(){}
    
    public ConceptDto(Long id){
        this.id = id;
    }
    
    private Long id;
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
        return Longs.hashCode(getId());
    } 
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
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
        return getBuilder()
                .id(c.getSerialisedId())
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
        
        public Builder id(Long id){
            built.setId(id);
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
