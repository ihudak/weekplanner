package eu.dec21.wp.categories.controller;

import eu.dec21.wp.categories.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import eu.dec21.wp.model.Version;

@RestController
@RequestMapping("/api/v1/version")
public class VersionController {
    @Autowired
    private CategoryService categoryService;
    @Value("${application.version}")
    private String svcVer;
    @Value("${application.date}")
    private String svcDate;

    @GetMapping("")
    @Operation(summary = "Get version, release date and number of records in the DB")
    public Version getVersion() {
        return new Version("categories", svcVer, svcDate, "OK", "Count: " + categoryService.count());
    }
}
