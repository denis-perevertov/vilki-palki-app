package com.example.vilkipalki2.security;

import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userService.findByLogin(username);
        return CustomUserDetails.fromUserToCustomUserDetails(user);
    }
}
