package com.example.vilkipalki.services;

import com.example.vilkipalki.models.AppUser;
import com.example.vilkipalki.repos.AppUserRepository;
import com.example.vilkipalki.util.AppUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public AppUser saveUser(AppUser appUser) {
        AppUserRole role = AppUserRole.ROLE_USER;
        appUser.setRole(role);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userRepository.save(appUser);
    }

    public AppUser findByLogin(String login) {
        return userRepository.findByEmail(login);
    }

    public AppUser findByLoginAndPassword(String login, String password) {
        AppUser user = userRepository.findByEmail(login);
        if(user != null) {
            if(passwordEncoder.matches(password, user.getPassword())) return user;
        }
        return null;
    }
}
