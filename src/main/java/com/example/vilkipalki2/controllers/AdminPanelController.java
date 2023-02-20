package com.example.vilkipalki2.controllers;

import com.example.vilkipalki2.dto.UserDTO;
import com.example.vilkipalki2.models.*;
import com.example.vilkipalki2.services.IngredientService;
import com.example.vilkipalki2.services.ItemService;
import com.example.vilkipalki2.services.OrderService;
import com.example.vilkipalki2.services.UserService;
import com.example.vilkipalki2.services.email.BannerService;
import com.example.vilkipalki2.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Log
@SuppressWarnings("unused")
public class AdminPanelController {

    private final UserService userService;
    private final OrderService orderService;
    private final ItemService itemService;
    private final IngredientService ingredientService;
    private final BannerService bannerService;

    private static final String imageUploadDirectory = "src/main/resources/static/images/";

    // ----------------- СТАТИСТИКА ----------------- //

    @GetMapping
    public String showStatsPage(Model model) {
        return "admin_panel/stats";
    }


    // ----------------- СТАТИСТИКА ----------------- //


    // ************************************************ //


    // ----------------- БАННЕРА ----------------- //

    @GetMapping("/banners")
    public String showBannersPage(Model model) {
        Iterable<Banner> bannerList = bannerService.getAllBanners();
        model.addAttribute("banners", bannerList);
        return "admin_panel/banners";
    }

    @PostMapping("/banners")
    public String addBanners(@RequestParam(required = false) MultipartFile[] banners,
                             RedirectAttributes redirAttrs,
                             Model model) throws IOException {

        StringBuilder info = new StringBuilder();
        for(MultipartFile picture : banners) {
            if(!picture.isEmpty() && picture.getOriginalFilename() != null) {
                try {
                    Banner banner = new Banner();
                    banner.setFileName(picture.getOriginalFilename());
                    FileUploadUtil.saveFile(imageUploadDirectory + "/banners",picture.getOriginalFilename(),picture);
                    Banner savedBanner = bannerService.saveBanner(banner);
                    info.append("Сохранён баннер (id=")
                            .append(savedBanner.getId())
                            .append(", name=")
                            .append(savedBanner.getFileName())
                            .append(")\n");
                } catch (Exception e) {
                    redirAttrs.addFlashAttribute("fail_message",
                            info +"\nНе удалось сохранить баннер "
                                    + ", причина: " + Arrays.toString(e.getStackTrace()));
                }
            }
        }

        redirAttrs.addFlashAttribute("success_message", info);

        return "redirect:/admin/banners";
    }

    // ----------------- БАННЕРА ----------------- //


    // ************************************************ //



    // ----------------- ПОЛЬЗОВАТЕЛИ ----------------- //

    @GetMapping("/users")
    public String showUsersPage(Model model) {
        model.addAttribute("users", userService.getUsersDTO());
        return "admin_panel/users";
    }

    @GetMapping("/users/add-user")
    public String showAddUserPage(@ModelAttribute UserDTO user) {
        return "admin_panel/user_edit";
    }

    @PostMapping("/users/add-user")
    public String addUser(@ModelAttribute(name="user") @Valid UserDTO userDTO,
                          BindingResult result,
                          RedirectAttributes redirAttrs) {
        try {
            AppUser appUser = userService.fromDTOToUser(userDTO);
            userService.saveUser(appUser);
            redirAttrs.addFlashAttribute("success", "Добавили пользователя");
        } catch (Exception e) {
            e.printStackTrace();
            redirAttrs.addFlashAttribute("fail", "Ошибка добавления пользователя");
        }

        return "redirect:/admin/users/add-user";
    }

    // ----------------- ПОЛЬЗОВАТЕЛИ ----------------- //


    // ************************************************ //


    // ----------------- ЗАКАЗЫ ----------------- //

    @GetMapping("/orders")
    public String showOrdersPage(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin_panel/orders";
    }

    @GetMapping("/orders/history")
    public String showOrderHistoryPage(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("now", LocalDate.now());
        return "admin_panel/order_history";
    }

    // ----------------- ЗАКАЗЫ ----------------- //


    // ************************************************ //


    // ----------------- ТОВАРЫ ----------------- //

    @GetMapping("/items")
    public String showItemsPage(Model model) {
        Iterable<Category> categoryList = itemService.getAllCategories();
        model.addAttribute("categories", categoryList);
        return "admin_panel/items";
    }

    @GetMapping("/items/add-category")
    public String showAddCategoryPage(Model model) {
        return "admin_panel/category_edit_page";
    }

    @PostMapping("/items/add-category")
    public String addCategory(@RequestParam String category_name,
                              @RequestParam MultipartFile picture,
                              RedirectAttributes redirAttrs) {

        Category category = new Category();
        category.setName(category_name);

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(picture.getOriginalFilename()));

        category.setIconFileName(fileName);

        try {
            Category savedCategory = itemService.saveCategory(category);
            String uploadDir = imageUploadDirectory + "/categories/" + savedCategory.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, picture);
            redirAttrs.addFlashAttribute("success_message",
                    "Успешно добавили новую категорию: " + category_name + "(id=" + savedCategory.getId() + ")");
        }
        catch(Exception e) {
            redirAttrs.addFlashAttribute("fail_message",
                    "Что-то пошло не так. " + Arrays.toString(e.getStackTrace()));
        }

        return "redirect:/admin/items/add-category";
    }

    @GetMapping("/items/categories/{id}")
    public String editCategory (@PathVariable long id, Model model) {
        model.addAttribute("cat",itemService.getSingleCategoryById(id));
        return "admin_panel/category_edit_page";
    }

    @PostMapping("/items/categories/{id}")
    public String editCategorySave(@PathVariable long id,
                                   @RequestParam String category_name,
                                   @RequestParam MultipartFile picture) throws IOException {
        Category category = itemService.getSingleCategoryById(id);
        category.setName(category_name);

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(picture.getOriginalFilename()));

        category.setIconFileName(fileName);

        Category savedCategory = itemService.saveCategory(category);
        String uploadDir = imageUploadDirectory + "/categories/" + savedCategory.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, picture);

        return "redirect:/admin/items";
    }

    @GetMapping("/items/categories/{id}/delete")
    public String deleteCategory(@PathVariable long id) {
        itemService.deleteCategory(id);
        return "redirect:/admin/items";
    }

    @GetMapping("/items/add-item")
    public String showAddItemPage(@ModelAttribute MenuItem item, Model model) {
        Iterable<Category> categoryList = itemService.getAllCategories();
        model.addAttribute("categories", categoryList);

        Iterable<Ingredient> ingredientsList = ingredientService.getAllIngredients();
        model.addAttribute("ingredients", ingredientsList);
        return "admin_panel/add_item";
    }

    @PostMapping("/items/add-item")
    public String addItem(@ModelAttribute MenuItem item,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) MultipartFile picture,
                          RedirectAttributes redirAttrs,
                          Model model) {

        try {
            item.setCategory(itemService.getSingleCategoryByName(category));

            if(!picture.isEmpty() && picture.getOriginalFilename() != null) {
                item.setPictureFileName(picture.getOriginalFilename());
                FileUploadUtil.saveFile(imageUploadDirectory, picture.getOriginalFilename(), picture);
            }

            MenuItem savedItem = itemService.saveItem(item);

            redirAttrs.addFlashAttribute("success_message",
                    "Успешно добавили новый предмет: " + savedItem.getName() + "(id=" + savedItem.getId() + ")");
        } catch(Exception e) {
            redirAttrs.addFlashAttribute("fail_message",
                    "Что-то пошло не так. " + Arrays.toString(e.getStackTrace()));
        }

        return "redirect:/admin/items/add-item";
    }


    @GetMapping("/items/{item_id}")
    public String showItemPage(@PathVariable Long item_id, Model model) {

        MenuItem item = itemService.getSingleItem(item_id);

        model.addAttribute("item", item);

        Iterable<Category> categoryList = itemService.getAllCategories();
        model.addAttribute("categories", categoryList);

        Iterable<Ingredient> ingredientsList = ingredientService.getAllIngredients();
        model.addAttribute("ingredients", ingredientsList);
        model.addAttribute("item_ingredients", item.getIngredients());

        return "admin_panel/item_edit";
    }

    //todo ругается на категории
    @PostMapping("/items/{item_id}")
    public String editItem(@ModelAttribute MenuItem item,
                          @PathVariable long item_id,
                          @RequestParam(required = false) String category,
                          @RequestParam MultipartFile picture,
                          @RequestParam(required = false) String[] ingredients,
                          RedirectAttributes redirAttrs,
                          Model model) throws IOException {

        String pictureFromDBName = itemService.getPictureOfItemByID(item_id);
        item.setPictureFileName(pictureFromDBName);

        item.setId(item_id);
        item.setCategory(itemService.getSingleCategoryByName(category));

        if(!picture.isEmpty() && picture.getOriginalFilename() != null) {
            item.setPictureFileName(picture.getOriginalFilename());
            FileUploadUtil.saveFile(imageUploadDirectory, picture.getOriginalFilename(), picture);
        }

        List<Ingredient> ingrList = item.getIngredients();
        if(ingrList != null && ingrList.size() > 0) ingrList.clear();
        else ingrList = new ArrayList<>();

        if(ingredients != null) {
            Map<Long, Ingredient> map = new HashMap<>();
            for(String ingredientId : ingredients) {
                System.out.println(ingredientId);
                long id = Long.parseLong(ingredientId);
                log.info("id of ingr: " + id);
                Ingredient ingredientToSave = ingredientService.getSingleIngredientById(id);
                log.info("ingr: " + ingredientToSave);

                map.putIfAbsent(id, ingredientToSave);

            }
            System.out.println(map.values());
            ingrList.addAll(map.values());
        }

        item.setIngredients(ingrList);

        MenuItem savedItem = itemService.saveItem(item);

        redirAttrs.addFlashAttribute("success_message",
                "Успешно добавили новый предмет: " + savedItem.getName() + "(id=" + savedItem.getId() + ")");

        return "redirect:/admin/items";
    }

    @GetMapping("/items/{item_id}/delete")
    public String deleteItem(@PathVariable long item_id) {
        itemService.deleteItem(item_id);
        return "redirect:/admin/items";
    }

    @GetMapping("/items/ingredients")
    public String showIngredientsPage(Model model) {
        Iterable<Ingredient> ingredientsList = ingredientService.getAllIngredients();
        model.addAttribute("ingredients", ingredientsList);
        return "admin_panel/ingredients";
    }

    @GetMapping("/items/ingredients/add")
    public String addIngredientPage(@ModelAttribute Ingredient ingredient, Model model) {
        return "admin_panel/ingredient_edit_page";
    }

    @PostMapping("/items/ingredients/add")
    public String addIngredientToDB(@RequestParam String name,
                                    @RequestParam MultipartFile picture) throws IOException {

        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);

        if(!picture.isEmpty() && picture.getOriginalFilename() != null) {
            ingredient.setIcon(picture.getOriginalFilename());
            FileUploadUtil.saveFile(imageUploadDirectory + "ingredients/", picture.getOriginalFilename(), picture);
        }

        Ingredient savedIngr = ingredientService.saveIngredientToDB(ingredient);

        log.info("Saved new ingredient to DB(name="+savedIngr.getName()+", id="+savedIngr.getId()+")");

        return "redirect:/admin/items/ingredients";
    }

    @GetMapping("/items/ingredients/{id}")
    public String editIngredientPage(@PathVariable long id, Model model) {
        model.addAttribute("ingr", ingredientService.getSingleIngredientById(id));
        return "admin_panel/ingredient_edit_page";
    }

    @PostMapping("/items/ingredients/{id}")
    public String editIngredientSaveToDB(@PathVariable long id,
                                         @RequestParam String name,
                                         @RequestParam MultipartFile picture) throws IOException {
        Ingredient ingredient = ingredientService.getSingleIngredientById(id);
        ingredient.setName(name);

        if(!picture.isEmpty() && picture.getOriginalFilename() != null) {
            ingredient.setIcon(picture.getOriginalFilename());
            FileUploadUtil.saveFile(imageUploadDirectory + "ingredients/", picture.getOriginalFilename(), picture);
        }

        Ingredient savedIngr = ingredientService.saveIngredientToDB(ingredient);

        log.info("Saved edits to ingredient to DB(name="+savedIngr.getName()+", id="+savedIngr.getId()+")");

        return "admin_panel/ingredients";
    }

    @GetMapping("/items/ingredients/{id}/delete")
    public String deleteIngredient(@PathVariable long id) {
        ingredientService.deleteIngredient(id);
        return "redirect:/admin/items/ingredients";
    }

    // ----------------- ТОВАРЫ ----------------- //


    // ************************************************ //


    // ----------------- РАССЫЛКА ----------------- //

    @GetMapping("/sms")
    public String showSMSPage(Model model) {
        model.addAttribute("users", userService.getUsersDTO());
        return "admin_panel/sms";
    }


    @GetMapping("/telegram")
    public String showTelegramPage(Model model) {
        model.addAttribute("users", userService.getUsersDTO());
        return "admin_panel/telegram";
    }

    @PostMapping("/sms")
    public @ResponseBody String sendSms(@RequestParam String field,
                                        @RequestParam(required = false) String users) {
        log.info(field);
        log.info(users);
        List<UserDTO> userList = new ArrayList<>();

        String[] ids = users.split("\\D+");
        Arrays.stream(ids).filter((id) -> !id.isBlank()).forEach((id) -> {
            long user_id = Long.parseLong(id);
            UserDTO user = userService.getUserDTOById(user_id);
            userList.add(user);
        });
        log.info(Arrays.toString(ids));

        userList.forEach(System.out::println);
        return "SENT SMS";
    }
}
