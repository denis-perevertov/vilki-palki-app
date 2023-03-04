package com.example.vilkipalki2.service;

import com.example.vilkipalki2.TestConfig;
import com.example.vilkipalki2.exception.OrderNotFoundException;
import com.example.vilkipalki2.models.Address;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.models.Order;
import com.example.vilkipalki2.repos.AppUserRepository;
import com.example.vilkipalki2.repos.MenuItemRepository;
import com.example.vilkipalki2.repos.OrderRepository;
import com.example.vilkipalki2.services.OrderService;
import com.example.vilkipalki2.util.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
@SpringBootTest(classes = OrderService.class)
public class OrderServiceTest {

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private AppUserRepository appUserRepository;
    @MockBean
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderService orderService;

    @Test
    public void throwsExceptionWhenOrderNotFound() {
        assertThatThrownBy(() -> orderService.findOrder(1)).hasMessageContaining("Order not found");
        assertThatThrownBy(() -> orderService.findOrder(1)).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    public void getFullOrderInfoFromIDTest() {

        given(menuItemRepository.findById(1L)).willReturn(Optional.of(new MenuItem(1, "testItem")));

        Order orderInfoFromApp =
                new Order(new Address("test"),
                        1,
                        List.of(new MenuItem(1L))
                );
        MenuItem fullItem = menuItemRepository.findById(orderInfoFromApp.getItemList().get(0).getId()).orElseThrow();
        MenuItem itemToFind = new MenuItem(1, "testItem");

        assertThat(fullItem).isEqualTo(itemToFind);

    }

    @Test
    public void datetimeAndStatusTest() {

        AppUser user = new AppUser();
        user.setOrderList(new ArrayList<>());

        Order orderInfoFromApp =
                new Order(new Address("test"),
                        1,
                        List.of(new MenuItem(1L, "Test", 100))
                );

        given(appUserRepository.findById(1L)).willReturn(Optional.of(user));
        given(menuItemRepository.findById(1L)).willReturn(Optional.of(new MenuItem(1, "testItem", 100)));
        given(orderRepository.save(orderInfoFromApp)).willReturn(orderInfoFromApp);

        Order savedOrder = orderService.createOrder(orderInfoFromApp);

        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.NEW);
        assertThat(savedOrder.getDatetime()).isNotNull();

    }

    @Test
    public void addOrderToUserListTest() {
        AppUser user = new AppUser();
        user.setOrderList(new ArrayList<>());

        Order orderInfoFromApp =
                new Order(new Address("test"),
                        1,
                        List.of(new MenuItem(1L, "Test", 100))
                );

        given(appUserRepository.findById(1L)).willReturn(Optional.of(user));
        given(menuItemRepository.findById(1L)).willReturn(Optional.of(new MenuItem(1, "testItem", 100)));
        given(orderRepository.save(orderInfoFromApp)).willReturn(orderInfoFromApp);

        Order savedOrder = orderService.createOrder(orderInfoFromApp);

        assertThat(user.getOrderList()).hasSize(1);
        assertThat(user.getBonus()).isEqualTo(10);

    }

}
