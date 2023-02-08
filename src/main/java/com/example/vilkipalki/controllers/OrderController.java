package com.example.vilkipalki.controllers;


import com.example.vilkipalki.models.AppUser;
import com.example.vilkipalki.models.Order;
import com.example.vilkipalki.repos.OrderRepository;
import com.example.vilkipalki.repos.AppUserRepository;
import com.example.vilkipalki.services.OrderService;
import com.example.vilkipalki.util.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v3/orders")
public class OrderController {

    private OrderRepository orderRepo;
    private AppUserRepository userRepo;

    private OrderService orderService;

    public OrderController(OrderRepository orderRepo, AppUserRepository userRepo, OrderService orderService) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.orderService = orderService;
    }

    //получить все заказы
    @GetMapping
    public List<Order> getOrders() {
        return orderRepo.findAll();
    }

    //получить все заказы одного пользователя
    @GetMapping("/{user_id}")
    public List<Order> getUserOrders(@PathVariable long user_id) {
        AppUser appUser = userRepo.findById(user_id).orElseThrow();
        System.out.println(appUser.getOrderList());
        return appUser.getOrderList();
    }

    //добавить новый заказ для какого-то пользователя
    @PostMapping("/add-order")
    public ResponseEntity<String> addNewOrder(@Valid @RequestBody Order orderInfo) {

        Order savedOrder = orderService.createOrder(orderInfo);

        return ResponseEntity.ok("AppUser (id=" + savedOrder.getUser_id() +")" +
                " added a new Order (id=" + savedOrder.getId() + ")\nContent: \n" + savedOrder.getItemList());
    }

    //изменять статус заказа ???
    @PutMapping("/{order_id}/edit")
    public Order changeOrder(@PathVariable long order_id, @RequestBody Order newOrder) {
        return orderRepo.findById(order_id)
                .map(order -> {
                    order.setItemList(newOrder.getItemList());
                    order.setStatus(newOrder.getStatus());
                    order.setAddress(newOrder.getAddress());
                    return orderRepo.save(order);
                }).orElseGet(() -> orderRepo.save(newOrder));
    }

    //отменить заказ
    @DeleteMapping("/{order_id}/delete")
    public ResponseEntity<String> cancelOrder(@PathVariable long order_id) {
        orderRepo.delete(orderRepo.findById(order_id).orElseThrow());

        return ResponseEntity.ok("Successfully deleted order (id=" + order_id + ")");
    }
}
