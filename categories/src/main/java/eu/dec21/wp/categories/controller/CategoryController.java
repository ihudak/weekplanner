package eu.dec21.wp.categories.controller;

import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.dto.CategoryResponse;
import eu.dec21.wp.categories.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="WeekPlanner-Categories", description = "Categories Management API")
@RestController
@RequestMapping("/api/v1/categories")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = { @Content(schema = @Schema(implementation = CategoryDto.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PostMapping("")
    @Operation(summary = "Create a new Category")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        if (categoryDto.getId() != null) {
            categoryDto.setId(null);  // avoid exception because of existing id
        }
        CategoryDto savedCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = CategoryDto.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("{id}")
    @Operation(summary = "Get Category by ID")
    public ResponseEntity<CategoryDto> getCategoryByID(@PathVariable("id") Long categoryId) {
        CategoryDto categoryDto = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(categoryDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = CategoryResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("")
    @Operation(summary = "Get all Categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        // this.prepopulateCategories();
        return new ResponseEntity<>(categoryService.getAllCategories(pageNo, pageSize), HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated", content = { @Content(schema = @Schema(implementation = CategoryDto.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PutMapping("{id}")
    @Operation(summary = "Update Category by ID")
    public ResponseEntity<CategoryDto> updateCategoryById(@PathVariable("id") Long categoryId,
                                                          @RequestBody CategoryDto updatedCategory) {
        CategoryDto categoryDto = categoryService.updateCategory(categoryId, updatedCategory);
        return ResponseEntity.ok(categoryDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @DeleteMapping("{id}")
    @Operation(summary = "Delete Category by ID")
    public ResponseEntity<String> deleteCategoryById(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("Category deleted with ID: " + categoryId);
    }


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = CategoryDto.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("find")
    @Operation(summary = "Get Category by name")
    public ResponseEntity<CategoryDto> getCategoryByName(@Parameter(name="name", description = "Category or Project name", example = "My Project") @RequestParam String name) {
        CategoryDto categoryDto = categoryService.findCategoryByName(name, 1L);
        return ResponseEntity.ok(categoryDto);
    }

    // TODO: Remove after Perform
    @PostMapping("prepopulate")
    @Operation(summary = "Get Category by name")
    public ResponseEntity<CategoryResponse> prepopulateCategories() {
        if (4 > categoryService.count()) {
            if (null == categoryService.getCategoryById(1L)) {
                categoryService.createCategory(new CategoryDto(1L, "Graal", 30, "bb", 1L, false));
            }
            if (null == categoryService.getCategoryById(2L)) {
                categoryService.createCategory(new CategoryDto(2L, "Grail", 15, "bb", 1L, false));
            }
            if (null == categoryService.getCategoryById(3L)) {
                categoryService.createCategory(new CategoryDto(3L, "Apps", 10, "bb", 1L, false));
            }
            if (null == categoryService.getCategoryById(4L)) {
                categoryService.createCategory(new CategoryDto(4L, "TechFit", 0, "bb", 1L, false));
            }
        }
        return new ResponseEntity<>(categoryService.getAllCategories(0, 1000), HttpStatus.OK);
    }
}
