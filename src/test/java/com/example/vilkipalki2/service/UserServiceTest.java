package com.example.vilkipalki2.service;

import com.example.vilkipalki2.TestConfig;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.repos.AppUserRepository;
import com.example.vilkipalki2.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
@SpringBootTest(classes = UserService.class)
public class UserServiceTest {

    @MockBean
    private AppUserRepository appUserRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
    }

    @Test
    public void canGetAllUsers() {

        userService.getAllUsers();
        verify(appUserRepository).findAll();
        given(appUserRepository.findAll()).willReturn(new ArrayList<>());
        assertThat(userService.getAllUsers()).isNotNull();

    }

    @Test
    public void canAddUser() {
        AppUser user = new AppUser("email", "password");

        userService.saveUser(user);

        ArgumentCaptor<AppUser> userArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        verify(appUserRepository).save(userArgumentCaptor.capture());

        AppUser capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    public void doesntFindNonExistingUsers() {
        given(appUserRepository.findByEmail("testEmail")).willReturn(null);
        assertThat(userService.findByLoginAndPassword("testEmail", "testPassword")).isNull();
    }

    @Test
    public void throwsExceptionWhenUserNotFound() {
        assertThatThrownBy(() -> userService.getUser(1)).hasMessageContaining("User not found");
        assertThatThrownBy(() -> userService.getUser(1)).isInstanceOf(UsernameNotFoundException.class);
    }


}
