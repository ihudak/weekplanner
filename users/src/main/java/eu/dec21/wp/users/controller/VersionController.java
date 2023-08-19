package eu.dec21.wp.users.controller;

import eu.dec21.wp.model.Version;
import eu.dec21.wp.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/version")
public class VersionController {
    private UserService userService;
    @Value("${service.version}")
    private String svcVer;
    @Value("${service.date}")
    private String svcDate;

    @GetMapping("")
    @Operation(summary = "Get version, release date and number of records in the DB")
    public Version getVersion() {
        return new Version("users", svcVer, svcDate, "OK", "Count: " + userService.count());
    }
}
