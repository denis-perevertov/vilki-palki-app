package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.models.Address;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.Category;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.services.ItemService;
import com.example.vilkipalki2.services.OrderService;
import com.example.vilkipalki2.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest(classes = ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ItemController controller;

    ObjectMapper mapper;
    AppUser user;
    Address address;
    MenuItem item;

    @MockBean
    private ItemService itemService;

    @BeforeAll
    public static void init() {
        System.out.println("INIT");
    }

    @BeforeEach
    public void testSetup() {
        mockMvc = standaloneSetup(new ItemController(itemService))
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
    public void getAllItemsTest() throws Exception {
        mockMvc.perform(get("/api/v3/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void getSingleItemTest() throws Exception {

        String jsonItem = mapper.writeValueAsString(item);

        when(itemService.getSingleItem(1)).thenReturn(item);

        mockMvc.perform(get("/api/v3/items/{item_id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

    }

    @Test
    public void getCategoriesTest() throws Exception {
        mockMvc.perform(get("/api/v3/items/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void getCategoryItemsTest() throws Exception {
        Category category = new Category();
        category.setId(1);
        category.setName("test");
        category.setIconFileName("test.jpg");

        when(itemService.getSingleCategoryById(1L)).thenReturn(new Category());
        when(itemService.getItemsOfCategory(category)).thenReturn(List.of(item, item));

        mockMvc.perform(get("/api/v3/items/categories/{id}/items", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

        assertThat(itemService.getItemsOfCategory(category)).hasSize(2);
    }
}
