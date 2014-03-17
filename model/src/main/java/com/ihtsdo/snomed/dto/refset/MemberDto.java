package com.ihtsdo.snomed.dto.refset;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.ihtsdo.snomed.model.refset.Member;

@XmlRootElement(name="member")
@JsonRootName("member")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDto {
    
    protected String publicId;
    protected boolean active;
    
    @NotNull(message="You must specify an effective time")
    protected Date effective;    
    
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
    			member.getPublicId(),
    			member.getModule() == null ? null : ConceptDto.parse(member.getModule()), 
    			ConceptDto.parse(member.getComponent()),
    			member.getEffective(),
    			member.isActive()).build();
    	return created;
    }  	
    
	
    @Override
    public boolean equals(Object o){
         if (o instanceof MemberDto){
        	 MemberDto r = (MemberDto) o;
             if (Objects.equal(r.getComponent(), getComponent()) &&
            		 Objects.equal(r.getModule(), getModule()) &&
            		 Objects.equal(r.getPublicId(), getPublicId()) );// &&
            		 //Objects.equal(r.getEffective(), getEffective()) &&
            		 //Objects.equal(r.isActive(), isActive()))
             {
                 return true;
             }
         }
         return false;
     }
     
     @Override
     public String toString(){
         return Objects.toStringHelper(this)
                 .add("effective", getEffective())
                 .add("isActive", isActive())
                 .add("component", getComponent())
                 .add("module", getModule())
                 .toString();
     }
     
     @Override
     public int hashCode(){
    	 return java.util.Objects.hash(
    			 getPublicId(),
    			 //getEffective(),
    			 //isActive(),
    			 getComponent(),
    			 getModule());
     }   

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
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
        return new Builder("", module, component, new Date(), true);
    }

    public static Builder getBuilder(String serialisedId, ConceptDto module, ConceptDto component, Date effective, boolean active) {
        return new Builder(serialisedId, module, component, effective, active);
    }
    
    public static class Builder {
        private MemberDto built;

        Builder(String serialisedId, ConceptDto module, ConceptDto component, Date effective, boolean active) {
            built = new MemberDto();
            built.setComponent(component);
            built.setModule(module);
            built.setPublicId(serialisedId);
            built.setEffective(effective);
            built.setActive(active);
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
            built.setPublicId(serialisedId);
            return this;
        }        
        public MemberDto build() {
            return built;
        }
    }

}
