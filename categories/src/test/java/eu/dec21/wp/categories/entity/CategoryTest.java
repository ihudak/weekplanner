package eu.dec21.wp.categories.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void setId() {
        var category = new Category();

        category.setId(1L);
        assertEquals(1L, category.getId());
        category.setId(-1L);
        assertEquals(-1L, category.getId());
    }

    @Test
    void setName() {
        var category = new Category();

        category.setName("BooBooBooBooBooBooBooBooBo");
        assertEquals("BooBooBooBooBooBooBooBooBo", category.getName());
    }

    @Test
    void setPriority() {
        var category = new Category();

        category.setPriority(200);
        assertEquals(100, category.getPriority());

        category.setPriority(-1);
        assertEquals(0, category.getPriority());
    }

    @Test
    void setColor() {
        var category = new Category();

        category.setColor("BooBooBooBooBooBooBooBooBo");
        assertEquals("BooBooBooBooBooBooBooBooBo", category.getColor());
    }

    @Test
    void setUserId() {
        var category = new Category();

        category.setUserId(200L);
        assertEquals(200L, category.getUserId());

        category.setUserId(-1L);
        assertEquals(-1L, category.getUserId());
    }
}