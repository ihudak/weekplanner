package eu.dec21.wp.workitems.controller;

import eu.dec21.wp.model.Version;
import eu.dec21.wp.workitems.repository.WorkItemRepository;
import eu.dec21.wp.workitems.service.WorkItemService;
import eu.dec21.wp.workitems.service.impl.WorkItemServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name="WeekPlanner-Simulator-Versions", description = "Simulator Versions API")
@RestController
@RequestMapping("/api/v1/version")
public class VersionController {
    private final WorkItemService workItemService = new WorkItemServiceImpl();
    @Value("${application.version}")
    private String svcVer;
    @Value("${application.date}")
    private String svcDate;

    private final Logger logger = LoggerFactory.getLogger(VersionController.class);

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(schema = @Schema(implementation = Version.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Server Error", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("")
    @Operation(summary = "Get version, release date and number of records in the DB")
    public Version getVersion() {
        if (logger.isDebugEnabled()) logger.debug("Version: {}; Release date: {}; WorkItems count: {}", svcVer, svcDate, workItemService.count());
        return new Version("simulator", svcVer, svcDate, "OK", "Count: " + workItemService.count());
    }
}
