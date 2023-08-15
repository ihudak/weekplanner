package eu.dec21.wp.categories.service.impl;

import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.entity.Category;
import eu.dec21.wp.categories.mapper.CategoryMapper;
import eu.dec21.wp.categories.repository.CategoryRepository;
import eu.dec21.wp.categories.service.CategoryService;
import eu.dec21.wp.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.mapToCategory(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.mapToCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with the given ID: " + categoryId));

        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(CategoryMapper::mapToCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto updatedCategoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with the given ID: " + categoryId));

        category.setName(updatedCategoryDto.getName());
        category.setPriority(updatedCategoryDto.getPriority());
        category.setColor(updatedCategoryDto.getColor());

        Category updatedCategory = categoryRepository.save(category);

        return CategoryMapper.mapToCategoryDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with the given ID: " + categoryId));

        categoryRepository.deleteById(categoryId);
    }
}
