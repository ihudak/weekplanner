package eu.dec21.wp.tasks.service;

import eu.dec21.wp.tasks.collection.Task;
import eu.dec21.wp.tasks.collection.TaskDirector;
import eu.dec21.wp.tasks.collection.TaskResponse;
import eu.dec21.wp.tasks.collection.TaskStates;
import eu.dec21.wp.tasks.repository.TaskRepository;
import eu.dec21.wp.tasks.service.impl.TaskServiceImpl;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private final TaskDirector taskDirector;
    private List<Task> tasks;

    TaskServiceTest() {
        taskDirector = new TaskDirector();
    }

    @BeforeAll
    static void init() {}

    @BeforeEach
    void setUp() {
        tasks = new ArrayList<>();

        this.setupMocks();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createTask() {
        Task task = taskDirector.constructRandomTaskWithId("myTaskId");
        Task savedTask = taskService.save(task);
        assertEquals(1, tasks.size());
        assertEquals(savedTask.getTaskId(), tasks.getFirst().getTaskId());
        assertEquals(task.getTaskId(), tasks.getFirst().getTaskId());
        assertEquals("myTaskId", savedTask.getTaskId());
    }

    @Test
    void updateTask() {
        Task task = taskDirector.constructRandomTaskWithId("myTaskId");
        task.setState(TaskStates.IMPL);
        Task anotherTask = taskDirector.constructRandomTaskWithId("myTaskId");
        anotherTask.setState(TaskStates.DONE);
        tasks.add(task);
        Task savedTask = taskService.save(anotherTask);
        assertEquals(1, tasks.size());
        assertEquals(savedTask.getTaskId(), tasks.getFirst().getTaskId());
        assertEquals(task.getTaskId(), tasks.getFirst().getTaskId());
        assertEquals(TaskStates.DONE, tasks.getFirst().getState());
        assertEquals("myTaskId", savedTask.getTaskId());
    }

    @Test
    void createMultipleTasks() {
        List<Task> taskList = taskDirector.constructRandomTasks(5);
        List<Task> savedTasks = taskService.saveAll(taskList);
        assertEquals(5, tasks.size());
        for (Task task : taskList) {
            assertNotNull(tasks.stream().filter(t -> t.getTaskId().equals(task.getTaskId())).findFirst().orElse(null));
            assertNotNull(savedTasks.stream().filter(t -> t.getTaskId().equals(task.getTaskId())).findFirst().orElse(null));
        }
    }

    @Test
    void createAndUpdateTasks() {
        List<Task> taskList = taskDirector.constructTasksForCategoryState(2L, TaskStates.DONE, 5);
        // save two tasks with the save IDs as the first two in the taskList
        taskService.saveAll(taskDirector.constructTasksForCategoryState(1L, TaskStates.IMPL, 2));

        // check current state of tasks
        assertEquals(2, tasks.size());
        for (Task task : tasks) {
            assertEquals(1L, task.getCategoryId());
            assertEquals(TaskStates.IMPL, task.getState());
        }

        // now save all 5 (2 of them will modify
        List<Task> savedTasks = taskService.saveAll(taskList);
        assertEquals(5, tasks.size());
        for (Task task : tasks) {
            assertEquals(2L, task.getCategoryId());
            assertEquals(TaskStates.DONE, task.getState());
        }
        assertEquals(5, savedTasks.size());
        for (Task task : savedTasks) {
            assertEquals(2L, task.getCategoryId());
            assertEquals(TaskStates.DONE, task.getState());
        }
    }

    @Test
    void getTasksByCategory() {
        tasks.addAll(taskDirector.constructTasksForCategoryState(2L, TaskStates.DONE, 3));
        tasks.addAll(taskDirector.constructTasksForCategoryState(1L, TaskStates.IMPL, 2));

        assertEquals(5, tasks.size());

        TaskResponse response = taskService.getAllTasksByCategoryId(2L, 0, 10);
        assertEquals(3, response.getContent().size());
        for (Task task : response.getContent()) {
            assertEquals(2L, task.getCategoryId());
        }

        response = taskService.getAllTasksByCategoryId(1L, 0, 10);
        assertEquals(2, response.getContent().size());
        for (Task task : response.getContent()) {
            assertEquals(1L, task.getCategoryId());
        }
    }

    @Test
    void getTasksByCategoryAndState() {
        tasks.addAll(taskDirector.constructTasksForCategoryState(2L, TaskStates.DONE, 2));
        tasks.addAll(taskDirector.constructTasksForCategoryState(2L, TaskStates.IMPL, 1));
        tasks.addAll(taskDirector.constructTasksForCategoryState(1L, TaskStates.IMPL, 2));
        assertEquals(5, tasks.size());

        TaskResponse response = taskService.getAllTasksByCategoryIdAndState(2L, TaskStates.DONE, 0, 10);
        assertEquals(2, response.getContent().size());
        for (Task task : response.getContent()) {
            assertEquals(2L, task.getCategoryId());
            assertEquals(TaskStates.DONE, task.getState());
        }

        response = taskService.getAllTasksByCategoryIdAndState(2L, TaskStates.IMPL, 0, 10);
        assertEquals(1, response.getContent().size());
        for (Task task : response.getContent()) {
            assertEquals(2L, task.getCategoryId());
            assertEquals(TaskStates.IMPL, task.getState());
        }

        response = taskService.getAllTasksByCategoryIdAndState(1L, TaskStates.IMPL, 0, 10);
        assertEquals(2, response.getContent().size());
        for (Task task : response.getContent()) {
            assertEquals(1L, task.getCategoryId());
            assertEquals(TaskStates.IMPL, task.getState());
        }
    }

    // ToDo: implement rest of the tests

    private void setupMocks() {
        //count
        lenient().when(taskRepository.count()).then((Answer<Long>) invocation -> (long)tasks.size());

        // find by id
        lenient().when(taskRepository.findById(Mockito.anyString())).then((Answer<Optional<Task>>) invocation -> {
            String taskId = (String) invocation.getArgument(0, String.class);
            Task task = tasks.stream().filter(t -> t.getTaskId().equals(taskId)).findFirst().orElse(null);
            return task == null ? Optional.empty() : Optional.of(task);
        });

        // getAllByCategory
        lenient().when(taskRepository.getAllByCategoryId(Mockito.anyLong(), Mockito.any(Pageable.class))).then((Answer<Page<Task>>) invocation -> {
            Long categoryId = (Long) invocation.getArgument(0, Long.class);
            Pageable pageable = (Pageable) invocation.getArgument(1, Pageable.class);

            List<Task> taskList = tasks.stream().filter(t -> categoryId.equals(t.getCategoryId())).toList();
            return new PageImpl<>(taskList, pageable, taskList.size());
        });

        // get all by category and state
        lenient().when(taskRepository.getAllByCategoryIdAndState(Mockito.anyLong(), Mockito.any(TaskStates.class), Mockito.any(Pageable.class))).then((Answer<Page<Task>>) invocation -> {
            long categoryId = (long) invocation.getArgument(0, Long.class);
            TaskStates taskState = (TaskStates) invocation.getArgument(1, TaskStates.class);
            Pageable pageable = (Pageable) invocation.getArgument(2, Pageable.class);
            List<Task> tasksToReturn = tasks.stream().filter(t -> t.getCategoryId().equals(categoryId)).toList();
            tasksToReturn = tasksToReturn.stream().filter(t -> t.getState().equals(taskState)).toList();
            return new PageImpl<>(tasksToReturn, pageable, tasksToReturn.size());
        });

        // find all
        lenient().when(taskRepository.findAll()).then((Answer<List<Task>>) invocation -> tasks);

        // find all pageable
        lenient().when(taskRepository.findAll(Mockito.any(Pageable.class))).then((Answer<Page<Task>>) invocation -> {
            Pageable pageable = (Pageable) invocation.getArgument(0, Pageable.class);
            return new PageImpl<>(tasks, pageable, tasks.size());
        });

        // save
        lenient().when(taskRepository.save(Mockito.any(Task.class))).then((Answer<Task>) invocation -> {
            Task task = invocation.getArgument(0, Task.class);

            tasks.replaceAll(t -> t.getTaskId().equals(task.getTaskId()) ? task : t);
            if (tasks.stream().noneMatch(t -> t.getTaskId().equals(task.getTaskId()))) {
                tasks.add(task);
            }
            return task;
        });

        // save all
        lenient().when(taskRepository.saveAll(Mockito.anyList())).then(new Answer<List<Task>>() {
            @Override
            public List<Task> answer(InvocationOnMock invocation) throws Throwable {
                @SuppressWarnings("unchecked")
                List<Task> tasksArgument = (List<Task>) invocation.getArgument(0, List.class);
                for (Task task : tasksArgument) {
                    tasks.replaceAll(t -> t.getTaskId().equals(task.getTaskId()) ? task : t);
                    if (tasks.stream().noneMatch(t -> t.getTaskId().equals(task.getTaskId()))) {
                        tasks.add(task);
                    }
                }
                return tasksArgument;
            }
        });

        // delete by id
        lenient().doAnswer((Answer<String>) invocation -> {
            String taskId = (String) invocation.getArgument(0, String.class);
            if (tasks.isEmpty()) {
                return "";
            }
            tasks.removeIf(task -> taskId.equals(task.getTaskId()));
            return taskId;
        }).when(taskRepository).deleteById(Mockito.anyString());

        // delete
        lenient().doAnswer((Answer<Task>) invocation -> {
            Task taskToDel = (Task) invocation.getArgument(0, Task.class);
            if (tasks.isEmpty()) {
                return null;
            }
            tasks.removeIf(task -> taskToDel.getTaskId().equals(task.getTaskId()));
            return taskToDel;
        }).when(taskRepository).delete(Mockito.any(Task.class));
    }
}
