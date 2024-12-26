package eu.dec21.wp.tasks.service;

import eu.dec21.wp.tasks.collection.Task;
import eu.dec21.wp.tasks.collection.TaskDirector;
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
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        //count
        lenient().when(taskRepository.count()).then((Answer<Long>) invocation -> (long)tasks.size());

        // find by id
        lenient().when(taskRepository.findById(Mockito.anyString())).then((Answer<Optional<Task>>) invocation -> {
            String taskId = (String) invocation.getArgument(0, String.class);
            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    if (taskId.equals(task.getTaskId())) {
                        return Optional.of(task);
                    }
                }
            }
            return Optional.empty();
        });

        // get all by category and state
        lenient().when(taskRepository.getAllByCategoryIdAndState(Mockito.anyLong(), Mockito.any(TaskStates.class))).then((Answer<List<Task>>) invocation -> {
            long categoryId = (long) invocation.getArgument(0, Long.class);
            TaskStates taskState = (TaskStates) invocation.getArgument(1, TaskStates.class);
            List<Task> tasksToReturn = new ArrayList<Task>();
            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    if (task.getCategoryId() == categoryId && taskState.equals(task.getState())) {
                        tasksToReturn.add(task);
                    }
                }
            }
            return tasksToReturn;
        });

        // find all
        lenient().when(taskRepository.findAll()).then((Answer<List<Task>>) invocation -> tasks);

        // save
        lenient().when(taskRepository.save(Mockito.any(Task.class))).then(new Answer<Task>() {
            long sequence = 1;

            @Override
            public Task answer(InvocationOnMock invocation) throws Throwable {
                Task task = (Task) invocation.getArgument(0, Object.class);

                String taskId = "";
                try {
                    taskId = task.getTaskId();
                } catch (Exception ignored) {}
                if (taskId.isEmpty()) {
                    task.setTaskId(String.valueOf(sequence++));
                }
                return task;
            }
        });

        // save all
        lenient().when(taskRepository.saveAll(Mockito.anyList())).then(new Answer<List<Task>>() {
           long sequence = 1;

           @Override
            public List<Task> answer(InvocationOnMock invocation) throws Throwable {
               List<Task> tasks = invocation.getArgument(0);
               for (Task task : tasks) {
                   String taskId = "";
                   try {
                       taskId = task.getTaskId();
                   } catch (Exception ignored) {}
                   if (taskId.isEmpty()) {
                       task.setTaskId(String.valueOf(sequence++));
                   }
               }
               return tasks;
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

        // getAllByCategory
        Page<Task> taskPage = Mockito.mock(Page.class);
        lenient().when(taskRepository.getAllByCategoryId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(taskPage);
        lenient().when(taskRepository.findAll(Mockito.any(Pageable.class))).thenReturn(taskPage);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createTask() {
        tasks.add(taskDirector.constructRandomTaskWithId("myTaskId"));
        Task savedTask = taskService.save(tasks.getFirst());
        assertEquals(tasks.getFirst().getTaskId(), savedTask.getTaskId());
        assertEquals("myTaskId", savedTask.getTaskId());
    }
}
