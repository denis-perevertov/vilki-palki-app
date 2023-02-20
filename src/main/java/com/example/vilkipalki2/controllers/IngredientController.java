package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.models.Ingredient;
import com.example.vilkipalki2.services.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v3/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    public Ingredient getSingleIngredient(@PathVariable long ingr_id) {
        return ingredientService.getSingleIngredientById(ingr_id);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addIngredient(@Valid @RequestBody Ingredient ingredient) {
        ingredientService.saveIngredientToDB(ingredient);
        return ResponseEntity.ok("Added ingredient " + ingredient.getName() + " (id=" + ingredient.getId() + ")");
    }

    @PutMapping("/{id}/edit")
    public Ingredient editIngredient(@Valid @RequestBody Ingredient newIngredient, @PathVariable long id) {
        return ingredientService.editIngredient(id, newIngredient);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteIngredient(@PathVariable long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.ok("Deleted ingredient(id=" + id + ")");
    }



}
