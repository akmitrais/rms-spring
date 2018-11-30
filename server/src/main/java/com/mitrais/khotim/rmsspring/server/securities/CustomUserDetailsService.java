package com.mitrais.khotim.rmsspring.server.securities;

import com.mitrais.khotim.rmsspring.server.domains.User;
import com.mitrais.khotim.rmsspring.server.exceptions.ResourceNotFoundException;
import com.mitrais.khotim.rmsspring.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user with email : " + email));

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find user with id : " + id));

        return UserPrincipal.create(user);
    }
}
