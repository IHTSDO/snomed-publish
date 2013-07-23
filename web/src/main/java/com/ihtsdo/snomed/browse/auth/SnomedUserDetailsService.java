package com.ihtsdo.snomed.browse.auth;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.ihtsdo.snomed.browse.model.User;

public interface SnomedUserDetailsService extends UserDetailsService{
    
    public void createUserDetails(User user);
    public void updateUserDetails(User user);

}
