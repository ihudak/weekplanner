package eu.dec21.wp.tasks.repository;

import eu.dec21.wp.tasks.collection.Task;
import eu.dec21.wp.tasks.collection.TaskStates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    Page<Task> getAllByCategoryId(Long categoryId, Pageable pageable);
    Page<Task> getAllByCategoryIdAndState(Long categoryId, TaskStates state, Pageable pageable);
}
