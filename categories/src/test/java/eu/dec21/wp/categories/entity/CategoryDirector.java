package eu.dec21.wp.categories.entity;

import com.github.javafaker.Faker;
import eu.dec21.wp.helper.Constraints;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryDirector {
    private CategoryBuilder categoryBuilder = new CategoryBuilder();
    private Faker faker = new Faker();

    private String getRandStr(int length) {
        return faker.regexify("[A-Z][a-z]{" + (length - 1) + "}");
    }

    private int getRantNameLength() {
        return faker.random().nextInt(5, 25);
    }

    private CategoryBuilder prepareCategory() {
        return categoryBuilder.reset()
                .setId(0)
                .setName(getRandStr(getRantNameLength()))
                .setPriority(faker.number().numberBetween(Constraints.minPrio, Constraints.maxPrio))
                .setColor(faker.color().hex())
                .setUserId(faker.random().nextLong());
    }

    public Category constructRandomCategory() {
        return prepareCategory()
                .build();
    }

    public Category constructRandomCategoryForUser(long userId) {
        return prepareCategory()
                .setUserId(userId)
                .build();
    }

    public List<Category> constructRandomCategories(int numCategories) {
        var categories = new ArrayList<Category>(numCategories);

        for(int i = 0; i < numCategories; i++) {
            categories.add(this.constructRandomCategory());
        }

        return categories;
    }

    public List<Category> constructRandomCategoriesForUser(long userId, int numCategories) {
        return this.constructRandomCategories(numCategories).stream().peek(category -> category.setUserId(userId)).collect(Collectors.toList());
    }
}
