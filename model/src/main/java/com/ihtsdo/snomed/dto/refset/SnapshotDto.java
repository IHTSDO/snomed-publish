package com.ihtsdo.snomed.dto.refset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.ihtsdo.snomed.model.refset.Member;
import com.ihtsdo.snomed.model.refset.Snapshot;

@XmlRootElement(name="snapshot")
@JsonRootName("snapshot")
public class SnapshotDto {
    
    protected Long id;
    
    @NotNull(message="Public ID can not be empty")
    @Size(min=2, max=20, message="Public ID must be between 2 and 50 characters")
    @Pattern(regexp="[a-zA-Z0-9_]+", message="Public ID may contain characters, numbers, and underscores only")
    protected String publicId;
    
    @NotNull(message="validation.title.not.empty")
    @Size(min=4, max=50, message="validation.title.wrong.size")
    protected String title;
    
    @NotNull(message="Description can not be empty")
    @Size(min=4, message="Description must be longer than 4 characters")
    protected String description;
    
    @XmlElementWrapper(name = "memberDtos")
    @XmlElement(name="member")
    @JsonProperty("memberDtos")
    protected Set<MemberDto> memberDtos = new HashSet<>();
    
    @XmlElementWrapper(name = "rules")
    @XmlElement(name="rule")
    @JsonProperty("rules")
    protected List<RuleDto> refsetRules = new ArrayList<>();   
    
    protected Long terminal;
    
    public SnapshotDto(){}

    public static SnapshotDto parse(Snapshot snapshot){        
        SnapshotDto snapshotDto = parseSansMembers(snapshot);
        if (snapshot.getTerminal() != null){
            PlanDto.RefsetPlanParser parser = new PlanDto.RefsetPlanParser();
            snapshot.getTerminal().accept(parser);
            snapshotDto.setRefsetRules(parser.getPlanDto().getRefsetRules());
            snapshotDto.setTerminal(parser.getPlanDto().getTerminal());
        }
        return fillMembers(snapshot, snapshotDto);
    }  
    

    public static SnapshotDto parseSansMembers(Snapshot snapshot){
        return SnapshotDto.getBuilder(snapshot.getId(), 
                snapshot.getTitle(),
                snapshot.getDescription(),
                snapshot.getPublicId(),
                null).build();
    }
    
    private static SnapshotDto fillMembers(Snapshot snap, SnapshotDto snapDto){
        Set<MemberDto> memberDtos = new HashSet<>();
        for (Member m : snap.getMembers()){
            memberDtos.add(MemberDto.parse(m));
        }
        snapDto.setMemberDtos(memberDtos);
        return snapDto;
    }
    
    public SnapshotDto(Long id, String publicId, String title, String description, Set<MemberDto> members){
        this.id = id;
        this.publicId = publicId;
        this.title = title;
        this.description = description;
        this.memberDtos = members;
    }
    
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("title", getTitle())
                .add("description", getDescription())
                .add("publicId", getPublicId())
                .add("conceptDtos", getMemberDtos() == null ? 0 : getMemberDtos().size())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof SnapshotDto){
            SnapshotDto dto = (SnapshotDto) o;
            if (Objects.equal(dto.getId(), getId()) &&
                    (Objects.equal(dto.getTitle(), getTitle())) &&
                    (Objects.equal(dto.getDescription(), getDescription())) &&
                    (Objects.equal(dto.getMemberDtos(), getMemberDtos())) &&
                    (Objects.equal(dto.getPublicId(), getPublicId()))){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode(){
        if (getId() != null){
            return Longs.hashCode(getId());
        }else if (getPublicId() != null){
            return getPublicId().hashCode();
        }else{
            return -1; //delegate to equals method
        }
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
    
    public Long getTerminal() {
        return terminal;
    }

    public void setTerminal(Long terminal) {
        this.terminal = terminal;
    }

    public List<RuleDto> getRefsetRules() {
        return refsetRules;
    }

    public void setRefsetRules(List<RuleDto> refsetRules) {
        this.refsetRules = refsetRules;
    }

    public Set<MemberDto> getMemberDtos() {
		return memberDtos;
	}

	public void setMemberDtos(Set<MemberDto> memberDtos) {
		this.memberDtos = memberDtos;
	}

	public static Builder getBuilder(Long id, String title, String description, String publicId, Set<MemberDto> members) {
        return new Builder(id, title, description, publicId, members);
    }
    
    public static class Builder {
        private SnapshotDto built;

        Builder(Long id, String title, String description, String publicId, Set<MemberDto> members) {
            built = new SnapshotDto();
            built.setDescription(description);
            built.setId(id);
            built.setPublicId(publicId);
            built.setTitle(title);
            built.setMemberDtos(members);
        }
        
        public SnapshotDto build(){
            return built;
        }
    }
}
