package com.example.vilkipalki2.service;

import com.example.vilkipalki2.TestConfig;
import com.example.vilkipalki2.repos.CategoryRepository;
import com.example.vilkipalki2.repos.MenuItemRepository;
import com.example.vilkipalki2.services.ItemService;
import com.example.vilkipalki2.services.OrderService;
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
@SpringBootTest(classes = ItemService.class)
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private MenuItemRepository itemRepo;
    @MockBean
    private CategoryRepository categoryRepo;

    @BeforeAll
    public static void init() {
        System.out.println("Starting tests");
    }

    @AfterEach
    public void clearDB() {
        itemRepo.deleteAll();
        categoryRepo.deleteAll();
    }
}
