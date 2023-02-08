package com.example.vilkipalki.models;


import com.example.vilkipalki.util.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="orders")
@Data
public class Order implements Serializable {

    @Id
    @SequenceGenerator(name="order_id_generator", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="order_id_generator")
    private long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    private Address address;

    private long user_id;

    @ElementCollection
    private List<MenuItem> itemList;

    @DateTimeFormat(pattern="yyyy-MM-dd // hh:mm")
    private LocalDateTime datetime;

}
