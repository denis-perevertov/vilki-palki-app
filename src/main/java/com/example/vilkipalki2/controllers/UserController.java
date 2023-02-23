package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.dto.UserDTO;
import com.example.vilkipalki2.models.Address;
import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.MenuItem;
import com.example.vilkipalki2.services.ItemService;
import com.example.vilkipalki2.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v3/users")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {

    private final ItemService itemService;
    private final UserService userService;

    // ----------------- ПОЛЬЗОВАТЕЛИ ----------------- //

    @GetMapping
    public List<AppUser> users() {
        return userService.getAllUsers();
    }

    @GetMapping("/{user_id}")
    public AppUser getUser(@PathVariable long user_id) {return userService.getUser(user_id);}

    @PostMapping("/add-user")
    public ResponseEntity<String> addUser(@Valid @RequestBody UserDTO dto) {
        AppUser newAppUser = userService.saveUser(userService.fromDTOToUser(dto));
        return ResponseEntity.ok().body("Added new appUser " + newAppUser.getName() + " , id = " + newAppUser.getId());
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<String> editUserById(@Valid @RequestBody UserDTO newUserData, @PathVariable long id) {
        userService.editUserData(id, newUserData);
        return ResponseEntity.ok("Edited user data(id="+id+")");
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body("Successfully deleted user (id="+id+")");
    }

    // ----------------- ПОЛЬЗОВАТЕЛИ ----------------- //


    // ************************************************ //


    // ----------------- АДРЕСА ----------------- //

    //получение всех адресов одного пользователя
    @GetMapping("/{user_id}/address")
    public List<Address> getAddressesForUser(@PathVariable long user_id) {
        AppUser appUser = userService.getUser(user_id);
        return appUser.getAddressList();
    }

    //получение одного адреса одного пользователя
    @GetMapping("/{user_id}/address/{address_id}")
    public Address getSingleAddressForUser(@PathVariable long user_id, @PathVariable int address_id) {
        AppUser appUser = userService.getUser(user_id);
        return appUser.getAddressList().get(address_id);
    }

    //добавление адреса
    @PostMapping("/{user_id}/address/add-new")
    public ResponseEntity<String> addNewAddress(@PathVariable long user_id, @Valid @RequestBody Address address) {

        AppUser appUser = userService.getUser(user_id);
        appUser.getAddressList().add(address);

        userService.saveUser(appUser);

        return ResponseEntity.ok("Added new address to user");
    }

    //изменение адреса
    @PutMapping("/{user_id}/address/{address_id}/edit")
    public ResponseEntity<String> editAddressForUser(@PathVariable long user_id,
                                                     @PathVariable int address_id,
                                                     @RequestBody Address newAddress) {

        AppUser appUser = userService.getUser(user_id);
        Address addressToEdit = appUser.getAddressList().get(address_id);

        addressToEdit.setComment(newAddress.getComment());
        addressToEdit.setCode(newAddress.getCode());
        addressToEdit.setApartment(newAddress.getApartment());
        addressToEdit.setFloor(newAddress.getFloor());
        addressToEdit.setEntrance(newAddress.getEntrance());
        addressToEdit.setStreet(newAddress.getStreet());
        addressToEdit.setHouse(newAddress.getHouse());

        userService.saveUser(appUser);

        return ResponseEntity.ok("Edited address " +
                "for AppUser " + appUser.getName() + " (id=" + appUser.getId() + ")");
    }

    //удаление адреса
    @DeleteMapping("/{user_id}/address/{address_id}/delete")
    public ResponseEntity<String> deleteAddressForUser(@PathVariable long user_id,
                                                       @PathVariable int address_id) {
        AppUser appUser = userService.getUser(user_id);
        Address addressToDelete = appUser.getAddressList().get(address_id);

        appUser.getAddressList().remove(addressToDelete);

        userService.saveUser(appUser);

        return ResponseEntity.ok("Deleted address for AppUser "+appUser.getName()+" (id="+appUser.getId()+")");
    }

    // ----------------- АДРЕСА ----------------- //


    // ************************************************ //


    // ----------------- ЛЮБИМЫЕ БЛЮДА ----------------- //


    @GetMapping("/{user_id}/favorites")
    public List<MenuItem> getFavoriteItemsForUser(@PathVariable long user_id) {
        AppUser appUser = userService.getUser(user_id);
        return appUser.getFavoriteItemsList();
    }

    @PostMapping("/{user_id}/favorites/add")
    public ResponseEntity<String> addFavoriteItemForUser(@PathVariable long user_id,
                                                         @Valid @RequestBody MenuItem item) {
        AppUser appUser = userService.getUser(user_id);
        appUser.getFavoriteItemsList().add(item);
        userService.saveUser(appUser);

        return ResponseEntity.ok("Added item to favorites for appUser (id=" + user_id + ")");
    }

    @DeleteMapping("/{user_id}/favorites/{item_id}/remove")
    public ResponseEntity<String> removeFavoriteItemForUser(@PathVariable long user_id,
                                                            @PathVariable long item_id) {
        MenuItem item = itemService.getSingleItem(item_id);
        AppUser appUser = userService.getUser(user_id);

        appUser.getFavoriteItemsList().remove(item);
        userService.saveUser(appUser);

        return ResponseEntity.ok("Removed item (id="+item_id+") from favorites for appUser (id="+user_id+")");
    }

    // ----------------- ЛЮБИМЫЕ БЛЮДА ----------------- //


    // ************************************************ //


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
