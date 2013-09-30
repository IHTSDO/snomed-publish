package com.ihtsdo.snomed.dto.refset;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;


public class ConceptDto {

    public ConceptDto(){}
    
    public ConceptDto(long id){
        this.id = id;
    }
    
    private Long id;
    private String displayName;
    
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
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public static Builder getBuilder(){
        return new Builder();
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

        public Builder displayName(String displayName){
            built.setDisplayName(displayName);
            return this;
        }

        public ConceptDto build() {
            return built;
        }
    }
}
