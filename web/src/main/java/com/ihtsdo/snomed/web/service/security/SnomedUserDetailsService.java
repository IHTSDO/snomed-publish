package com.ihtsdo.snomed.web.service.security;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.ihtsdo.snomed.web.model.User;

public interface SnomedUserDetailsService extends UserDetailsService{
    
    public void createUserDetails(User user);
    public void updateUserDetails(User user);

}
