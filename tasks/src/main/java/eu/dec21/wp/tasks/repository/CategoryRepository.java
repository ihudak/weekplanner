package eu.dec21.wp.tasks.repository;

import eu.dec21.wp.exceptions.ResourceNotFoundException;
import eu.dec21.wp.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Repository
public class CategoryRepository {
    @Value("${http.service.categories}")
    private String categoryBaseURL;

    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger(CategoryRepository.class);

    public CategoryRepository() {
        restTemplate = new RestTemplate();
    }

    public Object getCategoryById(Long categoryId) throws ResourceNotFoundException {
        String url = categoryBaseURL + "/" + categoryId.toString();
        if(logger.isInfoEnabled()) {
            logger.info("Getting category " + categoryId.toString());
        }

        Object category = restTemplate.getForObject(url, Object.class);

        if (null == category) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Category not found by ID: " + categoryId.toString());
            logger.error(ex.toString());
            throw ex;
        }
        return category;
    }

    public Category getCategoryByName(String name) throws ResourceNotFoundException {
        String url = categoryBaseURL + "/find?name=" + name;
        if(logger.isInfoEnabled()) {
            logger.info("Getting category " + name);
        }

        Category category = restTemplate.getForObject(url, Category.class);

        if (null == category) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Category not found by name: " + name);
            logger.error(ex.toString());
            throw ex;
        }
        return category;
    }

    public Category[] getAllCategories() {
        if(logger.isInfoEnabled()) {
            logger.info("Getting all categories");
        }

        return restTemplate.getForObject(categoryBaseURL, Category[].class);
    }

    // TODO: remove after Perform
    public boolean anyCategory() {
        return Arrays.stream(this.getAllCategories()).sequential().findAny().isPresent();
    }

    // TODO: remove after Perform
    public void createCategory(Long id, String name, Integer prio) {
        Category category = new Category(id, name, prio, "black", 1L, false);

        restTemplate.postForEntity(categoryBaseURL, category, Category.class);
    }
}
