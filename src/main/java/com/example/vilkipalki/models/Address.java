package com.example.vilkipalki.models;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class Address implements Serializable {

    private String street, house, apartment, entrance, code, floor;

    private String comment;

}
