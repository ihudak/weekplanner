package eu.dec21.wp.tasks.repository;

import eu.dec21.wp.tasks.collection.Task;
import eu.dec21.wp.tasks.collection.TaskDirector;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
public class TaskRepositoryTest {
    private final TaskDirector taskDirector = new TaskDirector();

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    void cleanUp() {
        taskRepository.deleteAll();
        mongoTemplate.dropCollection(Task.class);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void mongoConnection() {
        assertNotNull(mongoTemplate.getDb());
        mongoTemplate.getDb().createCollection("wp_tasks");
        assertEquals(0L, mongoTemplate.getDb().getCollection("wp_tasks").countDocuments(Document.parse("{}")));
    }

    @Test
    void getAllByCategoryIdAndState() {
        assertEquals(0, taskRepository.count());
        Task task = taskDirector.constructRandomTask();
        taskRepository.save(task);
        assertEquals(1, taskRepository.count());
        Pageable pageable = PageRequest.of(0, 10);

        List<Task> tasks = taskRepository.getAllByCategoryId(task.getCategoryId(), pageable).getContent();
        assertNotNull(tasks.getFirst());

        pageable = PageRequest.of(1, 10);
        tasks = taskRepository.getAllByCategoryId(task.getCategoryId(), pageable).getContent();
        assertEquals(0, tasks.size());

        tasks = taskRepository.getAllByCategoryIdAndState(task.getCategoryId(), task.getState());
        assertNotNull(tasks.getFirst());
    }

    @Test
    void saveAll() {
        final int numberOfTasks = 15, tasksPerPage = 5;
        assertEquals(0, taskRepository.count());
        List<Task> tasks = taskDirector.constructRandomTasks(numberOfTasks);
        taskRepository.saveAll(tasks);
        assertEquals(numberOfTasks, taskRepository.count());

        for (int i = 0; i < numberOfTasks / tasksPerPage; i++) {
            Pageable pageable = PageRequest.of(i, tasksPerPage);
            List<Task> returnedTasks = taskRepository.findAll(pageable).getContent();
            assertEquals(tasksPerPage, returnedTasks.size());
            for (int j = 0; j < tasksPerPage; j++) {
                Task task = returnedTasks.get(j);
                assertNotNull(task);
                assertTrue(tasks.remove(task));
            }
        }
        assertEquals(0, tasks.size());
    }

    @Test
    void getAllByCategoryId() {
        final int numberOfTasks = 50, tasksPerPage = 5;
        List<Task> tasks = taskDirector.constructRandomTasks(numberOfTasks);
        taskRepository.saveAll(tasks);
        Pageable pageable = PageRequest.of(0, tasksPerPage);

        final long categoryId = 3L;
        List<Task> returnedTasks = taskRepository.getAllByCategoryId(categoryId, pageable).getContent();
        for (Task returnedTask : returnedTasks) {
            assertEquals(categoryId, returnedTask.getCategoryId());
        }
    }
}
