package eu.dec21.wp.tasks.repository;

import eu.dec21.wp.tasks.collection.Task;
import eu.dec21.wp.tasks.collection.TaskDirector;
import eu.dec21.wp.tasks.collection.TaskLink;
import eu.dec21.wp.tasks.collection.TaskStates;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    void getOneByCategoryId() {
        assertEquals(0, taskRepository.count());
        Task task = taskDirector.constructRandomTask();
        taskRepository.save(task);
        assertEquals(1, taskRepository.count());
        Pageable pageable = PageRequest.of(0, 10);

        List<Task> tasks = taskRepository.getAllByCategoryIdAndArchived(task.getCategoryId(), false, pageable).getContent();
        assertNotNull(tasks.getFirst());

        pageable = PageRequest.of(1, 10);
        tasks = taskRepository.getAllByCategoryIdAndArchived(task.getCategoryId(), false, pageable).getContent();
        assertEquals(0, tasks.size());
    }

    @Test
    void getOneByCategoryIdAndState() {
        assertEquals(0, taskRepository.count());
        Task task = taskDirector.constructRandomTask();
        taskRepository.save(task);
        assertEquals(1, taskRepository.count());
        Pageable pageable = PageRequest.of(0, 10);

        pageable = PageRequest.of(0, 10);
        List<Task> tasks = taskRepository.getAllByCategoryIdAndStateAndArchived(task.getCategoryId(), task.getState(), false, pageable).getContent();
        assertEquals(task.getTaskId(), tasks.getFirst().getTaskId());
        assertEquals(1, tasks.size());

        pageable = PageRequest.of(1, 10);
        tasks = taskRepository.getAllByCategoryIdAndStateAndArchived(task.getCategoryId(), task.getState(), false, pageable).getContent();
        assertEquals(0, tasks.size());
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
                assertTrue(tasks.remove(tasks.stream().filter(t -> t.getTaskId().equals(task.getTaskId())).findFirst().get()));
            }
        }
        assertEquals(0, tasks.size());
    }

    @Test
    void getAllByCategoryIdAndArchived() {
        final int numberOfTasks = 50, tasksPerPage = 10;
        List<Task> tasks = taskDirector.constructRandomTasks(numberOfTasks);
        Pageable firstPage = PageRequest.of(0, tasksPerPage);
        Pageable secondPage = PageRequest.of(1, tasksPerPage);
        long categoryId = 3L;
        for (int i = 0; i < numberOfTasks; i++) {
            tasks.get(i).setCategoryId(i % 2 == 0 ? categoryId : categoryId - 1L);
            tasks.get(i).setArchived(i % 3 == 0);
        }
        taskRepository.saveAll(tasks);

        List<Task> returnedTasks = taskRepository.getAllByCategoryIdAndArchived(categoryId, false, firstPage).getContent();
        assertEquals(10, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertEquals(categoryId, returnedTask.getCategoryId());
            assertTrue(returnedTask.isActual());
        }
        returnedTasks = taskRepository.getAllByCategoryIdAndArchived(categoryId, false, secondPage).getContent();
        assertEquals(6, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertEquals(categoryId, returnedTask.getCategoryId());
            assertTrue(returnedTask.isActual());
        }

        categoryId -= 1L;
        returnedTasks = taskRepository.getAllByCategoryIdAndArchived(categoryId, true, firstPage).getContent();
        assertEquals(8, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertEquals(categoryId, returnedTask.getCategoryId());
            assertTrue(returnedTask.isArchived());
        }
    }

    @Test
    void getAllByCategoryIdAndStateAndArchived() {
        final int numberOfTasks = 50, tasksPerPage = 5;
        List<Task> tasks = taskDirector.constructRandomTasks(numberOfTasks);
        Pageable firstPage = PageRequest.of(0, tasksPerPage);
        Pageable secondPage = PageRequest.of(1, tasksPerPage);
        long categoryId = 3L;
        TaskStates state = TaskStates.IMPL;
        for (int i = 0; i < numberOfTasks; i++) {
            tasks.get(i).setCategoryId(i % 2 == 0 ? categoryId : categoryId - 1L);
            tasks.get(i).setState(i % 3 == 0 ? state : TaskStates.DONE);
            tasks.get(i).setArchived(i % 5 == 0);
        }
        taskRepository.saveAll(tasks);

        List<Task> returnedTasks = taskRepository.getAllByCategoryIdAndStateAndArchived(categoryId, state, false, firstPage).getContent();
        assertEquals(5, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertEquals(categoryId, returnedTask.getCategoryId());
            assertEquals(state, returnedTask.getState());
            assertTrue(returnedTask.isActual());
        }
        returnedTasks = taskRepository.getAllByCategoryIdAndStateAndArchived(categoryId, state, false, secondPage).getContent();
        assertEquals(2, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertEquals(categoryId, returnedTask.getCategoryId());
            assertEquals(state, returnedTask.getState());
            assertTrue(returnedTask.isActual());
        }

        categoryId -= 1L;
        returnedTasks = taskRepository.getAllByCategoryIdAndStateAndArchived(categoryId, state, true, firstPage).getContent();
        assertEquals(2, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertEquals(categoryId, returnedTask.getCategoryId());
            assertEquals(state, returnedTask.getState());
            assertTrue(returnedTask.isArchived());
        }
    }

    @Test
    void getAllByStateAndArchived() {
        final int numberOfTasks = 50, tasksPerPage = 10;
        List<Task> tasks = taskDirector.constructRandomTasks(numberOfTasks);
        Pageable firstPage = PageRequest.of(0, tasksPerPage);
        Pageable secondPage = PageRequest.of(1, tasksPerPage);
        TaskStates state = TaskStates.IMPL;
        for (int i = 0; i < numberOfTasks; i++) {
            tasks.get(i).setState(i % 2 == 0 ? state : TaskStates.DONE);
            tasks.get(i).setArchived(i % 3 == 0);
        }
        taskRepository.saveAll(tasks);

        List<Task> returnedTasks = taskRepository.getAllByStateAndArchived(state, false, firstPage).getContent();
        assertEquals(10, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertEquals(state, returnedTask.getState());
            assertTrue(returnedTask.isActual());
        }
        returnedTasks = taskRepository.getAllByStateAndArchived(state, false, secondPage).getContent();
        assertEquals(6, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertEquals(state, returnedTask.getState());
            assertTrue(returnedTask.isActual());
        }

        state = TaskStates.DONE;
        returnedTasks = taskRepository.getAllByStateAndArchived(state, true, firstPage).getContent();
        assertEquals(8, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertEquals(state, returnedTask.getState());
            assertTrue(returnedTask.isArchived());
        }
    }

    @Test
    void getAllByArchived() {
        final int numberOfTasks = 30, tasksPerPage = 15;
        List<Task> tasks = taskDirector.constructRandomTasks(numberOfTasks);
        Pageable firstPage = PageRequest.of(0, tasksPerPage);
        Pageable secondPage = PageRequest.of(1, tasksPerPage);
        for (int i = 0; i < numberOfTasks; i++) {
            tasks.get(i).setArchived(i % 3 == 0);
        }
        taskRepository.saveAll(tasks);

        List<Task> returnedTasks = taskRepository.getAllByArchived(false, firstPage).getContent();
        assertEquals(15, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertTrue(returnedTask.isActual());
        }
        returnedTasks = taskRepository.getAllByArchived(false, secondPage).getContent();
        assertEquals(5, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertTrue(returnedTask.isActual());
        }

        returnedTasks = taskRepository.getAllByArchived(true, firstPage).getContent();
        assertEquals(10, returnedTasks.size());
        for (Task returnedTask : returnedTasks) {
            assertTrue(returnedTask.isArchived());
        }
    }

    @Test
    void mongoTemplateFindByText() {
        this.prepareTasks(50);
        String searchStr = "project";
        int pageSize = 22;

        // only actual, page0
        Query query = this.makeQuery(searchStr, 0, pageSize, false);
        List<Task> returnedTasks = mongoTemplate.find(query, Task.class);
        assertEquals(pageSize, returnedTasks.size());
        assertEquals(pageSize, mongoTemplate.count(query, Task.class));
        for (Task returnedTask : returnedTasks) {
            assertTrue(returnedTask.isActual());
            assertTrue(this.checkTasksBySearchStr(returnedTask, searchStr));
        }

        // only actual, page1
        query = this.makeQuery(searchStr, 1, pageSize, false);
        returnedTasks = mongoTemplate.find(query, Task.class);
        assertEquals(8, returnedTasks.size());
        assertEquals(8, mongoTemplate.count(query, Task.class));
        for (Task returnedTask : returnedTasks) {
            assertTrue(returnedTask.isActual());
            assertTrue(this.checkTasksBySearchStr(returnedTask, searchStr));
        }

        // only actual, page2
        query = this.makeQuery(searchStr, 2, pageSize, false);
        returnedTasks = mongoTemplate.find(query, Task.class);
        assertEquals(0, returnedTasks.size());
        assertEquals(0, mongoTemplate.count(query, Task.class));

        // archived and actual, page0
        query = this.makeQuery(searchStr, 0, pageSize, true);
        returnedTasks = mongoTemplate.find(query, Task.class);
        assertEquals(pageSize, returnedTasks.size());
        assertEquals(pageSize, mongoTemplate.count(query, Task.class));
        for (Task returnedTask : returnedTasks) {
            assertTrue(this.checkArchived(returnedTask, 3));
            assertTrue(this.checkTasksBySearchStr(returnedTask, searchStr));
        }

        // archived and actual, page1
        query = this.makeQuery(searchStr, 1, pageSize, true);
        returnedTasks = mongoTemplate.find(query, Task.class);
        assertEquals(pageSize, returnedTasks.size());
        assertEquals(pageSize, mongoTemplate.count(query, Task.class));
        for (Task returnedTask : returnedTasks) {
            assertTrue(this.checkArchived(returnedTask, 3));
            assertTrue(this.checkTasksBySearchStr(returnedTask, searchStr));
        }

        // archived and actual, page2
        query = this.makeQuery(searchStr, 2, pageSize, true);
        returnedTasks = mongoTemplate.find(query, Task.class);
        assertEquals(1, returnedTasks.size());
        assertEquals(1, mongoTemplate.count(query, Task.class));
        for (Task returnedTask : returnedTasks) {
            assertTrue(this.checkArchived(returnedTask, 3));
            assertTrue(this.checkTasksBySearchStr(returnedTask, searchStr));
        }

        // find by state
        query = this.makeQuery("CANCEL", 0, 50, true);
        returnedTasks = mongoTemplate.find(query, Task.class);
        assertEquals(10, returnedTasks.size());
        assertEquals(10, mongoTemplate.count(query, Task.class));
        for (Task returnedTask : returnedTasks) {
            assertTrue(this.checkArchived(returnedTask, 3));
            assertEquals(TaskStates.CANCEL, returnedTask.getState());
        }

        query = this.makeQuery("READY", 0, 50, false);
        returnedTasks = mongoTemplate.find(query, Task.class);
        assertEquals(7, returnedTasks.size());
        assertEquals(7, mongoTemplate.count(query, Task.class));
        for (Task returnedTask : returnedTasks) {
            assertTrue(returnedTask.isActual());
            assertEquals(TaskStates.READY, returnedTask.getState());
        }
    }

    @Test
    void mongoTemplateGetAllByWeek() {
        this.prepareTasks(50);
        this.archiveTasks(3);

        List<Task> tasks = mongoTemplate.find(this.dateBasedTasksQuery(this.prepareWeekCriteria(this.getWeekEnd())), Task.class);
        assertEquals(23, tasks.size());

        for (Task task : tasks) {
            assertTrue(task.isActual());
            assertTrue(task.getTaskDateTime().isBefore(this.getWeekEnd()));
        }
    }

    @Test
    void mongoTemplateGetActiveByWeek() {
        this.prepareTasks(50);
        this.archiveTasks(3);
        List<Criteria> criteria = this.prepareWeekCriteria(this.getWeekEnd());
        criteria.add(Criteria.where("state").in(TaskStates.activeStates()));

        List<Task> tasks = mongoTemplate.find(this.dateBasedTasksQuery(criteria), Task.class);
        assertEquals(12, tasks.size());

        for (Task task : tasks) {
            assertTrue(task.isActual());
            assertFalse(task.isComplete());
            assertTrue(task.getTaskDateTime().isBefore(this.getWeekEnd()));
        }
    }

    @Test
    void mongoTemplateGetCompleteByWeek() {
        this.prepareTasks(50);
        this.archiveTasks(3);
        List<Criteria> criteria = this.prepareWeekCriteria(this.getWeekEnd());
        criteria.add(Criteria.where("state").in(TaskStates.inactiveStates()));

        List<Task> tasks = mongoTemplate.find(this.dateBasedTasksQuery(criteria), Task.class);
        assertEquals(11, tasks.size());

        for (Task task : tasks) {
            assertTrue(task.isActual());
            assertTrue(task.isComplete());
            assertTrue(task.getTaskDateTime().isBefore(this.getWeekEnd()));
        }
    }

    @Test
    void mongoTemplateGetAllByDay() {
        this.prepareTasks(50);
        this.archiveTasks(3);

        List<Task> tasks = mongoTemplate.find(this.dateBasedTasksQuery(this.prepareWeekCriteria(LocalDate.now().plusDays(1).atStartOfDay())), Task.class);
        assertEquals(23, tasks.size());
        for (Task task : tasks) {
            assertTrue(task.isActual());
            assertTrue(task.getTaskDateTime().isBefore(LocalDate.now().plusDays(1).atStartOfDay()));
        }
    }

    @Test
    void mongoTemplateGetActiveByDay() {
        this.prepareTasks(50);
        this.archiveTasks(3);
        List<Criteria> criteria = this.prepareWeekCriteria(LocalDate.now().plusDays(1).atStartOfDay());
        criteria.add(Criteria.where("state").in(TaskStates.activeStates()));

        List<Task> tasks = mongoTemplate.find(this.dateBasedTasksQuery(criteria), Task.class);
        assertEquals(12, tasks.size());

        for (Task task : tasks) {
            assertTrue(task.isActual());
            assertFalse(task.isComplete());
            assertTrue(task.getTaskDateTime().isBefore(LocalDate.now().plusDays(1).atStartOfDay()));
        }
    }

    @Test
    void mongoTemplateGetCompleteByDay() {
        this.prepareTasks(50);
        this.archiveTasks(3);
        List<Criteria> criteria = this.prepareWeekCriteria(LocalDate.now().plusDays(1).atStartOfDay());
        criteria.add(Criteria.where("state").in(TaskStates.inactiveStates()));

        List<Task> tasks = mongoTemplate.find(this.dateBasedTasksQuery(criteria), Task.class);
        assertEquals(11, tasks.size());

        for (Task task : tasks) {
            assertTrue(task.isActual());
            assertTrue(task.isComplete());
            assertTrue(task.getTaskDateTime().isBefore(LocalDate.now().plusDays(1).atStartOfDay()));
        }
    }

    private void prepareTasks(int numTasks) {
        List<Task> tasks = taskDirector.constructRandomTasks(numTasks);
        for (Task task : tasks) {
            task.setTitle("some title");
            task.setDescription("some description");
            task.setBlockReason("some reason");
            task.addTaskLink(new TaskLink("some first", "https://foo.example.com"));
            task.addTaskLink(new TaskLink("some second", "https://bar.example.com"));
            task.addBlockLink(new TaskLink("some third", "https://baz.example.com"));
            task.addBlockLink(new TaskLink("some fourth", "https://boo.example.com"));
        }

        for (int i = 0; i < numTasks; i++) {
            tasks.get(i).setArchived(i % 3 == 0);

            if (i % 10 == 0) { // title
                tasks.get(i).setTitle("My first project I did");
                tasks.get(i).setState(TaskStates.PREP);
                tasks.get(i).setTaskDateTime(LocalDateTime.now().minusDays(15));
            } else if (i % 10 == 1) {
                tasks.get(i).setDescription("My first project I did");
                tasks.get(i).setState(TaskStates.READY);
                tasks.get(i).setTaskDateTime(LocalDateTime.now().plusDays(15));
            } else if (i % 10 == 2) {
                tasks.get(i).setBlockReason("My blocking project I had");
                tasks.get(i).setState(TaskStates.CANCEL);
                tasks.get(i).setTaskDateTime(LocalDateTime.now());
            } else if (i % 10 == 3) {
                tasks.get(i).getTaskLinks().get(0).setName("another project to do");
                tasks.get(i).setState(TaskStates.IMPL);
                tasks.get(i).setTaskDateTime(LocalDateTime.now());
            } else if (i % 10 == 4) {
                tasks.get(i).getTaskLinks().get(1).setName("another project to do");
                tasks.get(i).setState(TaskStates.DONE);
                tasks.get(i).setTaskDateTime(LocalDateTime.now().minusDays(15));
            } else if (i % 10 == 5) {
                tasks.get(i).getBlockingIssues().get(0).setName("another project to do");
                tasks.get(i).setState(TaskStates.CANCEL);
                tasks.get(i).setTaskDateTime(LocalDateTime.now().plusDays(15));
            } else if (i % 10 == 6) {
                tasks.get(i).getBlockingIssues().get(1).setName("another project to do");
                tasks.get(i).setState(TaskStates.IMPL);
                tasks.get(i).setTaskDateTime(LocalDateTime.now().plusDays(15));
            } else if (i % 10 == 7) {
                tasks.get(i).getBlockingIssues().get(1).setUrl("https://foo.project.com");
                tasks.get(i).setState(TaskStates.DONE);
                tasks.get(i).setTaskDateTime(LocalDateTime.now());
            } else if (i % 10 == 8) {
                tasks.get(i).getTaskLinks().get(0).setUrl("https://bar.project.com");
                tasks.get(i).setState(TaskStates.READY);
                tasks.get(i).setTaskDateTime(LocalDateTime.now());
            } else if (i % 10 == 9) {
                tasks.get(i).setState(TaskStates.PREP);
                tasks.get(i).setTaskDateTime(LocalDateTime.now());
            }
        }

        taskRepository.saveAll(tasks);
    }

    private boolean checkTasksBySearchStr(Task task, String searchStr) {
        int taskId = Integer.parseInt(task.getTaskId());

        if (taskId % 10 == 0) {
            return task.getTitle().contains(searchStr);
        } else if (taskId % 10 == 1) {
            return task.getDescription().contains(searchStr);
        } else if (taskId % 10 == 2) {
            return task.getBlockReason().contains(searchStr);
        } else if (taskId % 10 == 3) {
            return task.getTaskLinks().get(0).getName().contains(searchStr);
        } else if (taskId % 10 == 4) {
            return task.getTaskLinks().get(1).getName().contains(searchStr);
        } else if (taskId % 10 == 5) {
            return task.getBlockingIssues().get(0).getName().contains(searchStr);
        } else if (taskId % 10 == 6) {
            return task.getBlockingIssues().get(1).getName().contains(searchStr);
        } else if (taskId % 10 == 7) {
            return task.getBlockingIssues().get(1).getUrl().contains(searchStr);
        } else if (taskId % 10 == 8) {
            return task.getTaskLinks().get(0).getUrl().contains(searchStr);
        } else if (taskId % 10 == 9) {
            return false;
        }
        return false;
    }

    private boolean checkArchived(Task task, int mod) {
        int taskId = Integer.parseInt(task.getTaskId());
        if (taskId % mod == 0) {
            return task.isArchived() && !task.isActual();
        } else {
            return task.isActual() && !task.isArchived();
        }
    }

    private Query makeQuery(String searchStr, int pageNo, int pageSize, boolean includeArchived) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        criteria.add(Criteria.where("title").regex(searchStr, "i"));
        criteria.add(Criteria.where("description").regex(searchStr, "i"));
        criteria.add(Criteria.where("state").regex(searchStr, "i"));
        criteria.add(Criteria.where("blockReason").regex(searchStr, "i"));

        criteria.add(Criteria.where("taskLinks").elemMatch(Criteria.where("name").regex(searchStr, "i")));
        criteria.add(Criteria.where("taskLinks").elemMatch(Criteria.where("url").regex(searchStr, "i")));
        criteria.add(Criteria.where("blockingIssues").elemMatch(Criteria.where("name").regex(searchStr, "i")));
        criteria.add(Criteria.where("blockingIssues").elemMatch(Criteria.where("url").regex(searchStr, "i")));

        if (!includeArchived) {
            Criteria finalCriteria = new Criteria().andOperator(Criteria.where("archived").is(Boolean.FALSE), new Criteria().orOperator(criteria));
            query.addCriteria(new Criteria().andOperator(finalCriteria));
        } else {
            query.addCriteria(new Criteria().orOperator(criteria));
        }

        query.with(Sort.by(Sort.Direction.DESC, "taskDateTime"));
        query.skip((long) pageNo * pageSize).limit(pageSize);
        return query;
    }

    private void archiveTasks(int mod) {
        for (Task task: taskRepository.findAll()) {
            int taskId = Integer.parseInt(task.getTaskId());
            if (taskId % mod == 0) {
                task.archive();
                taskRepository.save(task);
            }
        }
    }

    private int getCurrentWeekNo() {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return LocalDate.now().get(weekFields.weekOfWeekBasedYear());
    }

    private LocalDateTime getWeekEnd() {
        // Get the first day of the specified week
        return LocalDate.ofYearDay(LocalDate.now().getYear(), 1)
                .with(WeekFields.of(Locale.getDefault()).weekOfYear(), this.getCurrentWeekNo())
                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).plusWeeks(1).atStartOfDay();
    }

    private List<Criteria> prepareWeekCriteria(LocalDateTime endDate) {
        List<Criteria> criteria = new ArrayList<>();

        criteria.add(Criteria.where("archived").is(Boolean.FALSE));
        criteria.add(Criteria.where("taskDateTime").lt(endDate));
        return criteria;
    }

    private Query dateBasedTasksQuery(List<Criteria> criteria) {
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(criteria));
        query.with(Sort.by(Sort.Direction.DESC, "taskDateTime"));
        return query;
    }
}
