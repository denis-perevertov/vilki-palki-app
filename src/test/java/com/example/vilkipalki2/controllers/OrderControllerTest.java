package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.models.Address;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.models.Order;
import com.example.vilkipalki2.services.ItemService;
import com.example.vilkipalki2.services.OrderService;
import com.example.vilkipalki2.services.UserService;
import com.example.vilkipalki2.util.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest(classes = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OrderController controller;

    ObjectMapper mapper;
    AppUser user;
    Address address;
    MenuItem item;

    @MockBean
    private OrderService orderService;
    @MockBean
    private UserService userService;

    @BeforeAll
    public static void init() {
        System.out.println("INIT");
    }

    @BeforeEach
    public void testSetup() {
        mockMvc = standaloneSetup(new OrderController(userService, orderService))
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                .alwaysExpect(status().isOk())
                .build();

        mapper = new ObjectMapper();

        user = new AppUser("Test", "test", 100);
        user.setFavoriteItemsList(new ArrayList<>());
        user.setOrderList(new ArrayList<>());
        user.setAddressList(new ArrayList<>());
        Address address = new Address("testStreet");

        item = new MenuItem(1, "test", 100);
    }

    @Test
    public void getAllOrdersTest() throws Exception {
        mockMvc.perform(get("/api/v3/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getOrdersForUserTest() throws Exception {
        Order order = new Order();
        order.setItemList(new ArrayList<>());
        when(userService.getUserOrders(1L)).thenReturn(List.of(order, order, order));

        mockMvc.perform(get("/api/v3/orders/{user_id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(controller.getUserOrders(1L)).hasSize(3);
    }

    @Test
    public void addOrderTest() throws Exception {
        Order order = new Order();
        order.setItemList(List.of(new MenuItem(1, "test", 100)));
        order.setAddress(address);

        Order newOrder = order.clone();
        newOrder.setId(1);
        newOrder.setStatus(OrderStatus.NEW);

        String jsonOrder = mapper.writeValueAsString(order);

        when(orderService.createOrder(order)).thenReturn(newOrder);

        mockMvc.perform(post("/api/v3/orders/add-order")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonOrder))
                .andExpect(status().isOk());

        System.out.println(order);
        System.out.println(newOrder);
    }

    @Test
    public void editOrderTest() throws Exception {
        Order order = new Order();
        order.setItemList(List.of(new MenuItem(1, "test", 100)));
        order.setId(1);
        order.setAddress(address);

        Order newOrder = order.clone();
        newOrder.setAddress(new Address("testStreet10102"));
        newOrder.setCurrent(false);
        newOrder.setStatus(OrderStatus.ON_THE_WAY);
        newOrder.setDatetime(LocalDateTime.now());

        mapper.registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module());

        String jsonOrder = mapper.writeValueAsString(newOrder);

        when(orderService.editOrder(1, newOrder)).thenReturn(newOrder);

        mockMvc.perform(put("/api/v3/orders/{order_id}/edit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonOrder))
                .andExpect(status().isOk());

        System.out.println(order);
        System.out.println(newOrder);
    }

    @Test
    public void changeOrderStatusTest() throws Exception {
        Order order = new Order();
        order.setItemList(List.of(new MenuItem(1, "test", 100)));
        order.setId(1);
        order.setAddress(address);
        order.setStatus(OrderStatus.NEW);

        Order newOrder = order.clone();
        newOrder.setStatus(OrderStatus.COMPLETED);

        when(orderService.findOrder(1)).thenReturn(order);
        when(orderService.saveOrder(order)).thenReturn(newOrder);

        String expected = "Order(id="+order.getId()+"): changed order status from NEW(0) to COMPLETED(4)";

        mockMvc.perform(put("/api/v3/orders/{order_id}/change-status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("4"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));

        assertThat(orderService.saveOrder(order).getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

}
