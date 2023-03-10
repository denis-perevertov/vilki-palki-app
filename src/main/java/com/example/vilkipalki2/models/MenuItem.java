package com.example.vilkipalki2.models;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="items")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MenuItem {

    @Id
    @SequenceGenerator(name="sequence_generator", initialValue = 0, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    private long id;

    @NotBlank
    private String name;

    @Min(1)
    private int price;

    private int weight, fats, proteins, carbons, calories;

    private String description;

    private String pictureFileName;

    private LocalDate creationDate = LocalDate.of(2022, 1, 1);

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name="category_items",
            joinColumns={@JoinColumn(name="item_id")},
            inverseJoinColumns={@JoinColumn(name="Category_id")})
    @JsonBackReference(value = "category_reference")
    private Category category;

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "item_ingredients",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "ingr_id"))
    private List<Ingredient> ingredients;

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "itemList")
    private List<Order> orders;

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "favoriteItemsList")
    private List<AppUser> users;

    public MenuItem() {}

    public MenuItem (long id) {
        this.id = id;
    }

    public MenuItem(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MenuItem(long id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public MenuItem(String name, int price, int weight, int fats, int proteins, int carbons, int calories, String description, LocalDate creationDate, Category category) {
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.fats = fats;
        this.proteins = proteins;
        this.carbons = carbons;
        this.calories = calories;
        this.description = description;
        this.creationDate = creationDate;
        this.category = category;
    }
}
