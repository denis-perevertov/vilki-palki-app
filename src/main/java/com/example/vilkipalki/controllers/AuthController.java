package com.example.vilkipalki.controllers;

import com.example.vilkipalki.models.AppUser;
import com.example.vilkipalki.security.AuthRequest;
import com.example.vilkipalki.security.AuthResponse;
import com.example.vilkipalki.security.JwtProvider;
import com.example.vilkipalki.security.RegistrationRequest;
import com.example.vilkipalki.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.*;

@RestController
public class AuthController {

    private UserService userService;
    private JwtProvider jwtProvider;

    public AuthController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        AppUser user = new AppUser();
        user.setEmail(registrationRequest.login());
        user.setPassword(registrationRequest.password());
        userService.saveUser(user);
        return "Ok";
    }

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody AuthRequest request) {
        AppUser user = userService.findByLoginAndPassword(request.login(), request.password());
        String token = jwtProvider.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
