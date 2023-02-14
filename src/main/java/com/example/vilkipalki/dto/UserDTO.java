package com.example.vilkipalki.dto;

import com.example.vilkipalki.util.Language;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UserDTO {

    @NotBlank(message = "Имя обязательно")
    private String name;

    @PhoneNumber
    private String phone;

    @NotBlank(message = "Электронная почта обязательна")
    @Email
    private String email;

    private String avatarFileName;

    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    private String language;

    @NotBlank(message = "Пароль обязателен")
    private String password;
}
