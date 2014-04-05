package com.ihtsdo.snomed.model.refset;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Index;

import com.google.common.base.Objects;

@Entity
@org.hibernate.annotations.Table(appliesTo = "Snapshot",
        indexes={@Index(name="snapshotPublicIdAndRefsetIdIndex", columnNames={"publicId","refset_id"})})
@Table(name = "Snapshot", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"publicId","refset_id"}))
public class Snapshot {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    
    @NotNull
    @ManyToOne
    private Refset refset;
    
    @NotNull
    private int size;
    
    @ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinTable
    private Set<Member> immutableMembers = new HashSet<>();
    
    @NotNull
    @Size(min=2, max=40, message="Public ID must be between 2 and 20 characters")
    @Pattern(regexp="[a-zA-Z0-9_-]+", message="Public ID may contain characters, numbers, dashes, and underscores only")
    //For some reason, declaring uniqueness on columns like this, does not seem t work,
    //at least not for hibernate implementation of JPA 2. Moved to @Table level instead.
    //@Column(name="publicId", unique = true, nullable=false, length=30)
    private String publicId;
    
    @NotNull
    @Size(min=4, max=50, message="Title must be between 4 and 50 characters")
    private String title;
    
    @NotNull
    @Size(min=4, message="Description must be longer than 4 characters")
    private String description;
    
    @NotNull
    private Date creationTime;
    
    @NotNull
    private Date modificationTime;
    
    @Version
    private long version = 0;
    
    @OneToOne(targetEntity=BaseRule.class, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private Rule terminal;    
    
    public void update(String title, String description){
        //setPublicId(publicId);
        setTitle(title);
        setDescription(description);
    }
    
    public Snapshot() {}
        
    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("publicId", getPublicId())
                .add("title", getTitle())
                .add("description", getDescription())
                .add("immutableMembers", getImmutableMembers().size())
                .add("terminal", getTerminal())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Snapshot){
            Snapshot s = (Snapshot) o;
            if (Objects.equal(s.getPublicId(), getPublicId()) &&
                Objects.equal(s.getRefset(), getRefset()) && 
                Objects.equal(s.getImmutableMembers(), getImmutableMembers()) &&
                Objects.equal(s.getTerminal(), getTerminal())){
                return true;
            }
        }
        return false;
    }    
    
    @Override
    public int hashCode(){
        return java.util.Objects.hash(
                getRefset(),
                getPublicId());
    }

    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date(Calendar.getInstance().getTime().getTime());
    }
    
    @PrePersist
    public void prePersist() {
        Date now = new Date(Calendar.getInstance().getTime().getTime());
        creationTime = now;
        modificationTime = now;
    }    
    
//    
//    public Snapshot addMembers(Set<Member> immutableMembers){
//        getMembers().addAll(immutableMembers);
//        return this;
//    }
//
//    public Snapshot addMember(Member member){
//        getMembers().add(member);
//        return this;
//    }    
    
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
    
    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public Set<Member> getImmutableMembers() {
		return immutableMembers;
	}

	public void setImmutableMembers(Set<Member> members) {
		this.immutableMembers = members;
	}

	public Rule getTerminal() {
        return terminal;
    }

    public void setTerminal(Rule terminal) {
        this.terminal = terminal;
    }

    public void setStatus(Status status){
        this.status = status;
    }
    
    public Status getStatus(){
        return status;
    }   
    
    public void setRefset(Refset refset){
        this.refset = refset;
    }
    
    public Refset getRefset(){
        return refset;
    }
    
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static Builder getBuilder(String publicId, String title, String description, Refset refset, Set<Member> members, Rule terminal) {
        return new Builder(publicId, title, description, refset, members, terminal);
    }
    

    public static class Builder {
        private Snapshot built;

        Builder(String publicId, String title, String description, Refset refset, Set<Member> members, Rule terminal) {
            built = new Snapshot();
            built.title = title;
            built.publicId = publicId;
            built.description = description;
            built.refset = refset;
            built.immutableMembers.addAll(members);
            built.terminal = terminal;
            built.size = members.size();
        }

        public Snapshot build() {
            return built;
        }
    }

}
