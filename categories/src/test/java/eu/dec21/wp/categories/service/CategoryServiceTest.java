package eu.dec21.wp.categories.service;

import eu.dec21.wp.categories.dto.CategoryDto;
import eu.dec21.wp.categories.dto.CategoryResponse;
import eu.dec21.wp.categories.entity.Category;
import eu.dec21.wp.categories.entity.CategoryBuilder;
import eu.dec21.wp.categories.entity.CategoryDirector;
import eu.dec21.wp.categories.mapper.CategoryMapper;
import eu.dec21.wp.categories.repository.CategoryRepository;
import eu.dec21.wp.categories.service.impl.CategoryServiceImpl;
import eu.dec21.wp.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private final CategoryDirector categoryDirector;
    private List<Category> categories;

    CategoryServiceTest() {
        categoryDirector = new CategoryDirector();
    }

    @BeforeAll
    static void init() {

    }

    @BeforeEach
    void setUp() {
        categories = new ArrayList<>();
        //lenient().when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(categoriesUser1.get(0));

        // count
        lenient().when(categoryRepository.count()).then((Answer) invocation -> (long)categories.size());

        // find by id
        lenient().when(categoryRepository.findById(Mockito.any(Long.class))).then((Answer<Optional<Category>>) invocation -> {
           int categoryId = invocation.getArgument(0, Long.class).intValue();
           if (!categories.isEmpty()) {
               for (Category category: categories) {
                   if (categoryId == category.getId()) {
                       return Optional.of(category);
                   }
               }
           }
           return Optional.empty();
        });

        // find by name and user
        lenient().when(categoryRepository.findByNameAndUserId(Mockito.anyString(), Mockito.anyLong())).then((Answer<Optional<Category>>) invocation -> {
            String name = invocation.getArgument(0, String.class);
            long userId = invocation.getArgument(1, Long.class);
            if (!categories.isEmpty()) {
                for (Category category: categories) {
                    if(category.getName().equals(name) && category.getUserId().equals(userId)) {
                        return Optional.of(category);
                    }
                }
            }
            return Optional.empty();
        });

        // saveAll
        lenient().when(categoryRepository.saveAll(Mockito.anyList())).then(new Answer<List<Category>>() {
            long sequence = 1;

            @Override
            public List<Category> answer(InvocationOnMock invocation) throws Throwable {
                List<Category> categories = invocation.getArgument(0);
                for (Category category: categories) {
                    category.setId(sequence++);
                }
                return categories;
            }
        });

        // save
        lenient().when(categoryRepository.save(Mockito.any(Category.class))).then(new Answer<Category>() {
            long sequence = 1;

            @Override
            public Category answer(InvocationOnMock invocation) throws Throwable {
                Category category = invocation.getArgument(0, Category.class);
                if (0 == category.getId()) {
                    category.setId(sequence++);
                }
                return category;
            }
        });

        // delete by id
        lenient().doAnswer((Answer<Long>) invocation -> {
            long id = invocation.getArgument(0, Long.class);
            if (categories.isEmpty()) {
                return 0L;
            }
            categories.removeIf(category -> id == category.getId());
            return id;
        }).when(categoryRepository).deleteById(Mockito.any(Long.class));

        // delete
        lenient().doAnswer((Answer<Category>) invocation -> {
            Category categoryToDel = invocation.getArgument(0, Category.class);
            if (categories.isEmpty()) {
                return null;
            }
            categories.removeIf(category -> categoryToDel.getId().equals(category.getId()));
            return categoryToDel;
        }).when(categoryRepository).delete(Mockito.any(Category.class));


        // find all, all for user, paged
        lenient().when(categoryRepository.findAll()).then((Answer) invocation -> categories);
        Page<Category> categoryPage = Mockito.mock(Page.class);
        lenient().when(categoryRepository.findAll(Mockito.any(Pageable.class))).thenReturn(categoryPage);
        lenient().when(categoryRepository.findAllByUserId(Mockito.anyLong()))
                .then((Answer) invocation ->
                        categories.stream().filter(cat -> cat.getUserId().equals(invocation.getArgument(0, Long.class)))
                                .collect(Collectors.toList()));
        lenient().when(categoryRepository.findAllByUserId(Mockito.any(Pageable.class), Mockito.anyLong())).thenReturn(categoryPage);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createCategory() {
        categories.add(categoryDirector.constructRandomCategory());
        CategoryDto savedCategory = categoryService.createCategory(CategoryMapper.mapToCategoryDto(categories.get(0)));

        assertEquals(1,             savedCategory.getId());

        assertTrue(savedCategory.equals(CategoryMapper.mapToCategoryDto(categories.get(0))));
    }

    @Test
    void getCategoryById() {
        int numCategories = 3;
        long categoryToFind = 1;
        categories = categoryDirector.constructRandomCategories(numCategories);

        categoryRepository.saveAll(categories);

        assertEquals(numCategories, categoryRepository.count());

        CategoryDto category = categoryService.getCategoryById(categoryToFind);
        assertEquals(categoryToFind, category.getId());

        assertTrue(category.equals(CategoryMapper.mapToCategoryDto(categories.get((int) categoryToFind - 1))));

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }

    @Test
    void getAllCategories() {
        int numCategories = 3;
        categories = categoryDirector.constructRandomCategories(numCategories);
        categoryRepository.saveAll(categories);
        List<CategoryDto> categoryDtoList = categoryService.getAllCategories();

        assertEquals(numCategories, categoryDtoList.size());

        assertTrue(categoryDtoList.get(0).equals(CategoryMapper.mapToCategoryDto(categories.get(0))));
        assertTrue(categoryDtoList.get(1).equals(CategoryMapper.mapToCategoryDto(categories.get(1))));
        assertTrue(categoryDtoList.get(2).equals(CategoryMapper.mapToCategoryDto(categories.get(2))));
    }

    @Test
    void getAllCategoriesPaged() {
        CategoryResponse categoryResponse = categoryService.getAllCategories(1,10);
        assertNotNull(categoryResponse);
    }

    @Test
    void getAllCategoriesForUser() {
        int numCategories = 3;
        long userId = 5L;

        categories = categoryDirector.constructRandomCategoriesForUser(userId, numCategories);
        // create the same categories for another user
        copyCategoriesToOtherUser(userId + 1);
        categoryRepository.saveAll(categories);
        assertEquals(numCategories * 2, categoryRepository.count());

        List<CategoryDto> foundCategories = categoryService.getAllCategoriesForUser(userId);
        assertEquals(numCategories, foundCategories.size());

        for (CategoryDto categoryDto: foundCategories) {
            assertEquals(userId, categoryDto.getUserId());
        }
    }

    @Test
    void getAllCategoriesForUserPaged() {
        CategoryResponse categoryResponse = categoryService.getAllCategoriesForUser(1L, 1, 10);
        assertNotNull(categoryResponse);
    }

    @Test
    void updateCategory() {
        categories = categoryDirector.constructRandomCategories(1);
        CategoryDto categoryDto = CategoryMapper.mapToCategoryDto(categoryRepository.save(categories.get(0)));
        String newName = "My Plan";
        categoryDto.setName(newName);
        categoryService.updateCategory(categoryDto.getId(), categoryDto);

        assertEquals(1, categoryRepository.count());
        assertEquals(newName, categories.get(0).getName());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory((long) (categories.size() + 1), categoryDto));
    }

    @Test
    void updateCategoryByNameAndUserId() {
        int numCategories = 3;
        long userId = 5L;

        categories = categoryDirector.constructRandomCategoriesForUser(userId, numCategories);
        // create the same categories for another user
        copyCategoriesToOtherUser(userId + 1);
        categoryRepository.saveAll(categories);
        assertEquals(numCategories * 2, categoryRepository.count());

        CategoryDto lookingForCategory = CategoryMapper.mapToCategoryDto(categories.get(1));
        CategoryDto updatedCategory    = new CategoryDto(0L, "new name", 5, "blue", 44L, false);

        CategoryDto savedCategory = categoryService.updateCategory(
                lookingForCategory.getName(),
                lookingForCategory.getUserId(),
                updatedCategory
        );

        assertEquals(numCategories * 2, categoryRepository.count());
        assertFalse(savedCategory.equals(updatedCategory)); // user should not be updated
        updatedCategory.setUserId(lookingForCategory.getUserId());
        assertTrue(savedCategory.equals(updatedCategory)); // user is the same now

        // IDs of all these categories should be the same
        assertEquals(lookingForCategory.getId(), savedCategory.getId());
        assertEquals(categories.get(1).getId(), savedCategory.getId());
    }

    @Test
    void deleteCategoryByID() {
        int numCategories = 3;
        categories = categoryDirector.constructRandomCategories(numCategories);
        categoryRepository.saveAll(categories);
        assertEquals(numCategories, categoryRepository.count());

        long categoryToDel = 1;
        assertTrue(categoryRepository.findById(categoryToDel).isPresent());

        categoryService.deleteCategory(categoryToDel);
        assertEquals(numCategories - 1, categoryRepository.count());
        assertFalse(categoryRepository.findById(categoryToDel).isPresent());

        long category2ToDel = numCategories + 5;
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(category2ToDel));
    }

    @Test
    void deleteCategoryByNameAndUser() {
        int numCategories = 3;
        long userId = 5L;

        categories = categoryDirector.constructRandomCategoriesForUser(userId, numCategories);
        // create the same categories for another user
        copyCategoriesToOtherUser(userId + 1);
        categoryRepository.saveAll(categories);
        assertEquals(numCategories * 2, categoryRepository.count());

        CategoryDto lookingForCategory = CategoryMapper.mapToCategoryDto(categories.get(1));

        categoryService.deleteCategory(lookingForCategory.getName(), userId);
        assertEquals(numCategories * 2 - 1, categoryRepository.count());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.findCategoryByName(lookingForCategory.getName(), userId));
        CategoryDto categoryDtoOtherUser = categoryService.findCategoryByName(lookingForCategory.getName(), userId + 1);
        assertFalse(lookingForCategory.equals(categoryDtoOtherUser));
    }

    @Test
    void findCategoryByName() {
        int numCategories = 3;
        long userId = 5L;

        categories = categoryDirector.constructRandomCategoriesForUser(userId, numCategories);
        // create the same categories for another user
        copyCategoriesToOtherUser(userId + 1);
        categoryRepository.saveAll(categories);
        assertEquals(numCategories * 2, categoryRepository.count());

        CategoryDto lookingForCategory = CategoryMapper.mapToCategoryDto(categories.get(1));

        String nameToFind = lookingForCategory.getName();
        CategoryDto categoryDto = categoryService.findCategoryByName(nameToFind, userId);
        assertTrue(categoryDto.equals(lookingForCategory));
        CategoryDto categoryDtoOtherUser = categoryService.findCategoryByName(nameToFind, userId + 1);
        assertFalse(categoryDto.equals(categoryDtoOtherUser));
        assertEquals(categoryDto.getName(), categoryDtoOtherUser.getName());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.findCategoryByName("non-existing name", userId));
        assertThrows(ResourceNotFoundException.class, () -> categoryService.findCategoryByName(nameToFind, userId + 2));
    }

    @Test
    void count() {
        int numCategories = 3;
        categories = categoryDirector.constructRandomCategories(numCategories);
        categoryRepository.saveAll(categories);
        assertEquals(numCategories, categoryService.count());
    }




    private void copyCategoriesToOtherUser(long userId) {
        if (categories.isEmpty())
            return;

        int numCategories = categories.size();
        CategoryBuilder categoryBuilder = new CategoryBuilder();
        for (int i = 0; i < numCategories; i++) {
            Category category = categoryBuilder.reset()
                    .setName(categories.get(i).getName())
                    .setColor(categories.get(i).getColor())
                    .setPriority(categories.get(i).getPriority())
                    .setUserId(userId)
                    .setId(0L)
                    .build();
            categories.add(category);
        }
    }
}