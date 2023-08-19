package eu.dec21.wp.categories.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}