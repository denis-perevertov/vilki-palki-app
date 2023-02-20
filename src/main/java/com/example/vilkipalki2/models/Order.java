package com.example.vilkipalki2.models;


import com.example.vilkipalki2.util.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="orders")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property="@UUID")
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

    @ManyToMany
    @JoinTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    @NotBlank(message = "Список не должен быть пустым")
    private List<MenuItem> itemList;

    @Past
    @DateTimeFormat(pattern="yyyy-MM-dd // hh:mm")
    private LocalDateTime datetime;

    @Transient
    private boolean current;

    public Order(Address address, long user_id, List<MenuItem> itemList) {
        this.address = address;
        this.user_id = user_id;
        this.itemList = itemList;
    }

    public boolean current() {return this.status.current();}

    public int getTotalPrice() {
        int sum = 0;
        return this.getItemList().stream().map(MenuItem::getPrice).reduce(0, Integer::sum);
    }


}
