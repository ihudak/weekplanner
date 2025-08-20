package eu.dec21.wp.categories.mapper;

import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.entity.Category;
import eu.dec21.wp.categories.entity.CategoryBuilder;

import java.util.Optional;

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
        CategoryBuilder categoryBuilder =  new CategoryBuilder()
                .setName(categoryDto.getName())
                .setPriority(categoryDto.getPriority())
                .setColor(categoryDto.getColor())
                .setUserId(categoryDto.getUserId())
                .setDeleted(categoryDto.isDeleted());

        Optional.ofNullable(categoryDto.getId()).ifPresent(categoryBuilder::setId);

        return categoryBuilder.build();
    }
}
