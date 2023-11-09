package eu.dec21.wp.categories.controller;

import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.dto.CategoryResponse;
import eu.dec21.wp.categories.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @PostMapping("")
    @Operation(summary = "Create a new Category")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get Category by ID")
    public ResponseEntity<CategoryDto> getCategoryByID(@PathVariable("id") Long categoryId) {
        CategoryDto categoryDto = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(categoryDto);
    }

    @GetMapping("")
    @Operation(summary = "Get all Categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return new ResponseEntity<>(categoryService.getAllCategories(pageNo, pageSize), HttpStatus.OK);
    }

    @PutMapping("{id}")
    @Operation(summary = "Update Category by ID")
    public ResponseEntity<CategoryDto> updateCategoryById(@PathVariable("id") Long categoryId,
                                                          @RequestBody CategoryDto updatedCategory) {
        CategoryDto categoryDto = categoryService.updateCategory(categoryId, updatedCategory);
        return ResponseEntity.ok(categoryDto);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete Category by ID")
    public ResponseEntity<String> deleteCategoryById(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("Category deleted with ID: " + categoryId);
    }


    @GetMapping("/find")
    @Operation(summary = "Get Category by name")
    public ResponseEntity<CategoryDto> getCategoryByName(@Parameter(name="name", description = "Category or Project name", example = "My Project") @RequestParam String name) {
        CategoryDto categoryDto = categoryService.findCategoryByName(name, 1L);
        return ResponseEntity.ok(categoryDto);
    }
}
