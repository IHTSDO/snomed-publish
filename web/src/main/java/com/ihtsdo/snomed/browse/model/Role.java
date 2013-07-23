package com.ihtsdo.snomed.browse.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class Role implements GrantedAuthority{
    private static final long serialVersionUID = 2021323425325577160L;

    @Id
    private long id;
    
    @Column(unique=true)
    private String name;
    
    @ManyToMany(mappedBy="roles")
    List<User> users;

    public Role(){}
    
    public String toString(){
        return getName();
    }
    
    public Role(String name){
        this.setName(name);
    }
    
    @Override
    public String getAuthority(){
        return name;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
