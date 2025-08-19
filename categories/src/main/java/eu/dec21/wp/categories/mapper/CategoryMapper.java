package eu.dec21.wp.categories.mapper;

import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.entity.Category;
import eu.dec21.wp.categories.entity.CategoryBuilder;

public class CategoryMapper {
    public static CategoryDto mapToCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getPriority(),
                category.getColor(),
                category.getUserId(),
                category.isDeleted()
        );
    }

    public static Category mapToCategory(CategoryDto categoryDto) {
        return new CategoryBuilder()
                .setId(categoryDto.getId())
                .setName(categoryDto.getName())
                .setPriority(categoryDto.getPriority())
                .setColor(categoryDto.getColor())
                .setUserId(categoryDto.getUserId())
                .setDeleted(categoryDto.isDeleted())
                .build();
    }
}
