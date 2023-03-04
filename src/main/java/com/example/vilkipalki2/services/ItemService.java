package com.example.vilkipalki2.services;

import com.example.vilkipalki2.exception.CategoryNotFoundException;
import com.example.vilkipalki2.exception.ItemNotFoundException;
import com.example.vilkipalki2.models.Category;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.repos.CategoryRepository;
import com.example.vilkipalki2.repos.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final MenuItemRepository itemRepo;
    private final CategoryRepository categoryRepo;

    public List<MenuItem> getAllItems() {return itemRepo.findAll();}
    public MenuItem getSingleItem(long item_id) {return itemRepo.findById(item_id).orElseThrow(ItemNotFoundException::new);}
    public List<MenuItem> getItemsOfCategory(Category category) {return category.getItemList();}

    public String getPictureOfItemByID(long item_id) {return itemRepo.getPictureById(item_id);}

    public List<Category> getAllCategories() {return categoryRepo.findAll();}
    public Category getSingleCategoryById(long category_id) {return categoryRepo.findById(category_id).orElseThrow(CategoryNotFoundException::new);}
    public Category getSingleCategoryByName(String name) {return categoryRepo.findByName(name).orElseThrow(CategoryNotFoundException::new);}

    public MenuItem saveItem(MenuItem item) {return itemRepo.save(item);}
    public Category saveCategory(Category category) {return categoryRepo.save(category);}

    public void deleteItem(long item_id) {itemRepo.deleteById(item_id);}
    public void deleteCategory(long category_id) {categoryRepo.deleteById(category_id);}
}
