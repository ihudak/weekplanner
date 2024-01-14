package eu.dec21.wp.tasks.controller;

import eu.dec21.wp.exceptions.ResourceNotFoundException;
import eu.dec21.wp.model.Category;
import eu.dec21.wp.tasks.collection.Task;
import eu.dec21.wp.tasks.collection.TaskIdResponse;
import eu.dec21.wp.tasks.collection.TaskResponse;
import eu.dec21.wp.tasks.collection.TaskStates;
import eu.dec21.wp.tasks.repository.CategoryRepository;
import eu.dec21.wp.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="WeekPlanner-Tasks", description = "Tasks Management API")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private CategoryRepository categoryRepository;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "201", description = "Created", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PostMapping("")
    @Operation(summary = "Create a new Task")
    public ResponseEntity<Task> create(@RequestBody Task task) {
        // check if the category exists
        this.verifyCategory(task.getCategoryId());
        return new ResponseEntity<>(taskService.save(task), HttpStatus.CREATED);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PutMapping("{id}")
    @Operation(summary = "Update a Task")
    public ResponseEntity<Task> update(@PathVariable("id") String id, @RequestBody Task task) {
        Task storedTask = taskService.getTaskById(id);
        if (storedTask == null || !id.equals(storedTask.getTaskId())) {
            throw new ResourceNotFoundException("Task is wrong or not found with ID: " + id);
        }
        // check if the category exists
        this.verifyCategory(task.getCategoryId());
        if (null == task.getTaskId() || !id.equals(task.getTaskId())) {
            task.setTaskId(id);
        }
        return new ResponseEntity<>(taskService.save(task), HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("{id}")
    @Operation(summary = "Get Task by ID")
    public ResponseEntity<Task> getTaskByID(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = TaskResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("")
    @Operation(summary = "Get all Tasks")
    public TaskResponse getAllTasks(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return taskService.findAll(pageNo, pageSize);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = TaskResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("findByCategory")
    @Operation(summary = "Get all Tasks by category")
    public TaskResponse getTasksByCategory(
            @Parameter(name="categoryId", description = "Category ID", example = "45") @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        // verify if the category exists
        this.verifyCategory(categoryId);
        return taskService.getAllTasksByCategoryId(categoryId, pageNo, pageSize);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = Task.class)), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("findByCategoryAndState")
    @Operation(summary = "Get all Tasks")
    public List<Task> getTasksByCategoryAndState(
            @Parameter(name="categoryId", description = "Category ID", example = "45") @RequestParam("categoryId") Long categoryId,
            @Parameter(name="state", description = "Task State", example = "DONE") @RequestParam("state") TaskStates state
    ) {
        // check if the category exists
        this.verifyCategory(categoryId);
        return taskService.getAllTasksByCategoryIdAndState(categoryId, state);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = TaskResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("search")
    @Operation(summary = "Search Tasks")
    public TaskResponse searchTasks(
            @Parameter(name="searchString", description = "Search String", example = "clear database") @RequestParam("searchString") String searchString,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return taskService.searchTasks(searchString, pageNo, pageSize);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @DeleteMapping("{id}")
    @Operation(summary = "Delete Task by ID")
    public ResponseEntity<TaskIdResponse> delete(@PathVariable String id) {
        taskService.delete(id);
        return ResponseEntity.ok(new TaskIdResponse(id));
    }

    private void verifyCategory(Long id) throws ResourceNotFoundException {
        // no caching on purpose. Every time fetch category by a rest call
        Category category = categoryRepository.getCategoryById(id);
        if (null == category) {
            throw new ResourceNotFoundException("Category not found by ID: " + id.toString());
        }
    }

    // TODO: delete this after Perform
    private void prepopulateCategories() {
        categoryRepository.createCategory(1L, "Graal",    30);
        categoryRepository.createCategory(2L, "Grail",    15);
        categoryRepository.createCategory(3L, "Apps",     10);
        categoryRepository.createCategory(4L, "TechFit",   0);
    }
}
