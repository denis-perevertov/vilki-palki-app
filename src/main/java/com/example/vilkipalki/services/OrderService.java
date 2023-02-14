package com.example.vilkipalki.services;

import com.example.vilkipalki.exception.ItemNotFoundException;
import com.example.vilkipalki.exception.OrderNotFoundException;
import com.example.vilkipalki.models.Address;
import com.example.vilkipalki.models.AppUser;
import com.example.vilkipalki.models.MenuItem;
import com.example.vilkipalki.models.Order;
import com.example.vilkipalki.repos.AppUserRepository;
import com.example.vilkipalki.repos.MenuItemRepository;
import com.example.vilkipalki.repos.OrderRepository;
import com.example.vilkipalki.util.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final AppUserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(AppUserRepository userRepository,
                        OrderRepository orderRepository,
                        MenuItemRepository menuItemRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Order createOrder(Order orderInfo) {

        List<MenuItem> itemList = orderInfo.getItemList();
        for(MenuItem item : itemList) {
            item = menuItemRepository.findById(item.getId()).orElseThrow(ItemNotFoundException::new);
            System.out.println(item);
        }
        orderInfo.setItemList(itemList);

        orderInfo.setStatus(OrderStatus.NEW);
        orderInfo.setDatetime(LocalDateTime.now());

        long user_id = orderInfo.getUser_id();
        AppUser appUser = userRepository.findById(user_id).orElseThrow();
        appUser.getOrderList().add(orderInfo);

        Order savedOrder = orderRepository.save(orderInfo);

        userRepository.save(appUser);

        return savedOrder;
    }

    public List<Order> getAllOrders() {return orderRepository.findAll();}
    public Order editOrder(long order_id, Order newOrder) {
        return orderRepository.findById(order_id)
                .map(order -> {
                    order.setItemList(newOrder.getItemList());
                    order.setStatus(newOrder.getStatus());
                    order.setAddress(newOrder.getAddress());
                    return orderRepository.save(order);
                }).orElseGet(() -> orderRepository.save(newOrder));
    }
    public void deleteOrder(long order_id) {
        Order orderToDelete = orderRepository.findById(order_id).orElseThrow(OrderNotFoundException::new);
        orderRepository.delete(orderToDelete);
    }
}
