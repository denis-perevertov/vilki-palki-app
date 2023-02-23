package com.example.vilkipalki2.dto;

import com.example.vilkipalki2.models.AppUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@PasswordMatch(first = "password", second = "confirm_password", message = "Пароли должны быть одинаковы")
public class UserDTO {

    private long id;

    @NotBlank(message = "Имя обязательно")
    private String name;

    //кастомный валидатор , соответствует украинскому номеру телефона [пример: +380665025125]
    @PhoneNumber
    private String phone;

    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    private String language;

    private String gender;

    @NotBlank(message = "Электронная почта обязательна")
    @Email(message = "Неправильный формат эл.почты")
    private String email;

    private int bonus;

    @NotBlank(message = "Пароль обязателен")
    private String password;

    @NotBlank(message = "Подтвердите пароль")
    private String confirm_password;

    public UserDTO(){}

    public UserDTO(AppUser user) {
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.language = String.valueOf(user.getLanguage());
        this.gender = String.valueOf(user.getGender());
        this.birthdate = user.getBirthdate();
        this.bonus = user.getBonus();
    }

}
