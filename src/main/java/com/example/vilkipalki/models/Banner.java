package com.example.vilkipalki.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="banners")
public class Banner {

    @Id @GeneratedValue
    private long id;

    private String fileName;

}
