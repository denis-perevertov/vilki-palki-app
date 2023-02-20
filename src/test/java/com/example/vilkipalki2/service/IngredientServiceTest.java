package com.example.vilkipalki2.service;

import com.example.vilkipalki2.TestConfig;
import com.example.vilkipalki2.repos.IngredientRepository;
import com.example.vilkipalki2.services.IngredientService;
import com.example.vilkipalki2.services.ItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
@SpringBootTest(classes = IngredientService.class)
public class IngredientServiceTest {

    @Autowired
    private IngredientService ingredientService;

    @MockBean
    private IngredientRepository ingredientRepository;

    @BeforeAll
    public static void init() {
        System.out.println("Starting tests");
    }

    @AfterEach
    public void clearDB() {
        ingredientRepository.deleteAll();
    }
}
