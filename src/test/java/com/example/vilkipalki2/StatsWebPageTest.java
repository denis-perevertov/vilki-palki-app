package com.example.vilkipalki2;

import com.example.vilkipalki2.controllers.AdminPanelController;
import com.example.vilkipalki2.models.Address;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.models.Order;
import com.example.vilkipalki2.repos.TelegramUserRepository;
import com.example.vilkipalki2.services.*;
import com.example.vilkipalki2.telegram.MyBot;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static
        org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class StatsWebPageTest {

    @Test
    public void testStatsPage() throws Exception {

        UserService userService = mock(UserService.class);
        OrderService orderService = mock(OrderService.class);
        ItemService itemService = mock(ItemService.class);
        IngredientService ingredientService = mock(IngredientService.class);
        BannerService bannerService = mock(BannerService.class);
        TelegramUserRepository telegramUserRepository = mock(TelegramUserRepository.class);
        MyBot botMock = mock(MyBot.class);

        AdminPanelController controller = new AdminPanelController(userService,
                orderService, itemService, ingredientService, bannerService, telegramUserRepository, botMock);

        MockMvc mockMvc = standaloneSetup(controller).build();

        List<AppUser> expectedUserList = createUserList(40);
        List<Order> expectedOrderList = createOrderList(40);

        List<AppUser> fakeUserList = createUserList2(40);

        when(userService.getAllUsers()).thenReturn(expectedUserList);
        when(orderService.getAllOrders()).thenReturn(expectedOrderList);

        mockMvc.perform(get("/admin"))
                .andExpect(view().name("admin_panel/stats"))
                .andExpect(model().attributeExists("users", "orders"))
                .andExpect(model().attribute("users", hasItems(expectedUserList.toArray())))
                //.andExpect(model().attribute("users", hasItems(fakeUserList.toArray())))
                .andExpect(model().attribute("orders", hasItems(expectedOrderList.toArray())));
    }

    public static List<AppUser> createUserList(int amount) {
        List<AppUser> list = new ArrayList<>();
        for(int i = 0; i < amount; i++) list.add(new AppUser("name", "email", 100));
        return list;
    }

    public static List<AppUser> createUserList2(int amount) {
        List<AppUser> list = new ArrayList<>();
        for(int i = 0; i < amount-10; i++) list.add(new AppUser("name22", "email22", 5));
        return list;
    }

    public static List<Order> createOrderList(int amount) {
        List<Order> list = new ArrayList<>();
        for(int i = 0; i < amount; i++) list.add(new Order(new Address("testStreet"),
                1,
                List.of(new MenuItem(i, "name", 100))));
        return list;
    }
}
