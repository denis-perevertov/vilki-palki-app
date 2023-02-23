package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.models.Address;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.Ingredient;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.services.IngredientService;
import com.example.vilkipalki2.services.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest(classes = IngredientController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IngredientController controller;

    ObjectMapper mapper;
    AppUser user;
    Address address;
    MenuItem item;

    @MockBean
    private IngredientService ingredientService;

    @BeforeAll
    public static void init() {
        System.out.println("INIT");
    }

    @BeforeEach
    public void testSetup() {
        mockMvc = standaloneSetup(new IngredientController(ingredientService))
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
    public void getAllIngredientsTest() throws Exception {
        mockMvc.perform(get("/api/v3/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void getSingleIngredientTest() throws Exception {
        Ingredient ingredient = new Ingredient();

        when(ingredientService.getSingleIngredientById(1L)).thenReturn(ingredient);

        mockMvc.perform(get("/api/v3/ingredients/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void addIngredientTest() throws Exception {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("test");
        ingredient.setIcon("icon.jpg");
        ingredient.setItemList(new ArrayList<>());

        String jsonIngredient = mapper.writeValueAsString(ingredient);

        Ingredient savedIngredient = ingredient.clone();
        savedIngredient.setId(1);

        String expected = mapper.writeValueAsString(savedIngredient);

        when(ingredientService.saveIngredientToDB(ingredient)).thenReturn(savedIngredient);

        mockMvc.perform(post("/api/v3/ingredients/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonIngredient))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Added ingredient "+ingredient.getName() + " (id="+ingredient.getId()+")"));
    }

    @Test
    public void editIngredientTest() throws Exception {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1);
        ingredient.setName("test");
        ingredient.setIcon("icon.jpg");
        ingredient.setItemList(new ArrayList<>());

        String jsonIngredient = mapper.writeValueAsString(ingredient);

        Ingredient editedIngredient = ingredient.clone();
        editedIngredient.setName("editedName");

        when(ingredientService.editIngredient(1L, editedIngredient))
                .thenReturn(editedIngredient);

        editedIngredient = ingredientService.editIngredient(1, editedIngredient);

        String jsonEditedIngredient = mapper.writeValueAsString(editedIngredient);

        mockMvc.perform(put("/api/v3/ingredients/{id}/edit", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonIngredient))
                .andExpect(status().isOk());
    }

}
