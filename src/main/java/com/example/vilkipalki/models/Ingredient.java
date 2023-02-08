package com.example.vilkipalki.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="ingredients")
@Data
@Embeddable
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    //имя файла или blob
    private String icon;
}
