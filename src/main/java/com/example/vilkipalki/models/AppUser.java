package com.example.vilkipalki.models;


import com.example.vilkipalki.util.AppUserRole;
import com.example.vilkipalki.util.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="users")
@Data
public class AppUser {

    @Id
    @SequenceGenerator(name="user_sequence_generator", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "user_sequence_generator")
    private long id;

    @Enumerated(EnumType.STRING)
    private AppUserRole role;

    //@NotBlank(message = "Имя обязательно")
    private String name;

    //@NotBlank(message = "Телефон обязателен")
    //@Size(min = 13, max = 13, message = "Неправильный формат телефона")
    private String phone;

    private String avatarFileName;

    //@Past
    //@DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    private Language language;

    //@NotBlank(message = "Электронная почта обязательна")
    @Email
    private String email;

    //@Size(min = 6, max = 20, message = "Пароль должен быть от 6 до 20 символов")
    private String password;

    @ElementCollection
    private List<Order> orderList;

    @ElementCollection
    private List<Address> addressList;

    @ElementCollection
    private List<MenuItem> favoriteItemsList;

    private int bonus;


}
