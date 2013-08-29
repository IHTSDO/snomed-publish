package com.ihtsdo.snomed.web.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Objects;

@Entity
public class User implements UserDetails{
    private static final long serialVersionUID = 7945850323914332440L;
    
    @Id
    @GeneratedValue
    private long id;    
    
    private String firstname;
    private String lastname;
    private String middlename;
    private String prefix;
    private String email;
    private String password;
    
    @Column(columnDefinition = "BIT", length = 1)
    private boolean enabled;

    private Timestamp lastLogin;
    private Timestamp firstLogin;    

    @ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private List<Role> roles = new ArrayList<Role>();
    
    public User(){
        lastLogin = new Timestamp(new Date().getTime());
        firstLogin = new Timestamp(new Date().getTime());
        enabled = true;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("email", getEmail())
                .add("prefix", getPrefix()==null?"":getPrefix())
                .add("firstname", getFirstname()==null?"":getFirstname())
                .add("middlename", getMiddlename()==null?"":getMiddlename())
                .add("lastname", getLastname()==null?"":getLastname())
                .add("roles", getRoles())
                .toString();
    }    
    


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
 

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    
    public void addRole(Role role) {
        this.getRoles().add(role);
    }    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Timestamp getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Timestamp firstLogin) {
        this.firstLogin = firstLogin;
    }




    
}
