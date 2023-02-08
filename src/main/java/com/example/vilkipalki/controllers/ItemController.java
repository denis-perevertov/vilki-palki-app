package com.example.vilkipalki.controllers;

import com.example.vilkipalki.models.Category;
import com.example.vilkipalki.models.MenuItem;
import com.example.vilkipalki.repos.CategoryRepository;
import com.example.vilkipalki.repos.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v3/items")
public class ItemController {

    @Autowired
    private MenuItemRepository itemRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    //получить все товары
    @GetMapping
    public List<MenuItem> getItems() {
        return itemRepo.findAll();
    }

    //получить все категории товаров
    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepo.findAll();
    }

    //получить конкретный товар
    @GetMapping("/{item_id}")
    public MenuItem getSingleItem(@PathVariable long item_id) {
        return itemRepo.findById(item_id).orElseThrow();
    }

    //todo: создать кастомный товар??
}
