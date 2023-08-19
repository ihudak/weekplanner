package eu.dec21.wp.categories.dto;

import eu.dec21.wp.categories.entity.Category;
import eu.dec21.wp.categories.entity.CategoryDirector;
import eu.dec21.wp.categories.mapper.CategoryMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDtoTest {

    @Test
    void setId() {
        var category = new CategoryDto();

        category.setId(1L);
        assertEquals(1L, category.getId());
        category.setId(-1L);
        assertEquals(-1L, category.getId());
    }

    @Test
    void setName() {
        var category = new CategoryDto();

        category.setName("BooBooBooBooBooBooBooBooBo");
        assertEquals("BooBooBooBooBooBooBooBooBo", category.getName());
    }

    @Test
    void setPriority() {
        var category = new CategoryDto();

        category.setPriority(200);
        assertEquals(200, category.getPriority());

        category.setPriority(-1);
        assertEquals(-1, category.getPriority());
    }

    @Test
    void setColor() {
        var category = new CategoryDto();

        category.setColor("BooBooBooBooBooBooBooBooBo");
        assertEquals("BooBooBooBooBooBooBooBooBo", category.getColor());
    }

    @Test
    void setUserId() {
        var category = new CategoryDto();

        category.setUserId(200L);
        assertEquals(200L, category.getUserId());

        category.setUserId(-1L);
        assertEquals(-1L, category.getUserId());
    }

    @Test
    void equalsComparesCategories() {
        CategoryDirector categoryDirector = new CategoryDirector();
        Category category = categoryDirector.constructRandomCategory();

        CategoryDto category1, category2;
        category1 = CategoryMapper.mapToCategoryDto(category);
        category2 = CategoryMapper.mapToCategoryDto(category);
        category1.setId(10L);
        category2.setId(20L);

        // different IDs
        assertTrue(category1.equals(category2));
        assertTrue(category2.equals(category1));

        // different names
        category2.setName("New Name");
        assertFalse(category1.equals(category2));
        assertFalse(category2.equals(category1));

        // different users
        category1 = CategoryMapper.mapToCategoryDto(category);
        category2 = CategoryMapper.mapToCategoryDto(category);
        assertTrue(category1.equals(category2));
        assertTrue(category2.equals(category1));
        category1.setUserId(10L);
        category2.setUserId(20L);
        assertFalse(category1.equals(category2));
        assertFalse(category2.equals(category1));

        // different priorities
        category1 = CategoryMapper.mapToCategoryDto(category);
        category2 = CategoryMapper.mapToCategoryDto(category);
        assertTrue(category1.equals(category2));
        assertTrue(category2.equals(category1));
        category1.setPriority(2);
        category2.setPriority(5);
        assertFalse(category1.equals(category2));
        assertFalse(category2.equals(category1));

        // different colors
        category1 = CategoryMapper.mapToCategoryDto(category);
        category2 = CategoryMapper.mapToCategoryDto(category);
        assertTrue(category1.equals(category2));
        assertTrue(category2.equals(category1));
        category1.setColor("red");
        category2.setColor("blue");
        assertFalse(category1.equals(category2));
        assertFalse(category2.equals(category1));

        category2.setColor("red");
        assertTrue(category1.equals(category2));
        assertTrue(category2.equals(category1));
        category2.setColor(null);
        assertFalse(category1.equals(category2));
        assertFalse(category2.equals(category1));
    }
}