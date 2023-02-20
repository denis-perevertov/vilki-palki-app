package com.example.vilkipalki2.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@PasswordMatch(first = "password", second = "confirm_password", message = "Пароли должны быть одинаковы")
public class PasswordDTO {

    @NotBlank(message = "Пароль обязателен")
    private String password;

    @NotBlank(message = "Подтвердите пароль")
    private String confirm_password;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PasswordDTO() {
    }

    public PasswordDTO(String password, String confirm_password) {
        this.password = password;
        this.confirm_password = confirm_password;
    }

    public static boolean passwordsMatch(String password, String confirm_password) {
        return (password == null && confirm_password == null) || (password != null && password.equals(confirm_password));
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public String getEncodedPassword() {
        return encodePassword(password);
    }

    public String getRegularPassword() {return password;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }
}
