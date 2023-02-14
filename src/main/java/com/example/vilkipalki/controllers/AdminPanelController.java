package com.example.vilkipalki.controllers;

import com.example.vilkipalki.exception.CategoryNotFoundException;
import com.example.vilkipalki.models.*;
import com.example.vilkipalki.repos.*;
import com.example.vilkipalki.services.EmailService;
import com.example.vilkipalki.util.FileUploadUtil;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

@Controller
@RequestMapping("/admin")
@SuppressWarnings("unused")
public class AdminPanelController {

    private final MenuItemRepository itemRepo;
    private final CategoryRepository categoryRepository;
    private final BannerRepository bannerRepository;
    private final AppUserRepository appUserRepository;
    private final OrderRepository orderRepository;
    private final IngredientRepository ingredientsRepository;

    private final EmailService emailService;

    private static final String imageUploadDirectory = "src/main/resources/static/images/";

    public AdminPanelController(MenuItemRepository itemRepo,
                                CategoryRepository categoryRepository,
                                BannerRepository bannerRepository,
                                AppUserRepository appUserRepository,
                                OrderRepository orderRepository,
                                IngredientRepository ingredientsRepository,
                                EmailService emailService) {
        this.itemRepo = itemRepo;
        this.categoryRepository = categoryRepository;
        this.bannerRepository = bannerRepository;
        this.appUserRepository = appUserRepository;
        this.orderRepository = orderRepository;
        this.ingredientsRepository = ingredientsRepository;
        this.emailService = emailService;
    }

    @GetMapping
    public String showStatsPage(Model model) {
        return "admin_panel/stats";
    }


    // ----------------- ПОЛЬЗОВАТЕЛИ ----------------- //

    @GetMapping("/users")
    public String showUsersPage(Model model) {
        model.addAttribute("users", appUserRepository.findAll());
        return "admin_panel/users";
    }

    @GetMapping("/users/add-user")
    public String showAddUserPage(Model model) {
        return "admin_panel/user_edit";
    }

    @PostMapping("/users/add-user")
    public String addUser(@ModelAttribute AppUser appUser) {
        appUserRepository.save(appUser);
        return "redirect:/admin/users/add-user";
    }

    // ----------------- ПОЛЬЗОВАТЕЛИ ----------------- //


    // ************************************************ //


    // ----------------- ЗАКАЗЫ ----------------- //

    @GetMapping("/orders")
    public String showOrdersPage(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin_panel/orders";
    }

    @GetMapping("/orders/history")
    public String showOrderHistoryPage(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("now", LocalDate.now());
        return "admin_panel/order_history";
    }

    // ----------------- ЗАКАЗЫ ----------------- //


    // ************************************************ //


    // ----------------- ТОВАРЫ ----------------- //

    @GetMapping("/items")
    public String showItemsPage(Model model) {
        Iterable<Category> categoryList = categoryRepository.findAll();
        model.addAttribute("categories", categoryList);
        return "admin_panel/items";
    }

    @GetMapping("/items/add-category")
    public String showAddCategoryPage(Model model) {
        return "admin_panel/add_category";
    }

    @PostMapping("/items/add-category")
    public String addCategory(@RequestParam String category_name,
                              @RequestParam MultipartFile picture,
                              RedirectAttributes redirAttrs) throws IOException {



        Category category = new Category();
        category.setName(category_name);

        String fileName = StringUtils.cleanPath(picture.getOriginalFilename());

        category.setIconFileName(fileName);

        Category savedCategory = categoryRepository.save(category);

        String uploadDir = imageUploadDirectory + "/categories/" + savedCategory.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, picture);

        redirAttrs.addFlashAttribute("success_message",
                "Успешно добавили новую категорию: " + category_name + "(id=" + savedCategory.getId() + ")");

        return "redirect:/admin/items/add-category";
    }

    @GetMapping("/items/add-item")
    public String showAddItemPage(@ModelAttribute MenuItem item, Model model) {
        Iterable<Category> categoryList = categoryRepository.findAll();
        model.addAttribute("categories", categoryList);

        Iterable<Ingredient> ingredientsList = ingredientsRepository.findAll();
        model.addAttribute("ingredients", ingredientsList);
        return "admin_panel/add_item";
    }

    @PostMapping("/items/add-item")
    public String addItem(@ModelAttribute MenuItem item,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) MultipartFile picture,
                          RedirectAttributes redirAttrs,
                          Model model) throws IOException {

        System.out.println(category);
        System.out.println(categoryRepository.findByName("tt"));
        item.setCategory_id(categoryRepository.findByName(category).orElseThrow());

        if(!picture.isEmpty() && picture.getOriginalFilename() != null) {
            item.setPictureFileName(picture.getOriginalFilename());
            FileUploadUtil.saveFile(imageUploadDirectory, picture.getOriginalFilename(), picture);
        }

        MenuItem savedItem = itemRepo.save(item);

        redirAttrs.addFlashAttribute("success_message",
                "Успешно добавили новый предмет: " + savedItem.getName() + "(id=" + savedItem.getId() + ")");

        return "redirect:/admin/items/add-item";
    }


    @GetMapping("/items/{item_id}")
    public String showItemPage(@PathVariable Long item_id, Model model) {
        model.addAttribute("item", itemRepo.findById(item_id).orElseThrow());

        Iterable<Category> categoryList = categoryRepository.findAll();
        model.addAttribute("categories", categoryList);

        Iterable<Ingredient> ingredientsList = ingredientsRepository.findAll();
        model.addAttribute("ingredients", ingredientsList);

        return "admin_panel/item_edit";
    }

    @PostMapping("/items/{item_id}")
    public String editItem(@ModelAttribute MenuItem item,
                          @PathVariable long item_id,
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) MultipartFile picture,
                          RedirectAttributes redirAttrs,
                          Model model) throws IOException {

        item.setId(item_id);
        item.setCategory_id(categoryRepository.findByName(category).orElseThrow(CategoryNotFoundException::new));

        if(!picture.isEmpty() && picture.getOriginalFilename() != null) {
            item.setPictureFileName(picture.getOriginalFilename());
            FileUploadUtil.saveFile(imageUploadDirectory, picture.getOriginalFilename(), picture);
        }

        MenuItem savedItem = itemRepo.save(item);

        redirAttrs.addFlashAttribute("success_message",
                "Успешно добавили новый предмет: " + savedItem.getName() + "(id=" + savedItem.getId() + ")");

        return "redirect:/admin/items";
    }
    // ----------------- ТОВАРЫ ----------------- //


    // ************************************************ //


    // ----------------- БАННЕРА ----------------- //

    @GetMapping("/banners")
    public String showBannersPage(Model model) {
        Iterable<Banner> bannerList = bannerRepository.findAll();
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
                    Banner savedBanner = bannerRepository.save(banner);
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


    // ----------------- РАССЫЛКА ----------------- //

    @GetMapping("/sms")
    public String showSMSPage(Model model) {
        model.addAttribute("users", appUserRepository.findAll());
        return "admin_panel/sms";
    }

    @GetMapping("/email")
    public String showEmailPage(Model model) {
        model.addAttribute("users", appUserRepository.findAll());
        return "admin_panel/email";
    }

    @GetMapping("/telegram")
    public String showTelegramPage(Model model) {
        model.addAttribute("users", appUserRepository.findAll());
        return "admin_panel/telegram";
    }

    @PostMapping("/email")
    public @ResponseBody String sendEmails() throws MessagingException {
        emailService.sendMail();
        return "Sending mail...";
    }
}
