package com.example.vilkipalki2.repos;

import com.example.vilkipalki2.models.AppUser;
import com.example.vilkipalki2.models.Category;
import com.example.vilkipalki2.models.EmailTemplate;
import com.example.vilkipalki2.models.MenuItem;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
class RepositoriesTest {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @BeforeEach
    public void init() {
        System.out.println("START TEST");
    }

    @AfterEach
    public void clearDB() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        menuItemRepository.deleteAll();
        templateRepository.deleteAll();
    }

    @Test
    void loadTest() {
        assertThat(userRepository).isNotNull();
        assertThat(categoryRepository).isNotNull();
        assertThat(menuItemRepository).isNotNull();
        assertThat(templateRepository).isNotNull();

    }

    @Test
    void findUserByEmailTest() {
        String email = "test@gmail.com";
        AppUser user = new AppUser();
        user.setEmail(email);

        AppUser savedUser = userRepository.save(user);

        AppUser expected = userRepository.findByEmail(email);

        assertThat(expected).isEqualTo(savedUser);
    }

    @Test
    @Order(1)
    void findUserEmailByUserId() {
        String email = "test@gmail.com";
        AppUser user = new AppUser();
        user.setId(1L);
        user.setEmail(email);

        userRepository.save(user);

        String expected = userRepository.findEmailById(1L);

        System.out.println(expected);
        System.out.println(email);

        assertThat(expected).isEqualTo(email);
    }

    @Test
    void findCategoryByName() {
        Category category = new Category();
        category.setName("TESTNAME");
        category.setId(0);
        category.setIconFileName("icon.jpg");

        categoryRepository.save(category);

        Optional<Category> expected = categoryRepository.findByName("TESTNAME");

        assertThat(expected).isNotNull();
        assertThat(expected.get()).isEqualTo(category);
    }

    @Test
    void getItemPictureById() {
        MenuItem item = new MenuItem(0, "test", 100);
        item.setPictureFileName("icon.jpg");

        menuItemRepository.save(item);

        String expected = menuItemRepository.getPictureById(item.getId());

        assertThat(expected).isEqualTo(item.getPictureFileName());
    }

    @Test
    void findEmailTemplateByName() {
        EmailTemplate template = new EmailTemplate(0, "test");

        templateRepository.save(template);

        Optional<EmailTemplate> expected = templateRepository.findByName("test");

        assertThat(expected.get()).isEqualTo(template);
    }
}