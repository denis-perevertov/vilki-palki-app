package com.example.vilkipalki.controllers;

import com.example.vilkipalki.models.Address;
import com.example.vilkipalki.models.AppUser;
import com.example.vilkipalki.models.MenuItem;
import com.example.vilkipalki.repos.MenuItemRepository;
import com.example.vilkipalki.repos.AppUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v3/users")
@SuppressWarnings("unused")
public class UserController {

    private final AppUserRepository userRepo;
    private final MenuItemRepository menuItemRepository;

    public UserController(AppUserRepository userRepo, MenuItemRepository menuItemRepository) {
        this.userRepo = userRepo;
        this.menuItemRepository = menuItemRepository;
    }

    // ----------------- ПОЛЬЗОВАТЕЛИ ----------------- //

    @GetMapping
    public List<AppUser> users() {
        return userRepo.findAll();
    }

    @PostMapping("/add-appUser")
    public ResponseEntity<String> addUser(@Valid @RequestBody AppUser appUser) {
        AppUser newAppUser = userRepo.save(appUser);

        System.out.println(ResponseEntity.ok().build());

        return ResponseEntity.ok().body("Added new appUser " + newAppUser.getName() + " , id = " + newAppUser.getId());
    }

    @PutMapping("/{id}/edit")
    public AppUser editUserById(@Valid @RequestBody AppUser newAppUser, @PathVariable long id) {

        return userRepo.findById(id)
                .map(user -> {
                    user.setBirthdate(newAppUser.getBirthdate());
                    user.setName(newAppUser.getName());
                    user.setLanguage(newAppUser.getLanguage());
                    user.setPassword(newAppUser.getPassword());
                    user.setEmail(newAppUser.getEmail());
                    user.setPhone(newAppUser.getPhone());
                    user.setAvatarFileName(newAppUser.getAvatarFileName());
                    return userRepo.save(user);
                })
                .orElseGet(() -> userRepo.save(newAppUser));

    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        userRepo.delete(userRepo.findById(id).orElseThrow());
        return ResponseEntity.ok().body("Successfully deleted user (id="+id+")");
    }

    // ----------------- ПОЛЬЗОВАТЕЛИ ----------------- //


    // ----------------- АДРЕСА ----------------- //


    //получение всех адресов одного пользователя
    @GetMapping("/{user_id}/address")
    public ResponseEntity<List<Address>> getAddressesForUser(@PathVariable long user_id) {

        AppUser user = userRepo.findById(user_id).orElseThrow();
        List<Address> userAddressList = user.getAddressList();

        return ResponseEntity.ok().body(userAddressList);
    }

    //получение одного адреса одного пользователя
    @GetMapping("/{user_id}/address/{address_id}")
    public Address getSingleAddressForUser(@PathVariable long user_id, @PathVariable int address_id) {
        AppUser user = userRepo.findById(user_id).orElseThrow();

        return user.getAddressList().get(address_id);
    }

    //добавление адреса
    @PostMapping("/{user_id}/address/add-new")
    public ResponseEntity<String> addNewAddress(@PathVariable long user_id, @Valid @RequestBody Address address) {

        AppUser user = userRepo.findById(user_id).orElseThrow();
        user.getAddressList().add(address);

        userRepo.save(user);

        return ResponseEntity.ok("Added new address to user");
    }

    //изменение адреса
    @PutMapping("/{user_id}/address/{address_id}/edit")
    public ResponseEntity<String> editAddressForUser(@PathVariable long user_id,
                                                     @PathVariable int address_id,
                                                     @RequestBody Address newAddress) {
        // todo: сделать изменение

        AppUser user = userRepo.findById(user_id).orElseThrow();
        Address addressToEdit = user.getAddressList().get(address_id);

        addressToEdit.setComment(newAddress.getComment());
        addressToEdit.setCode(newAddress.getCode());
        addressToEdit.setApartment(newAddress.getApartment());
        addressToEdit.setFloor(newAddress.getFloor());
        addressToEdit.setEntrance(newAddress.getEntrance());
        addressToEdit.setStreet(newAddress.getStreet());
        addressToEdit.setHouse(newAddress.getHouse());

        userRepo.save(user);

        return ResponseEntity.ok("Edited address " +
                "for AppUser " + user.getName() + " (id=" + user.getId() + ")");
    }

    //удаление адреса
    @DeleteMapping("/{user_id}/address/{address_id}/delete")
    public ResponseEntity<String> deleteAddressForUser(@PathVariable long user_id,
                                                       @PathVariable int address_id) {
        AppUser user = userRepo.findById(user_id).orElseThrow();
        Address addressToDelete = user.getAddressList().get(address_id);

        user.getAddressList().remove(addressToDelete);

        userRepo.save(user);

        return ResponseEntity.ok("Deleted address for AppUser "+user.getName()+" (id="+user.getId()+")");
    }


    // ----------------- АДРЕСА ----------------- //

    // ----------------- ЛЮБИМЫЕ БЛЮДА ----------------- //

    //todo:сделать

    @GetMapping("/{user_id}/favorites")
    public List<MenuItem> getFavoriteItemsForUser(@PathVariable long user_id) {
        AppUser appUser = userRepo.findById(user_id).orElseThrow();

        return appUser.getFavoriteItemsList();
    }

    @PostMapping("/{user_id}/favorites/add")
    public ResponseEntity<String> addFavoriteItemForUser(@PathVariable long user_id,
                                                         @Valid @RequestBody MenuItem item) {
        AppUser appUser = userRepo.findById(user_id).orElseThrow();
        appUser.getFavoriteItemsList().add(item);
        userRepo.save(appUser);

        return ResponseEntity.ok("Added item to favorites for appUser (id=" + user_id + ")");
    }

    @DeleteMapping("/{user_id}/favorites/{item_id}/remove")
    public ResponseEntity<String> removeFavoriteItemForUser(@PathVariable long user_id,
                                                            @PathVariable long item_id) {
        MenuItem item = menuItemRepository.findById(item_id).orElseThrow();
        AppUser appUser = userRepo.findById(user_id).orElseThrow();

        appUser.getFavoriteItemsList().remove(item);
        userRepo.save(appUser);

        return ResponseEntity.ok("Removed item (id="+item_id+") from favorites for appUser (id="+user_id+")");
    }

    // ----------------- ЛЮБИМЫЕ БЛЮДА ----------------- //


    // ----------------- ВАЛИДАЦИЯ ----------------- //

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
