package com.example.vilkipalki2.services;

import com.example.vilkipalki2.dto.UserDTO;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.Order;
import com.example.vilkipalki2.repos.AppUserRepository;
import com.example.vilkipalki2.util.AppUserRole;
import com.example.vilkipalki2.util.Language;
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
    public AppUser getUser(long user_id) {return userRepository.findById(user_id).orElseThrow(() -> new UsernameNotFoundException("User not found"));}
    public List<Order> getUserOrders(long user_id) {
        AppUser user = this.getUser(user_id);
        return user.getOrderList();
    }
    public AppUser editUserData(long user_id, UserDTO newUserData) {
        AppUser userToEdit = userRepository.findById(user_id).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userToEdit.setBirthdate(newUserData.getBirthdate());
        userToEdit.setName(newUserData.getName());
        userToEdit.setLanguage(Language.valueOf(newUserData.getLanguage()));
        userToEdit.setEmail(newUserData.getEmail());
        userToEdit.setPhone(newUserData.getPhone());

        return userRepository.save(userToEdit);
    }
    public void deleteUser(long user_id) {
        AppUser userToDelete = this.getUser(user_id);
        userRepository.delete(userToDelete);
    }

    public List<UserDTO> getUsersDTO() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::new).toList();
    }
    public UserDTO getUserDTOById(long user_id) {
        return userRepository.findById(user_id).map(UserDTO::new).orElseThrow();
    }

    public String getUserEmailById(long user_id) {
        return userRepository.findEmailById(user_id);
    }

    public AppUser getFullUserInfoFromDTO(UserDTO dto) {
        return userRepository.findById(dto.getId()).orElseThrow();
    }

    public AppUser fromDTOToUser(UserDTO dto) {
        return new AppUser(dto);
    }
}
