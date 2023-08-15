package eu.dec21.wp.categories.repository;

import eu.dec21.wp.categories.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
