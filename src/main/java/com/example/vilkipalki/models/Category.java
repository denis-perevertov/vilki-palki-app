package com.example.vilkipalki.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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

    @ToString.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy="category_id")
    @JsonManagedReference
    private List<MenuItem> itemList;

}
