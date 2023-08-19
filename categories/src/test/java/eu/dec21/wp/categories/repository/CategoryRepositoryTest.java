package eu.dec21.wp.categories.repository;

import com.github.javafaker.Faker;
import eu.dec21.wp.categories.entity.Category;
import eu.dec21.wp.categories.entity.CategoryBuilder;
import eu.dec21.wp.categories.entity.CategoryDirector;
import eu.dec21.wp.helper.Constraints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
class CategoryRepositoryTest {
    private static final Faker faker = new Faker();

    private final CategoryBuilder categoryBuilder = new CategoryBuilder();

    private final CategoryDirector categoryDirector = new CategoryDirector();

    private String getRandStr(int length) {
        return faker.regexify("[A-Z][a-z]{" + (length - 1) + "}");
    }

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void save10Categories() {
        var categories = categoryDirector.constructRandomCategories(10);

        categoryRepository.saveAll(categories);

        assertEquals(10, categoryRepository.count());
    }

    @Test
    void findByNameAndUserId() {
        String catName = getRandStr(20);
        long userId = faker.random().nextLong();
        List<Category> categories = categoryDirector.constructRandomCategoriesForUser(userId, 2);
        categories.get(0).setName(catName);

        Category category1 = categoryRepository.save(categories.get(0));
        assertNotNull(category1);
        categoryRepository.save(categories.get(1));
        Optional<Category> category = categoryRepository.findByNameAndUserId(catName, userId);
        assertTrue(category.isPresent());
        assertEquals(catName, category.get().getName());
        assertEquals(category1.getId(), category.get().getId());

        category = categoryRepository.findByNameAndUserId(faker.beer().name(), userId);
        assertFalse(category.isPresent());
    }

    @Test
    void findAllByUserId() {
        long user1 = 1, user2 = 2, user3 = 3;
        int numCatUser1 = 5, numCatUser2 = 7, numCatUser3 = 3;
        List<Category> categoriesUser1 = categoryDirector.constructRandomCategoriesForUser(user1, numCatUser1);
        List<Category> categoriesUser2 = categoryDirector.constructRandomCategoriesForUser(user2, numCatUser2);
        List<Category> categoriesUser3 = categoryDirector.constructRandomCategoriesForUser(user3, numCatUser3);
        categoryRepository.saveAll(categoriesUser1);
        categoryRepository.saveAll(categoriesUser2);
        categoryRepository.saveAll(categoriesUser3);

        assertEquals(numCatUser1 + numCatUser2 + numCatUser3, categoryRepository.count());

        List<Category> foundCategoriesUser1 = categoryRepository.findAllByUserId(user1);
        assertEquals(numCatUser1, foundCategoriesUser1.size());

        for (Category category: foundCategoriesUser1) {
            assertEquals(user1, category.getUserId());
        }
    }

    @Test
    void nameAndUserIdConstraintShouldViolate() {
        String catName = getRandStr(20);
        long userId = faker.random().nextLong();
        List<Category> categories = categoryDirector.constructRandomCategoriesForUser(userId, 3);
        categories.get(0).setName(catName);
        categories.get(2).setName(catName);

        categoryRepository.save(categories.get(0));
        categoryRepository.save(categories.get(1));

        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            categoryRepository.save(categories.get(2));
        });
    }

    @Test
    void nameTooLong() {
        var category = categoryDirector.constructRandomCategory();

        String normName = getRandStr(25);
        String longName = getRandStr(26);

        category.setName(normName);
        var cat = categoryRepository.save(category);
        assertEquals(normName, cat.getName());

        category.setName(longName);
        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            categoryRepository.save(category);
        });
    }

    @Test
    void nameMustExist() {
        // build category w/o name
        var category = categoryBuilder.reset()
                .setId(faker.random().nextLong())
                .setPriority(faker.number().numberBetween(Constraints.minPrio, Constraints.maxPrio))
                .setColor(faker.color().hex())
                .setUserId(faker.random().nextLong())
                .build();

        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            categoryRepository.save(category);
        });
    }

    @Test
    void priorityMustBeBetween0and100() {
        var category = categoryDirector.constructRandomCategory();

        category.setPriority(Constraints.maxPrio + 1);
        category = categoryRepository.save(category);
        assertEquals(Constraints.maxPrio, category.getPriority());

        category.setPriority(Constraints.minPrio - 1);
        category = categoryRepository.save(category);
        assertEquals(Constraints.minPrio, category.getPriority());
    }

    @Test
    void priorityMustExist() {
        // build category w/o priority
        var category = categoryBuilder.reset()
                .setId(faker.random().nextLong())
                .setName(getRandStr(10))
                .setColor(faker.color().hex())
                .setUserId(faker.random().nextLong())
                .build();

        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            categoryRepository.save(category);
        });
    }

    @Test
    void userIDMustExist() {
        // build category w/o userId
        var category = categoryBuilder.reset()
                .setId(faker.random().nextLong())
                .setName(getRandStr(10))
                .setPriority(faker.number().numberBetween(Constraints.minPrio, Constraints.maxPrio))
                .setColor(faker.color().hex())
                .build();

        assertThrowsExactly(DataIntegrityViolationException.class, () -> {
            categoryRepository.save(category);
        });
    }
}