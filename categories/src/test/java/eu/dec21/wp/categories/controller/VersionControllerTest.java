package eu.dec21.wp.categories.controller;

import eu.dec21.wp.categories.service.CategoryService;
import eu.dec21.wp.model.Version;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = VersionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class VersionControllerTest {
    @Value("${application.version}")
    private String svcVer;
    @Value("${application.date}")
    private String svcDate;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    CategoryService categoryService;

    private Version version;

    @BeforeEach
    public void init() {
        version = new Version("categories", svcVer, svcDate, "OK", "Count: 10");
    }

    @Test
    public void VersionController_GetVersionReturnVersion() throws Exception {
        when(categoryService.count()).thenReturn(10L);

        ResultActions response = mockMvc.perform(get("/api/v1/version")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int)version.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.serviceId", CoreMatchers.is(version.getServiceId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ver", CoreMatchers.is(version.getVer())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date", CoreMatchers.is(version.getDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(version.getStatus())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(version.getMessage())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test public void VersionController_GetVersionReturnVersionNotFound() throws Exception {
        when(categoryService.count()).thenReturn(10L);

        ResultActions response = mockMvc.perform(get("/api/v1/version/5")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.equalTo("")))
                .andDo(MockMvcResultHandlers.print());

        response = mockMvc.perform(get("/api/v2/version")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.equalTo("")))
                .andDo(MockMvcResultHandlers.print());

        response = mockMvc.perform(post("/api/v1/version")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.equalTo("")))
                .andDo(MockMvcResultHandlers.print());
    }
}
