package com.example.vilkipalki.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.util.List;

@Entity
@Table(name="items")
@Data
public class MenuItem {

    @Id
    @SequenceGenerator(name="sequence_generator", initialValue = 0, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    private long id;

    private String name;

    private int price;
    //На страничке товара под названием
    private int weight, fats, proteins, carbons, calories;

    private String description;

    private String pictureFileName;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    @JsonBackReference
    private Category category_id;

    //Имя, стоимость, картинка
    @ManyToMany
    @JoinTable(
            name = "item_ingredients",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "ingr_id"))
    private List<Ingredient> ingredients;

    @ToString.Exclude
    @ManyToMany(mappedBy = "itemList")
    @JsonBackReference
    private List<Order> orders;

}
