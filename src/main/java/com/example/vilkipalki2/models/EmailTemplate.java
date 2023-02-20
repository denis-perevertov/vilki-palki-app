package com.example.vilkipalki2.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="templates")
public class EmailTemplate {

    @Id
    @SequenceGenerator(name="template_id_generator", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "template_id_generator")
    private int id;

    private String name;

    public EmailTemplate() {
    }

    public EmailTemplate(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
