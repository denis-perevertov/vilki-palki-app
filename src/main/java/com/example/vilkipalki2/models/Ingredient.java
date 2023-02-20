package com.example.vilkipalki2.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name="ingredients")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property="@UUID")
public class Ingredient {

    @Id
    @SequenceGenerator(name="ingr_generator", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingr_generator")
    private long id;

    @NotBlank
    private String name;

    private String icon;

    @ManyToMany(mappedBy = "ingredients")
    @ToString.Exclude
    private List<MenuItem> itemList;
}
