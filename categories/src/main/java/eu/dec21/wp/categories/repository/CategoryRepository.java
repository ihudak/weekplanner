package eu.dec21.wp.categories.repository;

import eu.dec21.wp.categories.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndUserId(String name, Long userId);
}
