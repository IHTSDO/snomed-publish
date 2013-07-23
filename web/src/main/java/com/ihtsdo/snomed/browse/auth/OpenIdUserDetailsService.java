package com.ihtsdo.snomed.browse.auth;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import com.ihtsdo.snomed.browse.model.User;

@Named("openIdUserDetailsService")
public class OpenIdUserDetailsService implements UserDetailsService, AuthenticationUserDetailsService<OpenIDAuthenticationToken> 
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger( OpenIdUserDetailsService.class );

    @Inject
    private @Named("localUserDetailsService") SnomedUserDetailsService localUserDetailsService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return localUserDetailsService.loadUserByUsername(username);
    }
    
    @Override
    //@Transactional("transactionManagerAuthentication")
    public UserDetails loadUserDetails(OpenIDAuthenticationToken token){
        String email = getEmail(token);
        User user = null;
        try {
            user = (User)loadUserByUsername(email);
            fillAttributes(token, user);
            localUserDetailsService.updateUserDetails(user);
        } catch (UsernameNotFoundException e) {
            user = new User();
            fillAttributes(token, user);
            localUserDetailsService.createUserDetails(user);
        }        
        return (UserDetails)user;
    }

    private void fillAttributes(OpenIDAuthenticationToken token, User user) {
        for (OpenIDAttribute attribute : token.getAttributes()) {
            if (attribute.getName().equals("email")) {
                user.setEmail(attribute.getValues().get(0));
            }else if (attribute.getName().equals("firstname")) {
                user.setFirstname(attribute.getValues().get(0));
            }else if (attribute.getName().equals("lastname")) {
                user.setLastname(attribute.getValues().get(0));
            }else if (attribute.getName().equals("middlename")) {
                user.setMiddlename(attribute.getValues().get(0));
            }else if (attribute.getName().equals("prefix")) {
                user.setPrefix(attribute.getValues().get(0));
            }
        }
    }  

    private String getEmail(OpenIDAuthenticationToken token) {
        for (OpenIDAttribute attribute : token.getAttributes()) {
            if (attribute.getName().equals("email")) {
                return attribute.getValues().get(0);
            }
        }
        return null;
    }
}
