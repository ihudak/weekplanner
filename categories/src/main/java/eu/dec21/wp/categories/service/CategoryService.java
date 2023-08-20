package eu.dec21.wp.categories.service;

import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto getCategoryById(Long categoryId);
    List<CategoryDto> getAllCategories();
    CategoryResponse getAllCategories(int pageNo, int pageSize);
    CategoryDto updateCategory(Long categoryId, CategoryDto updatedCategory);
    void deleteCategory(Long categoryId);
    CategoryDto findCategoryByName(String name, Long userId);
    List<CategoryDto> getAllCategoriesForUser(Long userId);
    CategoryResponse getAllCategoriesForUser(Long userId, int pageNo, int pageSize);
    CategoryDto updateCategory(String name, Long userId, CategoryDto updatedCategory);
    void deleteCategory(String name, Long userId);
    long count();
}
