package com.ihtsdo.snomed.dto.refset;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.ihtsdo.snomed.model.refset.Member;

@XmlRootElement(name="member")
@JsonRootName("member")
public class MemberDto {

    protected Long id;
    
    protected String serialisedId;
    protected Date effective;
    protected boolean active;
    
    @NotNull(message="You must specify a component")
    protected ConceptDto component;
    
    protected ConceptDto module;
    
	public MemberDto() {}
	
	public static Set<MemberDto> createFromConcepts(Collection<ConceptDto> concepts){
		Set<MemberDto> created = new HashSet<>();
		for (ConceptDto c : concepts){
			created.add(MemberDto.getBuilder(null, c).build());
		}
		return created;
	}
	
    public static MemberDto parse(Member member){
    	MemberDto created = MemberDto.getBuilder(
    			member.getId(),
    			member.getSerialisedId(),
    			member.getModule() == null ? null : ConceptDto.parse(member.getModule()), 
    			ConceptDto.parse(member.getComponent()),
    			member.getEffective(),
    			member.isActive()).build();
    	return created;
    }  	
    
	
    @Override
     public boolean equals(Object o){
    	System.out.println("In equals!!");
         if (o instanceof MemberDto){
        	 MemberDto r = (MemberDto) o;
             if (Objects.equal(r.getComponent(), getComponent()) &&
            		 Objects.equal(r.getModule(), getModule()) &&
            		 Objects.equal(r.getSerialisedId(), getSerialisedId()) &&
            		 Objects.equal(r.getEffective(), getEffective()) &&
            		 Objects.equal(r.isActive(), isActive()))
             {
                 return true;
             }
         }
         return false;
     }
     
     @Override
     public String toString(){
         return Objects.toStringHelper(this)
                 .add("id", getId())
                 .add("effective", getEffective())
                 .add("isActive", isActive())
                 .add("component", getComponent())
                 .add("module", getModule())
                 .toString();
     }
     
     @Override
     public int hashCode(){
    	 return java.util.Objects.hash(
    			 getSerialisedId(),
    			 getEffective(),
    			 isActive(),
    			 getComponent(),
    			 getModule());
     }   

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialisedId() {
		return serialisedId;
	}

	public void setSerialisedId(String serialisedId) {
		this.serialisedId = serialisedId;
	}

	public Date getEffective() {
		return effective;
	}

	public void setEffective(Date effective) {
		this.effective = effective;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ConceptDto getComponent() {
		return component;
	}

	public void setComponent(ConceptDto component) {
		this.component = component;
	}

	public ConceptDto getModule() {
		return module;
	}

	public void setModule(ConceptDto module) {
		this.module = module;
	}
	
    public static Builder getBuilder(ConceptDto module, ConceptDto component) {
        return new Builder(0L, "", module, component, new Date(), true);
    }

    public static Builder getBuilder(Long id, String serialisedId, ConceptDto module, ConceptDto component, Date effective, boolean active) {
        return new Builder(id, serialisedId, module, component, effective, active);
    }
    
    public static class Builder {
        private MemberDto built;

        Builder(Long id, String serialisedId, ConceptDto module, ConceptDto component, Date effective, boolean active) {
            built = new MemberDto();
            built.setComponent(component);
            built.setModule(module);
            built.setId(id);
            built.setSerialisedId(serialisedId);
            built.setEffective(effective);
            built.setActive(active);
        }
        
        public Builder id(Long id){
            built.setId(id);
            return this;
        }
        
        public Builder component(ConceptDto component){
            built.setComponent(component);
            return this;
        }
        
        public Builder module(ConceptDto module){
            built.setModule(module);
            return this;
        }
        public Builder serialisedId(String serialisedId){
            built.setSerialisedId(serialisedId);
            return this;
        }        
        public MemberDto build() {
            return built;
        }
    }

}
