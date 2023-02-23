package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.controllers.UserController;
import com.example.vilkipalki2.models.Address;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.services.ItemService;
import com.example.vilkipalki2.services.UserService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest(classes = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserController controller;

    ObjectMapper mapper;
    AppUser user;
    Address address;
    MenuItem item;

    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;

    @BeforeAll
    public static void init() {
        System.out.println("INIT");
    }

    @BeforeEach
    public void testSetup() {
        mockMvc = standaloneSetup(new UserController(itemService, userService))
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                .alwaysExpect(status().isOk())
                .build();

        mapper = new ObjectMapper();

        user = new AppUser("Test", "test", 100);
        user.setFavoriteItemsList(new ArrayList<>());
        user.setAddressList(new ArrayList<>());
        Address address = new Address("testStreet");

        item = new MenuItem(1, "test", 100);
    }

    @Test
    public void loadTest() throws Exception {
        assertThat(mockMvc).isNotNull();
        assertThat(controller).isNotNull();
    }

    @Test
    public void addUserTest() throws Exception {
        AppUser user = new AppUser("test@gmail.com", "test");
        user.setId(0);

        given(userService.saveUser(user)).willReturn(user);

        mockMvc.perform
                        (post("/api/v3/users/add-user")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(contains(user.toString())));

    }

    @Test
    public void getAllUsersTest() throws Exception {
        mockMvc.perform(get("/api/v3/users")
                        .with(user("admin").password("pass").roles("USER", "ADMIN"))
                        .contentType("application/json;charset=UTF-8")
                        .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk());
        System.out.println(controller.users());
        assertThat(controller.users()).isNotNull();
    }

    @Test
    public void getSpecificUserTest() throws Exception {
        given(userService.getUser(1L)).willReturn(new AppUser("Name", "Email", 11));

        mockMvc.perform(get("/api/v3/users/{user_id}", 1L)
                        .contentType("application/json;charset=UTF-8")
                        .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getSpecificAddressOfSpecificUser() throws Exception {
        user.setId(1L);
        user.setAddressList(new ArrayList<>());
        user.getAddressList().add(address);

        given(userService.getUser(1L)).willReturn(user);

        mockMvc.perform(get("/api/v3/users/{user_id}/address/{address_id}", 1L, 0)
                .contentType("application/json;charset=UTF-8")
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk());

        //todo пробовал проверять полученный объект, даёт ошибку Unparseable JSON String, когда создавал массив
    }

    @Test
    public void addAddressForUser() {

    }

    @Test
    public void addFavoriteItemForUser() throws Exception {
        given(userService.getUser(1L)).willReturn(user);

        mockMvc.perform(post("/api/v3/users/{user_id}/favorites/add", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"test\", \"price\": 100}")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getFavoriteItemsForUser() throws Exception {
        user.getFavoriteItemsList().add(item);

        given(userService.getUser(1L)).willReturn(user);

        mockMvc.perform(get("/api/v3/users/{user_id}/favorites", 1L)
                .contentType("application/json;charset=UTF-8")
                .accept("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk());

        //todo Expected: JSON object, Received: JSON array
    }

}
