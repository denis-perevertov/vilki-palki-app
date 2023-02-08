package com.example.vilkipalki.controllers;

import com.example.vilkipalki.models.Ingredient;
import com.example.vilkipalki.repos.IngredientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v3/ingredients")
public class IngredientController {

    @Autowired
    private IngredientRepository repo;

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return repo.findAll();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addIngredient(@Valid @RequestBody Ingredient ingredient) {
        repo.save(ingredient);
        return ResponseEntity.ok("Added ingredient " + ingredient.getName() + " (id=" + ingredient.getId() + ")");
    }

    @PutMapping("/{id}/edit")
    public Ingredient editIngredient(@Valid @RequestBody Ingredient newIngredient, @PathVariable long id) {
        return repo.findById(id)
                .map(ingr -> {
                    ingr.setName(newIngredient.getName());
                    ingr.setIcon(newIngredient.getIcon());
                    return repo.save(ingr);
                })
                .orElseGet(() -> repo.save(newIngredient));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteIngredient(@PathVariable long id) {
        Ingredient ingredient = repo.findById(id).orElseThrow();
        repo.delete(ingredient);
        return ResponseEntity.ok("Deleted ingr. " + ingredient.getName() +  " (id=" + ingredient.getId() + ")");
    }



}
