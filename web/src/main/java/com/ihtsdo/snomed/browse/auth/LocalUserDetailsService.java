package com.ihtsdo.snomed.browse.auth;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihtsdo.snomed.browse.model.Role;
import com.ihtsdo.snomed.browse.model.User;

@Service("localUserDetailsService")
public class LocalUserDetailsService implements SnomedUserDetailsService{
    private static final Logger LOG = LoggerFactory.getLogger( LocalUserDetailsService.class );

    @PersistenceContext(unitName="hibernatePersistenceUnitAuthentication")
    EntityManager em;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            TypedQuery<User> query = em.createQuery("SELECT u from User u WHERE u.email=:email", User.class);
            query.setParameter("email", username);
            User user = query.getSingleResult();
            return user;
        } catch (NoResultException e) {
            throw new UsernameNotFoundException("Unable to find user with email " + username + " locally", e);
        }
    }

    @Override
    @Transactional("transactionManagerAuthentication")
    public void updateUserDetails(User user){
        user.setLastLogin(new Timestamp(new Date().getTime()));
        em.merge(user);
        em.flush();
    }
    
    @Override
    @Transactional("transactionManagerAuthentication")
    public void createUserDetails(User user){
        
        Role role;
        try {
            TypedQuery<Role> query = em.createQuery("SELECT r from Role r WHERE r.name=:name", Role.class);
            query.setParameter("name", "ROLE_USER");        
            role = query.getSingleResult();
        } catch (NoResultException e) {
            role = new Role("ROLE_USER");
            LOG.info("Creating role [{}]", role.getName());
            em.persist(role);
        }
        
        user.addRole(role);
        LOG.info("Creating new user [{}]", user.toString());
        em.persist(user);
        em.flush();
    }
}
