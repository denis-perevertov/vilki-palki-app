package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.models.Category;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v3/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    //получить все товары
    @GetMapping
    public List<MenuItem> getItems() {
        return itemService.getAllItems();
    }

    //получить все категории товаров
    @GetMapping("/categories")
    public List<Category> getCategories() {
        return itemService.getAllCategories();
    }

    //получить все товары одной категории
    @GetMapping("/categories/{id}/items")
    public List<MenuItem> getItemsOfCategory(@PathVariable long id) {
        return itemService.getItemsOfCategory(itemService.getSingleCategoryById(id));
    }

    //получить конкретный товар
    @GetMapping("/{item_id}")
    public MenuItem getSingleItem(@PathVariable long item_id) {
        return itemService.getSingleItem(item_id);
    }

}
