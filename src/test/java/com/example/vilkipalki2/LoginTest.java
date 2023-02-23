package com.example.vilkipalki2;

import com.example.vilkipalki2.controllers.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = LoginController.class)
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void loginErrorTest() throws Exception {


    }
}
