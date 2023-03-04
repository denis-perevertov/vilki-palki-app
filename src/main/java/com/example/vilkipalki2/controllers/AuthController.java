package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.security.AuthRequest;
import com.example.vilkipalki2.security.AuthResponse;
import com.example.vilkipalki2.security.JwtProvider;
import com.example.vilkipalki2.security.RegistrationRequest;
import com.example.vilkipalki2.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    public AuthController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/api/register")
    public String registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        AppUser user = new AppUser();
        user.setEmail(registrationRequest.getLogin());
        user.setPassword(registrationRequest.getPassword());
        userService.saveUser(user);
        return "Ok";
    }

    @PostMapping("/api/auth")
    public AuthResponse auth(@RequestBody AuthRequest request) {
        AppUser user = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
        String token = jwtProvider.generateToken(user.getEmail());
        return new AuthResponse(token);
    }


}
