package eu.dec21.wp.tasks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dec21.wp.exceptions.BadRequestException;
import eu.dec21.wp.exceptions.ResourceNotFoundException;
import eu.dec21.wp.tasks.collection.*;
import eu.dec21.wp.tasks.repository.CategoryRepository;
import eu.dec21.wp.tasks.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;
    @MockitoBean
    private CategoryRepository categoryRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private List<Task> tasks;
    private final TaskDirector taskDirector;

    TaskControllerTest() {
        this.taskDirector = new TaskDirector();
    }

    @BeforeEach
    void setUp() {
        tasks = new ArrayList<>();

        this.setupMocks();
    }

    @Test
    void createTask() throws Exception {

        Task task = taskDirector.constructTaskForCategoryState(4, TaskStates.IMPL);
        task.block();
        task.addBlockLink(new TaskLink("blocking issue in Jira", "https://github.com/dec21/wp-tasks/blob/master/tasks.json"));
        task.setBlockReason("blocking issue in Jira");
        task.addTaskLink(new TaskLink("another task in Jira", "https://github.com/dec21/wp-tasks/blob/master/tasks.json"));

        ResultActions response = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(task.getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(task.getCategoryId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(task.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(task.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value(task.getState().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cronExpression").value(task.getCronExpression()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[0].name").value(task.getTaskLinks().get(0).getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[0].url").value(task.getTaskLinks().get(0).getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[1].name").value(task.getTaskLinks().get(1).getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[1].url").value(task.getTaskLinks().get(1).getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[2].name").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[2].url").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isBlocked").value(task.isBlocked()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockReason").value(task.getBlockReason()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockingIssues[0].name").value(task.getBlockingIssues().getFirst().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockingIssues[0].url").value(task.getBlockingIssues().getFirst().getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockingIssues[1].name").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockingIssues[1].url").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.addedPriority").value(task.getAddedPriority()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateTask() throws Exception {
        Task task = taskDirector.constructTaskForCategoryState(4, TaskStates.IMPL);
        String taskId = task.getTaskId();
        task.setTitle("Old Title");
        task.setDescription("Old Description");
        assertEquals(0, tasks.size());

        ResultActions response = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(taskId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Old Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Old Description"))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(1, tasks.size());

        task.setTitle("Another title");
        task.setDescription("Another description");
        task.setCategoryId(8L);

        response = mockMvc.perform(put("/api/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(taskId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(8L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Another title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Another description"))
                .andDo(MockMvcResultHandlers.print());
        assertEquals(1, tasks.size());
        assertEquals("Another title", tasks.getFirst().getTitle());
        assertEquals("Another description", tasks.getFirst().getDescription());
        assertEquals(8L, tasks.getFirst().getCategoryId());
    }

    @Test
    void updateNonExistingTask() throws Exception {
        Task task = taskDirector.constructTaskForCategoryState(4, TaskStates.IMPL);

        ResultActions response = mockMvc.perform(put("/api/v1/tasks/" + task.getTaskId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        response.andExpect(MockMvcResultMatchers.status().isNotFound()).andDo(MockMvcResultHandlers.print());

        assertEquals(0, tasks.size());
    }

    @Test
    void getTasks() throws Exception {
        Task task = taskDirector.constructTaskForCategoryState(4, TaskStates.IMPL);
        task.block();
        task.addBlockLink(new TaskLink("blocking issue in Jira", "https://github.com/dec21/wp-tasks/blob/master/tasks.json"));
        task.setBlockReason("blocking issue in Jira");
        task.addTaskLink(new TaskLink("another task in Jira", "https://github.com/dec21/wp-tasks/blob/master/tasks.json"));
        tasks.add(task);

        ResultActions response = mockMvc.perform(get("/api/v1/tasks/" + task.getTaskId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(task.getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(task.getCategoryId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(task.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(task.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value(task.getState().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cronExpression").value(task.getCronExpression()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[0].name").value(task.getTaskLinks().get(0).getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[0].url").value(task.getTaskLinks().get(0).getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[1].name").value(task.getTaskLinks().get(1).getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[1].url").value(task.getTaskLinks().get(1).getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[2].name").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[2].url").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isBlocked").value(task.isBlocked()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockReason").value(task.getBlockReason()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockingIssues[0].name").value(task.getBlockingIssues().getFirst().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockingIssues[0].url").value(task.getBlockingIssues().getFirst().getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockingIssues[1].name").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.blockingIssues[1].url").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.addedPriority").value(task.getAddedPriority()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getNonExistingTask() throws Exception {
        List<Task> taskList = taskDirector.constructRandomTasks(5);
        tasks.addAll(taskList);
        Task task = taskDirector.constructTaskForCategoryState(4, TaskStates.IMPL);
        task.setTaskId("non-existing-task");

        ResultActions response = mockMvc.perform(get("/api/v1/tasks/" + task.getTaskId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound()).andDo(MockMvcResultHandlers.print());

        assertEquals(5, tasks.size());
    }

    @Test
    void getAllTasks() throws Exception {
        List<Task> taskList = taskDirector.constructRandomTasks(5);
        tasks.addAll(taskList);

        ResultActions response = mockMvc.perform(get("/api/v1/tasks?pageNo=0&pageSize=10")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].taskId").value(tasks.get(0).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].taskId").value(tasks.get(1).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].taskId").value(tasks.get(2).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[3].taskId").value(tasks.get(3).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[4].taskId").value(tasks.get(4).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[5].taskId").doesNotExist())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(5, tasks.size());
    }

    @Test
    void getTasksByCategory() throws Exception {
        List<Task> taskList = taskDirector.constructRandomTasks(5);
        for (Task task : taskList) {
            task.setCategoryId(5L);
        }
        taskList.get(0).setCategoryId(4L);
        taskList.get(4).setCategoryId(4L);

        tasks.addAll(taskList);

        ResultActions response = mockMvc.perform(get("/api/v1/tasks/findByCategory?categoryId=5&pageNo=0&pageSize=10")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].taskId").value(tasks.get(1).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].taskId").value(tasks.get(2).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].taskId").value(tasks.get(3).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[3].taskId").doesNotExist())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getTasksByNonExistingCategory() throws Exception {
        List<Task> taskList = taskDirector.constructRandomTasks(5);
        for (Task task : taskList) {
            task.setCategoryId(3L);
        }

        tasks.addAll(taskList);

        ResultActions response = mockMvc.perform(get("/api/v1/tasks/findByCategory?categoryId=5&pageNo=0&pageSize=10")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].taskId").doesNotExist())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getTasksByCategoryAndState() throws Exception {
        List<Task> taskList = taskDirector.constructRandomTasks(5);
        for (Task task : taskList) {
            task.setCategoryId(5L);
            task.setState(TaskStates.IMPL);
        }
        taskList.get(0).setCategoryId(4L);
        taskList.get(4).setState(TaskStates.READY);

        tasks.addAll(taskList);

        ResultActions response = mockMvc.perform(get("/api/v1/tasks/findByCategoryAndState?categoryId=5&state=IMPL")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].taskId").value(tasks.get(1).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].taskId").value(tasks.get(2).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].taskId").value(tasks.get(3).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[3].taskId").doesNotExist())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getTasksByNonExistingCategoryAndState() throws Exception {
        List<Task> taskList = taskDirector.constructRandomTasks(5);
        for (Task task : taskList) {
            task.setCategoryId(5L);
            task.setState(TaskStates.IMPL);
        }
        taskList.get(0).setCategoryId(4L);
        taskList.get(1).setCategoryId(3L);
        taskList.get(2).setCategoryId(2L);
        taskList.get(3).setState(TaskStates.DONE);
        taskList.get(4).setState(TaskStates.READY);

        tasks.addAll(taskList);

        ResultActions response = mockMvc.perform(get("/api/v1/tasks/findByCategoryAndState?categoryId=5&state=IMPL")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].taskId").doesNotExist())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void searchTasks() throws Exception {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        for (Task task : tasks) {
            task.setTitle("my project in Java");
        }
        tasks.get(0).setTitle("boo");
        tasks.get(4).setTitle("foo");
        tasks.get(1).archive();

        ResultActions response = mockMvc.perform(get("/api/v1/tasks/search?searchString=project&pageNo=0&pageSize=10")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].taskId").value(tasks.get(2).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].taskId").value(tasks.get(3).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].taskId").doesNotExist())
                .andDo(MockMvcResultHandlers.print());

        response = mockMvc.perform(get("/api/v1/tasks/search?searchString=project&inclArchived=true&pageNo=0&pageSize=10")
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].taskId").value(tasks.get(1).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].taskId").value(tasks.get(2).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].taskId").value(tasks.get(3).getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[3].taskId").doesNotExist())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void searchTasksByNonExistingPattern() throws Exception {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        for (Task task : tasks) {
            task.setTitle("my issues in Java");
        }

        ResultActions response = mockMvc.perform(get("/api/v1/tasks/search?searchString=project&pageNo=0&pageSize=10")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNo").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].taskId").doesNotExist())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void deleteTask() throws Exception {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        String taskId = tasks.getFirst().getTaskId();

        ResultActions response = mockMvc.perform(delete("/api/v1/tasks/" + taskId)
            .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(taskId)).andDo(MockMvcResultHandlers.print());

        assertEquals(4, tasks.size());
        Task deletedTask = tasks.stream().filter(a -> a.getTaskId().equals(taskId)).findFirst().orElse(null);
        assertNull(deletedTask);
    }

    @Test
    void deleteNonExistingTask() throws Exception {
        tasks.addAll(taskDirector.constructRandomTasks(5));
        String taskId = "non-existing-task";

        ResultActions response = mockMvc.perform(delete("/api/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound()).andDo(MockMvcResultHandlers.print());

        assertEquals(5, tasks.size());
        Task deletedTask = tasks.stream().filter(a -> a.getTaskId().equals(taskId)).findFirst().orElse(null);
        assertNull(deletedTask);
    }

    @Test
    void archiveTask() throws Exception {
        this.setupPatchMocks();
        final String action = "archive";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();
        task.complete();
        assertFalse(task.getArchived());
        this.checkPositivePatchResponse(action, task, null, null);
        assertTrue(task.getArchived());

        this.checkPositivePatchResponse(action, task, null, null);
        assertTrue(task.getArchived());

        task = tasks.getLast();
        assertFalse(task.isArchived());
        assertFalse(task.isComplete());
        this.checkBadRequestPatchResponse(action, task, task.getState());

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void completeTask() throws Exception {
        this.setupPatchMocks();
        final String action = "complete";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();
        assertFalse(task.isComplete());

        this.checkPositivePatchResponse(action, task, null, TaskStates.DONE);

        // trying to complete a task that is already complete
        this.checkBadRequestPatchResponse(action, task, TaskStates.DONE);

        task.cancel();
        task.archive();
        this.checkBadRequestPatchResponse(action, task, TaskStates.CANCEL);

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void cancelTask() throws Exception {
        this.setupPatchMocks();
        final String action = "cancel";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();
        assertFalse(task.isComplete());

        this.checkPositivePatchResponse(action, task, null, TaskStates.CANCEL);

        // trying to cancel a task that is already cancelled
        this.checkPositivePatchResponse(action, task, TaskStates.CANCEL, TaskStates.CANCEL);

        task.setState(TaskStates.DONE);
        task.archive();
        this.checkBadRequestPatchResponse(action, task, TaskStates.DONE);

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void reopenTask() throws Exception {
        this.setupPatchMocks();
        final String action = "reopen";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();

        this.checkPositivePatchResponse(action, task, TaskStates.DONE, TaskStates.READY);

        // trying to reopen an open task
        this.checkBadRequestPatchResponse(action, task, TaskStates.READY);

        task.complete();
        task.archive();
        this.checkBadRequestPatchResponse(action, task, TaskStates.DONE);

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void blockTask() throws Exception {
        this.setupPatchMocks();
        final String action = "block";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();
        assertFalse(task.isBlocked());

        this.checkPositivePatchResponse(action, task, null, null);
        assertTrue(task.isBlocked());

        task.cancel();
        assertFalse(task.isBlocked());
        this.checkBadRequestPatchResponse(action, task, null);
        assertFalse(task.isBlocked());

        task.archive();
        this.checkBadRequestPatchResponse(action, task, null);
        assertFalse(task.isBlocked());

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void unblockTask() throws Exception {
        this.setupPatchMocks();
        final String action = "unblock";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();
        task.block();
        assertTrue(task.isBlocked());

        this.checkPositivePatchResponse(action, task, null, null);
        assertFalse(task.isBlocked());

        task.complete();
        task.archive();

        this.checkBadRequestPatchResponse(action, task, null);
        assertFalse(task.isBlocked());

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void activateTask() throws Exception {
        this.setupPatchMocks();
        final String action = "activate";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();
        task.deactivate();
        assertFalse(task.isActive());

        this.checkPositivePatchResponse(action, task, null, null);
        assertTrue(task.isActive());

        task.complete();
        task.archive();

        assertFalse(task.isActive());
        this.checkBadRequestPatchResponse(action, task, null);
        assertFalse(task.isActive());

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void deactivateTask() throws Exception {
        this.setupPatchMocks();
        final String action = "deactivate";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();
        assertTrue(task.isActive());

        this.checkPositivePatchResponse(action, task, null, null);
        assertFalse(task.isActive());
        this.checkPositivePatchResponse(action, task, null, null);
        assertFalse(task.isActive());

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void forwardTaskState() throws Exception {
        this.setupPatchMocks();
        final String action = "forward";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();

        this.checkPositivePatchResponse(action, task, TaskStates.PREP, TaskStates.READY);
        this.checkPositivePatchResponse(action, task, TaskStates.READY, TaskStates.IMPL);
        this.checkPositivePatchResponse(action, task, TaskStates.IMPL, TaskStates.DONE);

        this.checkBadRequestPatchResponse(action, task, TaskStates.DONE);
        this.checkBadRequestPatchResponse(action, task, TaskStates.CANCEL);

        task.archive();
        this.checkBadRequestPatchResponse(action, task, TaskStates.DONE);
        assertTrue(task.isArchived());

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void backwardTaskState() throws Exception {
        this.setupPatchMocks();
        final String action = "backward";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();

        this.checkPositivePatchResponse(action, task, TaskStates.IMPL, TaskStates.READY);
        this.checkPositivePatchResponse(action, task, TaskStates.READY, TaskStates.PREP);

        this.checkBadRequestPatchResponse(action, task, TaskStates.PREP);
        this.checkBadRequestPatchResponse(action, task, TaskStates.CANCEL);
        this.checkBadRequestPatchResponse(action, task, TaskStates.DONE);

        task.archive();
        this.checkBadRequestPatchResponse(action, task, TaskStates.CANCEL);
        assertTrue(task.isArchived());

        this.checkNotFoundPatchResponse(action);
    }

    @Test
    void startTask() throws Exception {
        this.setupPatchMocks();
        final String action = "start";
        tasks.addAll(taskDirector.constructRandomTasks(5));
        Task task = tasks.getFirst();

        this.checkPositivePatchResponse(action, task, TaskStates.PREP, TaskStates.IMPL);
        this.checkPositivePatchResponse(action, task, TaskStates.READY, TaskStates.IMPL);

        this.checkBadRequestPatchResponse(action, task, TaskStates.IMPL);
        this.checkBadRequestPatchResponse(action, task, TaskStates.DONE);
        this.checkBadRequestPatchResponse(action, task, TaskStates.CANCEL);

        task.archive();
        this.checkBadRequestPatchResponse(action, task, TaskStates.CANCEL);
        assertTrue(task.isArchived());

        this.checkNotFoundPatchResponse(action);
    }

    private void checkBadRequestPatchResponse(String action, Task task, TaskStates desiredState) throws Exception {
        if (null != desiredState) {
            task.setState(desiredState);
        }

        ResultActions response = mockMvc.perform(patch("/api/v1/tasks/" + task.getTaskId() + "/" + action)
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isBadRequest()).andDo(MockMvcResultHandlers.print());

        if (null != desiredState) {
            assertEquals(desiredState, task.getState());
        }
    }

    private void checkNotFoundPatchResponse(String action) throws Exception {
        ResultActions response = mockMvc.perform(patch("/api/v1/tasks/" + "non-existing" + "/" + action)
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isNotFound()).andDo(MockMvcResultHandlers.print());
    }

    private void checkPositivePatchResponse(String action, Task task, TaskStates initialState, TaskStates finalState) throws Exception {
        if (null != initialState) {
            task.setState(initialState);
        }

        ResultActions response = mockMvc.perform(patch("/api/v1/tasks/" + task.getTaskId() + "/" + action)
                .contentType(MediaType.APPLICATION_JSON));

        if (null != finalState) {
            assertEquals(finalState, task.getState());
        }

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(task.getTaskId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoryId").value(task.getCategoryId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(task.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(task.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.state").value(task.getState().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cronExpression").value(task.getCronExpression()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[0].name").value(task.getTaskLinks().getFirst().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[0].url").value(task.getTaskLinks().getFirst().getUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[1].name").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskLinks[1].url").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isBlocked").value(task.isBlocked()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addedPriority").value(task.getAddedPriority()))
                .andDo(MockMvcResultHandlers.print());
    }

    private void setupMocks() {
        when(taskService.save(any(Task.class))).then((Answer<Task>) invocation -> {
            Task task = invocation.getArgument(0, Task.class);

            tasks.replaceAll(t -> t.getTaskId().equals(task.getTaskId()) ? task : t);
            if (tasks.stream().noneMatch(t -> t.getTaskId().equals(task.getTaskId()))) {
                tasks.add(task);
            }
            return task;
        });

        when(taskService.saveAll(anyList())).then((Answer<List<Task>>) invocation -> {
            @SuppressWarnings("unchecked")
            List<Task> tasksArgument = (List<Task>) invocation.getArgument(0, List.class);
            for (Task task : tasksArgument) {
                tasks.replaceAll(t -> t.getTaskId().equals(task.getTaskId()) ? task : t);
                if (tasks.stream().noneMatch(t -> t.getTaskId().equals(task.getTaskId()))) {
                    tasks.add(task);
                }
            }
            return tasksArgument;
        });

        when(taskService.getTaskById(anyString())).then((Answer<Task>) invocation -> {
            String taskId = (String) invocation.getArgument(0, String.class);
            return tasks.stream().filter(t -> t.getTaskId().equals(taskId)).findFirst().orElseThrow(() -> new ResourceNotFoundException("Task does not exist with the given ID: " + taskId));
        });

        when(taskService.getAllTasksByCategoryId(anyLong(), any(Integer.class), any(Integer.class))).then((Answer<TaskResponse>) invocation -> {
            Long categoryId = (Long) invocation.getArgument(0, Long.class);
            Integer pageNo = (Integer) invocation.getArgument(1, Integer.class);
            Integer pageSize = (Integer) invocation.getArgument(2, Integer.class);

            List<Task> taskList = tasks.stream().filter(t -> t.getCategoryId().equals(categoryId)).toList();
            return new TaskResponse(taskList, pageNo, min(pageSize, taskList.size()), taskList.size(), 1, true);
        });

        when(taskService.getAllTasksByCategoryIdAndState(anyLong(), any(TaskStates.class), any(Integer.class), any(Integer.class))).then((Answer<TaskResponse>) invocation -> {
            Long categoryId = (Long) invocation.getArgument(0, Long.class);
            TaskStates taskStates = (TaskStates) invocation.getArgument(1, TaskStates.class);
            Integer pageNo = (Integer) invocation.getArgument(2, Integer.class);
            Integer pageSize = (Integer) invocation.getArgument(3, Integer.class);
            List<Task> taskList = tasks.stream().filter(t -> t.getCategoryId().equals(categoryId)).toList();
            taskList = taskList.stream().filter(t -> t.getState().equals(taskStates)).toList();
            return new TaskResponse(taskList, pageNo, min(pageSize, taskList.size()), taskList.size(), 1, true);
        });

        when(taskService.findAll(anyInt(), anyInt())).then((Answer<TaskResponse>) invocation -> {
            Integer pageNo = (Integer) invocation.getArgument(0, Integer.class);
            Integer pageSize = (Integer) invocation.getArgument(1, Integer.class);

            return new TaskResponse(tasks, pageNo, min(pageSize, tasks.size()), tasks.size(), 1, true);
        });

        when(taskService.searchTasks(anyString(), anyBoolean(), anyInt(), anyInt())).then((Answer<TaskResponse>) invocation -> {
            String searchStr = (String) invocation.getArgument(0, String.class);
            Boolean inclArchived = (Boolean) invocation.getArgument(1, Boolean.class);
            Integer pageNo = (Integer) invocation.getArgument(2, Integer.class);
            Integer pageSize = (Integer) invocation.getArgument(3, Integer.class);

            List<Task> taskList = tasks.stream().filter(a -> a.getTitle().contains(searchStr)).toList();
            if (!inclArchived) {
                taskList = taskList.stream().filter(t -> t.getArchived().equals(Boolean.FALSE)).toList();
            }
            return new TaskResponse(taskList, pageNo, min(pageSize, taskList.size()), taskList.size(), 1, true);
        });

        doAnswer((Answer<String>) invocation -> {
            String taskId = (String) invocation.getArgument(0, String.class);
            if (tasks.isEmpty() || tasks.stream().noneMatch(t -> t.getTaskId().equals(taskId))) {
                throw new ResourceNotFoundException("Task does not exist with the given ID: " + taskId);
            }
            tasks.removeIf(task -> task.getTaskId().equals(taskId));
            return taskId;
        }).when(taskService).delete(anyString());

        given(taskService.count()).willReturn((long) tasks.size());

        when(categoryRepository.getCategoryById(anyLong())).thenReturn(null);
    }

    private void setupPatchMocks() {
        when(taskService.archiveTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, false, false);
            if (!task.isComplete()) {
                throw new BadRequestException("Task is not complete.");
            }
            task.archive();
            return task;
        });

        when(taskService.completeTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, true, true);
            task.complete();
            return task;
        });

        when(taskService.cancelTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, true, false);
            task.cancel();
            return task;
        });

        when(taskService.reopenTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, true, false);
            if (!task.isComplete()) {
                throw new BadRequestException("Task is not complete.");
            }
            task.reopen();
            return task;
        });

        when(taskService.blockTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, true, true);
            task.block();
            return task;
        });

        when(taskService.unblockTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, true, true);
            task.unblock();
            return task;
        });

        when(taskService.activateTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, true, false);
            task.activate();
            return task;
        });

        when(taskService.deactivateTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, false, false);
            task.deactivate();
            return task;
        });

        when(taskService.stateForwardTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, true, true);
            task.nextState();
            return task;
        });

        when(taskService.stateBackwardTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, true, true);
            if (TaskStates.PREP.equals(task.getState())) {
                throw new BadRequestException("Task is in PREP state.");
            }
            task.prevState();
            return task;
        });

        when(taskService.startTask(anyString())).then((Answer<Task>) invocation -> {
            Task task = this.verifyTask(invocation, true, true);
            if (!task.getState().isNew()) {
                throw new BadRequestException("Task is not new");
            }
            task.start();
            return task;
        });
    }

    private Task verifyTask(InvocationOnMock invocation, boolean mustNotArchived, boolean mustNotComplete) throws ResourceNotFoundException, BadRequestException{
        String taskId = (String) invocation.getArgument(0, String.class);
        Task task = tasks.stream().filter(t -> t.getTaskId().equals(taskId)).findFirst().orElse(null);
        if (task == null) {
            throw new ResourceNotFoundException("Task does not exist with the given ID: " + taskId);
        }
        if (mustNotArchived && task.isArchived()) {
            throw new BadRequestException("Task is archived.");
        }
        if (mustNotComplete && task.isComplete()) {
            throw new BadRequestException("Task is complete.");
        }
        return task;
    }
}
