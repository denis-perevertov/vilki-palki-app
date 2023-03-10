package com.example.vilkipalki2.services;

import com.example.vilkipalki2.exception.ItemNotFoundException;
import com.example.vilkipalki2.exception.OrderNotFoundException;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.models.Order;
import com.example.vilkipalki2.repos.AppUserRepository;
import com.example.vilkipalki2.repos.MenuItemRepository;
import com.example.vilkipalki2.repos.OrderRepository;
import com.example.vilkipalki2.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log
public class OrderService {

    private final AppUserRepository userRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public Order createOrder(Order orderInfo) {

        log.info("Getting full item info from IDs...");
        List<MenuItem> itemList = orderInfo.getItemList();
        List<MenuItem> itemFullInfoList = new ArrayList<>();
        for(MenuItem item : itemList) {
            item = menuItemRepository.findById(item.getId()).orElseThrow(ItemNotFoundException::new);
            System.out.println(item);
            itemFullInfoList.add(item);
        }
        orderInfo.setItemList(itemFullInfoList);

        log.info("Setting NEW status and date/time...");
        orderInfo.setStatus(OrderStatus.NEW);
        orderInfo.setDatetime(LocalDateTime.now());

        log.info("Saving order into user's list...");
        long user_id = orderInfo.getUser_id();
        AppUser appUser = userRepository.findById(user_id).orElseThrow();
        appUser.getOrderList().add(orderInfo);

        log.info("Calculating bonus points from the order...");
        int bonusPoints = orderInfo.getTotalPrice() / 10;
        appUser.addBonus(bonusPoints);
        log.info("[Added " + bonusPoints + " bonus points to User(id="+user_id+")]");

        log.info("Saving order into DB...");
        Order savedOrder = orderRepository.save(orderInfo);
        userRepository.save(appUser);
        log.info("DONE");

        return savedOrder;
    }

    public List<Order> getAllOrders() {return orderRepository.findAll();}
    public Order findOrder(long order_id) {return orderRepository.findById(order_id).orElseThrow(OrderNotFoundException::new);}
    public Order editOrder(long order_id, Order newOrder) {
        return orderRepository.findById(order_id)
                .map(order -> {
                    order.setItemList(newOrder.getItemList());
                    order.setStatus(newOrder.getStatus());
                    order.setAddress(newOrder.getAddress());
                    return orderRepository.save(order);
                }).orElseGet(() -> orderRepository.save(newOrder));
    }
    public Order saveOrder(Order order) {return orderRepository.save(order);}
    public void deleteOrder(long order_id) {
        Order orderToDelete = orderRepository.findById(order_id).orElseThrow(OrderNotFoundException::new);
        orderRepository.delete(orderToDelete);
    }

    public void changeOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}
