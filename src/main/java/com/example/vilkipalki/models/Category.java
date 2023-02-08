package com.example.vilkipalki.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="categories")
public class Category {

    @SequenceGenerator(name="id_generator", initialValue = 0, allocationSize = 1)
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_generator")
    private long id;

    private String name;

    private String iconFileName;

    @OneToMany(mappedBy="category_id")
    private List<MenuItem> itemList;

}
