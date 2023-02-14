package com.example.vilkipalki.services;

import com.example.vilkipalki.dto.UserDTO;
import com.example.vilkipalki.models.AppUser;
import com.example.vilkipalki.models.Order;
import com.example.vilkipalki.repos.AppUserRepository;
import com.example.vilkipalki.util.AppUserRole;
import com.example.vilkipalki.util.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<AppUser> getAllUsers() {return userRepository.findAll();}
    public List<Order> getUserOrders(long user_id) {
        AppUser user = userRepository.findById(user_id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getOrderList();
    }
    public AppUser editUserData(long user_id, UserDTO newUserData) {
        AppUser userToEdit = userRepository.findById(user_id).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userToEdit.setBirthdate(newUserData.getBirthdate());
        userToEdit.setName(newUserData.getName());
        userToEdit.setLanguage(Language.valueOf(newUserData.getLanguage()));
        userToEdit.setPassword(newUserData.getPassword());
        userToEdit.setEmail(newUserData.getEmail());
        userToEdit.setPhone(newUserData.getPhone());
        userToEdit.setAvatarFileName(newUserData.getAvatarFileName());

        return userRepository.save(userToEdit);
    }
    public void deleteUser(long user_id) {
        AppUser userToDelete = userRepository.findById(user_id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(userToDelete);
    }
}
