package eu.dec21.wp.categories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.dto.CategoryResponse;
import eu.dec21.wp.categories.entity.Category;
import eu.dec21.wp.categories.entity.CategoryDirector;
import eu.dec21.wp.categories.mapper.CategoryMapper;
import eu.dec21.wp.categories.service.CategoryService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;
    private List<Category> categories;
    private final CategoryDirector categoryDirector;

    CategoryControllerTest() {
        categoryDirector = new CategoryDirector();
    }

    @BeforeEach
    public void init() {
        categories = new ArrayList<>();
    }

    @Test
    public void CategoryController_CreateCategory_ReturnCreated() throws Exception {
        given(categoryService.createCategory(any())).willAnswer(invocation -> invocation.getArgument(0));

        categories.add(categoryDirector.constructRandomCategoryForUser(1L));
        CategoryDto categoryDto = CategoryMapper.mapToCategoryDto(categories.get(0));

        ResultActions response = mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(categoryDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority", CoreMatchers.is(categoryDto.getPriority())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color", CoreMatchers.is(categoryDto.getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", CoreMatchers.is(categoryDto.getUserId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted", CoreMatchers.is(categoryDto.isDeleted())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(categoryDto.getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void CategoryController_UpdateCategory_ReturnUpdated() throws Exception {
        CategoryDto categoryDto = CategoryMapper.mapToCategoryDto(categoryDirector.constructRandomCategoryForUser(1L));
        categoryDto.setId(1L);
        when(categoryService.updateCategory(any(Long.class), any(CategoryDto.class))).thenReturn(categoryDto);

        ResultActions response = mockMvc.perform(put("/api/v1/categories/" + categoryDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(categoryDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority", CoreMatchers.is(categoryDto.getPriority())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color", CoreMatchers.is(categoryDto.getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", CoreMatchers.is(categoryDto.getUserId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted", CoreMatchers.is(categoryDto.isDeleted())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(categoryDto.getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void CategoryController_GetAllCategories_ReturnResponseDto() throws Exception {
        ArrayList<CategoryDto> categoryDtos = new ArrayList<>(); // = CategoryDto.builder().name("Boo").color("red").priority(10).userId(1L).deleted(false).build();
        for (int i = 0; i < 10; i++) {
            categoryDtos.add(CategoryMapper.mapToCategoryDto(categoryDirector.constructRandomCategoryForUser(1L)));
        }

        CategoryResponse responseDto = CategoryResponse.builder().pageSize(10).last(true).pageNo(1).content(categoryDtos).build();
        when(categoryService.getAllCategories(1, 10)).thenReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNo", "1")
                        .param("pageSize", "10"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(responseDto.getContent().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].name", CoreMatchers.is(responseDto.getContent().get(0).getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].priority", CoreMatchers.is(responseDto.getContent().get(0).getPriority())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].color", CoreMatchers.is(responseDto.getContent().get(0).getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].userId", CoreMatchers.is(responseDto.getContent().get(0).getUserId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].deleted", CoreMatchers.is(responseDto.getContent().get(0).isDeleted())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[0].id", CoreMatchers.is(responseDto.getContent().get(0).getId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].name", CoreMatchers.is(responseDto.getContent().get(9).getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].priority", CoreMatchers.is(responseDto.getContent().get(9).getPriority())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].color", CoreMatchers.is(responseDto.getContent().get(9).getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].userId", CoreMatchers.is(responseDto.getContent().get(9).getUserId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].deleted", CoreMatchers.is(responseDto.getContent().get(9).isDeleted())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.[9].id", CoreMatchers.is(responseDto.getContent().get(9).getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void CategoryController_CategoryDetail_ReturnCategoryDto() throws Exception {
        CategoryDto categoryDto = CategoryMapper.mapToCategoryDto(categoryDirector.constructRandomCategoryForUser(1L));
        when(categoryService.getCategoryById(categoryDto.getId())).thenReturn(categoryDto);


        ResultActions response = mockMvc.perform(get("/api/v1/categories/" + categoryDto.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(categoryDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority", CoreMatchers.is(categoryDto.getPriority())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color", CoreMatchers.is(categoryDto.getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", CoreMatchers.is(categoryDto.getUserId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted", CoreMatchers.is(categoryDto.isDeleted())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(categoryDto.getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void CategoryController_FindCategory_ReturnCategoryDto() throws Exception {
        CategoryDto categoryDto = CategoryMapper.mapToCategoryDto(categoryDirector.constructRandomCategoryForUser(1L));
        when(categoryService.findCategoryByName(categoryDto.getName(), categoryDto.getUserId())).thenReturn(categoryDto);


        ResultActions response = mockMvc.perform(get("/api/v1/categories/find?name=" + categoryDto.getName())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(categoryDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.priority", CoreMatchers.is(categoryDto.getPriority())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color", CoreMatchers.is(categoryDto.getColor())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", CoreMatchers.is(categoryDto.getUserId().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted", CoreMatchers.is(categoryDto.isDeleted())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(categoryDto.getId().intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void CategoryController_DeleteCategory_ReturnString() throws Exception {
        long categoryId = 1L;
        doNothing().when(categoryService).deleteCategory(any(Long.class));

        ResultActions response = mockMvc.perform(delete("/api/v1/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", CoreMatchers.is("Category deleted with ID: " + (int) categoryId)));
    }
}
