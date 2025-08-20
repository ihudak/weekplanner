package eu.dec21.wp.categories.service.impl;

import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.dto.CategoryResponse;
import eu.dec21.wp.categories.entity.Category;
import eu.dec21.wp.categories.mapper.CategoryMapper;
import eu.dec21.wp.categories.repository.CategoryRepository;
import eu.dec21.wp.categories.service.CategoryService;
import eu.dec21.wp.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.mapToCategory(categoryDto);
        try {
            Category savedCategory = categoryRepository.save(category);
            return CategoryMapper.mapToCategoryDto(savedCategory);
        } catch (ObjectOptimisticLockingFailureException e) {
            // Reload entity and retry or inform user
            throw new ConcurrentModificationException("Category was updated by another user " + e.getMessage());
        }
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
    public CategoryResponse getAllCategories(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Category> categories = categoryRepository.findAll(pageable);
        return getCategoryResponse(categories);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto updatedCategoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with the given ID: " + categoryId));

        category.setName(updatedCategoryDto.getName());
        category.setPriority(updatedCategoryDto.getPriority());
        category.setColor(updatedCategoryDto.getColor());
        category.setDeleted(updatedCategoryDto.isDeleted());

        try {
            Category updatedCategory = categoryRepository.save(category);
            return CategoryMapper.mapToCategoryDto(updatedCategory);
        } catch (ObjectOptimisticLockingFailureException e) {
            // Reload entity and retry or inform user
            throw new ConcurrentModificationException("Category was updated by another user");
        }
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(String name, Long userId, CategoryDto updatedCategoryDto) {
        Category category = categoryRepository.findByNameAndUserId(name, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with the given name: " + name));

        category.setName(updatedCategoryDto.getName());
        category.setPriority(updatedCategoryDto.getPriority());
        category.setColor(updatedCategoryDto.getColor());

        try {
            Category updatedCategory = categoryRepository.save(category);
            return CategoryMapper.mapToCategoryDto(updatedCategory);
        } catch (ObjectOptimisticLockingFailureException e) {
            // Reload entity and retry or inform user
            throw new ConcurrentModificationException("Category was updated by another user");
        }
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with the given ID: " + categoryId));

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public void deleteCategory(String name, Long userId) {
        Category category = categoryRepository.findByNameAndUserId(name, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with the given name: " + name));
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto findCategoryByName(String name, Long userId) {
        Category category = categoryRepository.findByNameAndUserId(name, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not exist with the given name: " + name));

        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategoriesForUser(Long userId) {
        List<Category> categories = categoryRepository.findAllByUserId(userId);
        return categories.stream().map(CategoryMapper::mapToCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getAllCategoriesForUser(Long userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Category> categories = categoryRepository.findAllByUserId(pageable, userId);
        return getCategoryResponse(categories);
    }

    private CategoryResponse getCategoryResponse(Page<Category> categories) {
        List<Category> categoryList = categories.getContent();
        List<CategoryDto> categoryDtoList = categoryList.stream().map(CategoryMapper::mapToCategoryDto).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDtoList);
        categoryResponse.setPageNo(categories.getNumber());
        categoryResponse.setPageSize(categories.getSize());
        categoryResponse.setTotalElements(categories.getTotalElements());
        categoryResponse.setTotalPages(categories.getTotalPages());
        categoryResponse.setLast(categoryResponse.isLast());

        return categoryResponse;
    }

    @Override
    public long count() {
        return categoryRepository.count();
    }

    @Override
    public boolean existCategory(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}
