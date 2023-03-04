package com.example.vilkipalki2.services;

import com.example.vilkipalki2.exception.ItemNotFoundException;
import com.example.vilkipalki2.models.Ingredient;
import com.example.vilkipalki2.repos.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<Ingredient> getAllIngredients() {return ingredientRepository.findAll();}

    public Ingredient getSingleIngredientById(long ingr_id) {return ingredientRepository.findById(ingr_id).orElseThrow(ItemNotFoundException::new);}

    public Ingredient saveIngredientToDB(Ingredient ingredient) {return ingredientRepository.save(ingredient);}

    public Ingredient editIngredient(long ingr_id, Ingredient newIngredient) {
        return ingredientRepository.findById(ingr_id)
                .map(ingr -> {
                    ingr.setName(newIngredient.getName());
                    ingr.setIcon(newIngredient.getIcon());
                    return ingredientRepository.save(ingr);
                })
                .orElseGet(() -> ingredientRepository.save(newIngredient));
    }

    public void deleteIngredient(long ingr_id) {ingredientRepository.deleteById(ingr_id);}
}
