package eu.dec21.wp.tasks.service;

import eu.dec21.wp.exceptions.BadRequestException;
import eu.dec21.wp.exceptions.ResourceNotFoundException;
import eu.dec21.wp.tasks.collection.*;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private MongoTemplate mongoTemplate;

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

    @Test
    void findAll() {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        TaskResponse response = taskService.findAll(0, 5);
        assertEquals(5, response.getContent().size());
        for (Task task : response.getContent()) {
            assertNotNull(tasks.stream().filter(t -> t.getTaskId().equals(task.getTaskId())).findFirst().orElse(null));
        }
    }

    @Test
    void getActualTasks() {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        tasks.get(2).archive();

        TaskResponse response = taskService.getActualTasks(0, 5);
        assertEquals(4, response.getContent().size());
        for (Task task : response.getContent()) {
            assertNotNull(tasks.stream().filter(t -> t.getTaskId().equals(task.getTaskId())).findFirst().orElse(null));
            assertFalse(task.isArchived());
        }
    }

    @Test
    void getTasksByStateActual() {
        tasks.addAll(taskDirector.constructTasksForCategoryState(2L, TaskStates.DONE, 2));
        tasks.addAll(taskDirector.constructTasksForCategoryState(2L, TaskStates.IMPL, 1));
        tasks.addAll(taskDirector.constructTasksForCategoryState(1L, TaskStates.IMPL, 2));
        assertEquals(5, tasks.size());
        tasks.get(1).archive();
        tasks.get(3).archive();
        tasks.get(4).archive();

        TaskResponse response = taskService.getTasksByStateActual(TaskStates.DONE, 0, 10);
        assertEquals(1, response.getContent().size());
        for (Task task : response.getContent()) {
            assertEquals(TaskStates.DONE, task.getState());
            assertTrue(task.isActual());
        }

        response = taskService.getTasksByStateActual(TaskStates.IMPL, 0, 10);
        assertEquals(1, response.getContent().size());
        for (Task task : response.getContent()) {
            assertTrue(task.isActual());
            assertEquals(TaskStates.IMPL, task.getState());
        }
    }

    @Test
    void searchTasks() {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        for (Task task : tasks) {
            task.setTitle("my project in dec");
        }
        tasks.get(1).setTitle("boo");
        tasks.get(3).setTitle("foo");

        TaskResponse response = taskService.searchTasks("project", false, 0, 5);
        assertEquals(3, response.getContent().size());
        assertEquals(3, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertEquals(5, response.getPageSize());
        assertTrue(response.isLast());
        for (Task task : response.getContent()) {
            assertEquals("my project in dec", task.getTitle());
        }

        assertThrowsExactly(BadRequestException.class, () -> taskService.searchTasks("", false, 0, 5));
        assertThrowsExactly(BadRequestException.class, () -> taskService.searchTasks(null, true, 0, 5));
        assertThrowsExactly(BadRequestException.class, () -> taskService.searchTasks("a", false, 0, 5));
        assertThrowsExactly(BadRequestException.class, () -> taskService.searchTasks("ab", true, 0, 5));
    }

    @Test
    void searchTasksInclArchived() {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        for (Task task : tasks) {
            task.setTitle("my project in dec");
        }
        tasks.get(1).setTitle("boo");
        tasks.get(3).setTitle("foo");
        tasks.get(2).archive();

        TaskResponse response = taskService.searchTasks("project", true, 0, 5);
        assertEquals(2, response.getContent().size());
        assertEquals(2, response.getTotalElements());
        assertEquals(0, response.getTotalPages());
        assertEquals(5, response.getPageSize());
        assertTrue(response.isLast());
        for (Task task : response.getContent()) {
            assertEquals("my project in dec", task.getTitle());
            assertFalse(task.isArchived());
        }
    }

    @Test
    void getTaskById() {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = taskService.getTaskById("3");
        assertEquals("3", task.getTaskId());
    }

    @Test
    void getNonExistingTask() {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        assertThrowsExactly(ResourceNotFoundException.class, () -> taskService.getTaskById("non-existing"));
    }

    @Test
    void deleteTask() {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        assertEquals(5, tasks.size());
        taskService.delete("3");
        assertEquals(4, tasks.size());
        assertNotNull(tasks.stream().filter(t -> t.getTaskId().equals("0")).findFirst().orElse(null));
        assertNotNull(tasks.stream().filter(t -> t.getTaskId().equals("1")).findFirst().orElse(null));
        assertNotNull(tasks.stream().filter(t -> t.getTaskId().equals("2")).findFirst().orElse(null));
        assertNotNull(tasks.stream().filter(t -> t.getTaskId().equals("4")).findFirst().orElse(null));
        assertNull(tasks.stream().filter(t -> t.getTaskId().equals("3")).findFirst().orElse(null));
    }

    @Test
    void deleteNonExistingTask() {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        assertEquals(5, tasks.size());
        assertThrowsExactly(ResourceNotFoundException.class, () -> taskService.delete("non existing task"));
        assertEquals(5, tasks.size());
    }

    @Test
    void weekStart() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LocalDateTime week5Year2025 = LocalDate.of(2025, 1, 26).atStartOfDay();
        assertEquals(week5Year2025, getweekStartMethod().invoke(taskService, 5, 2025));

        week5Year2025 = LocalDate.of(2025, 1, 27).atStartOfDay();
        assertEquals(week5Year2025, getweekStartLocaleMethod().invoke(taskService, 5, 2025, Locale.GERMANY));
    }

    @Test
    void allTasksOfWeek() {
        int weekNo = this.getCurrentWeekNo();
        int year = LocalDate.now().getYear();
        // mongo template find
        lenient().when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any())).then((Answer<List<Task>>) invocation -> this.filterTasksByWeek(weekNo, year));

        tasks = taskDirector.constructRandomTasks(50);
        for (Task task : tasks) {
            int taskId = Integer.parseInt(task.getTaskId());
            if (taskId % 2 == 0) {
                task.setTaskDateTime(LocalDateTime.now().minusDays(15));
            } else if (taskId % 3 == 0) {
                task.setTaskDateTime(LocalDateTime.now());
            } else {
                task.setTaskDateTime(LocalDateTime.now().plusDays(15));
            }
        }

        List<Task> taskList = taskService.allTasksOfWeek(weekNo, year);
        assertEquals(8, taskList.size());
        for (Task task : taskList) {
            int taskId = Integer.parseInt(task.getTaskId());
            assertEquals(0, taskId % 3);
            assertTrue(task.getTaskDateTime().isAfter(this.getWeekStart(weekNo, year)));
            assertTrue(task.getTaskDateTime().isBefore(this.getWeekStart(weekNo, year).plusWeeks(1)));
        }
    }

    @Test
    void activeTasksOfWeek() {
        int weekNo = this.getCurrentWeekNo();
        int year = LocalDate.now().getYear();
        // mongo template find
        lenient().when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any())).then((Answer<List<Task>>) invocation -> this.filterTasksByWeek(weekNo, year).stream().filter(Task::isToDo).toList());

        tasks = taskDirector.constructRandomTasks(50);
        for (Task task : tasks) {
            int taskId = Integer.parseInt(task.getTaskId());
            task.setTaskDateTime(LocalDateTime.now().plusDays(15));
            if (taskId % 2 == 0) {
                task.setTaskDateTime(LocalDateTime.now().minusDays(15));
                task.complete();
            } else if (taskId % 3 == 0) {
                task.setTaskDateTime(LocalDateTime.now());
            }
            if (taskId % 5 == 0) {
                task.archive();
            }
        }

        List<Task> taskList = taskService.allTasksOfWeek(weekNo, year);
        assertEquals(6, taskList.size());
        for (Task task : taskList) {
            int taskId = Integer.parseInt(task.getTaskId());
            assertEquals(0, taskId % 3);
            assertTrue(task.isActual());
            assertTrue(task.getState() != TaskStates.DONE && task.getState() != TaskStates.CANCEL);
            assertTrue(task.getTaskDateTime().isAfter(this.getWeekStart(weekNo, year)));
            assertTrue(task.getTaskDateTime().isBefore(this.getWeekStart(weekNo, year).plusWeeks(1)));
        }
    }

    @Test
    void inactiveTasksOfWeek() {
        int weekNo = this.getCurrentWeekNo();
        int year = LocalDate.now().getYear();
        // mongo template find
        lenient().when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any())).then((Answer<List<Task>>) invocation -> this.filterTasksByWeek(weekNo, year).stream().filter(Task::isComplete).toList());

        tasks = taskDirector.constructRandomTasks(50);
        for (Task task : tasks) {
            int taskId = Integer.parseInt(task.getTaskId());
            task.setTaskDateTime(LocalDateTime.now().plusDays(15));
            if (taskId % 2 == 0) {
                task.setTaskDateTime(LocalDateTime.now().minusDays(15));
                task.complete();
            }
            if (taskId % 3 == 0) {
                task.setTaskDateTime(LocalDateTime.now());
            }
            if (taskId % 5 != 0) {
                task.archive();
            }
        }

        List<Task> taskList = taskService.allTasksOfWeek(weekNo, year);
        assertEquals(2, taskList.size());
        for (Task task : taskList) {
            int taskId = Integer.parseInt(task.getTaskId());
            assertEquals(0, taskId % 3);
            assertFalse(task.isArchived());
            assertTrue(task.getState() == TaskStates.DONE || task.getState() == TaskStates.CANCEL);
            assertTrue(task.getTaskDateTime().isAfter(this.getWeekStart(weekNo, year)));
            assertTrue(task.getTaskDateTime().isBefore(this.getWeekStart(weekNo, year).plusWeeks(1)));
        }
    }

    private Method getweekStartMethod() throws NoSuchMethodException {
        Method method = TaskServiceImpl.class.getDeclaredMethod("getWeekStart", Integer.class, Integer.class);
        method.setAccessible(true);
        return method;
    }

    private Method getweekStartLocaleMethod() throws NoSuchMethodException {
        Method method = TaskServiceImpl.class.getDeclaredMethod("getWeekStart", Integer.class, Integer.class, Locale.class);
        method.setAccessible(true);
        return method;
    }

    private void setupMocks() {
        //count
        lenient().when(taskRepository.count()).then((Answer<Long>) invocation -> (long)tasks.size());

        // find by id
        lenient().when(taskRepository.findById(Mockito.anyString())).then((Answer<Optional<Task>>) invocation -> {
            String taskId = (String) invocation.getArgument(0, String.class);
            Task task = tasks.stream().filter(t -> t.getTaskId().equals(taskId)).findFirst().orElse(null);
            return task == null ? Optional.empty() : Optional.of(task);
        });

        // exists by id
        lenient().when(taskRepository.existsById(Mockito.anyString())).then((Answer<Boolean>) invocation -> {
            String taskId = (String) invocation.getArgument(0, String.class);
            Task task = tasks.stream().filter(t -> t.getTaskId().equals(taskId)).findFirst().orElse(null);
            return task != null;
        });

        // getAllByCategory
        lenient().when(taskRepository.getAllByCategoryIdAndArchived(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.any(Pageable.class))).then((Answer<Page<Task>>) invocation -> {
            Long categoryId = (Long) invocation.getArgument(0, Long.class);
            Boolean inclArchived = (Boolean) invocation.getArgument(1, Boolean.class);
            Pageable pageable = (Pageable) invocation.getArgument(2, Pageable.class);

            List<Task> taskList = tasks.stream().filter(t -> categoryId.equals(t.getCategoryId())).toList();

            if (!inclArchived) {
                taskList = taskList.stream().filter(t -> Boolean.FALSE.equals(t.getArchived())).toList();
            }

            return new PageImpl<>(taskList, pageable, taskList.size());
        });

        // get all by category and state
        lenient().when(taskRepository.getAllByCategoryIdAndStateAndArchived(Mockito.anyLong(), Mockito.any(TaskStates.class), Mockito.anyBoolean(), Mockito.any(Pageable.class))).then((Answer<Page<Task>>) invocation -> {
            long categoryId = (long) invocation.getArgument(0, Long.class);
            TaskStates taskState = (TaskStates) invocation.getArgument(1, TaskStates.class);
            Boolean inclArchived = (Boolean) invocation.getArgument(2, Boolean.class);
            Pageable pageable = (Pageable) invocation.getArgument(3, Pageable.class);
            List<Task> tasksToReturn = tasks.stream().filter(t -> t.getCategoryId().equals(categoryId)).toList();
            tasksToReturn = tasksToReturn.stream().filter(t -> t.getState().equals(taskState)).toList();
            if (!inclArchived) {
                tasksToReturn = tasksToReturn.stream().filter(t -> Boolean.FALSE.equals(t.getArchived())).toList();
            }
            return new PageImpl<>(tasksToReturn, pageable, tasksToReturn.size());
        });

        // get all by state and archived
        lenient().when(taskRepository.getAllByStateAndArchived(Mockito.any(TaskStates.class), Mockito.anyBoolean(), Mockito.any(Pageable.class))).then((Answer<Page<Task>>) invocation -> {
            TaskStates taskState = (TaskStates) invocation.getArgument(0, TaskStates.class);
            Boolean archived = (Boolean) invocation.getArgument(1, Boolean.class);
            Pageable pageable = (Pageable) invocation.getArgument(2, Pageable.class);

            List<Task> tasksToReturn = tasks.stream().filter(t -> archived.equals(t.getArchived())).toList();
            tasksToReturn = tasksToReturn.stream().filter(t -> taskState.equals(t.getState())).toList();
            return new PageImpl<>(tasksToReturn, pageable, tasksToReturn.size());
        });

        // find all
        lenient().when(taskRepository.findAll()).then((Answer<List<Task>>) invocation -> tasks);

        // find all not archived
        lenient().when(taskRepository.getAllByArchived(Mockito.anyBoolean(), Mockito.any(Pageable.class))).then((Answer<Page<Task>>) invocation -> {
            Boolean archived = (Boolean) invocation.getArgument(0, Boolean.class);
            Pageable pageable = (Pageable) invocation.getArgument(1, Pageable.class);
            return new PageImpl<>(tasks.stream().filter(t -> t.isArchived().equals(archived)).toList(), pageable, tasks.size());
        });

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

        // mongo template find
        lenient().when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any())).then((Answer<List<Task>>) invocation -> {
            Query query = (Query) invocation.getArgument(0, Query.class);
            String search = "project";
            boolean inclArch = query.toString().contains("true");
            return this.findTasks(search, inclArch);
        });

        lenient().when(mongoTemplate.count(Mockito.any(Query.class), Mockito.any(Class.class))).thenAnswer((Answer<Integer>) invocation -> {
            Query query = (Query) invocation.getArgument(0, Query.class);
            String search = "project";
            boolean inclArch = query.toString().contains("true");
            return this.findTasks(search, inclArch).size();
        });
    }

    private List<Task> findTasks(String search, boolean inclArch) {
        List<Task> taskList = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getTitle().contains(search) ||
                    null != task.getDescription() && task.getDescription().contains(search) ||
                    task.getState().toString().contains(search) ||
                    null != task.getBlockReason() && task.getBlockReason().contains(search)) {
                taskList.add(task);
            }
            if (task.getTaskLinks() != null && !task.getTaskLinks().isEmpty()) {
                for (TaskLink taskLink : task.getTaskLinks()) {
                    if (taskLink.getUrl().contains(search) || taskLink.getName().contains(search)) {
                        taskList.add(task);
                    }
                }
            }
            if (task.getBlockingIssues() != null && !task.getBlockingIssues().isEmpty()) {
                for (TaskLink taskLink : task.getBlockingIssues()) {
                    if (taskLink.getUrl().contains(search) || taskLink.getName().contains(search)) {
                        taskList.add(task);
                    }
                }
            }
        }
        if (!inclArch) {
            taskList = taskList.stream().filter(t -> t.getArchived().equals(Boolean.FALSE)).toList();
        }

        return taskList.stream().distinct().collect(Collectors.toList());
    }

    private ArrayList<Task> filterTasksByWeek(int weekNo, int year) {
        ArrayList<Task> taskList = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isActual() &&
                    task.getTaskDateTime().isAfter(ChronoLocalDateTime.from(this.getWeekStart(weekNo, year))) &&
                    task.getTaskDateTime().isBefore(ChronoLocalDateTime.from(this.getWeekStart(weekNo, year).plusWeeks(1)))) {
                taskList.add(task);
            }
        }
        return taskList;
    }

    private LocalDateTime getWeekStart(Integer weekNo, Integer year) {
        // Get the first day of the specified week
        return LocalDate.ofYearDay(year, 1)
                .with(WeekFields.of(Locale.getDefault()).weekOfYear(), weekNo)
                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1).atStartOfDay();
    }

    private int getCurrentWeekNo() {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return LocalDate.now().get(weekFields.weekOfWeekBasedYear());
    }
}
