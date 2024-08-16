package eu.dec21.wp.workitems.controller;

import eu.dec21.wp.workitems.dto.WorkItemDto;
import eu.dec21.wp.workitems.dto.WorkItemResponse;
import eu.dec21.wp.workitems.service.WorkItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="WeekPlanner-WorkItems", description = "WorkItems API")
@RestController
@RequestMapping("/api/v1/workitems")
@AllArgsConstructor
public class WorkItemController {
    private WorkItemService workItemService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created", content = { @Content(schema = @Schema(implementation = WorkItemDto.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PostMapping("")
    @Operation(summary = "Create a new WorkItem")
    public ResponseEntity<WorkItemDto> createWorkItem(@RequestBody WorkItemDto workItemDto) {
        WorkItemDto savedWorkItem = workItemService.createWorkItem(workItemDto);
        return new ResponseEntity<>(savedWorkItem, HttpStatus.CREATED);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = WorkItemDto.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("{id}")
    @Operation(summary = "Get WorkItem by ID")
    public ResponseEntity<WorkItemDto> getUserById(@PathVariable("id") Long workItemId) {
        WorkItemDto workItemDto = workItemService.getWorkItemById(workItemId);
        return ResponseEntity.ok(workItemDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = WorkItemResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("")
    @Operation(summary = "Get all WorkItems")
    public ResponseEntity<WorkItemResponse> getAllWorkItems(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize
    ) {
        return new ResponseEntity<>(workItemService.getAllWorkItems(pageNo, pageSize), HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated", content = { @Content(schema = @Schema(implementation = WorkItemDto.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @PutMapping("{id}")
    @Operation(summary = "Update WorkItem by ID")
    public ResponseEntity<WorkItemDto> updateWorkItem(@PathVariable("id") Long workItemId,
                                                      @RequestBody WorkItemDto updatedWorkItem) {
        WorkItemDto workItemDto = workItemService.updateWorkItem(workItemId, updatedWorkItem);
        return ResponseEntity.ok(workItemDto);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @DeleteMapping("{id}")
    @Operation(summary = "Delete WorkItem by ID")
    public ResponseEntity<String> deleteWorkItemById(@PathVariable("id") Long workItemId) {
        workItemService.deleteWorkItem(workItemId);
        return ResponseEntity.ok("WorkItem deleted with ID: " + workItemId);
    }
}
