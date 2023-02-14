package com.example.vilkipalki.controllers;


import com.example.vilkipalki.models.AppUser;
import com.example.vilkipalki.models.Order;
import com.example.vilkipalki.repos.OrderRepository;
import com.example.vilkipalki.repos.AppUserRepository;
import com.example.vilkipalki.services.OrderService;
import com.example.vilkipalki.services.UserService;
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
    private UserService userService;
    private OrderService orderService;

    public OrderController(OrderRepository orderRepo,
                           AppUserRepository userRepo,
                           OrderService orderService,
                           UserService userService) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.orderService = orderService;
        this.userService = userService;
    }

    //получить все заказы
    @GetMapping
    public List<Order> getOrders() {
        return orderService.getAllOrders();
    }

    //получить все заказы одного пользователя
    @GetMapping("/{user_id}")
    public List<Order> getUserOrders(@PathVariable long user_id) {
        return userService.getUserOrders(user_id);
    }

    //добавить новый заказ для какого-то пользователя
    @PostMapping("/add-order")
    public ResponseEntity<String> addNewOrder(@RequestBody Order orderInfo) {
        Order savedOrder = orderService.createOrder(orderInfo);
        return ResponseEntity.ok("AppUser (id=" + savedOrder.getUser_id() +")" +
                " added a new Order (id=" + savedOrder.getId() + ")\nContent: \n" + savedOrder.getItemList());
    }

    //изменять заказ - статус/содержание
    @PutMapping("/{order_id}/edit")
    public ResponseEntity<String> changeOrder(@PathVariable long order_id, @RequestBody Order newOrder) {
        Order editedOrder = orderService.editOrder(order_id, newOrder);
        return ResponseEntity.ok("Edited order(id=" +order_id+ ")");
    }

    //отменить заказ
    @DeleteMapping("/{order_id}/delete")
    public ResponseEntity<String> cancelOrder(@PathVariable long order_id) {
        orderService.deleteOrder(order_id);
        return ResponseEntity.ok("Successfully deleted order (id=" + order_id + ")");
    }
}
