package com.example.vilkipalki2.controllers;


import com.example.vilkipalki2.models.Order;
import com.example.vilkipalki2.services.OrderService;
import com.example.vilkipalki2.services.UserService;
import com.example.vilkipalki2.util.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v3/orders")
@RequiredArgsConstructor
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;

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
    public ResponseEntity<String> addNewOrder(@Valid @RequestBody Order orderInfo) {
        Order savedOrder = orderService.createOrder(orderInfo);
        return ResponseEntity.ok("AppUser (id=" + savedOrder.getUser_id() +")" +
                " added a new Order (id=" + savedOrder.getId() + ")\nContent: \n" + savedOrder.getItemList());
    }

    //изменять заказ - статус/содержание
    @PutMapping("/{order_id}/edit")
    public ResponseEntity<String> changeOrder(@PathVariable long order_id, @Valid @RequestBody Order newOrder) {
        Order editedOrder = orderService.editOrder(order_id, newOrder);
        return ResponseEntity.ok("Edited order(id=" +order_id+ ")");
    }

    //обновление статуса цифрой + отменить
    @PutMapping("/{order_id}/change-status")
    public ResponseEntity<String> changeOrderStatus(@PathVariable long order_id, @Valid @RequestBody int status) {
        Order orderToUpdate = orderService.findOrder(order_id);
        OrderStatus oldStatus = orderToUpdate.getStatus();
        OrderStatus newStatus = OrderStatus.getStatusByNumber(status);
        orderToUpdate.setStatus(newStatus);

        orderService.saveOrder(orderToUpdate);

        return ResponseEntity.ok("Order(id="+orderToUpdate.getId()+"): " +
                "changed order status from "+oldStatus.name()+"("+oldStatus.getNumber()+") " +
                "to "+newStatus.name()+"("+newStatus.getNumber()+")");
    }

    //удалить заказ
    @DeleteMapping("/{order_id}/delete")
    public ResponseEntity<String> cancelOrder(@PathVariable long order_id) {
        orderService.deleteOrder(order_id);
        return ResponseEntity.ok("Successfully deleted order (id=" + order_id + ")");
    }
}
