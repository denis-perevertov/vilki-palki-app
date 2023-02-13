package com.example.vilkipalki.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="ingredients")
@Data
@Embeddable
public class Ingredient {

    @Id
    @SequenceGenerator(name="ingr_generator", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingr_generator")
    private long id;

    private String name;

    //имя файла или blob
    private byte[] icon;
}
