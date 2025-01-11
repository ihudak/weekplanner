package eu.dec21.wp.tasks.controller;

import eu.dec21.wp.exceptions.ResourceNotFoundException;
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

@Tag(name="WeekPlanner-Tasks", description = "Tasks Management API")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TaskController(TaskService taskService, CategoryRepository categoryRepository) {
        this.taskService = taskService;
        this.categoryRepository = categoryRepository;
    }

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
        Task ignore = taskService.getTaskById(id); // check if the task exist
        // check if the category exists
        this.verifyCategory(task.getCategoryId());
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
        categoryRepository.getAllCategories();
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
    public TaskResponse getTasksByCategoryAndState(
            @Parameter(name="categoryId", description = "Category ID", example = "45") @RequestParam("categoryId") Long categoryId,
            @Parameter(name="state", description = "Task State", example = "DONE") @RequestParam("state") TaskStates state,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        // check if the category exists
        this.verifyCategory(categoryId);
        return taskService.getAllTasksByCategoryIdAndState(categoryId, state, pageNo, pageSize);
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
            @Parameter(name="inclArchived", description = "Whether to include archived tasks", example = "true") @RequestParam(value = "inclArchived", defaultValue = "false", required = false) boolean inclArchived,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        return taskService.searchTasks(searchString, inclArchived, pageNo, pageSize);
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

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/archive")
    @Operation(summary = "Archive Task by ID")
    public ResponseEntity<Task> archiveTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.archiveTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/complete")
    @Operation(summary = "Complete Task by ID")
    public ResponseEntity<Task> completeTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.completeTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/cancel")
    @Operation(summary = "Cancel Task by ID")
    public ResponseEntity<Task> cancelTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.cancelTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/reopen")
    @Operation(summary = "Reopen Task by ID")
    public ResponseEntity<Task> reopenTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.reopenTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/block")
    @Operation(summary = "Block Task by ID")
    public ResponseEntity<Task> blockTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.blockTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/unblock")
    @Operation(summary = "Unblock Task by ID")
    public ResponseEntity<Task> unblockTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.unblockTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/activate")
    @Operation(summary = "Activate Task by ID")
    public ResponseEntity<Task> activateTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.activateTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/deactivate")
    @Operation(summary = "Deactivate Task by ID")
    public ResponseEntity<Task> deactivateTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.deactivateTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/forward")
    @Operation(summary = "Move Task state Forward by taskID")
    public ResponseEntity<Task> forwardTaskState(@PathVariable String id) {
        return ResponseEntity.ok(taskService.stateForwardTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/backward")
    @Operation(summary = "Move Task state Backwaerd by taskID")
    public ResponseEntity<Task> backwardTaskState(@PathVariable String id) {
        return ResponseEntity.ok(taskService.stateBackwardTask(id));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PatchMapping("{id}/start")
    @Operation(summary = "Start Task by ID")
    public ResponseEntity<Task> startTask(@PathVariable String id) {
        return ResponseEntity.ok(taskService.startTask(id));
    }

    private void verifyCategory(Long id) throws ResourceNotFoundException {
        // no caching on purpose. Every time fetch category by a rest call
        categoryRepository.getCategoryById(id == 0L ? 1L : id);
    }

    // TODO: delete this after Perform
    private void prepopulateCategories() {
        categoryRepository.createCategory(1L, "Graal",    30);
        categoryRepository.createCategory(2L, "Grail",    15);
        categoryRepository.createCategory(3L, "Apps",     10);
        categoryRepository.createCategory(4L, "TechFit",   0);
    }
}
