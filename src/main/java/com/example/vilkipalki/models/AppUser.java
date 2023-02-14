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

    @Enumerated(EnumType.STRING)
    private Language language;

    private String name;
    private String phone;
    private String avatarFileName;
    private LocalDate birthdate;
    private String email;
    private String password;
    private int bonus;

    @ElementCollection
    private List<Order> orderList;

    @ElementCollection
    private List<Address> addressList;

    @ElementCollection
    private List<MenuItem> favoriteItemsList;

}
