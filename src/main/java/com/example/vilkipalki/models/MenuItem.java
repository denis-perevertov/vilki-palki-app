package com.example.vilkipalki.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.util.List;

@Entity
@Table(name="items")
@Data
@ToString(exclude = "category_id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    @JsonBackReference
    private Category category_id;

    //Имя, стоимость, картинка
    @ElementCollection
    private List<Ingredient> ingredients;

}
