package com.example.vilkipalki.services;

import com.example.vilkipalki.models.AppUser;
import com.example.vilkipalki.models.Order;
import com.example.vilkipalki.repos.AppUserRepository;
import com.example.vilkipalki.repos.OrderRepository;
import com.example.vilkipalki.util.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final AppUserRepository userRepository;
    private final OrderRepository orderRepository;

    public OrderService(AppUserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order orderInfo) {

        orderInfo.setStatus(OrderStatus.NEW);
        orderInfo.setDatetime(LocalDateTime.now());

        long user_id = orderInfo.getUser_id();
        AppUser appUser = userRepository.findById(user_id).orElseThrow();
        appUser.getOrderList().add(orderInfo);

        Order savedOrder = orderRepository.save(orderInfo);

        userRepository.save(appUser);

        return savedOrder;
    }
}
