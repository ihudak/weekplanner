package eu.dec21.wp.categories.mapper;

import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.entity.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryMapperTest {

    @Test
    void mapToCategoryDto() {
        var category = new Category();

        category.setId(1L);
        category.setName("Boooo");
        category.setColor("Foooo");
        category.setPriority(100);
        category.setUserId(1L);

        CategoryDto categoryDto = CategoryMapper.mapToCategoryDto(category);

        assertEquals(category.getId(),       categoryDto.getId());
        assertEquals(category.getName(),     categoryDto.getName());
        assertEquals(category.getColor(),    categoryDto.getColor());
        assertEquals(category.getPriority(), categoryDto.getPriority());
        assertEquals(category.getUserId(),   categoryDto.getUserId());
    }

    @Test
    void mapToCategory() {
        var categoryDto = new CategoryDto();

        categoryDto.setId(1L);
        categoryDto.setName("Boooo");
        categoryDto.setColor("Foooo");
        categoryDto.setPriority(100);
        categoryDto.setUserId(1L);

        Category category = CategoryMapper.mapToCategory(categoryDto);

        assertEquals(categoryDto.getId(),       category.getId());
        assertEquals(categoryDto.getName(),     category.getName());
        assertEquals(categoryDto.getColor(),    category.getColor());
        assertEquals(categoryDto.getPriority(), category.getPriority());
        assertEquals(categoryDto.getUserId(),   category.getUserId());
    }
}